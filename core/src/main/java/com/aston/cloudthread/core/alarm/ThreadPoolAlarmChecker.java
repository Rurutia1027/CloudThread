package com.aston.cloudthread.core.alarm;

import cn.hutool.core.date.DateUtil;
import com.aston.cloudthread.core.config.ApplicationProperties;
import com.aston.cloudthread.core.executor.CloudThreadExecutor;
import com.aston.cloudthread.core.executor.CloudThreadRegistry;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorProperties;
import com.aston.cloudthread.core.executor.ThreadPoolExecutorWrapper;
import com.aston.cloudthread.core.notification.dto.ThreadPoolAlarmNotifyDTO;
import com.aston.cloudthread.core.notification.service.NotifierDispatcher;
import com.aston.cloudthread.core.toolkit.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread Pool Alarm Checker
 * <p>
 * This class is responsible for monitoring thread pools and sending alarms
 * when certain thresholds are exceeded. It periodically checks:
 * <ul>
 *     <li>Queue usage rate</li>
 *     <li>Thread activity rate (active threads / maximum threads)</li>
 *     <li>Rejected task count</li>
 * </ul>
 * <p>
 * Alarms are dispatched through a {@link NotifierDispatcher} to the configured
 * notification channels (e.g., DingTalk, Slack, WeChat). Rate-limiting is applied
 * to avoid sending repeated alarms too frequently.
 * <p>
 * The class uses a scheduled executor service to run checks at fixed intervals.
 * It maintains a cache of last reject counts to determine if a new reject alarm
 * should be triggered.
 */

@Slf4j
@RequiredArgsConstructor
public class ThreadPoolAlarmChecker {

    private final NotifierDispatcher notifierDispatcher;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            1,
            ThreadFactoryBuilder.builder()
                    .namePrefix("scheduler_thread-pool_alarm_checker")
                    .build()
    );
    private final Map<String, Long> lastRejectCountMap = new ConcurrentHashMap<>();

    /**
     * Setup scheduler alarm checking task
     */
    public void start() {
        // delay 0 seconds, every 5 seconds
        scheduler.scheduleWithFixedDelay(this::checkAlarm, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Stop
     */
    public void stop() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Alarm checking core logic
     */
    private void checkAlarm() {
        Collection<ThreadPoolExecutorWrapper> wrappers = CloudThreadRegistry.getAllWrappers();
        for (ThreadPoolExecutorWrapper wrapper : wrappers) {
            if (wrapper.getExecutorProperties().getAlarm().getEnable()) {
                checkQueueUsage(wrapper);
                checkActiveRate(wrapper);
                checkRejectCount(wrapper);
            }
        }
    }

    /**
     * check thread pool queue utility
     */
    private void checkQueueUsage(ThreadPoolExecutorWrapper wrapper) {
        ThreadPoolExecutor executor = wrapper.getExecutor();
        ThreadPoolExecutorProperties properties = wrapper.getExecutorProperties();

        BlockingQueue<?> queue = executor.getQueue();
        int queueSize = queue.size();
        int capacity = queueSize + queue.remainingCapacity();

        if (capacity == 0) {
            return;
        }

        int usageRate = (int) Math.round((queueSize * 100.0) / capacity);
        int threshold = properties.getAlarm().getQueueThreshold();

        if (usageRate >= threshold) {
            sendAlarmMessage("Capacity", wrapper);
        }
    }

    /**
     * check thread pool activity rate = (active thread cnt / maximum thread cnt)
     */
    private void checkActiveRate(ThreadPoolExecutorWrapper wrapper) {
        ThreadPoolExecutor executor = wrapper.getExecutor();
        ThreadPoolExecutorProperties properties = wrapper.getExecutorProperties();

        int activeCount = executor.getActiveCount();
        int maximumPoolSize = executor.getMaximumPoolSize();

        if (maximumPoolSize == 0) {
            return;
        }

        int activeRate = (int) Math.round((activeCount * 100.0) / maximumPoolSize);
        int threshold = properties.getAlarm().getActiveThreadThreshold();

        if (activeRate >= threshold) {
            sendAlarmMessage("Activity", wrapper);
        }
    }

    /**
     * check thread pool reject policy execute times
     */
    private void checkRejectCount(ThreadPoolExecutorWrapper wrapper) {
        ThreadPoolExecutor executor = wrapper.getExecutor();
        String threadPoolId = wrapper.getThreadPoolUID();

        if (!(executor instanceof CloudThreadExecutor)) {
            return;
        }

        CloudThreadExecutor oneThreadExecutor = (CloudThreadExecutor) executor;
        long currentRejectCount = oneThreadExecutor.getRejectCount().get();
        long lastRejectCount = lastRejectCountMap.getOrDefault(threadPoolId, 0L);

        if (currentRejectCount > lastRejectCount) {
            sendAlarmMessage("Reject", wrapper);
            lastRejectCountMap.put(threadPoolId, currentRejectCount);
        }
    }

    private void sendAlarmMessage(String alarmType, ThreadPoolExecutorWrapper holder) {
        ThreadPoolExecutorProperties properties = holder.getExecutorProperties();
        String threadPoolId = holder.getThreadPoolUID();

        ThreadPoolAlarmNotifyDTO alarm = ThreadPoolAlarmNotifyDTO.builder()
                .alarmType(alarmType)
                .threadPoolUID(threadPoolId)
                .intervalMinutes(properties.getNotify().getIntervalMinutes())
                .build();

        alarm.setSupplier(() -> {
            try {
                alarm.setIdentify(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                log.warn("Error in obtaining HostAddress", e);
            }

            ThreadPoolExecutor executor = holder.getExecutor();
            BlockingQueue<?> queue = executor.getQueue();

            int size = queue.size();
            int remaining = queue.remainingCapacity();
            long rejectCount = (executor instanceof CloudThreadExecutor)
                    ? ((CloudThreadExecutor) executor).getRejectCount().get()
                    : -1L;

            alarm.setCorePoolSize(executor.getCorePoolSize())
                    .setMaximumPoolSize(executor.getMaximumPoolSize())
                    .setActivePoolSize(executor.getActiveCount())
                    .setCurrentPoolSize(executor.getPoolSize())
                    .setCompletedTaskCount(executor.getCompletedTaskCount())
                    .setLargestPoolSize(executor.getLargestPoolSize())
                    .setWorkQueueName(queue.getClass().getSimpleName())
                    .setWorkQueueSize(size)
                    .setWorkQueueRemainingCapacity(remaining)
                    .setWorkQueueCapacity(size + remaining)
                    .setRejectedHandlerName(executor.getRejectedExecutionHandler().toString())
                    .setRejectCount(rejectCount)
                    .setCurrentTime(DateUtil.now())
                    .setApplicationName(ApplicationProperties.getApplicationName())
                    .setActiveProfile(ApplicationProperties.getActiveProfile())
                    .setSubscribers(properties.getNotify().getSubscribers());
            return alarm;
        });

        notifierDispatcher.sendAlarmMessage(alarm);
    }
}

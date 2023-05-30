package com.chinedu.backend.events.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class ActivityEventRepository {
    private static Logger logger = LoggerFactory.getLogger(ActivityEventRepository.class);

    private static volatile ActivityEventRepository instance;

    private static Object synchronizedObject = new Object();

    // Timer for updating activity event
    private static Timer eventUpdateTimer;

    // Map to store activity events
    private ConcurrentMap<String, ActivityEvent> activityEventMap;

    private ActivityEventRepository() {
        activityEventMap = new ConcurrentHashMap<>();
        eventUpdateTimer = new Timer();
        eventUpdateTimer.schedule(eventUpdateTask, 0, 1000); //update activityEventMap every 1 second
    }

    public static ActivityEventRepository getInstance() {
        ActivityEventRepository result = instance;

        if (result == null) {
            // used synchronized to avoid multiple threads creating new instance at the same time
            synchronized (synchronizedObject) {
                result = instance;
                if (result == null)
                    instance = result = new ActivityEventRepository();
            }
        }

        return result;
    }

    // TimerTask to remove events older than 12 hours and calculate new total
    TimerTask eventUpdateTask = new TimerTask() {
        public void run() {
            activityEventMap.values().parallelStream()
                    .forEach(ae -> { updateActivityEvent(ae);});
        }
    };

    public void addActivityEvent(String key, long value, Date eventDate) {
        ActivityEvent activityEvent = null;
        if (activityEventMap.containsKey(key)) {
            activityEvent = activityEventMap.get(key);
            activityEvent.setTotalValue(activityEvent.getTotalValue() + value);
        } else {
            activityEvent = new ActivityEvent();
            activityEvent.setEvent(key);
            activityEvent.setTotalValue(value);
        }

        ActivityEvent.Event event = new ActivityEvent.Event();
        event.setEventDate(eventDate);
        event.setValue(value);
        activityEvent.getEventList().add(event);
        activityEventMap.put(key, activityEvent);
    }

    public long getTotalActivityEvent(String key) {
        long totalValue = 0;
        if (activityEventMap.containsKey(key)) {
            ActivityEvent activityEvent = activityEventMap.get(key);
            totalValue = activityEvent.getTotalValue();
        }

        return totalValue;
    }

    public boolean hasActivityEvent(String key) {
        return activityEventMap.containsKey(key);
    }

    private void updateActivityEvent(ActivityEvent activityEvent) {
        // filter event less than 12 hours old
        synchronized (activityEvent.getEventList()) {
            List<ActivityEvent.Event> currentEvents = activityEvent.getEventList().stream()
                    .filter(e -> {
                        Instant twelveHoursEarlier = Instant.now().minus(12, ChronoUnit.HOURS);
                        return e.getEventDate().toInstant().isAfter(twelveHoursEarlier);
                    })
                    .collect(Collectors.toList());

            // calculate new total value
            long totalValue = currentEvents.stream()
                    .mapToLong(e -> e.getValue())
                    .sum();

            activityEvent.setTotalValue(totalValue);
            activityEvent.setEventList(currentEvents);
        }
    }


}

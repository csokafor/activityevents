package com.chinedu.backend.events;

import com.chinedu.backend.events.service.ActivityEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ActivityEventRepositoryTests {

    @InjectMocks
    ActivityEventRepository activityEventRepository;

    @DisplayName("Unit test addActivityEvent success")
    @Test
    public void addActivityEvent_success() throws Exception {
        String key = "new_role";

        assertDoesNotThrow(() -> activityEventRepository.addActivityEvent(key, 5, new Date()));

    }

    @DisplayName("Unit test hasActivityEvent success")
    @Test
    public void hasActivityEvent_success() throws Exception {
        String key = "update_role";
        activityEventRepository.addActivityEvent(key, 5, new Date());

        assertTrue(activityEventRepository.hasActivityEvent(key));
    }

    @DisplayName("Unit test getTotalActivityEvent success")
    @Test
    public void getTotalActivityEvent_success() throws Exception {
        String key = "add_role";
        activityEventRepository.addActivityEvent(key, 50, new Date());
        long total = activityEventRepository.getTotalActivityEvent(key);

        assertTrue(total == 50);
    }

    @DisplayName("Unit test addActivityEvent and getTotalActivityEvent multiple keys")
    @Test
    public void getTotalActivityEvent_multipleKeysSuccess() throws Exception {
        String key1 = "view_agent";
        String key2 = "update_agent";
        String key3 = "delete_agent";
        Date eventDate = new Date();
        activityEventRepository.addActivityEvent(key2, 8, eventDate);
        activityEventRepository.addActivityEvent(key1, 23, eventDate);
        activityEventRepository.addActivityEvent(key3, 92, eventDate);

        long total1 = activityEventRepository.getTotalActivityEvent(key1);
        long total2 = activityEventRepository.getTotalActivityEvent(key2);
        long total3 = activityEventRepository.getTotalActivityEvent(key3);

        assertTrue(total1 == 23);
        assertTrue(total2 == 8);
        assertTrue(total3 == 92);
    }

    @DisplayName("Unit test addActivityEvent and getTotalActivityEvent multiple valid keys and values")
    @Test
    public void getTotalActivityEvent_multipleKeysAndValuesSuccess() throws Exception {
        String key1 = "view_notebook";
        String key2 = "update_notebook";
        String key3 = "delete_notebook";
        Date eventDate = new Date();
        activityEventRepository.addActivityEvent(key2, 8, eventDate);
        activityEventRepository.addActivityEvent(key1, 23, eventDate);
        activityEventRepository.addActivityEvent(key3, 92, eventDate);
        activityEventRepository.addActivityEvent(key2, 28, eventDate);
        activityEventRepository.addActivityEvent(key1, 18, eventDate);
        activityEventRepository.addActivityEvent(key3, 5, eventDate);

        long total1 = activityEventRepository.getTotalActivityEvent(key1);
        long total2 = activityEventRepository.getTotalActivityEvent(key2);
        long total3 = activityEventRepository.getTotalActivityEvent(key3);

        assertTrue(total1 == 41);
        assertTrue(total2 == 36);
        assertTrue(total3 == 97);
    }

    @DisplayName("Unit test getTotalActivityEvent remove old events")
    @Test
    public void getTotalActivityEvent_removeOldEventsSuccess() throws Exception {
        String key1 = "view_notebook";
        String key2 = "update_notebook";
        String key3 = "delete_notebook";
        Date eventDate = new Date();
        activityEventRepository.addActivityEvent(key2, 8, eventDate);
        activityEventRepository.addActivityEvent(key1, 23, eventDate);
        activityEventRepository.addActivityEvent(key3, 92, eventDate);


        Calendar thirteenHoursAgo = Calendar.getInstance();
        thirteenHoursAgo.setTime(new Date());
        thirteenHoursAgo.add(Calendar.HOUR_OF_DAY, -13);
        activityEventRepository.addActivityEvent(key2, 28, thirteenHoursAgo.getTime());

        Calendar oneDayAgo = Calendar.getInstance();
        oneDayAgo.setTime(new Date());
        oneDayAgo.add(Calendar.DATE, -1);
        activityEventRepository.addActivityEvent(key1, 18, oneDayAgo.getTime());

        Calendar sixHoursAgo = Calendar.getInstance();
        sixHoursAgo.setTime(new Date());
        sixHoursAgo.add(Calendar.HOUR_OF_DAY, -6);
        activityEventRepository.addActivityEvent(key3, 5, sixHoursAgo.getTime());

        // wait for eventUpdateTask to run, it will remove old events and calculate new total value
        Thread.sleep(3000);

        long total1 = activityEventRepository.getTotalActivityEvent(key1);
        long total2 = activityEventRepository.getTotalActivityEvent(key2);
        long total3 = activityEventRepository.getTotalActivityEvent(key3);

        assertTrue(total1 == 23);
        assertTrue(total2 == 8);
        assertTrue(total3 == 97);
    }

}

package com.chinedu.backend.events;

import com.chinedu.backend.events.rest.ApiData;
import com.chinedu.backend.events.service.ActivityEventRepository;
import com.chinedu.backend.events.service.ActivityEventService;
import com.chinedu.backend.events.service.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityEventServiceTests {

    @Mock
    ActivityEventRepository activityEventRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ActivityEventService activityEventService;

    @DisplayName("Unit test addActivityEvent for value less than zero")
    @Test
    public void addActivityEvent_invalidValue() throws Exception {
        String key = "update_user";
        when(messageSource.getMessage("error.invalid.value", null, Locale.getDefault()))
                .thenReturn("Invalid request");

        Exception exception = assertThrows(ApiException.class, () -> {
            activityEventService.addActivityEvent(key, new ApiData(-5));
        });

        assertTrue(exception instanceof ApiException);
        assertTrue(exception.getMessage().equals("Invalid request"));

    }

    @DisplayName("Unit test addActivityEvent valid value")
    @Test
    public void addActivityEvent_success() throws Exception {
        String key = "update_user";

        assertDoesNotThrow(() -> activityEventService.addActivityEvent(key, new ApiData(5)));

    }

    @DisplayName("Unit test getTotalActivityEvent for invalid key")
    @Test
    public void getTotalActivityEvent_invalidKey() throws Exception {
        String key = "add_user";
        when(messageSource.getMessage("error.key.notfound", null, Locale.getDefault()))
                .thenReturn("Key not found");

        Exception exception = assertThrows(ApiException.class, () -> {
            activityEventService.getTotalActivityEvent(key);
        });

        assertTrue(exception instanceof ApiException);
        assertTrue(exception.getMessage().equals("Key not found"));

    }

    @DisplayName("Unit test getTotalActivityEvent with valid key")
    @Test
    public void getTotalActivityEvent_success() throws Exception {
        String key = "view_profile";
        activityEventService.addActivityEvent(key, new ApiData(15));
        ApiData apiData = activityEventService.getTotalActivityEvent(key);

        assertNotNull(apiData);
        assertTrue(apiData.getValue() == 15);
    }

    @DisplayName("Unit test addActivityEvent and getTotalActivityEvent multiple valid keys")
    @Test
    public void getTotalActivityEvent_multipleKeysSuccess() throws Exception {
        String key1 = "view_task";
        String key2 = "update_task";
        String key3 = "delete_task";
        activityEventService.addActivityEvent(key2, new ApiData(5));
        activityEventService.addActivityEvent(key1, new ApiData(150));
        activityEventService.addActivityEvent(key3, new ApiData(24));

        ApiData apiData1 = activityEventService.getTotalActivityEvent(key1);
        ApiData apiData2 = activityEventService.getTotalActivityEvent(key2);
        ApiData apiData3 = activityEventService.getTotalActivityEvent(key3);

        assertNotNull(apiData1);
        assertNotNull(apiData2);
        assertNotNull(apiData3);
        assertTrue(apiData1.getValue() == 150);
        assertTrue(apiData2.getValue() == 5);
        assertTrue(apiData3.getValue() == 24);
    }

    @DisplayName("Unit test addActivityEvent and getTotalActivityEvent multiple valid keys and values")
    @Test
    public void getTotalActivityEvent_multipleKeysAndValuesSuccess() throws Exception {
        String key1 = "view_note";
        String key2 = "update_note";
        String key3 = "delete_note";
        activityEventService.addActivityEvent(key2, new ApiData(5));
        activityEventService.addActivityEvent(key1, new ApiData(150));
        activityEventService.addActivityEvent(key3, new ApiData(24));
        activityEventService.addActivityEvent(key2, new ApiData(6));
        activityEventService.addActivityEvent(key1, new ApiData(7));
        activityEventService.addActivityEvent(key3, new ApiData(36));

        ApiData apiData1 = activityEventService.getTotalActivityEvent(key1);
        ApiData apiData2 = activityEventService.getTotalActivityEvent(key2);
        ApiData apiData3 = activityEventService.getTotalActivityEvent(key3);

        assertNotNull(apiData1);
        assertNotNull(apiData2);
        assertNotNull(apiData3);
        assertTrue(apiData1.getValue() == 157);
        assertTrue(apiData2.getValue() == 11);
        assertTrue(apiData3.getValue() == 60);
    }

}

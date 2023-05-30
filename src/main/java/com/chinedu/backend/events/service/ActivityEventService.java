package com.chinedu.backend.events.service;

import com.chinedu.backend.events.rest.ApiData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Locale;

@Service
public class ActivityEventService {
    private static Logger logger = LoggerFactory.getLogger(ActivityEventService.class);

    private ActivityEventRepository activityEventRepository;

    private MessageSource messageSource;

    public ActivityEventService(MessageSource messageSource) {
        this.messageSource = messageSource;
        activityEventRepository = ActivityEventRepository.getInstance();
    }

    public void addActivityEvent(String event, ApiData apiData) throws ApiException {
        double value = apiData.getValue();

        //return error if value less than zero
        if (value <= 0) {
            String errorMessage = messageSource.getMessage("error.invalid.value", null, Locale.getDefault());
            throw new ApiException(errorMessage);
        }

        activityEventRepository.addActivityEvent(event, Math.round(value), new Date());
    }

    public ApiData getTotalActivityEvent(String event) throws ApiException {
        ApiData apiData = new ApiData();
        boolean eventExists = activityEventRepository.hasActivityEvent(event);

        // return error if key does not exists
        if (!eventExists) {
            String errorMessage = messageSource.getMessage("error.key.notfound", null, Locale.getDefault());
            throw new ApiException(errorMessage);
        }

        long totalEvents = activityEventRepository.getTotalActivityEvent(event);
        apiData.setValue(totalEvents);

        return apiData;
    }
}

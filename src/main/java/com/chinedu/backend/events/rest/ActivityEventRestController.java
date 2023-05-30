package com.chinedu.backend.events.rest;

import com.chinedu.backend.events.service.ActivityEventService;
import com.chinedu.backend.events.service.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivityEventRestController {
    private static Logger logger = LoggerFactory.getLogger(ActivityEventRestController.class);

    private ActivityEventService activityEventService;

    public ActivityEventRestController(ActivityEventService activityEventService) {
        this.activityEventService = activityEventService;
    }

    @PostMapping(value = "/activity/{key}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> addActivityEvent(@RequestBody ApiData data, @PathVariable String key) {
        try {
            activityEventService.addActivityEvent(key, data);
            return ResponseEntity.ok().body(new EmptyJson());

        } catch (ApiException ape) {
            logger.error(ape.getMessage(), ape);
            return ResponseEntity.badRequest().body(new ApiError(ape.getMessage()));
        } catch (Exception e)  {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/activity/{key}/total", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getTotalActivityEvent(@PathVariable String key) {
        try {
            ApiData response = activityEventService.getTotalActivityEvent(key);
            return ResponseEntity.ok().body(response);

        } catch (ApiException ape) {
            logger.error(ape.getMessage(), ape);
            return ResponseEntity.badRequest().body(new ApiError(ape.getMessage()));
        } catch (Exception e)  {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

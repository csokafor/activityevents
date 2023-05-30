package com.chinedu.backend.events.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ActivityEvent implements Serializable {
    private static final long serialVersionUID = 2624726180748515528L;

    private String event;

    private long totalValue;

    private List<Event> eventList = new ArrayList<>();

    @Getter
    @Setter
    public static class Event {
        private long value;

        private Date eventDate;
    }

}

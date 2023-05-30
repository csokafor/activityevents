package com.chinedu.backend.events;

import com.chinedu.backend.events.rest.ActivityEventRestController;
import com.chinedu.backend.events.rest.ApiData;
import com.chinedu.backend.events.service.ActivityEventService;
import com.chinedu.backend.events.service.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityEventRestController.class)
public class ActivityEventRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MessageSource messageSource;

    @MockBean
    ActivityEventService activityEventService;

    @DisplayName("Unit test for get event total invalid key response")
    @Test
    public void getTotalActivityEvent_invalidKey() throws Exception {
        String key = "new_user";
        when(activityEventService.getTotalActivityEvent(key))
                .thenThrow(new ApiException("Key not found"));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/activity/{key}/total", key)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @DisplayName("Unit test for get event success response")
    @Test
    public void getTotalActivityEvent_success() throws Exception {
        String key = "new_user";
        when(activityEventService.getTotalActivityEvent(key))
                .thenReturn(new ApiData(10));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get("/activity/{key}/total", key)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(10));
    }

    @DisplayName("Unit test for post activity event invalid value response")
    @Test
    public void postActivityEvent_invalidValue() throws Exception {
        String key = "new_user";
        ApiData apiData = new ApiData(-4.0);
        doThrow(new ApiException("Invalid value")).when(activityEventService).addActivityEvent(
                eq(key), any(ApiData.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/activity/{key}", key)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(apiData));

        mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @DisplayName("Unit test for post activity event success")
    @Test
    public void postActivityEvent_success() throws Exception {
        String key = "new_user";
        ApiData apiData = new ApiData(4);

        doNothing().when(activityEventService).addActivityEvent(
                eq(key), any(ApiData.class));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/activity/{key}", key)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(apiData));

        mockMvc.perform(mockRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

}

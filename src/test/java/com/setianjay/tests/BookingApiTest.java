package com.setianjay.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.setianjay.base.BaseTest;
import com.setianjay.enums.Method;
import com.setianjay.models.request.booking.BookingAuthRequest;
import com.setianjay.models.response.booking.*;
import com.setianjay.utils.LoggerUtils;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class BookingApiTest extends BaseTest {
    private Integer id;

    private void setId(Integer id) {
        this.id = id;
    }

    private Integer getId() {
        return this.id;
    }

    @Override
    protected Class<?> getClazz() {
        return this.getClass();
    }

    @Override
    protected void setBaseURI() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Override
    protected void customSetupBeforeClass() throws JsonProcessingException {
        setBaseURI();
        testCreateToken();
    }

    @Override
    protected void customTearDownAfterClass() {
        // clear id
        id = null;
    }

    private void testCreateToken() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        BookingAuthRequest bookingAuthRequestBody = new BookingAuthRequest("admin", "password123");
        Map<String, String> headers = new HashMap<>() {{
            put("Content-Type", "application/json");
            put("Accept", "application/json");
            put("User-Agent", "API-Test-Automation/1.0");
        }};
        executeRequest(Method.POST, "/auth", bookingAuthRequestBody, headers, null, null);
        BookingAuthResponse response = deserializeResponse(getResponse().asString(), BookingAuthResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertNotNull(response.getToken());
        setToken(response.getToken());
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }

    @Test(testName = "testCreateBooking", priority = 1)
    public void testCreateBooking() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        BookingDatesResponse bookingDatesBodyRequest = new BookingDatesResponse("2025-09-16", "2025-09-17");
        BookingResponse bookingBodyRequest = new BookingResponse("Vinsmoke", "Sanji", 100000, true, bookingDatesBodyRequest, "Professional Chef");
        executeRequest(Method.POST, "/booking", bookingBodyRequest, null, null, null);
        BookingCreateResponse bookingCreateResponse = deserializeResponse(getResponse().asString(), BookingCreateResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertNotNull(bookingCreateResponse);
        assertEquals(bookingCreateResponse.getBooking(), bookingBodyRequest);
        setId(bookingCreateResponse.getBookingid());
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }

    @Test(testName = "testUpdateBooking", priority = 2)
    public void testUpdateBooking() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        BookingDatesResponse bookingDatesBodyRequest = new BookingDatesResponse("2025-09-17", "2025-09-18");
        BookingResponse bookingBodyRequest = new BookingResponse("Tony", "Chopper", 200000, false, bookingDatesBodyRequest, "Professional Doctor");
        Map<String, String> customHeader = new HashMap<>() {{
            put("Cookie", "token=" + getTokenAuth());
        }};
        Map<String, Object> requestPaths = new HashMap<>() {{
            put("id", getId());
        }};
        executeRequest(Method.PUT, "/booking/{id}", bookingBodyRequest, customHeader, null, requestPaths);
        BookingResponse bookingUpdateResponse = deserializeResponse(getResponse().asString(), BookingResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertNotNull(bookingUpdateResponse);
        assertEquals(bookingUpdateResponse, bookingBodyRequest);
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }

    @Test(testName = "testPartialUpdateBooking", priority = 3)
    public void testPartialUpdateBooking() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        BookingResponse bookingBodyRequest =
                BookingResponse.builder().firstname("Nico").lastname("Robin").totalprice(400000).build();
        Map<String, String> customHeader = new HashMap<>() {{
            put("Cookie", "token=" + getTokenAuth());
        }};
        Map<String, Object> requestPaths = new HashMap<>() {{
            put("id", getId());
        }};
        executeRequest(Method.PATCH, "/booking/{id}", bookingBodyRequest, customHeader, null, requestPaths);
        BookingResponse bookingPartialUpdateResponse = deserializeResponse(getResponse().asString(), BookingResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertNotNull(bookingPartialUpdateResponse);
        assertEquals(bookingPartialUpdateResponse.getFirstname(), bookingBodyRequest.getFirstname());
        assertEquals(bookingPartialUpdateResponse.getLastname(), bookingBodyRequest.getLastname());
        assertEquals(bookingPartialUpdateResponse.getTotalprice(), bookingBodyRequest.getTotalprice());
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }

    @Test(testName = "testGetBooking", priority = 4)
    public void testGetBooking() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        Map<String, Object> requestPaths = new HashMap<>() {{
            put("id", getId());
        }};
        executeRequest(Method.GET, "/booking/{id}", null, null, null, requestPaths);
        BookingResponse bookingResponse = deserializeResponse(getResponse().asString(), BookingResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertNotNull(bookingResponse);
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }



    @Test(testName = "testDeleteBooking", priority = 5)
    public void testDeleteBooking() {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        Map<String, String> customHeader = new HashMap<>() {{
            put("Cookie", "token=" + getTokenAuth());
        }};
        Map<String, Object> requestPaths = new HashMap<>() {{
            put("id", getId());
        }};
        executeRequest(Method.DELETE, "/booking/{id}", null,customHeader, null, requestPaths);
        String bookingDeleteResponse = getResponse().asString();

        assertEquals(getResponse().statusCode(), 201);
        assertNotNull(bookingDeleteResponse);
        assertEquals(bookingDeleteResponse, "Created");
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());

    }


    @Test(testName = "testGetBookingId", priority = 6)
    public void testGetBookingId() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        executeRequest(Method.GET, "/booking", null, null, null, null);
        List<BookingIdResponse> listBookingIdResponse = deserializeResponseToList(getResponse().asString(), BookingIdResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertFalse(listBookingIdResponse.isEmpty());
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }
}

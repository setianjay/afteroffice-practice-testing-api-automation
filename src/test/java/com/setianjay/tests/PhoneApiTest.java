package com.setianjay.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.setianjay.base.BaseTest;
import com.setianjay.enums.Method;
import com.setianjay.models.response.phone.PhoneResponse;
import com.setianjay.models.response.phone.PhoneSpecificationResponse;
import com.setianjay.utils.LoggerUtils;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class PhoneApiTest extends BaseTest {

    /*
     * RestAssured punya 3 method utama, yaitu given, when, then
     * given: untuk menyimpan request  (header, body, parameter)
     * when: untuk mengirim request (get, post, delete, put, patch)
     * then: untuk verifikasi response (status code, body, header)
     * */

    @Override
    protected Class<?> getClazz() {
        return this.getClass();
    }

    @Override
    protected void setBaseURI() {
        RestAssured.baseURI = "https://api.restful-api.dev";
    }

    @Override
    protected void customSetupBeforeClass() {
        setBaseURI();
    }

    @Test(testName = "testGetAllObject")
    public void testGetAllObjects() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getTestName());
        executeRequest(Method.GET, "/objects", null, null,null, null);
        List<PhoneResponse> jsonToObj = deserializeResponseToList(getResponse().asString(), PhoneResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertEquals(jsonToObj.size(), 13);
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }

    @Test(testName = "testGetObjectById")
    public void testGetObjectById() throws JsonProcessingException {
        LoggerUtils.logTestStart(logger, getClazzName(), getClazzName());
        PhoneResponse expectedPhoneResponse = new PhoneResponse(1, "Google Pixel 6 Pro", new PhoneSpecificationResponse("Cloudy White", "128 GB"));
        executeRequest(Method.GET,"/objects/{id}", null, null, null, new HashMap<>(){{
            put("id", 1);
        }});
        PhoneResponse actualPhoneResponse = deserializeResponse(getResponse().asString(), PhoneResponse.class);

        assertEquals(getResponse().statusCode(), 200);
        assertEquals(actualPhoneResponse, expectedPhoneResponse);
        LoggerUtils.logTestEnd(logger, getClazzName(), getTestName());
    }
}

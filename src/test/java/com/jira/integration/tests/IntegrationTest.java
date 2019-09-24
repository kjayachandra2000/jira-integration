package com.jira.integration.tests;

import com.google.gson.Gson;
import com.jira.integration.model.LoginModel;
import com.jira.integration.model.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

public class IntegrationTest {

    private static final String LOGIN_URL = "https://jiraagile.xyz.com/rest/auth/1/session";
    private static final String ISSUE_URL = "https://jiraagile.xyz.com/rest/api/2/issue/";

    @Test
    public void login() {
        LoginModel loginModel = new LoginModel();
        loginModel
                .withUsername("username")
                .withPassword("password");

        String requestBody = new Gson().toJson(loginModel);

        System.out.println(requestBody);
        String responseBody = login(requestBody, LOGIN_URL);
        System.out.println(responseBody);

        LoginResponse loginResponse = new Gson().fromJson(responseBody, LoginResponse.class);

        responseBody = getIssueDetails("BUG-12345",loginResponse.getSession().getValue());

        System.out.println(responseBody);
    }

    private String getIssueDetails(String issueId, String cookie) {
        Header header = new Header("Cookie","JSESSIONID=" +cookie);

        Response response = null;
        response = RestAssured
                .given()
                .header(header)
                .when().
                        get(ISSUE_URL+issueId);

        System.out.println(response.getBody().asString());

        Assert.assertEquals(response.statusCode(), 200);

        return response.getBody().asString();
    }

    private String login(String requestBody, String endpoint) {
        Response response = null;
        response = RestAssured
                .given().contentType(ContentType.JSON)
                .body(requestBody)
                .when().
                        post(LOGIN_URL);

        Assert.assertEquals(response.statusCode(), 200);

        return response.getBody().asString();
    }
}

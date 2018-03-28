package com.only5.automation_framework.implementations;

import com.only5.automation_framework.rest.modules.Authentication;
import com.only5.automation_framework.rest.modules.BasicMediaTypes;
import com.only5.automation_framework.rest.modules.CustomObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Headers;
import io.restassured.mapper.ObjectMapper;
import io.restassured.specification.RequestSpecification;

public class RestFactory {

    final RequestSpecification requestSpec;

    public RestFactory(BasicMediaTypes mediaType, Authentication auth, Headers headers) {
        RequestSpecification requestSpec = RestAssured.given()
                .config(createConfiguration(new CustomObjectMapper()))
                .relaxedHTTPSValidation()
                .header("Accept", mediaType.getAccept())
                .header("Content-Type", mediaType.getContentType());
        if (headers == null) {
        } else {
            requestSpec.headers(headers);
        }
        this.requestSpec = auth.createRequest(requestSpec);
    }

    public RequestSpecification getRequestSpec() {
        return requestSpec;
    }

    private RestAssuredConfig createConfiguration(ObjectMapper mapper) {
        ObjectMapperConfig omc = ObjectMapperConfig.objectMapperConfig().defaultObjectMapper(mapper);
        RestAssuredConfig restConfig = RestAssuredConfig.newConfig()
                .objectMapperConfig(omc)
                .encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8"));
        return restConfig;
    }
}

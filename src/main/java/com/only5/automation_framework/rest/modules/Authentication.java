package com.only5.automation_framework.rest.modules;

import io.restassured.specification.RequestSpecification;

public abstract class Authentication {

    private final AuthContext context;

    public Authentication(AuthContext context) {
        this.context = context;
    }

    public abstract RequestSpecification createRequest(RequestSpecification requestSpec);
}

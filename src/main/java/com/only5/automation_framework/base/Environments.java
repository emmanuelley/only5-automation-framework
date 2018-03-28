package com.only5.automation_framework.base;

public enum Environments {
    DEMO("https://only5.com"),
    PROD("https://only5.com");

    private final String url;

    private Environments(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

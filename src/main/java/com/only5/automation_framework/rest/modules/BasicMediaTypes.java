package com.only5.automation_framework.rest.modules;

import javax.ws.rs.core.MediaType;

public enum BasicMediaTypes {
    XML(MediaType.APPLICATION_XML,MediaType.APPLICATION_XML,"xml"),
    JSON(MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,"json");
    
    private final String accept;
    private final String contentType;
    private final String serialType;

    private BasicMediaTypes(String accept, String contentType, String serialType) {
        this.accept = accept;
        this.contentType = contentType;
        this.serialType = serialType;
    }

    public String getAccept() {
        return accept;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSerialType() {
        return serialType;
    }
    
    
}

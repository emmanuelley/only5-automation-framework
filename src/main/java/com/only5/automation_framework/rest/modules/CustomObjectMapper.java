package com.only5.automation_framework.rest.modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class CustomObjectMapper implements ObjectMapper {

    protected final com.fasterxml.jackson.databind.ObjectMapper jsonSerializer;
    protected final com.fasterxml.jackson.databind.ObjectMapper jsonDeserializer;

    public CustomObjectMapper() {
        jsonSerializer = new com.fasterxml.jackson.databind.ObjectMapper();
        jsonDeserializer = new com.fasterxml.jackson.databind.ObjectMapper();
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext omdc) {
        String data = omdc.getDataToDeserialize().asString();
        if (omdc.getType().equals(String.class)) {
            return data;
        }

        if (omdc.getContentType().contains("json")) {
            try {
                return jsonDeserializer.readValue(data, omdc.getType());
            } catch (IOException ex) {
                String msg = "Error reading as JSON: " + ex.getMessage() + "\nCaused by: " + data;
                throw new RuntimeException(msg, ex);
            }
        } else if (omdc.getContentType().contains("xml")) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(omdc.getType());
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes(Charset.forName(omdc.getCharset())));
                JAXBElement<?> element = jaxbUnmarshaller.unmarshal(new StreamSource(bais), omdc.getType());
                bais.close();
                return element.getValue();
            } catch (JAXBException ex) {
                String msg = "Error reading as XML: " + ex.getMessage() + "\nCaused by: " + data;
                throw new RuntimeException(msg, ex);
            } catch (IOException ex) {
                throw new RuntimeException("Error closing byte array stream.", ex);
            }
        } else {
            throw new RuntimeException("Don't know how to parse content type \"" + omdc.getContentType() + "\". Error reading " + data);
        }
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext omsc) {
        if (omsc.getContentType().contains("json")) {
            try {
                return jsonSerializer.writeValueAsString(omsc.getObjectToSerialize());
            } catch (JsonProcessingException ex) {
                throw new RuntimeException("Error writing " + omsc.getObjectToSerialize().getClass().getCanonicalName() + " as JSON. Reason: " + ex.getMessage(), ex);
            }
        } else if (omsc.getContentType().contains("xml")) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(omsc.getObjectToSerialize().getClass());
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                StringWriter sw = new StringWriter();
                if (jaxbContext.createJAXBIntrospector().isElement(omsc.getObjectToSerialize())) {
                    jaxbMarshaller.marshal(omsc.getObjectToSerialize(), sw);
                } else {
                    throw new RuntimeException("Need access to the dev apis in order to find the root element");
                }
                return sw.toString();
            } catch (JAXBException ex) {
                throw new RuntimeException("Error writing " + omsc.getObjectToSerialize().getClass().getCanonicalName() + " as XML. Reason: " + ex.getMessage(), ex);
            }
        } else {
            throw new RuntimeException("Don't know how to parse content type \"" + omsc.getContentType() + "\". Error writing " + omsc.getObjectToSerialize().getClass().getCanonicalName());
        }
    }
}

package com.only5.automation_framework.json.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.only5.automation_framework.base.Res;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class JsonParser {

    private final String jsonFile;
    Map<String, Object> obj;

    public JsonParser(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public JSONObject convertFileToJson() throws IOException {
        InputStream is = Res.getResource(jsonFile).openStream();
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        return jsonObjectFromString(jsonTxt);
    }

    private JSONObject jsonObjectFromString(String jsonTxt) {
        JSONObject json = new JSONObject(jsonTxt);
        return json;
    }

    public <T> T getJavaObjectFromJson(Class<T> clazz) throws IOException {
        JSONObject jsonObj = convertFileToJson();
        JsonToJavaConverters converter = new JsonToJavaConverters();
        JsonNode node = converter.convertJsonFormat(jsonObj);
        ObjectMapper mapper = new ObjectMapper() ;
        return (T) mapper.readValue(new TreeTraversingParser(node), clazz);
    }

    public <T> T updatePojoBasedOnAttributes(Class<T> clazz, T t, Map<String, String> attribs) {
        T a = getUpdatedPojo(clazz, t, attribs);
        return a;
    }

    private <T> T getUpdatedPojo(Class<T> clazz, T t, Map<String, String> attribs) {
        obj = convertPojoToMap(t);
        for (Map.Entry<String, String> entry : attribs.entrySet()) {
            updateValue(obj, entry);
        }
        return (T) convertMapBackToPojo(obj, clazz);
    }

    private void updateValue(Map<String, Object> obj, Map.Entry<String, String> entry) {
        for (Map.Entry<String, Object> objEntry : obj.entrySet()) {
            if (objEntry.getValue() == null) {
            } else {
                if (isMethodReturnPrimitiveDataType(objEntry.getValue().getClass())) {
                    if (objEntry.getKey().equalsIgnoreCase(entry.getKey())) {
                        objEntry.setValue(entry.getValue());
                    }
                } else if (List.class.isAssignableFrom(objEntry.getValue().getClass())) {
                    List<Object> listObjs = (List<Object>) objEntry.getValue();
                    for (Object listObj : listObjs) {
                        updateValue((Map<String, Object>) listObj, entry);
                    }
                } else {
                    updateValue((Map<String, Object>) objEntry.getValue(), entry);
                }
            }
        }
    }

    private <T> Map<String, Object> convertPojoToMap(T t) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapObj = mapper.convertValue(t, Map.class);
        return mapObj;
    }

    private boolean isMethodReturnPrimitiveDataType(Class<?> returnType) {
        return String.class.isAssignableFrom(returnType) || returnType.isPrimitive()
                || Number.class.isAssignableFrom(returnType) || Boolean.class.isAssignableFrom(returnType);
    }

    private <T> T convertMapBackToPojo(Map<String, Object> obj, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T t = (T) mapper.convertValue(obj, clazz);
        return t;
    }

}

package cmpe275.lab2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Util {
    public static <T> String toJsonString(T obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    /*public static <T> String toXMLString(T obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new XmlMapper();
        return objectMapper.writeValueAsString(obj);
    }*/
}
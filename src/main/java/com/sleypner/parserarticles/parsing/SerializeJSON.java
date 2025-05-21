package com.sleypner.parserarticles.parsing;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SerializeJSON <T> implements Serialize<T> {
    Logger logger = LoggerFactory.getLogger(SerializeJSON.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    public SerializeJSON() {
    }
    public String serialize(List<T> list){
        try {
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);

        } catch (JsonProcessingException e) {
            logger.atError()
                    .setMessage("Serialization error")
                    .addKeyValue("exception_class",e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }
    }
    public String serializeInFile(List<T> list,File file){
        try {
            objectMapper.writeValue(file, list);
            return file.getPath();
        } catch (IOException e) {
            logger.atError()
                    .setMessage("Serialization error")
                    .addKeyValue("exception_class",e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }

    }
    public List<T> deserialize(String filePath, Class<T> useClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().
                    constructCollectionType(List.class, useClass);
            var res = objectMapper.readValue(new File(filePath),type);
            return objectMapper.readValue(new File(filePath),type);
        } catch (IOException e) {
            logger.atError()
                    .setMessage("Deserialization error")
                    .addKeyValue("exception_class",e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }
    }
}

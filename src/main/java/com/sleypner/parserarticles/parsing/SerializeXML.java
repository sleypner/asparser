package com.sleypner.parserarticles.parsing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class SerializeXML <T> implements Serialize<T> {
    Logger logger = LoggerFactory.getLogger(SerializeXML.class);
    private final XmlMapper xmlMapper = new XmlMapper();

    public SerializeXML() {
    }

    public String getFilePath() {
        return filePath;
    }
    public String serialize(List<T> listNews) {
        String serializeString = null;
        try {
            serializeString = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(listNews);
            return serializeString;

            
        }catch (JsonProcessingException e){
            logger.atError()
//                    .setMessage(message)
                    .addKeyValue("exception_class",e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }
    }

    @Override
    public List<T> deserialize(String filePath, Class<T> useClass) {
        return null;
    }

    @Override
    public String serializeInFile(List<T> list, File file) {
        return null;
    }
}

package com.sleypner.parserarticles.parsing;

import java.io.File;
import java.util.List;

public class SerializeHTML<T> implements Serialize<T> {

    public SerializeHTML() {
    }

    public String getFilePath() {
        return filePath;
    }

    public String serialize(List<T> listArticle){
        StringBuilder finalString = new StringBuilder();
        for (var i = 0; i < (listArticle).size(); i++){
            var element = listArticle.get(i);
            finalString
                    .append("<div>")
                    .append(element)
                    .append("</div>\n");
        }
        return finalString.toString();
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

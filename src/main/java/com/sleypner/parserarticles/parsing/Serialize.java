package com.sleypner.parserarticles.parsing;

import java.io.File;
import java.util.List;

public interface Serialize<T> {
    String filePath = null;

    String serialize(List<T> list);
    String serializeInFile(List<T> list, File file);
    List<T> deserialize(String filePath, Class<T> useClass);
}

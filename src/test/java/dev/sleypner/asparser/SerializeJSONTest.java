package dev.sleypner.asparser;


import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.util.serialize.SerializeJSON;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SerializeJSONTest {

    @Disabled
    @Test
    public void testSerializeJSON(){

//        Arrange
//        List<Article> list = new ArrayList<>(List.of(
//                new Article("a","s","d","f",new Date()),
//                new Article("a1","s1","d1","f1",new Date()),
//                new Article("a2","s2","d2","f2",new Date())
//        ));

//        Act
//        SerializeJSON<List<Article>> serializer = new SerializeJSON<>();
//        String json = serializer.serialize(list);

//        Assert
//        String expected = """
//            [ {
//              "link" : "a",
//              "title" : "s",
//              "subtitle" : "d",
//              "description" : "f",
//              "createOn" : "g"
//            }, {
//              "link" : "a1",
//              "title" : "s1",
//              "subtitle" : "d1",
//              "description" : "f1",
//              "createOn" : "g1"
//            }, {
//              "link" : "a2",
//              "title" : "s2",
//              "subtitle" : "d2",
//              "description" : "f2",
//              "createOn" : "g2"
//            } ]""".replaceAll("\n" ,"\r\n");
//        assertEquals( expected,json);
    }
    @Disabled
    @Test
    public void wrongDtaSerializationJSON(){
//        Arrange
//        Act
        SerializeJSON<List<Article>> serializer = new SerializeJSON<>();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            serializer.serialize(null);
        });

        String expectedMessage = "fake exception";
        String actualMessage = exception.getMessage();
//        Assert
        assertNotNull(exception);
        assertNotNull(exception);
        assertEquals(actualMessage,expectedMessage);
    }
}

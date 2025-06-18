package dev.sleypner.asparser.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.sleypner.asparser.domain.model.Role;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RoleSetDeserializer extends JsonDeserializer<Set<Role>> {
    @Override
    public Set<Role> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        Set<Role> roles = new HashSet<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.isArray()) {
            for (JsonNode roleNode : node) {
//                Role role =
            }
        }

        return Set.of();
    }
}

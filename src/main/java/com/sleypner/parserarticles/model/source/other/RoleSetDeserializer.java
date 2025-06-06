package com.sleypner.parserarticles.model.source.other;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.sleypner.parserarticles.model.source.entityes.Roles;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RoleSetDeserializer extends JsonDeserializer<Set<Roles>> {
    @Override
    public Set<Roles> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

        Set<Roles> roles = new HashSet<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.isArray()) {
            for (JsonNode roleNode : node) {
//                Roles role =
            }
        }

        return Set.of();
    }
}

package io.github.geniot.aura.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.geniot.aura.model.FlightStatus;

import java.io.IOException;

public class FlightStatusDeserializer extends StdDeserializer<FlightStatus> {
    public FlightStatusDeserializer() {
        super(FlightStatus.class);
    }

    @Override
    public FlightStatus deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String statusStr = node.get("status").asText();
        if (statusStr.equals("c")) {
            return FlightStatus.CANCELLED;
        }
        return null;
    }
}

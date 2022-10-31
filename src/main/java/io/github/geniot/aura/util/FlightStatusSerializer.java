package io.github.geniot.aura.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.geniot.aura.model.FlightStatus;

import java.io.IOException;

public class FlightStatusSerializer extends StdSerializer<FlightStatus> {
    public FlightStatusSerializer() {
        super(FlightStatus.class);
    }

    @Override
    public void serialize(FlightStatus flightStatus, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (flightStatus.equals(FlightStatus.CANCELLED)) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("status");
            jsonGenerator.writeString("c");
            jsonGenerator.writeEndObject();
        }
    }
}

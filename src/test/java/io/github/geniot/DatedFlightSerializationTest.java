package io.github.geniot;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.FlightStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.github.geniot.aura.util.Utils.OBJECT_MAPPER;
import static io.github.geniot.aura.util.Utils.buildFlight;

public class DatedFlightSerializationTest {

    @Test
    public void testFlightSerialization() {
        try {
            DatedFlight datedFlight = new DatedFlight();
            Assertions.assertEquals("{}", OBJECT_MAPPER.writeValueAsString(datedFlight));
            datedFlight.setStatus(FlightStatus.CANCELLED);
            Assertions.assertEquals("{\"status\":{\"status\":\"c\"}}", OBJECT_MAPPER.writeValueAsString(datedFlight));
            datedFlight.setStatus(FlightStatus.DELETED);
            Assertions.assertEquals("{}", OBJECT_MAPPER.writeValueAsString(datedFlight));
            datedFlight.setStatus(FlightStatus.ACTIVE);
            Assertions.assertEquals("{}", OBJECT_MAPPER.writeValueAsString(datedFlight));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testValidFlightSerialization() {
        try {
            DatedFlight inFlight = buildFlight(LocalDate.now().getYear());
            String out = OBJECT_MAPPER.writeValueAsString(inFlight);
            DatedFlight outFlight = OBJECT_MAPPER.readValue(out, DatedFlight.class);
            Assertions.assertEquals(inFlight, outFlight);

            inFlight.setStatus(FlightStatus.CANCELLED);
            out = OBJECT_MAPPER.writeValueAsString(inFlight);
            outFlight = OBJECT_MAPPER.readValue(out, DatedFlight.class);
            Assertions.assertEquals(inFlight, outFlight);

            inFlight.setStatus(FlightStatus.DELETED);
            out = OBJECT_MAPPER.writeValueAsString(inFlight);
            outFlight = OBJECT_MAPPER.readValue(out, DatedFlight.class);
            Assertions.assertEquals(FlightStatus.ACTIVE, outFlight.getStatus());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}

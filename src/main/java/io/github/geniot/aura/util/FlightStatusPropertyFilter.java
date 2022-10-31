package io.github.geniot.aura.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.FlightStatus;

public class FlightStatusPropertyFilter extends SimpleBeanPropertyFilter {

    @Override
    public void serializeAsField
            (Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {

        //we only serialize status if it is cancelled
        if (pojo instanceof DatedFlight &&
                writer.getName().equals("status") &&
                !((DatedFlight) pojo).getStatus().equals(FlightStatus.CANCELLED)) {
        } else {
            if (include(writer)) {
                writer.serializeAsField(pojo, jgen, provider);
            } else if (!jgen.canOmitFields()) { // since 2.3
                writer.serializeAsOmittedField(pojo, jgen, provider);
            }
        }
    }

}

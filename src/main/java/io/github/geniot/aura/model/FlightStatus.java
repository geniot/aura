package io.github.geniot.aura.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.geniot.aura.util.FlightStatusDeserializer;
import io.github.geniot.aura.util.FlightStatusSerializer;

import java.util.EnumSet;
import java.util.Map;

@JsonDeserialize(using = FlightStatusDeserializer.class)
@JsonSerialize(using = FlightStatusSerializer.class)
public enum FlightStatus {

    MIXED("mixed"),
    ACTIVE("active"),
    CANCELLED("cancelled"),
    DELETED("deleted"),
    CREATED("created"),
    CONFLICT("conflict");

    public static final Map<FlightStatus, Character> LETTER_STATUS;
    public static final EnumSet<FlightStatus> STATUS_FROM = EnumSet.of(MIXED, ACTIVE, CANCELLED, DELETED);
    public static final EnumSet<FlightStatus> STATUS_TO = EnumSet.of(ACTIVE, CANCELLED, DELETED);

    static {
        LETTER_STATUS = Map.of(
                FlightStatus.ACTIVE, 'A',
                FlightStatus.CANCELLED, 'C',
                FlightStatus.DELETED, 'D',
                FlightStatus.MIXED, 'M',
                FlightStatus.CREATED, 'N',
                FlightStatus.CONFLICT, 'F'
        );
    }

    private String displayName;

    FlightStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    // Optionally and/or additionally, toString.
    @Override
    public String toString() {
        return displayName;
    }
}

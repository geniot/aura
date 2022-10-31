package io.github.geniot.aura.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class CompressedFlight implements Serializable {
    String from;
    String to;
    String startDate;
    String endDate;
    String days;
    String departureTime;
    String arrivalTime;
    String flight;
    String aircraft;
    String travelTime;
}

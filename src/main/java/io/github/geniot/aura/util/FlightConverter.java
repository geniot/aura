package io.github.geniot.aura.util;

import io.github.geniot.aura.model.CompressedFlight;
import io.github.geniot.aura.model.DatedFlight;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FlightConverter {

    List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

    public List<DatedFlight> toDated(CompressedFlight compressedFlight) throws Exception {
        List<DatedFlight> datedFlights = new ArrayList<>();

        LocalDate startDate = getDate(compressedFlight.getStartDate());
        LocalDate endDate = getDate(compressedFlight.getEndDate());

        SortedSet<Integer> days = getDays(compressedFlight.getDays());

        while (startDate.compareTo(endDate) <= 0) {
            if (days.contains(startDate.getDayOfWeek().getValue())) {

                DatedFlight datedFlight = new DatedFlight();
                datedFlight.setFrom(compressedFlight.getFrom());
                datedFlight.setTo(compressedFlight.getTo());

                String airlineDesignator = extractAirlineDesignator(compressedFlight.getFlight());
                if (StringUtils.isEmpty(airlineDesignator)) {
                    throw new Exception("Cannot be empty.");
                }
                datedFlight.setAirlineDesignator(airlineDesignator);
                datedFlight.setFlightNumber(compressedFlight.getFlight().substring(airlineDesignator.length()));

                datedFlight.setDepartureDate(startDate);

                datedFlight.setDepartureTime(parseTime(compressedFlight.getDepartureTime()));
                datedFlight.setArrivalTime(parseTime(compressedFlight.getArrivalTime()));

                datedFlight.setAircraft(compressedFlight.getAircraft());

                datedFlights.add(datedFlight);
            }
            startDate = startDate.plusDays(1);
        }

        return datedFlights;
    }

    private LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr.split("\\+")[0]);
    }

    private Integer extractTravelTimeMinutes(String travelTime) throws Exception {
        int hours = Integer.parseInt(travelTime.split("H")[0]);
        int minutes = Integer.parseInt(travelTime.split("H")[1].replaceAll("M", ""));
        return hours * 60 + minutes;
    }

    private String extractAirlineDesignator(String flight) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < flight.length(); i++) {
            if (!Character.isDigit(flight.charAt(i))) {
                stringBuilder.append(flight.charAt(i));
            } else {
                return stringBuilder.toString();
            }
        }
        return stringBuilder.toString();
    }

    private SortedSet<Integer> getDays(String days) throws Exception {
        SortedSet<Integer> daysSet = new TreeSet<>();
        for (int i = 0; i < days.length(); i++) {
            if (Character.isDigit(days.charAt(i))) {
                daysSet.add(Integer.parseInt(String.valueOf(days.charAt(i))));
            } else {
                if (days.charAt(i) != ' ') {
                    throw new Exception("Expecting a digit or a space: " + days);
                }
            }
        }
        return daysSet;
    }

    private LocalDate getDate(String dateStr) {
        int year = LocalDate.now().getYear();
        int month = months.indexOf(dateStr.split("\\s")[1]) + 1;
        int date = Integer.parseInt(dateStr.split("\\s")[0]);
        return LocalDate.of(year, month, date);
    }
}


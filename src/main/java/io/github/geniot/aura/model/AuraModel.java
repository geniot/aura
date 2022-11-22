package io.github.geniot.aura.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Data
@Component
public class AuraModel {

    private String pathToRepository;
    private Integer repositoryYear;

    private SortedMap<LocalDate, SortedMap<Integer, SortedSet<DatedFlight>>> datedFlightsMap = new TreeMap<>();
    private SortedMap<String, SortedSet<DatedFlight>> namedFlightsMap = new TreeMap<>();//key=airlineDesignator+flightNumber

    private SortedSet<DatedFlight> createdFlights = new TreeSet<>();
    private SortedSet<DatedFlight> updatedFlights = new TreeSet<>();
    private SortedSet<DatedFlight> deletedFlights = new TreeSet<>();

    private LocalDate selectedDate;
    private Integer selectedHourOfDay;
    private DatedFlight selectedHourDatedFlight;
    private List<DatedFlight> selectedDatedFlights;

    public int getFlightsCount() {
        int count = 0;
        for (SortedSet<DatedFlight> flights : namedFlightsMap.values()) {
            count += flights.size();
        }
        return count;
    }

    public void reset() {
        pathToRepository = null;
        repositoryYear = null;

        datedFlightsMap.clear();
        namedFlightsMap.clear();

        createdFlights.clear();
        updatedFlights.clear();
        deletedFlights.clear();

        selectedDate = null;
        selectedHourOfDay = null;
        selectedHourDatedFlight = null;
        selectedDatedFlights = null;
    }

    public boolean isEmptyDate(LocalDate localDate) {
        SortedMap<Integer, SortedSet<DatedFlight>> dateFlights = datedFlightsMap.get(localDate);
        if (dateFlights != null) {
            for (SortedSet<DatedFlight> hourFlights : dateFlights.values()) {
                if (!hourFlights.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Integer selectHourOfDayByDate(LocalDate localDate) {
        SortedMap<Integer, SortedSet<DatedFlight>> dateFlights = datedFlightsMap.get(localDate);
        if (dateFlights != null) {
            for (Integer hour : dateFlights.keySet()) {
                SortedSet<DatedFlight> hourFlights = dateFlights.get(hour);
                if (!hourFlights.isEmpty()) {
                    return hour;
                }
            }
        }
        return null;
    }

    public void addFlight(DatedFlight datedFlight) {
        datedFlight.setNewDepartureTime(datedFlight.getDepartureTime());
        datedFlight.setNewArrivalTime(datedFlight.getArrivalTime());
        datedFlight.setNewStatus(datedFlight.getStatus());

        LocalDate departureDate = datedFlight.getDepartureDate();
        int hourOfDay = datedFlight.getDepartureTime().getHour();
        SortedMap<Integer, SortedSet<DatedFlight>> dateMap = datedFlightsMap.get(departureDate);
        if (dateMap == null) {
            dateMap = new TreeMap<>();
        }
        SortedSet<DatedFlight> hourFlightsSet = dateMap.get(hourOfDay);
        if (hourFlightsSet == null) {
            hourFlightsSet = new TreeSet<>();
        }
        hourFlightsSet.add(datedFlight);
        dateMap.put(hourOfDay, hourFlightsSet);
        datedFlightsMap.put(departureDate, dateMap);
        //
        String nameKey = datedFlight.getAirlineDesignator() + datedFlight.getFlightNumber();
        SortedSet<DatedFlight> flightsSet = namedFlightsMap.get(nameKey);
        if (flightsSet == null) {
            flightsSet = new TreeSet<>();
        }
        flightsSet.add(datedFlight);
        namedFlightsMap.put(nameKey, flightsSet);
    }

    public void deleteFlight(DatedFlight datedFlight) {
        datedFlightsMap.get(datedFlight.getDepartureDate()).get(datedFlight.getDepartureTime().getHour()).remove(datedFlight);
        String nameKey = datedFlight.getAirlineDesignator() + datedFlight.getFlightNumber();
        namedFlightsMap.get(nameKey).remove(datedFlight);
    }
}

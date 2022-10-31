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

    private SortedMap<LocalDate, SortedMap<Integer, SortedSet<DatedFlight>>> datedFlightsMap;
    private SortedMap<String, SortedSet<DatedFlight>> namedFlightsMap;//key=airlineDesignator+flightNumber

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
}

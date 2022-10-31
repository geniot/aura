package io.github.geniot.aura.action;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationEventPublisher;

import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static io.github.geniot.aura.util.Utils.OBJECT_MAPPER;

class LoadRepositoryTask extends SwingWorker<Void, String> {
    private Progressable progressable;
    private AuraModel auraModel;
    private ApplicationEventPublisher applicationEventPublisher;


    public LoadRepositoryTask(Progressable p, AuraModel m, ApplicationEventPublisher a) {
        this.progressable = p;
        this.auraModel = m;
        this.applicationEventPublisher = a;
    }

    @Override
    protected Void doInBackground() {
        long t1 = System.currentTimeMillis();
        List<File> files = new ArrayList<>();
        addFiles(files, new File(auraModel.getPathToRepository()), 0, 4);
        progressable.setMax(files.size());

        long t2 = System.currentTimeMillis();

        System.out.println("Listing files took: " + (t2 - t1) + "ms");

        int min = 0;
        int max = files.size() == 0 ? 0 : files.size() - 1;
        int current = 0;

        SortedMap<LocalDate, SortedMap<Integer, SortedSet<DatedFlight>>> datedFlightsMap = new TreeMap<>();
        SortedMap<String, SortedSet<DatedFlight>> namedFlightsMap = new TreeMap<>();

        if (!files.isEmpty()) {
            while (!progressable.isCancelRequested() && current <= max) {

                File file = files.get(current);
                try {
                    String str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    DatedFlight[] datedFlights = OBJECT_MAPPER.readValue(str, DatedFlight[].class);

                    for (DatedFlight datedFlight : datedFlights) {

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                progressable.setProgress(current);
                ++current;
            }
        }

        progressable.close();

        //SUCCESS
        if (current >= max) {

            auraModel.setDatedFlightsMap(datedFlightsMap);
            auraModel.setNamedFlightsMap(namedFlightsMap);

            if (!auraModel.getDatedFlightsMap().isEmpty()) {
                auraModel.setSelectedDate(auraModel.getDatedFlightsMap().firstKey());
                SortedMap<Integer, SortedSet<DatedFlight>> selectedDateHoursMap =
                        auraModel.getDatedFlightsMap().get(auraModel.getSelectedDate());
                if (!selectedDateHoursMap.isEmpty()) {
                    auraModel.setSelectedHourOfDay(selectedDateHoursMap.firstKey());
                }
            }

            applicationEventPublisher.publishEvent(new AppEvent(this, EventType.REPOSITORY_LOADED));
        } else {//CANCELLED
            auraModel.setPathToRepository(null);
        }
        return null;
    }

    @Override
    protected void done() {
        progressable.close();
    }

    private List<File> addFiles(List<File> files, File dir, int depth, int maxDepth) {
        if (depth >= maxDepth) {
            return files;
        }
        if (!dir.isDirectory() && dir.getName().endsWith(".json")) {
            files.add(dir);
            return files;
        }
        File[] ffs = dir.listFiles();
        if (ffs != null) {
            ++depth;
            for (File file : ffs) {
                addFiles(files, file, depth, maxDepth);
            }
        }
        return files;
    }

}

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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

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

        if (!files.isEmpty()) {
            while (!progressable.isCancelRequested() && current <= max) {

                File file = files.get(current);
                try {
                    String str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    DatedFlight[] datedFlights = OBJECT_MAPPER.readValue(str, DatedFlight[].class);

                    for (DatedFlight datedFlight : datedFlights) {
                        auraModel.addFlight(datedFlight);
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

    private void addFiles(List<File> files, File dir, int depth, int maxDepth) {
        if (depth >= maxDepth) {
            return;
        }
        if (!dir.isDirectory() && dir.getName().endsWith(".json")) {
            files.add(dir);
            return;
        }
        File[] ffs = dir.listFiles();
        if (ffs != null) {
            ++depth;
            for (File file : ffs) {
                addFiles(files, file, depth, maxDepth);
            }
        }
    }

}

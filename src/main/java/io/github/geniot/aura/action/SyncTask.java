package io.github.geniot.aura.action;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.view.dialogs.SyncDialog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.ApplicationEventPublisher;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.geniot.aura.util.Utils.OBJECT_MAPPER;

public class SyncTask extends SwingWorker<Void, String> {

    private SyncDialog syncDialog;

    private AuraModel auraModel;

    private ApplicationEventPublisher applicationEventPublisher;

    private enum ActionType {
        UPDATE,
        CREATE,
        DELETE
    }


    public SyncTask(SyncDialog sd, AuraModel m, ApplicationEventPublisher aep) {
        this.syncDialog = sd;
        this.auraModel = m;
        this.applicationEventPublisher = aep;
    }

    @Override
    protected Void doInBackground() {
        try {

            processFlights(auraModel.getUpdatedFlights(), ActionType.UPDATE);
            processFlights(auraModel.getCreatedFlights(), ActionType.CREATE);
            processFlights(auraModel.getDeletedFlights(), ActionType.DELETE);

            File f = getGitRootDir(auraModel.getPathToRepository());
            runCommand("git add -A", f, true);
            runCommand("git commit -m \"" + syncDialog.syncView.messageTextField.getText() + "\"", f, false);

            //merging model, update
            for (DatedFlight datedFlight : auraModel.getUpdatedFlights()) {
                datedFlight.update(datedFlight);
            }
            auraModel.getUpdatedFlights().clear();
            //create
            for (DatedFlight datedFlight : auraModel.getCreatedFlights()) {
                auraModel.addFlight(datedFlight);
            }
            auraModel.getCreatedFlights().clear();
            //delete
            for (DatedFlight datedFlight : auraModel.getDeletedFlights()) {
                auraModel.deleteFlight(datedFlight);
            }
            auraModel.getDeletedFlights().clear();

            applicationEventPublisher.publishEvent(new AppEvent(this, EventType.REPOSITORY_LOADED));

//            runCommand("git push", f, true);

        } catch (Exception ex) {
            syncDialog.syncView.logTextArea.append(ExceptionUtils.getStackTrace(ex));
            syncDialog.syncView.logTextArea.append("\n");
        }
        return null;
    }

    private void processFlights(SortedSet<DatedFlight> flights, ActionType actionType) throws Exception {
        for (DatedFlight flight : flights) {

            String key = flight.getFileKeyShort();
            Map<String, DatedFlight> datedFlightsMap = new TreeMap<>();
            SortedSet<DatedFlight> datedFlights = new TreeSet<>();
            File file = new File(auraModel.getPathToRepository() + key + ".json");

            if (file.exists()) {
                String str = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                datedFlights = new TreeSet<>(List.of(OBJECT_MAPPER.readValue(str, DatedFlight[].class)));
                datedFlightsMap = datedFlights.stream().collect(Collectors.toMap(DatedFlight::getUniqueId, Function.identity()));
            }
            switch (actionType) {
                case UPDATE -> {
                    DatedFlight datedFlight = datedFlightsMap.get(flight.getUniqueId());
                    if (datedFlight != null) {
                        datedFlight.update(flight);
                    }
                }
                case CREATE -> {
                    datedFlightsMap.put(flight.getUniqueId(), flight);
                }
                case DELETE -> {
                    datedFlightsMap.remove(flight.getUniqueId());
                }
            }

            datedFlights = new TreeSet<>(datedFlightsMap.values());
            String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(datedFlights);
            FileUtils.write(file, json, StandardCharsets.UTF_8);

        }
    }

    private File getGitRootDir(String projectFile) {
        File f = new File(projectFile);
        while (!isRepo(f)) {
            f = f.getParentFile();
        }
        return f;
    }

    private boolean isRepo(File file) {
        File f = new File(file.getAbsolutePath() + File.separator + ".git");
        return f.exists() && f.isDirectory();
    }

    private void runCommand(String command, File f, boolean isStatusImportant) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(command.toString(), null, f);

        InputStreamReader isr = new InputStreamReader(proc.getInputStream());
        BufferedReader rdr = new BufferedReader(isr);
        String line;
        while ((line = rdr.readLine()) != null) {
            syncDialog.syncView.logTextArea.append(line);
            syncDialog.syncView.logTextArea.append("\n");
        }

        isr = new InputStreamReader(proc.getErrorStream());
        rdr = new BufferedReader(isr);
        while ((line = rdr.readLine()) != null) {
            syncDialog.syncView.logTextArea.append(line);
            syncDialog.syncView.logTextArea.append("\n");
        }
//            boolean rc = proc.waitFor(1, TimeUnit.SECONDS);  // Wait for the process to complete
        int rc = proc.waitFor();  // Wait for the process to complete
        if (isStatusImportant) {
//            System.out.println(rc);
        }
    }
}

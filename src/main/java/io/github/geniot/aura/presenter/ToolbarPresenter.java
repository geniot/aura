package io.github.geniot.aura.presenter;

import io.github.geniot.aura.action.LoadRepositoryAction;
import io.github.geniot.aura.action.UnloadRepositoryAction;
import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.view.CreateFlightsDialog;
import io.github.geniot.aura.view.MainFrameView;
import io.github.geniot.aura.view.PreferencesDialog;
import io.github.geniot.aura.view.ToolbarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ToolbarPresenter implements ApplicationListener<AppEvent> {
    @Autowired
    private ToolbarView toolbarView;

    @Autowired
    private MainFrameView mainFrameView;
    @Autowired
    private LoadRepositoryAction loadRepositoryAction;

    @Autowired
    private AuraModel auraModel;

    @Autowired
    private UnloadRepositoryAction unloadRepositoryAction;

    @PostConstruct
    private void init() {
        enableWorkspaceButtons(false);
        toolbarView.loadButton.setVisible(true);
        toolbarView.unloadButton.setVisible(false);

        toolbarView.airlineDesignatorFilter.setText("✻✻");
        toolbarView.flightNumberFilter.setText("✻✻✻✻");

        toolbarView.loadButton.addActionListener(loadRepositoryAction);
        toolbarView.unloadButton.addActionListener(unloadRepositoryAction);
        toolbarView.preferencesButton.addActionListener(e -> {
            PreferencesDialog preferencesDialog = new PreferencesDialog(mainFrameView);
            preferencesDialog.setVisible(true);
        });
        toolbarView.addButton.addActionListener(e -> {
            DatedFlight datedFlight = auraModel.getSelectedHourDatedFlight() == null ? Utils.buildFlight(auraModel.getRepositoryYear()) : auraModel.getSelectedHourDatedFlight();
            CreateFlightsDialog createFlightsDialog = new CreateFlightsDialog(mainFrameView, datedFlight, auraModel);
            createFlightsDialog.setVisible(true);
        });
    }

    private void enableWorkspaceButtons(boolean b) {
        toolbarView.airlineDesignatorFilter.setEnabled(b);
        toolbarView.flightNumberFilter.setEnabled(b);
        toolbarView.addButton.setEnabled(b);
        toolbarView.preferencesButton.setEnabled(b);
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.REPOSITORY_LOADED)) {
                enableWorkspaceButtons(true);
                toolbarView.loadButton.setVisible(false);
                toolbarView.unloadButton.setVisible(true);
                //debug
                toolbarView.addButton.doClick();
            }
            if (event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
                enableWorkspaceButtons(false);
                toolbarView.loadButton.setVisible(true);
                toolbarView.unloadButton.setVisible(false);
            }

            if (event.getEventType().equals(EventType.FLIGHTS_UPDATED) ||
                    event.getEventType().equals(EventType.REPOSITORY_LOADED) ||
                    event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
                toolbarView.saveButton.setEnabled((auraModel.getCreatedFlights().size() +
                        auraModel.getUpdatedFlights().size() +
                        auraModel.getDeletedFlights().size()) > 0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

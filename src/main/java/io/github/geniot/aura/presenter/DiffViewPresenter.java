package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.view.DiffView;
import io.github.geniot.aura.view.ToolbarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;

import static io.github.geniot.aura.util.Utils.listModelFromSet;

@Component
public class DiffViewPresenter implements ApplicationListener<AppEvent> {
    @Autowired
    ToolbarView toolbarView;

    @Autowired
    AuraModel auraModel;

    @Autowired
    DiffView diffView;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @PostConstruct
    private void init() {
        updateTitles();
        diffView.revertButton.addActionListener(e -> {
            for (DatedFlight datedFlight : diffView.createList.getSelectedValuesList()) {

                auraModel.getDatedFlightsMap()
                        .get(datedFlight.getDepartureDate())
                        .get(datedFlight.getDepartureTime().getHour())
                        .remove(datedFlight);

                auraModel.getNamedFlightsMap()
                        .get(datedFlight.getFlightNumberFull())
                        .remove(datedFlight);

                auraModel.getCreatedFlights().remove(datedFlight);
            }
            for (DatedFlight datedFlight : diffView.updateList.getSelectedValuesList()) {
                datedFlight.reset();
                auraModel.getUpdatedFlights().remove(datedFlight);
            }
            for (DatedFlight datedFlight : diffView.deleteList.getSelectedValuesList()) {
                datedFlight.reset();
                auraModel.getDeletedFlights().remove(datedFlight);
            }
            applicationEventPublisher.publishEvent(new AppEvent(this, EventType.FLIGHTS_UPDATED));
        });

        ListSelectionListener listSelectionListener = e -> {
            if (!e.getValueIsAdjusting()) {
                diffView.revertButton.setEnabled(
                        (diffView.createList.getSelectedValuesList().size() +
                                diffView.updateList.getSelectedValuesList().size() +
                                diffView.deleteList.getSelectedValuesList().size()) > 0
                );
            }
        };
        diffView.createList.addListSelectionListener(listSelectionListener);
        diffView.updateList.addListSelectionListener(listSelectionListener);
        diffView.deleteList.addListSelectionListener(listSelectionListener);
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.FLIGHTS_UPDATED) ||
                    event.getEventType().equals(EventType.REPOSITORY_LOADED) ||
                    event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
                updateTitles();
                DefaultListModel<DatedFlight> newCreateModel = listModelFromSet(auraModel.getCreatedFlights());
                DefaultListModel<DatedFlight> newUpdateModel = listModelFromSet(auraModel.getUpdatedFlights());
                DefaultListModel<DatedFlight> newDeleteModel = listModelFromSet(auraModel.getDeletedFlights());
                if (diffView.createList.getModel().getSize() != newCreateModel.size()) {
                    diffView.tabbedPane.setSelectedIndex(0);
                }
                if (newUpdateModel.size() > diffView.updateList.getModel().getSize()) {
                    diffView.tabbedPane.setSelectedIndex(1);
                }
                if (newDeleteModel.size() > diffView.deleteList.getModel().getSize()) {
                    diffView.tabbedPane.setSelectedIndex(2);
                }
                diffView.createList.setModel(newCreateModel);
                diffView.updateList.setModel(newUpdateModel);
                diffView.deleteList.setModel(newDeleteModel);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateTitles() {
        diffView.createDiffTabTitlePanel.getTitleLabel().setText("Create (" + auraModel.getCreatedFlights().size() + ")");
        diffView.updateDiffTabTitlePanel.getTitleLabel().setText("Update (" + auraModel.getUpdatedFlights().size() + ")");
        diffView.deleteDiffTabTitlePanel.getTitleLabel().setText("Delete (" + auraModel.getDeletedFlights().size() + ")");
    }
}

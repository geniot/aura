package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.model.FlightStatus;
import io.github.geniot.aura.view.EditorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.util.List;

import static io.github.geniot.aura.util.Utils.HOUR_MINUTE_FORMATTER;

@Component
public class EditorViewPresenter implements ApplicationListener<AppEvent> {
    @Autowired
    private EditorView editorView;

    @Autowired
    private AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    private void init() {
        editorView.updateButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<DatedFlight> selectedDatedFlights = auraModel.getSelectedDatedFlights();
                    for (DatedFlight datedFlight : selectedDatedFlights) {

                        LocalTime newDepartureTime = LocalTime.parse(editorView.newDepartureTimeTextField.getText());
                        LocalTime newArrivalTime = LocalTime.parse(editorView.newArrivalTimeTextField.getText());
                        FlightStatus newStatus = (FlightStatus) editorView.newStatusComboBox.getSelectedItem();

                        if (editorView.newDepartureTimeTextField.isEnabled()) {
                            datedFlight.setNewDepartureTime(newDepartureTime);
                        }
                        if (editorView.newArrivalTimeTextField.isEnabled()) {
                            datedFlight.setNewArrivalTime(newArrivalTime);
                        }
                        if (editorView.newStatusComboBox.isEnabled()) {
                            datedFlight.setNewStatus(newStatus);
                        }

                        if (!datedFlight.isCreated()) {
                            if (datedFlight.isUpdated()) {
                                auraModel.getUpdatedFlights().add(datedFlight);
                            } else {
                                auraModel.getUpdatedFlights().remove(datedFlight);
                            }
                            if (datedFlight.isDeleted()) {
                                auraModel.getDeletedFlights().add(datedFlight);
                            } else {
                                auraModel.getDeletedFlights().remove(datedFlight);
                            }
                        }
                    }

                    applicationEventPublisher.publishEvent(new AppEvent(this, EventType.FLIGHTS_UPDATED));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editorView.departureTimeRadioButton.addActionListener(e -> {
            disableEditor();
            editorView.departureArrowLabel.setEnabled(true);
            editorView.newDepartureTimeTextField.setEnabled(true);
        });
        editorView.arrivalTimeRadioButton.addActionListener(e -> {
            disableEditor();
            editorView.arrivalArrowLabel.setEnabled(true);
            editorView.newArrivalTimeTextField.setEnabled(true);
        });
        editorView.statusRadioButton.addActionListener(e -> {
            disableEditor();
            editorView.statusArrowLabel.setEnabled(true);
            editorView.newStatusComboBox.setEnabled(true);
        });

        disableEditor();
        enableForm(false);
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.FLIGHT_SELECTED)) {
                if (auraModel.getSelectedDatedFlights() == null) {
                    editorView.titleLabel.setText(" ");
                    enableForm(false);
                    editorView.departureTimeTextField.setText("");
                    editorView.newDepartureTimeTextField.setText("");
                    editorView.arrivalTimeTextField.setText("");
                    editorView.newArrivalTimeTextField.setText("");
                    editorView.flightNumberLabel.setText(" ");
                    editorView.statusComboBox.setModel(new DefaultComboBoxModel<>());
                    editorView.newStatusComboBox.setModel(new DefaultComboBoxModel<>());
                } else {
                    List<DatedFlight> selectedDatedFlights = auraModel.getSelectedDatedFlights();
                    editorView.titleLabel.setText("Selected " + selectedDatedFlights.size() + " dated flight" + (selectedDatedFlights.size() > 1 ? "s" : ""));
                    enableForm(true);

                    DatedFlight datedFlight = selectedDatedFlights.get(0);
                    editorView.flightNumberLabel.setText(datedFlight.getFlightNumberFull());

                    if (selectedDatedFlights.size() == 1) {
                        editorView.departureTimeTextField.setText(datedFlight.getDepartureTime().format(HOUR_MINUTE_FORMATTER));
                        editorView.arrivalTimeTextField.setText(datedFlight.getArrivalTime().format(HOUR_MINUTE_FORMATTER));
                    } else {
                        editorView.departureTimeTextField.setText("xx:xx");
                        editorView.arrivalTimeTextField.setText("xx:xx");
                    }

                    DefaultComboBoxModel<FlightStatus> statusComboBoxModel = new DefaultComboBoxModel<>();
                    statusComboBoxModel.addAll(FlightStatus.STATUS_FROM);

                    DefaultComboBoxModel<FlightStatus> newStatusComboBoxModel = new DefaultComboBoxModel<>();
                    newStatusComboBoxModel.addAll(FlightStatus.STATUS_TO);

                    editorView.statusComboBox.setModel(statusComboBoxModel);
                    editorView.newStatusComboBox.setModel(newStatusComboBoxModel);

                    if (selectedDatedFlights.size() == 1) {
                        FlightStatus flightStatus = selectedDatedFlights.get(0).getStatus() == FlightStatus.CREATED ?
                                FlightStatus.ACTIVE : selectedDatedFlights.get(0).getStatus();
                        editorView.statusComboBox.getModel().setSelectedItem(flightStatus);
                    } else {
                        editorView.statusComboBox.getModel().setSelectedItem(FlightStatus.MIXED);
                    }

                    editorView.newDepartureTimeTextField.setText(datedFlight.getCombinedDepartureTime().format(HOUR_MINUTE_FORMATTER));
                    editorView.newArrivalTimeTextField.setText(datedFlight.getCombinedArrivalTime().format(HOUR_MINUTE_FORMATTER));

                    FlightStatus newFlightStatus = datedFlight.getCombinedStatus() == FlightStatus.CREATED ?
                            FlightStatus.ACTIVE : datedFlight.getCombinedStatus();
                    editorView.newStatusComboBox.getModel().setSelectedItem(newFlightStatus);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void disableEditor() {
        editorView.newDepartureTimeTextField.setEnabled(false);
        editorView.newArrivalTimeTextField.setEnabled(false);
        editorView.newStatusComboBox.setEnabled(false);

        editorView.departureArrowLabel.setEnabled(false);
        editorView.arrivalArrowLabel.setEnabled(false);
        editorView.statusArrowLabel.setEnabled(false);
    }

    private void enableForm(boolean shouldEnable) {

        editorView.departureTimeRadioButton.setEnabled(shouldEnable);
        editorView.arrivalTimeRadioButton.setEnabled(shouldEnable);
        editorView.statusRadioButton.setEnabled(shouldEnable);

        editorView.updateButton.setEnabled(shouldEnable);

        if (shouldEnable) {
            editorView.statusArrowLabel.setEnabled(true);
            editorView.newStatusComboBox.setEnabled(true);
            editorView.statusRadioButton.setSelected(true);
        } else {
            disableEditor();
            editorView.editorButtonGroup.clearSelection();
        }

    }
}

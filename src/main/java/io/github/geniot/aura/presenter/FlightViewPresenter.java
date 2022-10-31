package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.view.FlightView;
import io.github.geniot.aura.view.SelectButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class FlightViewPresenter implements ApplicationListener<AppEvent> {

    @Autowired
    private FlightView flightView;

    @Autowired
    private AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    private void init() {
        flightView.flightsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                List<DatedFlight> list = flightView.flightsList.getSelectedValuesList();
                auraModel.setSelectedDatedFlights(list.isEmpty() ? null : list);
                applicationEventPublisher.publishEvent(new AppEvent(this, EventType.FLIGHT_SELECTED));
            }
        });

        flightView.selectAllButton.addActionListener(e -> {
            toggleWeekDays(flightView.selectAllButton.isSelected());
            if (flightView.selectAllButton.isSelected()) {
                flightView.flightsList.setSelectedIndices(IntStream.rangeClosed(0, flightView.flightsList.getModel().getSize() - 1).toArray());
            } else {
                flightView.flightsList.clearSelection();
            }
        });

        ActionListener actionListener = e -> {
            SelectButton selectButton = (SelectButton) e.getSource();
            Integer[] alreadySelected = Arrays.stream(flightView.flightsList.getSelectedIndices()).boxed().toArray(Integer[]::new);
            Set<Integer> myList = new HashSet<>(Arrays.asList(alreadySelected));
            for (int i = 0; i < flightView.flightsList.getModel().getSize(); i++) {
                DatedFlight datedFlight = flightView.flightsList.getModel().getElementAt(i);
                if (datedFlight.getDepartureDate().getDayOfWeek().getValue() == selectButton.getDayOfWeek()) {
                    if (selectButton.isSelected()) {
                        myList.add(i);
                    } else {
                        myList.remove(i);
                    }
                }
            }
            int[] intArray = myList.parallelStream().mapToInt(Integer::intValue).toArray();
            flightView.flightsList.setSelectedIndices(intArray);
        };
        flightView.selectMoButton.addActionListener(actionListener);
        flightView.selectTuButton.addActionListener(actionListener);
        flightView.selectWeButton.addActionListener(actionListener);
        flightView.selectThButton.addActionListener(actionListener);
        flightView.selectFrButton.addActionListener(actionListener);
        flightView.selectSaButton.addActionListener(actionListener);
        flightView.selectSuButton.addActionListener(actionListener);
    }

    private void toggleWeekDays(boolean isSelected) {
        flightView.selectMoButton.setSelected(isSelected);
        flightView.selectTuButton.setSelected(isSelected);
        flightView.selectWeButton.setSelected(isSelected);
        flightView.selectThButton.setSelected(isSelected);
        flightView.selectFrButton.setSelected(isSelected);
        flightView.selectSaButton.setSelected(isSelected);
        flightView.selectSuButton.setSelected(isSelected);
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.FLIGHTS_UPDATED)) {
                flightView.flightsList.revalidate();
                flightView.flightsList.repaint();
            }
            if (event.getEventType().equals(EventType.HOUR_FLIGHT_SELECTED)) {
                if (auraModel.getSelectedHourDatedFlight() == null) {
                    flightView.titleLabel.setText(" ");
                    DefaultListModel<DatedFlight> model = new DefaultListModel<>();
                    flightView.flightsList.setModel(model);
                } else {
                    String flightNumber = auraModel.getSelectedHourDatedFlight().getAirlineDesignator() + auraModel.getSelectedHourDatedFlight().getFlightNumber();

                    SortedSet<DatedFlight> datedFlights = auraModel.getNamedFlightsMap().get(flightNumber);
                    if (datedFlights != null) {
                        DefaultListModel<DatedFlight> model = new DefaultListModel<>();
                        model.addAll(datedFlights);
                        flightView.flightsList.setModel(model);

                        flightView.flightsList.clearSelection();
                        toggleWeekDays(false);
                        flightView.selectAllButton.setSelected(false);
//                        flightView.flightsList.setSelectedValue(auraModel.getSelectedHourDatedFlight(), true);

                        flightView.titleLabel.setText(flightNumber + " (" + datedFlights.size() + ")");
                    } else {
                        flightView.titleLabel.setText(flightNumber);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

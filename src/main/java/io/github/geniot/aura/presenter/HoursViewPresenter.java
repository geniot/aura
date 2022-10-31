package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.view.HoursView;
import io.github.geniot.aura.view.calendar.HourButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import static io.github.geniot.aura.util.Utils.MONTH_LABELS;

@Component
public class HoursViewPresenter implements ApplicationListener<AppEvent> {

    @Autowired
    private HoursView hoursView;

    @Autowired
    private AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    private void init() {
        for (HourButton hourButton : hoursView.hourButtons) {
            hourButton.addActionListener(e -> {
                auraModel.setSelectedHourOfDay(hourButton.isSelected() ? hourButton.getLocalTime().getHour() : null);
                applicationEventPublisher.publishEvent(new AppEvent(this, EventType.HOUR_SELECTED));
            });
        }
        hoursView.flightsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                auraModel.setSelectedHourDatedFlight(hoursView.flightsList.getSelectedValue());
                applicationEventPublisher.publishEvent(new AppEvent(this, EventType.HOUR_FLIGHT_SELECTED));
            }
        });
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.DATE_SELECTED)) {
                SortedMap<Integer, SortedSet<DatedFlight>> selectedDateHoursMap =
                        auraModel.getSelectedDate() == null ?
                                Collections.emptySortedMap() :
                                auraModel.getDatedFlightsMap().get(auraModel.getSelectedDate());

                for (HourButton hourButton : hoursView.hourButtons) {
                    int dateButtonHour = hourButton.getLocalTime().getHour();
                    hourButton.setEnabled(selectedDateHoursMap.containsKey(dateButtonHour));
                    hourButton.setSelected(auraModel.getSelectedHourOfDay() != null && dateButtonHour == auraModel.getSelectedHourOfDay());
                    hourButton.revalidate();
                    hourButton.repaint();
                }
                applicationEventPublisher.publishEvent(new AppEvent(this, EventType.HOUR_SELECTED));
            }
            if (event.getEventType().equals(EventType.HOUR_SELECTED)) {

                if (auraModel.getSelectedDate() == null || auraModel.getSelectedHourOfDay() == null) {
                    hoursView.flightsList.setModel(new DefaultListModel<>());
                    hoursView.titleLabel.setText(" ");

                } else {
                    SortedSet<DatedFlight> hourFlights = auraModel.getDatedFlightsMap()
                            .get(auraModel.getSelectedDate())
                            .get(auraModel.getSelectedHourOfDay());

                    for (HourButton hourButton : hoursView.hourButtons) {
                        boolean shouldSelectHourButton = hourButton.getLocalTime().getHour() == auraModel.getSelectedHourOfDay();
                        hourButton.setSelected(shouldSelectHourButton);
                    }
                    updateColors();

                    DefaultListModel<DatedFlight> model = new DefaultListModel<>();
                    model.addAll(hourFlights);
                    hoursView.flightsList.setModel(model);

                    String panelTitle = "Departures on " +
                            auraModel.getSelectedDate().getDayOfMonth() + ", " +
                            MONTH_LABELS[auraModel.getSelectedDate().getMonthValue() - 1] +
                            " (" + hourFlights.size() + ")";

                    hoursView.titleLabel.setText(panelTitle);

//                    hoursView.flightsList.setSelectedIndex(0);
//                    hoursView.flightsList.ensureIndexIsVisible(0);
                }
            }

            if (event.getEventType().equals(EventType.FLIGHTS_UPDATED) ||
                    event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
                updateColors();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateColors() {
        Set<LocalTime> createdTimes = Utils.getTimes(auraModel.getCreatedFlights(), auraModel.getSelectedDate());
        Set<LocalTime> updatedTimes = Utils.getTimes(auraModel.getUpdatedFlights(), auraModel.getSelectedDate());
        Set<LocalTime> deletedTimes = Utils.getTimes(auraModel.getDeletedFlights(), auraModel.getSelectedDate());

        for (HourButton hourButton : hoursView.hourButtons) {
            hourButton.setCreated(createdTimes.contains(hourButton.getLocalTime()));
            hourButton.setUpdated(updatedTimes.contains(hourButton.getLocalTime()));
            hourButton.setDeleted(deletedTimes.contains(hourButton.getLocalTime()));
        }

        hoursView.flightsList.revalidate();
        hoursView.flightsList.repaint();
    }
}

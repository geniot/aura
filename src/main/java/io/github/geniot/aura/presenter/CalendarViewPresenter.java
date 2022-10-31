package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.view.CalendarView;
import io.github.geniot.aura.view.calendar.CalendarPanel;
import io.github.geniot.aura.view.calendar.DateButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.time.LocalDate;
import java.util.Set;

@Component
public class CalendarViewPresenter implements ApplicationListener<AppEvent> {
    @Autowired
    CalendarView calendarView;

    @Autowired
    AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private CalendarPanel calendarPanel;

    @PostConstruct
    private void init() {
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        try {
            if (event.getEventType().equals(EventType.REPOSITORY_LOADED)) {
                try {
                    calendarView.rootPanel.removeAll();
                    calendarPanel = new CalendarPanel(auraModel.getRepositoryYear(), auraModel);
                    calendarView.rootPanel.add(calendarPanel, BorderLayout.CENTER);

                    for (DateButton dateButton : calendarPanel.getDateButtonSet()) {
                        dateButton.addActionListener(e -> {
                            auraModel.setSelectedDate(dateButton.isSelected() ? dateButton.getLocalDate() : null);
                            auraModel.setSelectedHourOfDay(dateButton.isSelected() ? auraModel.selectHourOfDayByDate(dateButton.getLocalDate()) : null);
                            applicationEventPublisher.publishEvent(new AppEvent(this, EventType.DATE_SELECTED));
                        });
                        dateButton.setSelected(dateButton.getLocalDate().equals(auraModel.getSelectedDate()));
                    }
                    applicationEventPublisher.publishEvent(new AppEvent(this, EventType.DATE_SELECTED));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (event.getEventType().equals(EventType.FLIGHTS_UPDATED)) {

                Set<LocalDate> createdDates = Utils.getDates(auraModel.getCreatedFlights());
                Set<LocalDate> updatedDates = Utils.getDates(auraModel.getUpdatedFlights());
                Set<LocalDate> deletedDates = Utils.getDates(auraModel.getDeletedFlights());

                for (DateButton dateButton : calendarPanel.getDateButtonSet()) {
                    dateButton.setEnabled(!auraModel.isEmptyDate(dateButton.getLocalDate()));
                    dateButton.setCreated(createdDates.contains(dateButton.getLocalDate()));
                    dateButton.setUpdated(updatedDates.contains(dateButton.getLocalDate()));
                    dateButton.setDeleted(deletedDates.contains(dateButton.getLocalDate()));
                }

                //are there any flights left in the selected date?
                if (auraModel.getSelectedDate() != null && auraModel.isEmptyDate(auraModel.getSelectedDate())) {
                    auraModel.setSelectedDate(null);
                    applicationEventPublisher.publishEvent(new AppEvent(this, EventType.DATE_SELECTED));
                }
            }

            if (event.getEventType().equals(EventType.DATE_SELECTED)) {
                for (DateButton dateButton : calendarPanel.getDateButtonSet()) {
                    dateButton.setSelected(dateButton.getLocalDate().equals(auraModel.getSelectedDate()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

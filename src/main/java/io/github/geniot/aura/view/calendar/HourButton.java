package io.github.geniot.aura.view.calendar;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class HourButton extends DateButton {
    private LocalTime localTime;

    public HourButton(LocalTime localTime) {
        super(LocalDate.now());
        this.localTime = localTime;
        setText(String.format("%02d", localTime.getHour()));
    }
}

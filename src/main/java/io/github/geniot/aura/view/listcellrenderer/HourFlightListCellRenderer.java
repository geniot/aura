package io.github.geniot.aura.view.listcellrenderer;

import io.github.geniot.aura.model.DatedFlight;

import javax.swing.*;
import java.awt.*;

public class HourFlightListCellRenderer extends FlightListCellRenderer implements ListCellRenderer<DatedFlight> {

    @Override
    public Component getListCellRendererComponent(JList<? extends DatedFlight> list, DatedFlight value, int index, boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(value.isCreated() ? value.toCreatedHourString() : value.toHourString());
        return component;
    }
}


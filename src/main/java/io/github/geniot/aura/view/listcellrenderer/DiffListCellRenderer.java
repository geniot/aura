package io.github.geniot.aura.view.listcellrenderer;

import io.github.geniot.aura.model.DatedFlight;

import javax.swing.*;
import java.awt.*;

public class DiffListCellRenderer implements ListCellRenderer<DatedFlight> {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends DatedFlight> list, DatedFlight value, int index, boolean isSelected, boolean cellHasFocus) {
        String text = value.isCreated() ? value.toCreatedFlightString() : value.toFlightString();
        return defaultRenderer.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
    }
}



package io.github.geniot.aura.view.listcellrenderer;

import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.util.Utils;

import javax.swing.*;
import java.awt.*;

public class NewFlightListCellRenderer extends FlightListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<? extends DatedFlight> list, DatedFlight value, int index, boolean isSelected, boolean cellHasFocus) {
        init(list,value,index,isSelected,cellHasFocus);

        boolean isConflict = value.isConflict();

        if (isConflict) {
            listColorButtonPanel.setVisible(true);
            listColorButtonPanel.colorButton.setBackground(Utils.RED);
        } else {
            listColorButtonPanel.setVisible(false);
        }

        return this;
    }
}

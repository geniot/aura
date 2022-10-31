package io.github.geniot.aura.view.listcellrenderer;

import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.model.DatedFlight;

import javax.swing.*;
import java.awt.*;

public class FlightListCellRenderer extends JPanel implements ListCellRenderer<DatedFlight> {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    protected final ListColorButtonPanel listColorButtonPanel = new ListColorButtonPanel();

    JLabel label;

    public FlightListCellRenderer() {
        super();
        setLayout(new BorderLayout());
        setVisible(false);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DatedFlight> list, DatedFlight value, int index, boolean isSelected, boolean cellHasFocus) {
        init(list, value, index, isSelected, cellHasFocus);

        boolean isCreated = value.isCreated();
        boolean isUpdated = value.isUpdated();
        boolean isDeleted = value.isDeleted();

        Color color = Utils.RED;//to display that something went wrong

        if (isCreated) {
            color = Utils.GREEN;
        } else if (isUpdated) {
            color = Utils.YELLOW;
        } else if (isDeleted) {
            color = Utils.GRAY;
        }

        if (isUpdated || isDeleted || isCreated) {
            listColorButtonPanel.setVisible(true);
            listColorButtonPanel.colorButton.setBackground(color);
        } else {
            listColorButtonPanel.setVisible(false);
        }

        return this;
    }

    protected void init(JList<? extends DatedFlight> list,
                        DatedFlight value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus) {
        label = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String text = value.isCreated() ? value.toCreatedFlightString() : value.toFlightString();
        label.setText(text);
        add(label, BorderLayout.CENTER);
        add(listColorButtonPanel, BorderLayout.WEST);
        listColorButtonPanel.setForeground(label.getForeground());
        listColorButtonPanel.setBackground(label.getBackground());
    }
}

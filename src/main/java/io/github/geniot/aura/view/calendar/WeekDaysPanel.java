package io.github.geniot.aura.view.calendar;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class WeekDaysPanel extends JPanel {

    private static final String[] LABELS = new String[]{"", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};

    public WeekDaysPanel() {
        super();

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(30, 30 * LABELS.length));

        for (int i = 0; i <= 7; i++) {

            final JPanel weekDayPanel = new JPanel();
            weekDayPanel.setLayout(new BorderLayout(0, 0));
            weekDayPanel.setMaximumSize(new Dimension(30, 30));
            weekDayPanel.setMinimumSize(new Dimension(30, 30));
            weekDayPanel.setPreferredSize(new Dimension(30, 30));
            weekDayPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.BOTH;

            final JLabel weekDayLabel = new JLabel();
            weekDayLabel.setFocusable(false);
            weekDayLabel.setHorizontalAlignment(0);
            weekDayLabel.setHorizontalTextPosition(0);
            weekDayLabel.setRequestFocusEnabled(false);
            weekDayLabel.setText(LABELS[i]);
            weekDayPanel.add(weekDayLabel, BorderLayout.CENTER);

            add(weekDayPanel, gbc);
        }
    }
}

package io.github.geniot.aura.view.calendar;

import io.github.geniot.aura.model.AuraModel;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;

import static io.github.geniot.aura.util.Utils.MONTH_LABELS;

@Getter
public class MonthPanel extends JPanel {

    private final DaysPanel daysPanel;

    public MonthPanel(int year, int month, AuraModel auraModel, Set<DateButton> dateButtonSet) {
        super();
        setLayout(new BorderLayout(0, 0));

        setPreferredSize(new Dimension(5 * 30, 8 * 30));

        final JPanel monthTitlePanel = new JPanel();
        monthTitlePanel.setLayout(new BorderLayout(0, 0));
        monthTitlePanel.setPreferredSize(new Dimension(0, 30));
        monthTitlePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JLabel monthTitleLabel = new JLabel();
        monthTitleLabel.setHorizontalAlignment(0);
        monthTitleLabel.setHorizontalTextPosition(0);
        monthTitleLabel.setText(MONTH_LABELS[month - 1]);
        monthTitlePanel.add(monthTitleLabel, BorderLayout.CENTER);

        add(monthTitlePanel, BorderLayout.NORTH);

        daysPanel = new DaysPanel(year, month, auraModel, dateButtonSet);
        add(daysPanel, BorderLayout.CENTER);
    }
}

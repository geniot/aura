package io.github.geniot.aura.view;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

@Component
public class CalendarView extends JPanel {
    public JPanel rootPanel;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        rootPanel.setPreferredSize(new Dimension(10, 240));
        add(rootPanel, BorderLayout.CENTER);
    }

}

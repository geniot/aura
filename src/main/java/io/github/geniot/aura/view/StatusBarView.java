package io.github.geniot.aura.view;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@Component
public class StatusBarView extends JPanel {
    private JPanel rootPanel;
    public JPanel rightPanel;
    public JPanel leftPanel;
    public JPanel centerPanel;
    public JLabel rightStatusLabel;
    public JLabel leftStatusLabel;
    public JLabel centerStatusLabel;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        add(rootPanel, BorderLayout.CENTER);
    }

}

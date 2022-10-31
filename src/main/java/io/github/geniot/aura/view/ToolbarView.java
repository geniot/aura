package io.github.geniot.aura.view;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

@Component
public class ToolbarView extends JPanel {
    public JPanel filterPanel;
    public JButton airlineDesignatorFilter;
    public JButton flightNumberFilter;
    private JPanel actionsPanel;
    public JButton loadButton;
    public JButton unloadButton;
    public JButton saveButton;
    public JButton preferencesButton;
    private JPanel rootPanel;
    public JButton addButton;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        add(rootPanel, BorderLayout.CENTER);
    }

}

package io.github.geniot.aura.view;

import io.github.geniot.aura.model.FlightStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;

import static io.github.geniot.aura.util.Utils.*;

@Component
public class EditorView extends JPanel {
    public JFormattedTextField departureTimeTextField;
    public JFormattedTextField newDepartureTimeTextField;
    public JComboBox<FlightStatus> newStatusComboBox;
    public JLabel titleLabel;
    public JFormattedTextField arrivalTimeTextField;
    public JComboBox<FlightStatus> statusComboBox;
    public JFormattedTextField newArrivalTimeTextField;
    public JButton updateButton;
    public JLabel flightNumberLabel;
    public JPanel rootPanel;
    public JRadioButton departureTimeRadioButton;
    public JRadioButton arrivalTimeRadioButton;
    public JRadioButton statusRadioButton;
    public JLabel departureArrowLabel;
    public JLabel arrivalArrowLabel;
    public JLabel statusArrowLabel;
    public JPanel titlePanel;
    public JPanel actionsPanel;
    public JPanel contentPanel;

    public ButtonGroup editorButtonGroup = new ButtonGroup();


    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        flightNumberLabel.setFont(BIG_MONOSPACED_FONT);

        departureTimeTextField.setFont(MONOSPACED_FONT);
        arrivalTimeTextField.setFont(MONOSPACED_FONT);
        newDepartureTimeTextField.setFont(MONOSPACED_FONT);
        newArrivalTimeTextField.setFont(MONOSPACED_FONT);

        newDepartureTimeTextField.setFormatterFactory(new DefaultFormatterFactory(createFormatter("##:##")));
        newArrivalTimeTextField.setFormatterFactory(new DefaultFormatterFactory(createFormatter("##:##")));

        editorButtonGroup.add(departureTimeRadioButton);
        editorButtonGroup.add(arrivalTimeRadioButton);
        editorButtonGroup.add(statusRadioButton);

        add(rootPanel, BorderLayout.CENTER);
    }

}

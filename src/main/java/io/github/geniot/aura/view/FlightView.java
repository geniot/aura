package io.github.geniot.aura.view;

import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.presenter.DeselectableListSelectionModel;
import io.github.geniot.aura.view.listcellrenderer.FlightListCellRenderer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

import static io.github.geniot.aura.util.Utils.MONOSPACED_FONT;

@Component
public class FlightView extends JPanel {
    public JPanel rootPanel;
    public JList<DatedFlight> flightsList;
    public JLabel titleLabel;
    public JScrollPane flightsScrollPane;
    public JPanel allButtonPanel;
    public JPanel weekDaysPanel;

    public SelectButton selectAllButton = new SelectButton("All", 0);
    public SelectButton selectMoButton = new SelectButton("Mo", 1);
    public SelectButton selectTuButton = new SelectButton("Tu", 2);
    public SelectButton selectWeButton = new SelectButton("We", 3);
    public SelectButton selectThButton = new SelectButton("Th", 4);
    public SelectButton selectFrButton = new SelectButton("Fr", 5);
    public SelectButton selectSaButton = new SelectButton("Sa", 6);
    public SelectButton selectSuButton = new SelectButton("Su", 7);

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        flightsList = new JList<>();
        flightsList.setCellRenderer(new FlightListCellRenderer());
        flightsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        flightsList.setFocusable(false);
        flightsList.setRequestFocusEnabled(false);
        flightsList.setFont(MONOSPACED_FONT);
        flightsList.setSelectionModel(new DeselectableListSelectionModel());

        flightsScrollPane.setViewportView(flightsList);

        allButtonPanel.add(selectAllButton);
        weekDaysPanel.add(selectMoButton);
        weekDaysPanel.add(selectTuButton);
        weekDaysPanel.add(selectWeButton);
        weekDaysPanel.add(selectThButton);
        weekDaysPanel.add(selectFrButton);
        weekDaysPanel.add(selectSaButton);
        weekDaysPanel.add(selectSuButton);

        add(rootPanel, BorderLayout.CENTER);
    }

}

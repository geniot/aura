package io.github.geniot.aura.view.dialogs;


import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.model.FlightStatus;
import io.github.geniot.aura.view.MainFrameView;
import io.github.geniot.aura.view.SelectButton;
import io.github.geniot.aura.view.listcellrenderer.NewFlightListCellRenderer;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static io.github.geniot.aura.util.Utils.*;
import static io.github.geniot.aura.view.MainFrameView.ICON;

public class CreateFlightsDialog extends JDialog {
    private CreateFlightsView createFlightsView = new CreateFlightsView();
    private GenerateFlightsView generateFlightsView = new GenerateFlightsView();

    private MainFrameView mainFrameView;

    public SelectButton selectMoButton = new SelectButton("Mo", 1);
    public SelectButton selectTuButton = new SelectButton("Tu", 2);
    public SelectButton selectWeButton = new SelectButton("We", 3);
    public SelectButton selectThButton = new SelectButton("Th", 4);
    public SelectButton selectFrButton = new SelectButton("Fr", 5);
    public SelectButton selectSaButton = new SelectButton("Sa", 6);
    public SelectButton selectSuButton = new SelectButton("Su", 7);

    public SelectButton[] weekdaysButtons = new SelectButton[]{
            selectMoButton,
            selectTuButton,
            selectWeButton,
            selectThButton,
            selectFrButton,
            selectSaButton,
            selectSuButton
    };

    public CreateFlightsDialog(MainFrameView mfv, DatedFlight datedFlight, AuraModel auraModel) {
        this.mainFrameView = mfv;

        for (SelectButton selectButton : weekdaysButtons) {
            generateFlightsView.weekDaysPanel.add(selectButton);
            selectButton.setEnabled(false);
            generateFlightsView.weekDaysPanel.add(Box.createHorizontalStrut(5));
        }

        JFormattedTextField[] textFields = new JFormattedTextField[]{
                generateFlightsView.flightNumberTextField,
                generateFlightsView.departureStationTextField,
                generateFlightsView.departureDateTextField,
                generateFlightsView.departureTimeTextField,
                generateFlightsView.endOfSeriesTextField,
                generateFlightsView.arrivalStationTextField,
                generateFlightsView.arrivalTimeTextField,
                generateFlightsView.aircraftTextField,
        };

        int padding = 3;
        for (JFormattedTextField textField : textFields) {
            textField.setBorder(BorderFactory.createCompoundBorder(
                    textField.getBorder(),
                    BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        }

        generateFlightsView.continuousCheckBox.addActionListener(e -> {
            for (SelectButton selectButton : weekdaysButtons) {
                selectButton.setEnabled(generateFlightsView.continuousCheckBox.isSelected());
            }
            generateFlightsView.endOfSeriesTextField.setEnabled(generateFlightsView.continuousCheckBox.isSelected());
        });

        generateFlightsView.generateButton.addActionListener(e -> {
            SortedSet<DatedFlight> newFlights = new TreeSet<>();
            if (generateFlightsView.continuousCheckBox.isSelected()) {
                BitSet selectedWeekDays = getSelectedWeekDays();
                LocalDate fromDate = LocalDate.parse(generateFlightsView.departureDateTextField.getText(), BUTTON_DATE_FORMATTER);
                LocalDate toDate = LocalDate.parse(generateFlightsView.endOfSeriesTextField.getText(), BUTTON_DATE_FORMATTER).plusDays(1);
                while (!fromDate.equals(toDate)) {
                    if (selectedWeekDays.get(fromDate.getDayOfWeek().getValue()) && fromDate.getYear() == auraModel.getRepositoryYear()) {
                        DatedFlight newDatedFlight = getDatedFlight(auraModel, fromDate);
                        newDatedFlight.setDepartureDate(fromDate);
                        newFlights.add(newDatedFlight);
                    }
                    fromDate = fromDate.plusDays(1);
                }
            } else {
                LocalDate departureDate = LocalDate.parse(generateFlightsView.departureDateTextField.getText(), BUTTON_DATE_FORMATTER);
                DatedFlight newDatedFlight = getDatedFlight(auraModel, departureDate);
                newFlights.add(newDatedFlight);
            }
            DefaultListModel<DatedFlight> defaultListModel = (DefaultListModel<DatedFlight>) createFlightsView.newFlightsList.getModel();
            defaultListModel.clear();
            defaultListModel.addAll(newFlights);
        });

        generateFlightsView.flightNumberTextField.setText(datedFlight.getFlightNumberFull());
        generateFlightsView.departureStationTextField.setText(datedFlight.getFrom());
        generateFlightsView.departureTimeTextField.setText(datedFlight.getDepartureTime().format(HOUR_MINUTE_FORMATTER));
        generateFlightsView.departureDateTextField.setText(datedFlight.getDepartureDate().format(BUTTON_DATE_FORMATTER));
        generateFlightsView.endOfSeriesTextField.setText(datedFlight.getDepartureDate().format(BUTTON_DATE_FORMATTER));
        generateFlightsView.arrivalStationTextField.setText(datedFlight.getTo());
        generateFlightsView.arrivalTimeTextField.setText(datedFlight.getArrivalTime().format(HOUR_MINUTE_FORMATTER));
        generateFlightsView.aircraftTextField.setText(datedFlight.getAircraft());


//        generateFlights2View.departureTimeTextField.setFormatterFactory(new DefaultFormatterFactory(createFormatter("##:##")));
//        generateFlights2View.arrivalTimeTextField.setFormatterFactory(new DefaultFormatterFactory(createFormatter("##:##")));

        JPanel contentPanel = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, generateFlightsView.rootPanel, createFlightsView.rootPanel);
        splitPane.setDividerLocation(400);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        setContentPane(contentPanel);

        setModal(true);
        setIconImage(ICON);
        setResizable(true);
        setTitle("Create Flights");
        setPreferredSize(new Dimension(800, 600));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        createFlightsView.newFlightsList.setCellRenderer(new NewFlightListCellRenderer());
        createFlightsView.newFlightsList.setFont(MONOSPACED_FONT);
        createFlightsView.newFlightsList.setModel(new DefaultListModel<>());
        createFlightsView.newFlightsList.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                createFlightsView.createButton.setEnabled(createFlightsView.newFlightsList.getModel().getSize() > 0);
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                createFlightsView.createButton.setEnabled(createFlightsView.newFlightsList.getModel().getSize() > 0);
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                createFlightsView.createButton.setEnabled(createFlightsView.newFlightsList.getModel().getSize() > 0);
            }
        });

        createFlightsView.createButton.addActionListener(e -> {
            //update model
            DefaultListModel<DatedFlight> defaultListModel = (DefaultListModel<DatedFlight>) createFlightsView.newFlightsList.getModel();
            for (int i = 0; i < defaultListModel.size(); i++) {
                DatedFlight df = defaultListModel.get(i);
                if (df.isConflict()) {
                    continue;
                }
                //diff
                auraModel.getCreatedFlights().add(df);
                //named
                SortedSet<DatedFlight> namedFlights = auraModel.getNamedFlightsMap().get(df.getFlightNumberFull());
                if (namedFlights == null) {
                    namedFlights = new TreeSet<>();
                }
                namedFlights.add(df);
                auraModel.getNamedFlightsMap().put(df.getFlightNumberFull(), namedFlights);
                //dated
                SortedMap<Integer, SortedSet<DatedFlight>> dateFlights = auraModel.getDatedFlightsMap().get(df.getDepartureDate());
                if (dateFlights == null) {
                    dateFlights = new TreeMap<>();
                }
                SortedSet<DatedFlight> hourFlights = dateFlights.get(df.getDepartureTime().getHour());
                if (hourFlights == null) {
                    hourFlights = new TreeSet<>();
                }
                hourFlights.add(df);
                //
                dateFlights.put(df.getDepartureTime().getHour(), hourFlights);
                auraModel.getDatedFlightsMap().put(df.getDepartureDate(), dateFlights);
                //
                mainFrameView.getApplicationEventPublisher().publishEvent(new AppEvent(this, EventType.FLIGHTS_UPDATED));
                dispose();
            }
        });

        pack();

        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(mainFrameView);
        });
    }

    private BitSet getSelectedWeekDays() {
        BitSet bitSet = new BitSet();
        if (selectMoButton.isSelected()) bitSet.set(1);
        if (selectTuButton.isSelected()) bitSet.set(2);
        if (selectWeButton.isSelected()) bitSet.set(3);
        if (selectThButton.isSelected()) bitSet.set(4);
        if (selectFrButton.isSelected()) bitSet.set(5);
        if (selectSaButton.isSelected()) bitSet.set(6);
        if (selectSuButton.isSelected()) bitSet.set(7);
        return bitSet;
    }

    public DatedFlight getDatedFlight(AuraModel auraModel, LocalDate departureDate) {
        DatedFlight newDatedFlight = new DatedFlight();
        newDatedFlight.setDepartureDate(departureDate);
        newDatedFlight.setAirlineDesignator(generateFlightsView.flightNumberTextField.getText().substring(0, 2));
        newDatedFlight.setFlightNumber(generateFlightsView.flightNumberTextField.getText().substring(2));
        newDatedFlight.setFrom(generateFlightsView.departureStationTextField.getText());
        newDatedFlight.setDepartureTime(LocalTime.parse(generateFlightsView.departureTimeTextField.getText()));
        newDatedFlight.setTo(generateFlightsView.arrivalStationTextField.getText());
        newDatedFlight.setArrivalTime(LocalTime.parse(generateFlightsView.arrivalTimeTextField.getText()));
        newDatedFlight.setAircraft(generateFlightsView.aircraftTextField.getText());

        SortedSet<DatedFlight> datedFlights = auraModel.getNamedFlightsMap().get(newDatedFlight.getFlightNumberFull());
        if (datedFlights != null) {
            newDatedFlight.setStatus(datedFlights.contains(newDatedFlight) ? FlightStatus.CONFLICT : FlightStatus.CREATED);
        } else {
            newDatedFlight.setStatus(FlightStatus.CREATED);
        }

        return newDatedFlight;
    }

    public static void main(String[] args) {
        new CreateFlightsDialog(null, buildFlight(LocalDate.now().getYear()), new AuraModel()).setVisible(true);
    }

}

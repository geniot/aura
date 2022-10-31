package io.github.geniot.aura.view;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.view.calendar.HourButton;
import io.github.geniot.aura.view.listcellrenderer.HourFlightListCellRenderer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static io.github.geniot.aura.util.Utils.MONOSPACED_FONT;

@Component
public class HoursView extends JPanel {
    public JPanel rootPanel;
    public JList<DatedFlight> flightsList;
    public JPanel hoursPanel;

    public JLabel titleLabel;

    public Set<HourButton> hourButtons = new HashSet<>();

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout(0, 0));
        titlePanel.setPreferredSize(new Dimension(30, 30));
        titlePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        rootPanel.add(titlePanel, BorderLayout.NORTH);

        titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(0);
        titleLabel.setHorizontalTextPosition(0);
        titleLabel.setText(" ");
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        hoursPanel = new JPanel();
        hoursPanel.setLayout(new FormLayout("fill:30px:grow", Utils.dup("center:d:grow", ",", 24)));
//        hoursPanel.setMinimumSize(new Dimension(-1, -1));
//        hoursPanel.setPreferredSize(new Dimension(30, -1));
        centerPanel.add(hoursPanel, BorderLayout.WEST);

        CellConstraints cc = new CellConstraints();
        for (int i = 0; i < 24; i++) {
            HourButton hourButton = new HourButton(LocalTime.of(i,0,0));
            hourButtons.add(hourButton);
            hoursPanel.add(hourButton, cc.xy(1, i + 1, CellConstraints.FILL, CellConstraints.FILL));
        }

        final JScrollPane hourFlightsScrollPane = new JScrollPane();
        centerPanel.add(hourFlightsScrollPane, BorderLayout.CENTER);

        flightsList = new JList<>();
        flightsList.setFont(MONOSPACED_FONT);
        flightsList.setCellRenderer(new HourFlightListCellRenderer());
        flightsList.setFocusable(false);
        flightsList.setRequestFocusEnabled(false);
        flightsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        hourFlightsScrollPane.setViewportView(flightsList);
        add(rootPanel, BorderLayout.CENTER);
    }
}

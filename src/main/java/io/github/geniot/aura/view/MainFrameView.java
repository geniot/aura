package io.github.geniot.aura.view;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

@Data
@Component
public class MainFrameView extends JFrame {

    public static final Image ICON = new ImageIcon(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("favicon.png"))).getImage();
    @Autowired
    private ToolbarView toolbarView;

    @Autowired
    public StatusBarView statusBarView;

    @Autowired
    private CalendarView calendarView;

    @Autowired
    private HoursView hoursView;

    @Autowired
    private FlightView flightView;

    @Autowired
    private EditorView editorView;

    @Autowired
    private DiffView diffView;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public JPanel contentPanel = new JPanel();

    public JSplitPane splitPane2;

    public JSplitPane splitPane1;

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());

        contentPanel.setLayout(new BorderLayout());

        JPanel editorLeftPanel = new JPanel();
        editorLeftPanel.setLayout(new BorderLayout());

        JPanel calendarRightPanel = new JPanel();
        calendarRightPanel.setLayout(new BorderLayout());

        editorLeftPanel.add(flightView, BorderLayout.CENTER);
        editorLeftPanel.add(editorView, BorderLayout.SOUTH);

        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, diffView, hoursView);

        calendarRightPanel.add(calendarView, BorderLayout.NORTH);
        calendarRightPanel.add(splitPane2, BorderLayout.CENTER);

        splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorLeftPanel, calendarRightPanel);
        contentPanel.add(splitPane1, BorderLayout.CENTER);

        add(toolbarView, BorderLayout.NORTH);
        add(statusBarView, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void showView() {
        this.pack();
        this.setVisible(true);
    }
}

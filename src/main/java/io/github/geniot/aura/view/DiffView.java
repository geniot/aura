package io.github.geniot.aura.view;

import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.view.listcellrenderer.DiffListCellRenderer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

import static io.github.geniot.aura.util.Utils.MONOSPACED_FONT;

@Component
public class DiffView extends JPanel {

    public JButton revertButton;
    public JPanel rootPanel;
    public JTabbedPane tabbedPane;
    public JList<DatedFlight> deleteList;
    public JList<DatedFlight> updateList;
    public JList<DatedFlight> createList;
    public JPanel titlePanel;
    public JLabel titleLabel;

    public DiffTabTitlePanel createDiffTabTitlePanel = new DiffTabTitlePanel(Utils.GREEN);
    public DiffTabTitlePanel updateDiffTabTitlePanel = new DiffTabTitlePanel(Utils.YELLOW);
    public DiffTabTitlePanel deleteDiffTabTitlePanel = new DiffTabTitlePanel(Utils.GRAY);

    @PostConstruct
    public void init() {
        setLayout(new BorderLayout());
        add(rootPanel, BorderLayout.CENTER);

        tabbedPane.setTabComponentAt(0, createDiffTabTitlePanel);
        tabbedPane.setTabComponentAt(1, updateDiffTabTitlePanel);
        tabbedPane.setTabComponentAt(2, deleteDiffTabTitlePanel);

        initList(createList, new DiffListCellRenderer());
        initList(updateList, new DiffListCellRenderer());
        initList(deleteList, new DiffListCellRenderer());
    }

    private void initList(JList<DatedFlight> list, ListCellRenderer<DatedFlight> renderer) {
        list.setFont(MONOSPACED_FONT);
        list.setCellRenderer(renderer);
        list.setFocusable(false);
        list.setRequestFocusEnabled(false);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
}

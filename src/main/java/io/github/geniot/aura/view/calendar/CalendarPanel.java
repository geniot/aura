package io.github.geniot.aura.view.calendar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.model.AuraModel;
import lombok.Getter;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

@Getter
public class CalendarPanel extends JPanel {

    private Set<DateButton> dateButtonSet = new HashSet<>();

    public CalendarPanel(int year, AuraModel auraModel) {
        super();
        setLayout(new FormLayout("fill:m:noGrow," + Utils.dup("fill:m:grow", ",", 12),
                "fill:m:grow"));

        CellConstraints cc = new CellConstraints();
        add(new WeekDaysPanel(), cc.xy(1, 1));
        for (int i = 0; i < 12; i++) {
            MonthPanel monthPanel = new MonthPanel(year, i + 1, auraModel, dateButtonSet);
            add(monthPanel, cc.xy(i + 2, 1));
        }
    }
}

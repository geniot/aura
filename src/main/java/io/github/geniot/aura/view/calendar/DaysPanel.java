package io.github.geniot.aura.view.calendar;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import io.github.geniot.aura.util.Utils;
import io.github.geniot.aura.model.AuraModel;
import lombok.Data;
import lombok.Getter;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;

@Getter
public class DaysPanel extends JPanel {


    public DaysPanel(int year, int month, AuraModel auraModel, Set<DateButton> dateButtonSet) {
        super();

        List<DateButtonContainer> list = new ArrayList<>();
        CellConstraints cc = new CellConstraints();
        LocalDate localDate = LocalDate.of(year, month, 1);
        int col = 1;
        while (localDate.getMonthValue() == month) {
            int row = localDate.getDayOfWeek().getValue();

            DateButton dateButton = new DateButton(localDate);
            dateButton.setEnabled(auraModel.getDatedFlightsMap().containsKey(localDate));
            dateButtonSet.add(dateButton);

            list.add(new DateButtonContainer(dateButton, col, row));

            //if last day of the month is Sunday, there's no new column
            if (localDate.getDayOfWeek().getValue() == 7 &&
                    localDate.plusDays(1).getMonthValue() == month) {
                ++col;
            }
            localDate = localDate.plusDays(1);
        }

        setLayout(new FormLayout(
                Utils.dup("fill:m:grow", ",", col),
                Utils.dup("fill:m:grow", ",", 7)
        ));
        for (DateButtonContainer dbc : list) {
            add(dbc.dateButton, cc.xy(dbc.col, dbc.row));
        }

    }

    @Data
    private static class DateButtonContainer {
        DateButton dateButton;
        int col;
        int row;

        public DateButtonContainer(DateButton dateButton, int col, int row) {
            this.dateButton = dateButton;
            this.col = col;
            this.row = row;
        }
    }


}

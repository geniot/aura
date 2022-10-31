package io.github.geniot.aura.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class SelectButton extends JToggleButton {

    private int dayOfWeek = 0;

    public SelectButton(String label, int dow) {
        super();
        this.dayOfWeek = dow;
        setMinimumSize(new Dimension(30, 30));
        setMaximumSize(new Dimension(30, 30));
        setPreferredSize(new Dimension(30, 30));
        setFocusPainted(false);
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setText(label);
    }
}

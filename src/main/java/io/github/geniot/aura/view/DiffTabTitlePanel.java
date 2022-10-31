package io.github.geniot.aura.view;

import lombok.Data;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

@Data
public class DiffTabTitlePanel extends JPanel {
    private JLabel titleLabel = new JLabel();

    public DiffTabTitlePanel(Color color) {
        super();
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JPanel colorButton = new JPanel();
        colorButton.setBorder(new LineBorder(Color.GRAY));
        colorButton.setFocusable(false);
        colorButton.setEnabled(true);
        int size = 13;
        colorButton.setMinimumSize(new Dimension(size, size));
        colorButton.setMaximumSize(new Dimension(size, size));
        colorButton.setPreferredSize(new Dimension(size, size));
        colorButton.setBackground(color);

        setOpaque(false);

        add(colorButton);
        add(Box.createHorizontalStrut(5));
        add(titleLabel);
    }
}

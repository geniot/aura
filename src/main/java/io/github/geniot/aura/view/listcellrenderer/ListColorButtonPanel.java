package io.github.geniot.aura.view.listcellrenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ListColorButtonPanel extends JPanel {

    protected JButton colorButton = new JButton("");
    public ListColorButtonPanel(){
        super();

        int size = 13;
        colorButton.setPreferredSize(new Dimension(size, size));
        colorButton.setMinimumSize(new Dimension(size, size));
        colorButton.setMaximumSize(new Dimension(size, size));
        colorButton.setBorder(new LineBorder(Color.GRAY));

        setLayout(new BorderLayout());
        add(colorButton, BorderLayout.CENTER);
        setBorder(new EmptyBorder(3, 3, 3, 3));
    }
}

package io.github.geniot.aura.view.calendar;

import io.github.geniot.aura.util.LafManager;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import static io.github.geniot.aura.util.Utils.*;

@Getter
public class DateButton extends JToggleButton {
    Color[] colors;

    private LocalDate localDate;

    BasicStroke selectedStroke = new BasicStroke(2);
    BasicStroke regularStroke = new BasicStroke(1);

    public DateButton(LocalDate localDate) {
        super(String.format("%02d", localDate.getDayOfMonth()));
        this.localDate = localDate;
        setMinimumSize(new Dimension(10, 10));
        setPreferredSize(new Dimension(30, 30));
        setMargin(new Insets(0, 0, 0, 0));

        this.colors = new Color[]{null, null, null};
        setFocusable(false);
        setFocusPainted(false);

        setEnabled(false);

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();

        int alpha = isEnabled() ? 100 : 30;

        for (int i = 0; i < 3; i++) {
            if (colors[i] != null) {
                Color c = new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), alpha);
                g2.setPaint(c);
                g2.fillRect(0, i * (height / 3), width, height / 3);
            }
        }

        paintChildren(g2);

        if (isSelected()) {
            g2.setStroke(selectedStroke);
            g2.setPaint(Color.BLACK);
            g2.drawRect(1, 1, width - 3, height - 3);
        } else {
            g2.setStroke(regularStroke);
            g2.setPaint(Color.GRAY);
            g2.drawRect(1, 1, width - 2, height - 2);
        }

        if (isEnabled()) {
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;

        buttonPanel.add(new DateButton(LocalDate.of(1, 1, 1)), gbc);
        buttonPanel.add(new DateButton(LocalDate.of(1, 1, 2)), gbc);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(buttonPanel);
        frame.setSize(400, 400);
        frame.setLocation(300, 300);

        LafManager.setLAF(null, frame);

        frame.setVisible(true);
    }

    public void setCreated(boolean isCreated) {
        this.colors[0] = isCreated ? GREEN : null;
        repaint();
    }

    public void setUpdated(boolean isUpdated) {
        this.colors[1] = isUpdated ? YELLOW : null;
        repaint();
    }

    public void setDeleted(boolean isDeleted) {
        this.colors[2] = isDeleted ? GRAY : null;
        repaint();
    }
}

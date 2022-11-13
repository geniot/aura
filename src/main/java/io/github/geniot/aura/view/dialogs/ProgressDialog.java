package io.github.geniot.aura.view.dialogs;

import io.github.geniot.aura.action.Progressable;
import io.github.geniot.aura.view.MainFrameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static io.github.geniot.aura.view.MainFrameView.ICON;

public class ProgressDialog extends JDialog implements Progressable {
    private JPanel contentPane;
    public JProgressBar progressBar;

    private boolean isCancelRequested = false;

    public ProgressDialog(MainFrameView mainFrameView) {
        setIconImage(ICON);
        setContentPane(contentPane);
        setModal(true);
        setTitle("Loading Repository");
        setResizable(false);
        setSize(new Dimension(200, 80));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                isCancelRequested = true;
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

//        pack();
        setLocationRelativeTo(mainFrameView);

        progressBar.setIndeterminate(true);

    }

    public static void main(String[] args) {
        ProgressDialog dialog = new ProgressDialog(null);
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onCancel() {
        dispose();
    }

    @Override
    public void setProgress(int progress) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(progress);
    }

    @Override
    public void setMax(int max) {
        progressBar.setMaximum(max);
    }

    @Override
    public void append(String text) {

    }

    @Override
    public boolean isCancelRequested() {
        return this.isCancelRequested;
    }

    @Override
    public void close() {
        dispose();
    }

}

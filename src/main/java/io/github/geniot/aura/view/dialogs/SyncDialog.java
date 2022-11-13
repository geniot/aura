package io.github.geniot.aura.view.dialogs;

import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.view.MainFrameView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

import static io.github.geniot.aura.view.MainFrameView.ICON;

public class SyncDialog extends JDialog {
    private MainFrameView mainFrameView;

    private SyncView syncView = new SyncView();

    public SyncDialog(MainFrameView mfv, AuraModel auraModel) {
        this.mainFrameView = mfv;
        JPanel contentPanel = new JPanel(new BorderLayout());

        contentPanel.add(syncView.rootPanel, BorderLayout.CENTER);
        setContentPane(contentPanel);

        setModal(true);
        setIconImage(ICON);
        setResizable(true);
        setTitle("Sync");
        setPreferredSize(new Dimension(800, 600));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        int padding = 3;
        syncView.messageTextField.setBorder(BorderFactory.createCompoundBorder(
                syncView.messageTextField.getBorder(),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));

        syncView.messageTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                onMessageChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onMessageChange();
            }

            public void insertUpdate(DocumentEvent e) {
                onMessageChange();
            }
        });

        syncView.closeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        pack();

        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(mainFrameView);
        });
    }

    private void onMessageChange() {
        syncView.syncButton.setEnabled(StringUtils.isNotBlank(syncView.messageTextField.getText()));
    }
}

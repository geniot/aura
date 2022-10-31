package io.github.geniot.aura.view;

import io.github.geniot.aura.AuraApplication;
import io.github.geniot.aura.model.Prop;
import io.github.geniot.aura.util.LafManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.prefs.Preferences;

import static io.github.geniot.aura.util.LafManager.setLAF;
import static io.github.geniot.aura.view.MainFrameView.ICON;

public class PreferencesDialog extends JDialog {
    public JPanel contentPane;
    public JButton buttonOK;
    public JButton buttonCancel;
    public JComboBox<String> themeComboBox;

    private MainFrameView mainFrameView;

    public PreferencesDialog(MainFrameView mainFrameView) {
        this.mainFrameView = mainFrameView;
        setContentPane(contentPane);
        setModal(true);
        setIconImage(ICON);
        setResizable(false);
        setTitle("Preferences");
        setPreferredSize(new Dimension(400, 400));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> dispose());

        themeComboBox.addActionListener(e -> setLAF((String) themeComboBox.getSelectedItem(), PreferencesDialog.this));

        themeComboBox.setModel(new DefaultComboBoxModel<>(LafManager.LAFS.keySet().toArray(new String[0])));
        themeComboBox.setSelectedItem(Preferences.userRoot().node(AuraApplication.class.getName()).get(Prop.PROP_LAF.name(), "Luna"));

        pack();

        SwingUtilities.invokeLater(() -> {
            setLocationRelativeTo(mainFrameView);
        });
    }

    private void onOK() {
        String oldLaf = Preferences.userRoot().node(AuraApplication.class.getName()).get(Prop.PROP_LAF.name(), "Luna");
        String newLaf = Objects.requireNonNull(themeComboBox.getSelectedItem()).toString();

        if (!oldLaf.equals(newLaf)) {
            Preferences.userRoot().node(AuraApplication.class.getName()).put(Prop.PROP_LAF.name(), newLaf);
            setLAF(newLaf, mainFrameView);
        }
        dispose();
    }
}

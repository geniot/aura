package io.github.geniot.aura.action;

import io.github.geniot.aura.AuraApplication;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.Prop;
import io.github.geniot.aura.view.MainFrameView;
import io.github.geniot.aura.view.ProgressDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

@Component
public class LoadRepositoryAction implements ActionListener {

    @Autowired
    MainFrameView mainFrameView;
    @Autowired
    AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public void actionPerformed(ActionEvent e) {
        File folder = new File(Preferences.userRoot().node(AuraApplication.class.getName()).get(Prop.LAST_OPEN_DIR.name(), System.getProperty("user.home")));
        JFileChooser fc;
        if (folder.exists()) {
            fc = new JFileChooser(folder.getParentFile());
        } else {
            fc = new JFileChooser(System.getProperty("user.home"));
        }
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Folder in YYYY format";
            }
        });

        int returnVal = fc.showOpenDialog(mainFrameView);

        if (fc.getSelectedFile() != null) {
            Preferences.userRoot().node(AuraApplication.class.getName()).put(Prop.LAST_OPEN_DIR.name(), fc.getSelectedFile().getAbsolutePath());
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadRepository(fc.getSelectedFile());
        }
    }

    public void loadRepository(File selectedFile) {
        try {
            String yearFolder = selectedFile.getName();
            if (yearFolder.length() != 4) {
                throw new Exception();
            }
            auraModel.setRepositoryYear(Integer.parseInt(yearFolder));

            Preferences.userRoot().node(AuraApplication.class.getName()).putBoolean(Prop.SHOULD_OPEN.name(), true);
            auraModel.setPathToRepository(selectedFile.getAbsolutePath());

            ProgressDialog progressDialog = new ProgressDialog(mainFrameView);
            progressDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    super.windowOpened(e);
                    LoadRepositoryTask loadRepositoryTask = new LoadRepositoryTask(progressDialog, auraModel, applicationEventPublisher);
                    loadRepositoryTask.addPropertyChangeListener(new WorkerListener(progressDialog));
                    loadRepositoryTask.execute();
                }
            });

            progressDialog.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrameView,
                    "Folder name should be in YYYY format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

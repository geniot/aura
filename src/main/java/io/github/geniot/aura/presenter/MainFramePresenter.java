package io.github.geniot.aura.presenter;

import io.github.geniot.aura.AuraApplication;
import io.github.geniot.aura.util.LafManager;
import io.github.geniot.aura.action.LoadRepositoryAction;
import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.Prop;
import io.github.geniot.aura.view.MainFrameView;
import io.github.geniot.aura.view.StatusBarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import static io.github.geniot.aura.view.MainFrameView.ICON;

@Component
public class MainFramePresenter implements ApplicationListener<AppEvent> {
    @Autowired
    private MainFrameView mainFrameView;

    @Autowired
    private StatusBarView statusBarView;
    @Autowired
    private AuraModel auraModel;

    @Autowired
    private LoadRepositoryAction loadRepositoryAction;

    @PostConstruct
    private void init() {

        mainFrameView.setTitle("Aura");

        try {
            mainFrameView.setIconImage(ICON);
            //macos
            try {
                final Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(ICON);
            } catch (final UnsupportedOperationException e) {
                System.out.println("The os does not support: 'taskbar.setIconImage'");
            } catch (final SecurityException e) {
                System.out.println("There was a security exception for: 'taskbar.setIconImage'");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            int width = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.WIDTH.name(), 800);
            int height = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.HEIGHT.name(), 600);
            width = Math.max(width, 800);
            height = Math.max(height, 600);
            mainFrameView.setPreferredSize(new Dimension(width, height));
        } catch (Exception ex) {
            mainFrameView.setPreferredSize(new Dimension(600, 600));
        }

        try {
            int posX = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.POS_X.name(), 50);
            int posY = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.POS_Y.name(), 50);
            posX = Math.max(posX, 50);
            posY = Math.max(posY, 50);
            mainFrameView.setLocation(posX, posY);
        } catch (Exception ex) {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            mainFrameView.setLocation(dim.width / 2 - mainFrameView.getSize().width / 2, dim.height / 2 - mainFrameView.getSize().height / 2);
        }

        mainFrameView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int extendedState = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.WINDOW_EXTENDED_STATE.name(), JFrame.NORMAL);
                        if (extendedState == JFrame.MAXIMIZED_BOTH){
                            mainFrameView.setExtendedState(extendedState);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    try {
                        int part = mainFrameView.getWidth() / 4;
                        int dividerLocation1 = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.DIVIDER_LOCATION_1.name(), part);
                        int dividerLocation2 = Preferences.userRoot().node(AuraApplication.class.getName()).getInt(Prop.DIVIDER_LOCATION_2.name(), part * 2);
                        mainFrameView.splitPane2.setDividerLocation(dividerLocation1);
                        mainFrameView.splitPane1.setDividerLocation(dividerLocation2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    boolean shouldOpen = Preferences.userRoot().node(AuraApplication.class.getName()).getBoolean(Prop.SHOULD_OPEN.name(), false);
                    if (shouldOpen) {
                        String pathToLastOpenDir = Preferences.userRoot().node(AuraApplication.class.getName()).get(Prop.LAST_OPEN_DIR.name(), null);
                        File lastOpenDir = new File(pathToLastOpenDir);
                        if (lastOpenDir.exists()) {
                            loadRepositoryAction.loadRepository(lastOpenDir);
                        }
                    }
                });
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                saveWindowState();

                Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.DIVIDER_LOCATION_1.name(), mainFrameView.splitPane2.getDividerLocation());
                Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.DIVIDER_LOCATION_2.name(), mainFrameView.splitPane1.getDividerLocation());

                Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.WINDOW_EXTENDED_STATE.name(), mainFrameView.getExtendedState());

                e.getWindow().dispose();
                System.exit(0);
            }

        });

        mainFrameView.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                saveWindowState();
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
                saveWindowState();
            }
        });

        mainFrameView.contentPanel.setVisible(false);
        statusBarView.setVisible(false);

    }

    private void saveWindowState() {
        if (mainFrameView.getExtendedState() == JFrame.NORMAL) {
            Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.WIDTH.name(), mainFrameView.getWidth());
            Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.HEIGHT.name(), mainFrameView.getHeight());
            Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.POS_X.name(), (int) mainFrameView.getLocation().getX());
            Preferences.userRoot().node(AuraApplication.class.getName()).putInt(Prop.POS_Y.name(), (int) mainFrameView.getLocation().getY());
        }
    }

    public void showMainFrameView() {
        LafManager.setLAF(Preferences.userRoot().node(AuraApplication.class.getName()).get(Prop.PROP_LAF.name(), "Luna"), mainFrameView);
        mainFrameView.showView();
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        if (event.getEventType().equals(EventType.REPOSITORY_LOADED)) {
            mainFrameView.setTitle(auraModel.getPathToRepository());
            mainFrameView.contentPanel.setVisible(true);
            statusBarView.setVisible(true);
        }
        if (event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
            mainFrameView.setTitle("Aura");
            mainFrameView.contentPanel.setVisible(false);
            statusBarView.setVisible(false);
        }
    }
}

package io.github.geniot.aura.action;

import io.github.geniot.aura.AuraApplication;
import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.model.Prop;
import io.github.geniot.aura.view.MainFrameView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

@Component
public class UnloadRepositoryAction implements ActionListener {

    @Autowired
    MainFrameView mainFrameView;
    @Autowired
    AuraModel auraModel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void actionPerformed(ActionEvent e) {
        Preferences.userRoot().node(AuraApplication.class.getName()).putBoolean(Prop.SHOULD_OPEN.name(), false);
        auraModel.reset();
        applicationEventPublisher.publishEvent(new AppEvent(this, EventType.REPOSITORY_UNLOADED));
    }

}

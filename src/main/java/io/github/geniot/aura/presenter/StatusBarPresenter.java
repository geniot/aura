package io.github.geniot.aura.presenter;

import io.github.geniot.aura.event.AppEvent;
import io.github.geniot.aura.model.EventType;
import io.github.geniot.aura.model.AuraModel;
import io.github.geniot.aura.view.StatusBarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class StatusBarPresenter implements ApplicationListener<AppEvent> {
    @Autowired
    StatusBarView statusBarView;

    @Autowired
    AuraModel auraModel;

    @PostConstruct
    private void init() {
    }

    @Override
    public void onApplicationEvent(AppEvent event) {
        if (event.getEventType().equals(EventType.REPOSITORY_LOADED)) {
            String sizeStr = NumberFormat.getNumberInstance(Locale.US).format(auraModel.getFlightsCount());
            String text = sizeStr + " flights";
            statusBarView.leftStatusLabel.setText(text);

            long heapSize = Runtime.getRuntime().totalMemory();

        }
        if (event.getEventType().equals(EventType.REPOSITORY_UNLOADED)) {
        }
    }
}

package io.github.geniot.aura.event;

import io.github.geniot.aura.model.EventType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppEvent extends ApplicationEvent {
    private EventType eventType;

    public AppEvent(Object source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }
}

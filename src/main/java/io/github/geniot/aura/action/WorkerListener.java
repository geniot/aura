package io.github.geniot.aura.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WorkerListener implements PropertyChangeListener {
    private final Progressable progressable;

    public WorkerListener(Progressable progressable) {
        this.progressable = progressable;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() != null && evt.getPropertyName().equals("progress")) {
            int progress = (int)evt.getNewValue();
            progressable.setProgress(progress);
        }
    }
}
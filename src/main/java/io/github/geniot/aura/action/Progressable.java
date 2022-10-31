package io.github.geniot.aura.action;

public interface Progressable {
    void setProgress(int progress);

    void setMax(int max);
    void append(String text);

    boolean isCancelRequested();

    void close();
}

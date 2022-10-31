package io.github.geniot.aura;

import io.github.geniot.aura.presenter.MainFramePresenter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class AuraApplication {
    public static void main(String[] args) {
        try {
            final AbstractApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
            applicationContext.getBean(MainFramePresenter.class).showMainFrameView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

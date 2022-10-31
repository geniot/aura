package io.github.geniot.aura.util;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class LafManager {
    public static final Map<String, String> LAFS;

    static {
        Map<String, String> aMap = new LinkedHashMap<>();
        aMap.put("FlatLight", "com.formdev.flatlaf.FlatLightLaf");
        aMap.put("FlatDark", "com.formdev.flatlaf.FlatDarkLaf");
        aMap.put("FlatDarcula", "com.formdev.flatlaf.FlatDarculaLaf");
        aMap.put("FlatIntelliJ", "com.formdev.flatlaf.FlatIntelliJLaf");
        LAFS = Collections.unmodifiableMap(aMap);
    }

    public static final String DEFAULT_LAF = "FlatLight";


    public static void setLAF(String lafName, Component component) {
        String lafClassName = LAFS.get(lafName);
        try {
            if (lafClassName == null) {
                lafClassName = LAFS.get(DEFAULT_LAF);
            }
            initFlat(lafClassName);
            UIManager.setLookAndFeel(lafClassName);
            SwingUtilities.updateComponentTreeUI(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initFlat(String lafClassName) {
        if (lafClassName.equals("FlatLight")) {
            FlatLightLaf.setup();
        }
        if (lafClassName.equals("FlatDark")) {
            FlatDarkLaf.setup();
        }
        if (lafClassName.equals("FlatDarcula")) {
            FlatDarculaLaf.setup();
        }
        if (lafClassName.equals("FlatIntelliJ")) {
            FlatIntelliJLaf.setup();
        }
    }
}

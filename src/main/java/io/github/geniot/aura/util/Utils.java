package io.github.geniot.aura.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.geniot.aura.model.DatedFlight;
import io.github.geniot.aura.model.FlightStatus;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Utils {

    public static final String[] MONTH_LABELS = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("FlightStatusPropertyFilter", new FlightStatusPropertyFilter());
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setFilterProvider(filters)
            .registerModule(new JavaTimeModule())
            .registerModule(new Jdk8Module());

    //https://www.rapidtables.com/web/color/index.html
    public static final Color GRAY = new Color(169, 169, 169, 100);
    public static final Color GREEN = new Color(124, 252, 0, 100);
    public static final Color YELLOW = new Color(255, 215, 0, 100);
    public static final Color RED = new Color(255, 0, 0, 100);

    public static final String SPACER = "  ";
    public static final String SHORT_SPACER = " ";
    public static final String HYPHEN = "/";
    public static final String DIFF_ARROW = "➔";
    public static final DateTimeFormatter LIST_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-EE");
    public static final DateTimeFormatter HOUR_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter BUTTON_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final Font MONOSPACED_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font BIG_MONOSPACED_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 26);
    public static final int SECONDS_PER_DAY = (int) TimeUnit.DAYS.toSeconds(1);

    public static String dup(String input, String delimiter, int count) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            stringBuilder.append(input);
            if (i < count - 1) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }

    public static Set<LocalDate> getDates(Set<DatedFlight> datedFlights) {
        Set<LocalDate> set = new HashSet<>();
        for (DatedFlight datedFlight : datedFlights) {
            set.add(datedFlight.getDepartureDate());
        }
        return set;
    }

    public static Set<LocalTime> getTimes(Set<DatedFlight> datedFlights, LocalDate selectedDate) {
        Set<LocalTime> set = new HashSet<>();
        for (DatedFlight datedFlight : datedFlights) {
            if (datedFlight.getDepartureDate().equals(selectedDate)) {
                LocalTime localTime = LocalTime.of(datedFlight.getDepartureTime().getHour(), 0, 0);
                set.add(localTime);
            }
        }
        return set;
    }

    public static Color[] getRandomColorSet() {
        Random random = new Random();
        List<Color> colorList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int randomColor = random.nextInt(4);
            colorList.add(null);
//            switch (randomColor) {
//                case 0 -> colorList.add(null);
//                case 1 -> colorList.add(DateButton.GREEN);
//                case 2 -> colorList.add(DateButton.YELLOW);
//                case 3 -> colorList.add(DateButton.GRAY);
//            }
        }
        return colorList.toArray(new Color[0]);
    }

    public static MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    public static DefaultListModel<DatedFlight> listModelFromSet(SortedSet<DatedFlight> set) {
        DefaultListModel<DatedFlight> defaultListModel = new DefaultListModel<>();
        defaultListModel.addAll(set);
        return defaultListModel;
    }

    public static String getDiff(LocalTime origValue, LocalTime newValue, FlightStatus flightStatus) {
        if (newValue == null || origValue.equals(newValue) || flightStatus.equals(FlightStatus.DELETED)) {
            return origValue.toString();
        } else {
            return origValue + DIFF_ARROW + newValue;
        }
    }

    public static String getDurationStr(LocalTime lt1, LocalTime lt2) {
        int travelTimeMinutes = getDuration(lt1, lt2);
        int hours = travelTimeMinutes / 60;
        int minutes = travelTimeMinutes % 60;
        return StringUtils.leftPad(String.valueOf(hours), 2, '0') +
                "H" +
                StringUtils.leftPad(String.valueOf(minutes), 2, '0') +
                "M" +
                (lt1.isAfter(lt2) ? "+1" : "  ")
                ;
    }

    public static int getDuration(LocalTime lt1, LocalTime lt2) {
        if (lt1.equals(lt2)) {
            return 0;
        }
        if (lt1.isBefore(lt2)) {
            return (int) MINUTES.between(lt1, lt2);
        } else {
            return (lt2.toSecondOfDay() + (SECONDS_PER_DAY - lt1.toSecondOfDay())) / 60;
        }
    }

    public static DatedFlight buildFlight(int year) {
        DatedFlight datedFlight = new DatedFlight();
        datedFlight.setFrom("AMS");
        datedFlight.setTo("JFK");
        datedFlight.setDepartureDate(LocalDate.now().withYear(year));
        datedFlight.setDepartureTime(LocalTime.now());
        datedFlight.setArrivalTime(LocalTime.now());
        datedFlight.setAirlineDesignator("KL");
        datedFlight.setFlightNumber("1234");
        datedFlight.setAircraft("321");
        return datedFlight;
    }
}

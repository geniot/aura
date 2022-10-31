package io.github.geniot.aura.util;

import io.github.geniot.aura.model.CompressedFlight;
import io.github.geniot.aura.model.DatedFlight;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.github.geniot.aura.util.Utils.OBJECT_MAPPER;

public class FlightsExtractor {
    static final String INPUT_FILE = "data/Skyteam_Timetable_html.zip";
    static final String[] MONTHS = new String[]{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};
    static final String[] WEEKDAYS = new String[]{"mo", "tu", "we", "th", "fr", "sa", "su"};

    static final Pattern FROM_TO_PATTERN = Pattern.compile(">([A-Z]{3})</span>");
    static final Pattern SPAN_VALUE_PATTERN = Pattern.compile("<span[^>]+>([^<]+)</span>");
    static final DecimalFormat DOUBLE_ZERO_FORMAT = new DecimalFormat("00");

    /**
     * 1643977 dated flights
     * Min departure date: 2022-09-01
     * Max departure date: 2022-11-30
     * <p>
     * 6575908 dated flights
     * Min departure date: 2022-01-01
     * Max departure date: 2022-12-30
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            ZipFile zipFile = new ZipFile(INPUT_FILE);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            ZipEntry entry = entries.nextElement();
            InputStream stream = zipFile.getInputStream(entry);
            byte[] bytes = IOUtils.toByteArray(stream);

            String str = new String(bytes, StandardCharsets.UTF_8);
            String[] lines = str.split("\n");

            List<String> paragraphs = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("<p ")) {
                    paragraphs.add(line.trim());
                }
            }

            List<Integer> fromTosIndexes = getFromTos(paragraphs);

            List<List<String>> fromTos = new ArrayList<>();

            for (int i = 0; i < fromTosIndexes.size(); i++) {
                int from = fromTosIndexes.get(i);
                int to = paragraphs.size();
                if (i < fromTosIndexes.size() - 1) {
                    to = fromTosIndexes.get(i + 1);
                }
                fromTos.add(extract(paragraphs, from, to));
            }

            List<CompressedFlight> compressedFlights = new ArrayList<>();
            for (List<String> fromTo : fromTos) {
                compressedFlights.addAll(parseCompressed(fromTo));
            }

            FlightConverter flightConverter = new FlightConverter();

            List<DatedFlight> datedFlights = new ArrayList<>();
            for (CompressedFlight compressedFlight : compressedFlights) {
                datedFlights.addAll(flightConverter.toDated(compressedFlight));
            }

            printMinMax(datedFlights);

            //@todo: comment out if necessary
            copyFlights(datedFlights, 1, 9);
            copyFlights(datedFlights, 2, 10);
            copyFlights(datedFlights, 3, 11);
            copyFlights(datedFlights, 4, 9);
            copyFlights(datedFlights, 5, 10);
            copyFlights(datedFlights, 6, 11);
            copyFlights(datedFlights, 7, 9);
            copyFlights(datedFlights, 8, 10);
            copyFlights(datedFlights, 12, 11);

            printMinMax(datedFlights);

            //check unique constraint, remove duplicates
            int skippedFlightsCount = 0;
            Set<DatedFlight> uniqueDatedFlights = new HashSet<>();
            for (DatedFlight datedFlight : datedFlights) {

                //@todo: to generate test flights for following years set the value here
//                datedFlight.setDepartureDate(datedFlight.getDepartureDate().plusYears(1));

                if (uniqueDatedFlights.contains(datedFlight)) {
                    ++skippedFlightsCount;
                } else {
                    uniqueDatedFlights.add(datedFlight);
                }
            }

            System.out.println("Skipped duplicate flights: " + skippedFlightsCount);
            System.out.println("Unique dated flights: " + uniqueDatedFlights.size());

            //prepare for repository creation
            SortedMap<String, SortedSet<DatedFlight>> repositoryMap = new TreeMap<>();
            for (DatedFlight datedFlight : uniqueDatedFlights) {
                LocalDate departureDate = datedFlight.getDepartureDate();
                LocalTime departureTime = datedFlight.getDepartureTime();
                String key = departureDate.getYear() +
                        File.separator + DOUBLE_ZERO_FORMAT.format(departureDate.getMonthValue()) + "_" + MONTHS[departureDate.getMonthValue() - 1] +
                        File.separator + DOUBLE_ZERO_FORMAT.format(departureDate.getDayOfMonth()) + "_" + WEEKDAYS[departureDate.getDayOfWeek().getValue() - 1] +
                        File.separator + DOUBLE_ZERO_FORMAT.format(departureTime.getHour());
                SortedSet<DatedFlight> hourFlights = repositoryMap.get(key);
                if (hourFlights == null) {
                    hourFlights = new TreeSet<>();
                }
                hourFlights.add(datedFlight);
                repositoryMap.put(key, hourFlights);
            }

            for (String key : repositoryMap.keySet()) {
                SortedSet<DatedFlight> hourFlights = repositoryMap.get(key);
                File dir = new File(key.substring(0, key.lastIndexOf(File.separator)));
                File file = new File(key + ".json");
                dir.mkdirs();
                String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(hourFlights);
                FileUtils.write(file, json, StandardCharsets.UTF_8);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printMinMax(List<DatedFlight> datedFlights) {
        LocalDate minDepartureDate = datedFlights.get(0).getDepartureDate();
        LocalDate maxDepartureDate = datedFlights.get(0).getDepartureDate();

        for (DatedFlight datedFlight : datedFlights) {
            if (datedFlight.getDepartureDate().compareTo(minDepartureDate) < 0) {
                minDepartureDate = datedFlight.getDepartureDate();
            }
            if (datedFlight.getDepartureDate().compareTo(maxDepartureDate) > 0) {
                maxDepartureDate = datedFlight.getDepartureDate();
            }
        }

        System.out.println(datedFlights.size() + " dated flights");
        System.out.println("Min departure date: " + minDepartureDate);
        System.out.println("Max departure date: " + maxDepartureDate);
    }

    private static void copyFlights(List<DatedFlight> datedFlights, int toMonth, int fromMonth) throws Exception {
        System.out.println("Copy month from " + fromMonth + " to " + toMonth);
        List<DatedFlight> monthFlights = extractMonthFlights(datedFlights, fromMonth);
        for (DatedFlight datedFlight : monthFlights) {

            DatedFlight newFlight = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(datedFlight), DatedFlight.class);
//            DatedFlight newFlight = SerializationUtils.clone(datedFlight);
            LocalDate departureDate = newFlight.getDepartureDate();
            departureDate = departureDate.withMonth(toMonth);
            newFlight.setDepartureDate(departureDate);

            datedFlights.add(newFlight);
        }
    }

    private static List<DatedFlight> extractMonthFlights(List<DatedFlight> datedFlights, int month) {
        List<DatedFlight> monthFlights = new ArrayList<>();
        for (DatedFlight datedFlight : datedFlights) {
            if (datedFlight.getDepartureDate().getMonth().getValue() == month) {
                monthFlights.add(datedFlight);
            }
        }
        return monthFlights;
    }

    private static List<String> extract(List<String> paragraphs,
                                        Integer fromInclusive,
                                        Integer toExclusive) {
        List<String> fromTo = new ArrayList<>();
        for (int i = fromInclusive; i < toExclusive; i++) {
            fromTo.add(paragraphs.get(i));
        }
        return fromTo;
    }

    private static List<Integer> getFromTos(List<String> paragraphs) {
        List<Integer> fromTos = new ArrayList<>();
        for (int i = 0; i < paragraphs.size(); i++) {
            if (paragraphs.get(i).contains(">FROM:</span>")) {
                fromTos.add(i);
            }
        }
        return fromTos;
    }

    private static List<CompressedFlight> parseCompressed(List<String> fromTo) throws Exception {
        List<CompressedFlight> compressedFlights = new ArrayList<>();

        String from = extractFromTo(fromTo.get(1));
        String to = extractFromTo(fromTo.get(4));

        List<Integer> flightIndexes = getFlightIndexes(fromTo);
        for (int i = 1; i < flightIndexes.size(); i++) {//skipping table header
            int fromIndex = flightIndexes.get(i);
            int toIndex = fromTo.size();
            if (i < flightIndexes.size() - 1) {
                toIndex = flightIndexes.get(i + 1);
            }
            compressedFlights.add(toFlight(from, to, extract(fromTo, fromIndex, toIndex)));
        }
        return compressedFlights;

    }

    private static String extractFromTo(String fromToStr) throws Exception {
        Matcher m = FROM_TO_PATTERN.matcher(fromToStr);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new Exception("Couldn't find from/to in: " + fromToStr);
        }
    }

    private static CompressedFlight toFlight(String from, String to, List<String> flightParagraphs) throws Exception {
        CompressedFlight compressedFlight = new CompressedFlight();
        compressedFlight.setFrom(from);
        compressedFlight.setTo(to);
        List<String> spanValues = getSpanValues(flightParagraphs);
        if (spanValues.size() < 6) {
            throw new Exception("Expecting >=6 columns.");
        }
        compressedFlight.setStartDate(spanValues.get(0).split(" - ")[0].trim());
        compressedFlight.setEndDate(spanValues.get(0).split(" - ")[1].trim());
        compressedFlight.setDays(spanValues.get(1).trim());
        compressedFlight.setDepartureTime(spanValues.get(2).trim());
        compressedFlight.setArrivalTime(spanValues.get(3).trim());

        String flight = spanValues.get(4).trim().replaceAll("\\*", "");
        compressedFlight.setFlight(flight);
        if (flight.contains(" ") || flight.contains("*")) {
            throw new Exception(spanValues.get(4));
        }

        if (spanValues.size() > 6) {
            compressedFlight.setAircraft(spanValues.get(5).trim());
            compressedFlight.setTravelTime(spanValues.get(6).trim());
        } else {
            compressedFlight.setTravelTime(spanValues.get(5).trim());
        }

        return compressedFlight;
    }

    private static List<String> getSpanValues(List<String> paragraphs) {
        String allStr = StringUtils.join(paragraphs, "");
        List<String> values = new ArrayList<>();
        Matcher m = SPAN_VALUE_PATTERN.matcher(allStr);
        while (m.find()) {
            String value = m.group(1);
            //error in markup? MU4407* 321 should be parsed as flight number and aircraft
            if (values.size() == 4 && value.contains(" ")) {
                values.add(value.split(" ")[0]);
                values.add(value.split(" ")[1]);
            } else {
                values.add(value);
            }

        }
        return values;
    }

    private static List<Integer> getFlightIndexes(List<String> fromTo) {
        List<Integer> flightIndexes = new ArrayList<>();
        for (int i = 0; i < fromTo.size(); i++) {
            if (fromTo.get(i).contains("left:20.6pt") || fromTo.get(i).contains("left:309.5pt")) {
                flightIndexes.add(i);
            }
        }
        return flightIndexes;
    }
}

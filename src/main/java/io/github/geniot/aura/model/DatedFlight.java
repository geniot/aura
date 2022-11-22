package io.github.geniot.aura.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import static io.github.geniot.aura.util.Utils.*;

@Data
@JsonFilter("FlightStatusPropertyFilter")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatedFlight implements Comparable<DatedFlight> {

    String from;
    String to;

    String airlineDesignator;
    String flightNumber;

    LocalDate departureDate;

    LocalTime departureTime;
    LocalTime arrivalTime;

    String aircraft;

    FlightStatus status = FlightStatus.ACTIVE;

    @JsonIgnore
    LocalTime newDepartureTime;
    @JsonIgnore
    LocalTime newArrivalTime;
    @JsonIgnore
    FlightStatus newStatus;

    @Override
    public int compareTo(DatedFlight o) {
        if (this == o) {
            return 0;
        }
        if (!this.departureDate.equals(o.departureDate)) {
            return this.departureDate.compareTo(o.departureDate);
        }
        if (!this.departureTime.equals(o.departureTime)) {
            return this.departureTime.compareTo(o.departureTime);
        }
        if (!this.airlineDesignator.equals(o.airlineDesignator)) {
            return this.airlineDesignator.compareTo(o.airlineDesignator);
        }
        if (!this.flightNumber.equals(o.flightNumber)) {
            return this.flightNumber.compareTo(o.flightNumber);
        }
        if (!this.from.equals(o.from)) {
            return this.from.compareTo(o.from);
        }

        //throw new RuntimeException("Found equal DatedFlights.");
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof DatedFlight c)) {
            return false;
        }

        return ObjectUtils.compare(this.from, c.from) == 0
                && ObjectUtils.compare(this.departureDate, c.departureDate) == 0
                && ObjectUtils.compare(this.airlineDesignator, c.airlineDesignator) == 0
                && ObjectUtils.compare(this.flightNumber, c.flightNumber) == 0
                ;
    }

    @JsonIgnore
    public String getUniqueId() {
        return this.from + SHORT_SPACER +
                this.departureDate + SHORT_SPACER +
                this.airlineDesignator + SHORT_SPACER +
                this.flightNumber + SHORT_SPACER +
                this.departureTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, departureDate, airlineDesignator, flightNumber);
    }

    @JsonIgnore
    public String toHourString() {
        return getDiff(departureTime, newDepartureTime, getCombinedStatus()) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                (isChanged() ? getFlightNumberFull() + SHORT_SPACER : StringUtils.rightPad(getFlightNumberFull(), 10)) +
                from + HYPHEN + to +
                (isChanged() ? SHORT_SPACER : SPACER) +
                getDurationStr(getCombinedDepartureTime(), getCombinedArrivalTime()) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                aircraft +
                (isChanged() ? SHORT_SPACER : SPACER) +
                (isChanged() ? getStatusDiff(status, newStatus) : (status.equals(FlightStatus.CANCELLED) ? "C" : ""))
                ;
    }

    @JsonIgnore
    public String toCreatedHourString() {
        return departureTime +
                SHORT_SPACER +
                getFlightNumberFull() + SHORT_SPACER +
                from + HYPHEN + to +
                SHORT_SPACER +
                getDurationStr(departureTime, arrivalTime) +
                SHORT_SPACER +
                aircraft +
                SHORT_SPACER +
                (status.equals(FlightStatus.CANCELLED) ? "C" : "")
                ;
    }


    @JsonIgnore
    public String toFlightString() {
        return departureDate.format(LIST_DATE_FORMATTER) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                (isChanged() ? getFlightNumberFull() + SHORT_SPACER : StringUtils.rightPad(getFlightNumberFull(), 10)) +
                from + HYPHEN + to +
                (isChanged() ? SHORT_SPACER : SPACER) +
                getDiff(departureTime, newDepartureTime, getCombinedStatus()) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                getDiff(arrivalTime, newArrivalTime, getCombinedStatus()) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                getDurationStr(getCombinedDepartureTime(), getCombinedArrivalTime()) +
                (isChanged() ? SHORT_SPACER : SPACER) +
                aircraft +
                (isChanged() ? SHORT_SPACER : SPACER) +
                getStatusDiff(status, newStatus)
                ;
    }

    @JsonIgnore
    public String toCreatedFlightString() {
        return departureDate.format(LIST_DATE_FORMATTER) +
                SHORT_SPACER +
                getFlightNumberFull() + SHORT_SPACER +
                from + HYPHEN + to +
                SHORT_SPACER +
                departureTime +
                SHORT_SPACER +
                arrivalTime +
                SHORT_SPACER +
                getDurationStr(departureTime, arrivalTime) +
                SHORT_SPACER +
                aircraft +
                SHORT_SPACER +
                (status.equals(FlightStatus.CANCELLED) ? "C" : "")
                ;
    }

    private String getStatusDiff(FlightStatus origValue, FlightStatus newValue) {
        if (newValue == null || origValue.equals(newValue)) {
            if (origValue.equals(FlightStatus.CANCELLED)) {
                return "C";
            } else {
                return "";
            }
        } else {
            return FlightStatus.LETTER_STATUS.get(origValue) + DIFF_ARROW + FlightStatus.LETTER_STATUS.get(newValue);
        }
    }


    @JsonIgnore
    public String getFlightNumberFull() {
        return airlineDesignator + flightNumber;
    }

    @JsonIgnore
    public boolean isUpdated() {
        if (isDeleted()) {
            return false;
        }
        return !departureTime.equals(newDepartureTime) ||
                !arrivalTime.equals(newArrivalTime) ||
                !status.equals(newStatus)
                ;
    }

    @JsonIgnore
    public boolean isDeleted() {
        return newStatus != null && newStatus.equals(FlightStatus.DELETED);
    }

    @JsonIgnore
    public boolean isChanged() {
        return isDeleted() || isUpdated();
    }

    @JsonIgnore
    public FlightStatus getCombinedStatus() {
        return newStatus == null ? status : newStatus;
    }

    @JsonIgnore
    public LocalTime getCombinedArrivalTime() {
        return newArrivalTime == null ? arrivalTime : newArrivalTime;
    }

    @JsonIgnore
    public LocalTime getCombinedDepartureTime() {
        return newDepartureTime == null ? departureTime : newDepartureTime;
    }

    public void reset() {
        newDepartureTime = departureTime;
        newArrivalTime = arrivalTime;
        newStatus = status;
    }

    @JsonIgnore
    public boolean isCreated() {
        return status.equals(FlightStatus.CREATED);
    }

    @JsonIgnore
    public boolean isConflict() {
        return status.equals(FlightStatus.CONFLICT);
    }

    @JsonIgnore
    public String getFileKeyFull() {
        return departureDate.getYear() + getFileKeyShort();
    }

    @JsonIgnore
    public String getFileKeyShort() {
        return File.separator + DOUBLE_ZERO_FORMAT.format(departureDate.getMonthValue()) + "_" + MONTHS[departureDate.getMonthValue() - 1] +
                File.separator + DOUBLE_ZERO_FORMAT.format(departureDate.getDayOfMonth()) + "_" + WEEKDAYS[departureDate.getDayOfWeek().getValue() - 1] +
                File.separator + DOUBLE_ZERO_FORMAT.format(departureTime.getHour());
    }

    public void update(DatedFlight flight) {
        this.departureTime = flight.getNewDepartureTime();
        this.arrivalTime = flight.getNewArrivalTime();
        this.status = flight.getNewStatus();
    }
}

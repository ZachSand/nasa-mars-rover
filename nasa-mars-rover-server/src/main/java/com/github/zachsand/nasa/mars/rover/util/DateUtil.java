package com.github.zachsand.nasa.mars.rover.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

/**
 * {@link DateUtil} utility class for common date parsing and formatting.
 */
public class DateUtil {

    private static final DateTimeFormatter VALID_DATE_FORMATS = DateTimeFormatter.ofPattern(""
                    + "[MM/dd/uuuu]"
                    + "[MM/dd/uu]"
                    + "[MMMM d, uuuu]"
                    + "[MMM-d-uuuu]"
                    + "[uuuu-MM-dd]"
                    + "[M/d/uuuu]"
            , Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter NASA_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Retrieves the {@link LocalDate} from the input string if the input date is of a valid format.
     *
     * @param date String representation of a date.
     * @return {@link Optional} of {@link LocalDate}.
     */
    public static Optional<LocalDate> getLocalDateFromDate(String date) {
        try{
            return Optional.of(LocalDate.parse(date, VALID_DATE_FORMATS));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Converts a {@link LocalDate} from whatever format it is in to the {@code earth_date} format that the NASA API expects
     * , which is of the form {@code yyyy-MM-dd}.
     *
     * @param date {@link LocalDate} The date to format into the NASA {@code earth_date} format that the NASA API expects
     *  which is of the form {@code yyyy-MM-dd}.
     * @return String representation of the NASA API {@code earth_date}.
     */
    public static String convertDateToNasaFormat(LocalDate date) {
        return date.format(NASA_DATE_FORMAT);
    }
}

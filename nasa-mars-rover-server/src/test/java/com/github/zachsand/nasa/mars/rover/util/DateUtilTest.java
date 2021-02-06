package com.github.zachsand.nasa.mars.rover.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"02/27/17", "June 2, 2018", "Jul-13-2016"})
    public void whenValidDate_ShouldParse(String validDate) {
        assertTrue(DateUtil.getLocalDateFromDate(validDate).isPresent(), validDate + " is a valid date.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"023/25/1234", "not/a/date", "October 301, 2000", "April 31, 2018"})
    public void whenInvalidDate_shouldNotParse(String invalidDate) {
        assertFalse(DateUtil.getLocalDateFromDate(invalidDate).isPresent(), invalidDate + " is an invalid date.");
    }

    @Test
    public void whenGivenLocalDate_shouldConvertToNasaDate() {
        assertEquals("2000-01-25", DateUtil.convertDateToNasaFormat(LocalDate.of(2000, 1, 25)));
    }
}

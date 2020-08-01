package ru.ifmo.rain.yarlychenko.i18n.Entities;

import java.util.Locale;

/**
 * @author Nikolay Yarlychenko
 */
public interface StatisticsEntity {
    int getNumberOfOccurrences();

    int getNumberOfUniqueElements();

    String getMinimalValue(Locale outLocale);

    String getMaximalValue(Locale outLocale);

    int getMinimalLength();

    String getMinimalLengthElement(Locale outLocale);

    int getMaximalLength();

    String getMaximalLengthElement(Locale outLocale);

    double getMeanValue();
}

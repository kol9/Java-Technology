package ru.ifmo.rain.yarlychenko.i18n.Entities;

import ru.ifmo.rain.yarlychenko.i18n.AnsBlock;

import java.text.Collator;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Nikolay Yarlychenko
 */
public class DateEntity implements StatisticsEntity {
    Locale locale;
    SortedSet<Date> uniqueDates;
    AnsBlock<Integer, Date> minLen;
    AnsBlock<Integer, Date> maxLen;

    int datesNumber;
    int lengthCounter;
    Collator collator;

    public DateEntity(Locale locale) {
        this.locale = locale;
        uniqueDates = new TreeSet<>();
        minLen = new AnsBlock<>(0, null);
        maxLen = new AnsBlock<>(0, null);
    }

    public void addDate(Date date, int length) {
        datesNumber++;
        lengthCounter += length;
        uniqueDates.add(date);
        if (minLen.getValue() > length || minLen.getValue() == 0) {
            minLen.setItem(date);
            minLen.setValue(length);
        }

        if (maxLen.getValue() < length) {
            maxLen.setItem(date);
            maxLen.setValue(length);
        }
    }

    @Override
    public int getNumberOfOccurrences() {
        return datesNumber;
    }

    @Override
    public int getNumberOfUniqueElements() {
        return uniqueDates.size();
    }

    @Override
    public String getMinimalValue(Locale outLocale) {
        if (uniqueDates.isEmpty()) return null;
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, outLocale);
        return df.format(uniqueDates.first());
    }

    @Override
    public String getMaximalValue(Locale outLocale) {
        if (uniqueDates.isEmpty()) return null;
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, outLocale);
        return df.format(uniqueDates.last());
    }

    @Override
    public int getMinimalLength() {
        return minLen.getValue();
    }

    @Override
    public String getMinimalLengthElement(Locale outLocale) {
        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, outLocale);
            return df.format(minLen.getItem());
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    @Override
    public int getMaximalLength() {
        return maxLen.getValue();
    }

    @Override
    public String getMaximalLengthElement(Locale outLocale) {
        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, outLocale);
            return df.format(maxLen.getItem());
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    @Override
    public double getMeanValue() {
        return (double) lengthCounter / datesNumber;
    }
}

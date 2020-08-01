package ru.ifmo.rain.yarlychenko.i18n.Entities;

import ru.ifmo.rain.yarlychenko.i18n.AnsBlock;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Nikolay Yarlychenko
 */
public class NumbersEntity implements StatisticsEntity {
    Locale locale;
    SortedSet<Number> uniqueNumbers;
    AnsBlock<Integer, Number> minLen;
    AnsBlock<Integer, Number> maxLen;

    int numbersNumber;
    double total;

    NumberFormat numberFormat;


    static class NumberComparator implements Comparator<Number> {

        NumberComparator() {
            super();
        }

        public int compare(Number a, Number b) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        }

    }

    public NumbersEntity(Locale locale, NumberFormat numberFormat) {
        this.locale = locale;
        this.numberFormat = numberFormat;
        NumberComparator comparator = new NumberComparator();
        uniqueNumbers = new TreeSet<>(comparator);
        minLen = new AnsBlock<>(0, 0);
        maxLen = new AnsBlock<>(0, 0);
    }

    public void addNumber(Number s, int length) {
        if (s != null) {
            numbersNumber++;
            total += s.doubleValue();
            uniqueNumbers.add(s);
            if (minLen.getValue() > length || minLen.getValue() == 0) {
                minLen.setItem(s);
                minLen.setValue(length);
            }

            if (maxLen.getValue() < length) {
                maxLen.setItem(s);
                maxLen.setValue(length);
            }
        }
    }

    @Override
    public int getNumberOfOccurrences() {
        return numbersNumber;
    }

    @Override
    public int getNumberOfUniqueElements() {
        return uniqueNumbers.size();
    }

    @Override
    public String getMinimalValue(Locale outLocale) {
        if(uniqueNumbers.isEmpty())return null;
        return numberFormat.format(uniqueNumbers.first());
    }

    @Override
    public String getMaximalValue(Locale outLocale) {
        if(uniqueNumbers.isEmpty())return null;
        return numberFormat.format(uniqueNumbers.last());
    }

    @Override
    public int getMinimalLength() {
        return minLen.getValue();
    }

    @Override
    public String getMinimalLengthElement(Locale outLocale) {
        return minLen.getItem().toString();
    }

    @Override
    public int getMaximalLength() {
        return maxLen.getValue();
    }

    @Override
    public String getMaximalLengthElement(Locale outLocale) {
        return numberFormat.format(maxLen.getItem());
    }

    @Override
    public double getMeanValue() {
        return (double) total / numbersNumber;
    }
}

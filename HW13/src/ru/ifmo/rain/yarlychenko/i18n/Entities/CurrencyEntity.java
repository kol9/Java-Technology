package ru.ifmo.rain.yarlychenko.i18n.Entities;

import ru.ifmo.rain.yarlychenko.i18n.AnsBlock;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Nikolay Yarlychenko
 */
public class CurrencyEntity implements StatisticsEntity {
    Locale locale;
    SortedSet<Number> uniqueCurrencies;
    AnsBlock<Integer, Number> minLen;
    AnsBlock<Integer, Number> maxLen;

    int currencyNumber;
    double total;

    NumberFormat numberFormat;

    public CurrencyEntity(Locale locale, NumberFormat numberFormat) {
        this.locale = locale;
        this.numberFormat = numberFormat;
        NumbersEntity.NumberComparator comparator = new NumbersEntity.NumberComparator();
        uniqueCurrencies = new TreeSet<>(comparator);
        minLen = new AnsBlock<>(0, 0);
        maxLen = new AnsBlock<>(0, 0);
    }

    public void addCurrency(Number s, int length) {
        if (s != null) {
            currencyNumber++;
            total += s.doubleValue();
            uniqueCurrencies.add(s);
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
        return currencyNumber;
    }

    @Override
    public int getNumberOfUniqueElements() {
        return uniqueCurrencies.size();
    }

    @Override
    public String getMinimalValue(Locale outLocale) {
        if (uniqueCurrencies.isEmpty()) return null;
        return numberFormat.format(uniqueCurrencies.first());
    }

    @Override
    public String getMaximalValue(Locale outLocale) {
        if (uniqueCurrencies.isEmpty()) return null;
        return numberFormat.format(uniqueCurrencies.last());
    }

    @Override
    public int getMinimalLength() {
        return minLen.getValue();
    }

    @Override
    public String getMinimalLengthElement(Locale outLocale) {
        return numberFormat.format(minLen.getItem());
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
        return (double) total / currencyNumber;
    }
}

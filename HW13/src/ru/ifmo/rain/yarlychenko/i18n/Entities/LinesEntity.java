package ru.ifmo.rain.yarlychenko.i18n.Entities;

import ru.ifmo.rain.yarlychenko.i18n.AnsBlock;

import java.text.Collator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Nikolay Yarlychenko
 */
public class LinesEntity implements StatisticsEntity{
    Locale locale;
    SortedSet<String> uniqueLines;
    AnsBlock<Integer, String> minLen;
    AnsBlock<Integer, String> maxLen;

    int linesNumber;
    int lengthCounter;
    Collator collator;

    public LinesEntity(Locale locale, Collator collator) {
        this.locale = locale;
        this.collator = collator;
        uniqueLines = new TreeSet<>(collator);
        minLen = new AnsBlock<>(0, "");
        maxLen = new AnsBlock<>(0, "");
    }

    public void addLine(String s, int length) {
        if (s != null && !s.equals("")) {
            linesNumber++;
            lengthCounter += length;
            uniqueLines.add(s);
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
        return linesNumber;
    }

    @Override
    public int getNumberOfUniqueElements() {
        return uniqueLines.size();
    }

    @Override
    public String getMinimalValue(Locale outLocale) {
        return uniqueLines.first();
    }

    @Override
    public String getMaximalValue(Locale outLocale) {
        return uniqueLines.last();
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
        return maxLen.getItem();
    }

    @Override
    public double getMeanValue() {
        return (double) lengthCounter / linesNumber;
    }
}

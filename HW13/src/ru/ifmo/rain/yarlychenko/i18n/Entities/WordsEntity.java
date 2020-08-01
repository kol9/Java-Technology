package ru.ifmo.rain.yarlychenko.i18n.Entities;

import ru.ifmo.rain.yarlychenko.i18n.AnsBlock;

import java.text.Collator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Nikolay Yarlychenko
 */
public class WordsEntity implements StatisticsEntity {
    Locale locale;
    SortedSet<String> uniqueWords;
    AnsBlock<Integer, String> minLen;
    AnsBlock<Integer, String> maxLen;

    int wordsNumber;
    int lengthCounter;
    Collator collator;

    public WordsEntity(Locale locale, Collator collator) {
        this.locale = locale;
        this.collator = collator;
        uniqueWords = new TreeSet<>(collator);
        minLen = new AnsBlock<>(0, "");
        maxLen = new AnsBlock<>(0, "");
    }

    public void addWord(String s, int length) {
        if (s != null && !s.equals("") && isWord(s)) {
            wordsNumber++;
            lengthCounter += length;
            uniqueWords.add(s);
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

    boolean isWord(String s) {
        if (s.length() == 1) {
            return Character.isLetter(s.charAt(0));
        }
        return !"".equals(s.trim());
    }

    @Override
    public int getNumberOfOccurrences() {
        return wordsNumber;
    }

    @Override
    public int getNumberOfUniqueElements() {
        return uniqueWords.size();
    }

    @Override
    public String getMinimalValue(Locale outLocale) {
        return uniqueWords.first();
    }

    @Override
    public String getMaximalValue(Locale outLocale) {
        return uniqueWords.last();
    }

    @Override
    public int getMinimalLength() {
        return minLen.getValue();
    }

    @Override
    public String getMinimalLengthElement(Locale outLocale) {
        return minLen.getItem();
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
        return (double) lengthCounter / wordsNumber;
    }
}

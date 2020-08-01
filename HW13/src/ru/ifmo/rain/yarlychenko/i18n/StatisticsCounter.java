package ru.ifmo.rain.yarlychenko.i18n;

import ru.ifmo.rain.yarlychenko.i18n.Entities.*;

import java.text.*;
import java.util.*;

/**
 * @author Nikolay Yarlychenko
 */
public class StatisticsCounter {
    private final Locale locale;
    private final String text;

    private final BreakIterator sentenceIterator;
    private final BreakIterator wordIterator;
    private final BreakIterator charIterator;


    DateFormat dateFormat;
    NumberFormat numberFormat;
    NumberFormat currencyFormat;

    SentencesEntity sentences;
    LinesEntity lines;
    WordsEntity words;
    NumbersEntity numbers;
    CurrencyEntity currencies;
    DateEntity dates;

    StatisticsCounter(Locale locale, String text, LinesEntity lines) {
        this.locale = locale;
        this.text = text;
        this.lines = lines;

        sentenceIterator = BreakIterator.getSentenceInstance(locale);
        wordIterator = BreakIterator.getWordInstance(locale);
        charIterator = BreakIterator.getCharacterInstance(locale);

        sentenceIterator.setText(text);
        wordIterator.setText(text);
        charIterator.setText(text);


        numberFormat = NumberFormat.getNumberInstance(locale);
        currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setMinimumFractionDigits(0);


        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        Collator collator = Collator.getInstance(locale);

        sentences = new SentencesEntity(locale, collator);
        words = new WordsEntity(locale, collator);
        numbers = new NumbersEntity(locale, numberFormat);
        currencies = new CurrencyEntity(locale, currencyFormat);
        dates = new DateEntity(locale);
    }

    public void work() {
        calculateSentences();
        calculateWordsNumberCurrency();
        calculateDate();
    }

    private void calculateSentences() {

        int begin = sentenceIterator.first();
        for (int end = sentenceIterator.next();
             end != BreakIterator.DONE;
             begin = end, end = sentenceIterator.next()) {
            String current = text.substring(begin, end);
            sentences.addSentence(current, current.length());
        }
    }

    private void calculateWordsNumberCurrency() {
        int begin = wordIterator.first();
        int lastCounted = 0;
        for (int end = wordIterator.next();
             end != BreakIterator.DONE;
             begin = end, end = wordIterator.next()) {


            if (end <= lastCounted) {
                continue;
            }

            String cur = text.substring(begin, end);
            Number c = currencyFormat.parse(text, new ParsePosition(begin));
            if (c != null) {
                currencies.addCurrency(c, currencyFormat.format(c).length());
                lastCounted = begin + currencyFormat.format(c).length();
            } else {
                Number n = numberFormat.parse(text, new ParsePosition(begin));
                if (n != null) {
                    numbers.addNumber(n, numberFormat.format(n).length());
                    lastCounted = begin + numberFormat.format(n).length();
                } else {
                    words.addWord(cur, cur.length());
                    lastCounted = end;
                }
            }
        }
    }

    private void calculateDate() {

        DateFormat defaultDateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        DateFormat mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        DateFormat longDateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
        DateFormat fullDateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);


        List<DateFormat> dateFormats = new ArrayList<>();
        dateFormats.add(defaultDateFormat);
        dateFormats.add(shortDateFormat);
        dateFormats.add(mediumDateFormat);
        dateFormats.add(longDateFormat);
        dateFormats.add(fullDateFormat);

        int begin = charIterator.first();

        int lastCounted = -1;
        for (int end = charIterator.next();
             end != BreakIterator.DONE;
             begin = end, end = charIterator.next()) {

            if (begin < lastCounted) {
                continue;
            }

            for (var dateFormat : dateFormats) {
                Date date = dateFormat.parse(text, new ParsePosition(begin));
                if (date != null) {
                    lastCounted = begin + dateFormat.format(date).length();
                    dates.addDate(date, dateFormat.format(date).length());
                    break;
                }
            }
        }
    }
}

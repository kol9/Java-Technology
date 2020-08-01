package ru.ifmo.rain.yarlychenko.i18n;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Nikolay Yarlychenko
 */
public class HTMLPresenter {
    StatisticsCounter counter;
    NumberFormat numberFormat;
    NumberFormat currencyFormat;
    DateFormat dateFormat;
    Locale locale;
    Locale inputLocale;


    public HTMLPresenter(StatisticsCounter counter) {
        this.counter = counter;
    }

    public void print(File in, File out, Locale locale, Locale inLocale) {
        this.locale = locale;
        this.inputLocale = inLocale;
        numberFormat = NumberFormat.getNumberInstance(locale);
        currencyFormat = NumberFormat.getCurrencyInstance(inLocale);
        dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        ResourceBundle bundle = PropertyResourceBundle.getBundle("ru.ifmo.rain.yarlychenko.i18n.TextStatistics", locale);
        try (Writer writer = new FileWriter(out)) {
            writer.write(String.format(
                    "<html lang=\"" + locale.getLanguage() + "\">\n" +
                            "\n" +
                            "<head>\n" +
                            "    <title> %s </title>\n" +
                            "    <META charset=\"UTF-8\">\n" +
                            "</head>\n" +
                            "\n" +
                            "<body>\n" +
                            "<h2><strong> %s: " + in.getName() + "</strong></h2>\n" +
                            getSummaryStatistics(bundle) +
                            getSentenceStatistics(bundle) +
//                            getLineStatistics(bundle) +
                            getWordStatistics(bundle) +
                            getNumberStatistics(bundle) +
                            getCurrencyStatistics(bundle) +
                            getDateStatistics(bundle) +
                            "</body>\n" +
                            "</html>",
                    bundle.getString("html.title"),
                    bundle.getString("analyzed.file")
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getSummaryStatistics(ResourceBundle bundle) {

        return String.format(
                "<h3><strong>%s</strong></h3>\n" +
                        "<p>%s: " + formattedNumber(counter.sentences.getNumberOfOccurrences()) + "</p>\n" +
                        "<p>%s: " + formattedNumber(counter.lines.getNumberOfOccurrences()) + "</p>\n" +
                        "<p>%s: " + formattedNumber(counter.words.getNumberOfOccurrences()) + "</p>\n" +
                        "<p>%s: " + formattedNumber(counter.numbers.getNumberOfOccurrences()) + "</p>\n" +
                        "<p>%s: " + formattedNumber(counter.currencies.getNumberOfOccurrences()) + "</p>\n" +
                        "<p>%s: " + formattedNumber(counter.dates.getNumberOfOccurrences()) + "</p>\n",
                bundle.getString("statistics.summary"),
                bundle.getString("statistics.summary.sentences"),
                bundle.getString("statistics.summary.lines"),
                bundle.getString("statistics.summary.words"),
                bundle.getString("statistics.summary.numbers"),
                bundle.getString("statistics.summary.currencies"),
                bundle.getString("statistics.summary.dates")
        );
    }


    private String header(String s) {
        return "<h" + 3 + "><strong>" + s + "</strong></h" + 3 + ">";
    }

    private String block(String s) {
        return "<p><em>" + s + "</em></p>";
    }

    private String formattedNumber(int x) {
        return numberFormat.format(x);
    }

    private String formattedNumber(double x) {
        return numberFormat.format(x);
    }

    private String formattedMoney(int x) {
        return currencyFormat.format(x);
    }

    private String formattedMoney(double x) {
        return currencyFormat.format(x);
    }

    private String formattedDate(Date x) {
        return dateFormat.format(x);
    }

    private String getSentenceStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(header(bundle.getString("statistics.sentences")));
        stringBuilder.append("\n");
        int count = counter.sentences.getNumberOfOccurrences();
        int unique = counter.sentences.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %s (%s %s)",
                bundle.getString("sentences.count"),
                formattedNumber(count),
                formattedNumber(unique),
                bundle.getString("sentences.unique"))));
        stringBuilder.append("\n");

        String min = counter.sentences.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("sentences.min"), min)));
        stringBuilder.append("\n");


        String max = counter.sentences.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("sentences.max"), max)));
        stringBuilder.append("\n");


        int len = counter.sentences.getMinimalLength();
        String val = counter.sentences.getMinimalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("sentences.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.sentences.getMaximalLength();
        val = counter.sentences.getMaximalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("sentences.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.sentences.getMeanValue();
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("sentences.mean"),
                numberFormat.format(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String getLineStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(header(bundle.getString("statistics.lines")));
        stringBuilder.append("\n");
        int count = counter.lines.getNumberOfOccurrences();
        int unique = counter.lines.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %s (%s %s)",
                bundle.getString("lines.count"),
                formattedNumber(count),
                formattedNumber(unique),
                bundle.getString("lines.unique"))));
        stringBuilder.append("\n");

        String min = counter.lines.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("lines.min"), min)));
        stringBuilder.append("\n");


        String max = counter.lines.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("lines.max"), max)));
        stringBuilder.append("\n");


        int len = counter.lines.getMinimalLength();
        String val = counter.lines.getMinimalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("lines.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.lines.getMaximalLength();
        val = counter.lines.getMaximalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("lines.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.lines.getMeanValue();
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("lines.mean"),
                numberFormat.format(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String getWordStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(header(bundle.getString("statistics.words")));
        stringBuilder.append("\n");
        int count = counter.words.getNumberOfOccurrences();
        int unique = counter.words.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %d (%d %s)",
                bundle.getString("words.count"),
                count,
                unique,
                bundle.getString("words.unique"))));
        stringBuilder.append("\n");

        String min = counter.words.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("words.min"), min)));
        stringBuilder.append("\n");


        String max = counter.words.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("words.max"), max)));
        stringBuilder.append("\n");


        int len = counter.words.getMinimalLength();
        String val = counter.words.getMinimalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("words.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.words.getMaximalLength();
        val = counter.words.getMaximalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("words.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.words.getMeanValue();
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("words.mean"),
                numberFormat.format(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String getNumberStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();


        stringBuilder.append(header(bundle.getString("statistics.numbers")));
        stringBuilder.append("\n");
        if (counter.numbers.getNumberOfOccurrences() == 0) {
            stringBuilder.append(block(bundle.getString("zero.numbers")));
            stringBuilder.append("\n");
            return stringBuilder.toString();
        }
        int count = counter.numbers.getNumberOfOccurrences();
        int unique = counter.numbers.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %s (%s %s)",
                bundle.getString("numbers.count"),
                formattedNumber(count),
                formattedNumber(unique),
                bundle.getString("numbers.unique"))));
        stringBuilder.append("\n");

        String min = counter.numbers.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("numbers.min"), min)));
        stringBuilder.append("\n");


        String max = counter.numbers.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("numbers.max"), max)));
        stringBuilder.append("\n");


        int len = counter.numbers.getMinimalLength();
        String val = counter.numbers.getMinimalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("numbers.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.numbers.getMaximalLength();
        val = counter.numbers.getMaximalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("numbers.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.numbers.getMeanValue();
        if (Double.isNaN(mean)) mean = 0;
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("numbers.mean"),
                numberFormat.format(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String getCurrencyStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(header(bundle.getString("statistics.currencies")));
        stringBuilder.append("\n");
        if (counter.currencies.getNumberOfOccurrences() == 0) {
            stringBuilder.append(block(bundle.getString("zero.currencies")));
            stringBuilder.append("\n");
            return stringBuilder.toString();
        }
        int count = counter.currencies.getNumberOfOccurrences();
        int unique = counter.currencies.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %s (%s %s)",
                bundle.getString("currencies.count"),
                formattedNumber(count),
                formattedNumber(unique),
                bundle.getString("currencies.unique"))));
        stringBuilder.append("\n");

        String min = counter.currencies.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("currencies.min"), min)));
        stringBuilder.append("\n");


        String max = counter.currencies.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("currencies.max"), max)));
        stringBuilder.append("\n");


        int len = counter.currencies.getMinimalLength();
        String val = counter.currencies.getMinimalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("currencies.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.currencies.getMaximalLength();
        val = counter.currencies.getMaximalLengthElement(locale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("currencies.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.currencies.getMeanValue();
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("currencies.mean"),
                formattedMoney(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private String getDateStatistics(ResourceBundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(header(bundle.getString("statistics.dates")));
        stringBuilder.append("\n");
        if (counter.dates.getNumberOfOccurrences() == 0) {
            stringBuilder.append(block(bundle.getString("zero.dates")));
            stringBuilder.append("\n");
            return stringBuilder.toString();
        }
        int count = counter.dates.getNumberOfOccurrences();
        int unique = counter.dates.getNumberOfUniqueElements();

        stringBuilder.append(block(String.format("%s: %s (%s %s)",
                bundle.getString("dates.count"),
                formattedNumber(count),
                formattedNumber(unique),
                bundle.getString("dates.unique"))));
        stringBuilder.append("\n");

        String min = counter.dates.getMinimalValue(locale);
        if (min == null || min.equals("")) {
            min = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("dates.min"), min)));
        stringBuilder.append("\n");


        String max = counter.dates.getMaximalValue(locale);
        if (max == null || max.equals("")) {
            max = "-";
        }
        stringBuilder.append(block(String.format("%s: %s", bundle.getString("dates.max"), max)));
        stringBuilder.append("\n");


        int len = counter.dates.getMinimalLength();
        String val = counter.dates.getMinimalLengthElement(inputLocale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("dates.min.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");

        len = counter.dates.getMaximalLength();
        val = counter.dates.getMaximalLengthElement(inputLocale);
        stringBuilder.append(block(String.format("%s: %s (%s)",
                bundle.getString("dates.max.len"),
                formattedNumber(len),
                val)));
        stringBuilder.append("\n");


        double mean = counter.dates.getMeanValue();
        stringBuilder.append(block(String.format("%s: %s",
                bundle.getString("dates.mean"),
                formattedNumber(mean))));
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}

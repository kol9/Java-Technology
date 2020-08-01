package ru.ifmo.rain.yarlychenko.i18n;

import ru.ifmo.rain.yarlychenko.i18n.Entities.LinesEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.*;

/**
 * @author Nikolay Yarlychenko
 */
public class TextStatistics {

    public static void main(String[] args) {
        if (args == null || args.length < 4) {
            System.err.println("Wrong arguments");
            return;
        }
        for (String arg : args) {
            if (arg == null) {
                System.out.println("Wrong arguments");
                return;
            }
        }
        try {
            Locale inputLocale = null;
            Locale outputLocale = null;
            if (args.length == 4) {
                inputLocale = new Locale(args[0]);
                outputLocale = new Locale(args[1]);
            } else if (args.length == 5) {
                inputLocale = new Locale(args[0], args[1]);
                outputLocale = new Locale(args[2]);
            } else if (args.length == 6) {
                inputLocale = new Locale(args[0], args[1]);
                outputLocale = new Locale(args[2], args[3]);
            } else if (args.length == 7) {
                inputLocale = new Locale(args[0], args[1], args[2]);
                outputLocale = new Locale(args[3], args[4]);
            } else if (args.length == 8) {
                inputLocale = new Locale(args[0], args[1], args[2]);
                outputLocale = new Locale(args[3], args[4], args[5]);
            } else {
                System.out.println("Wrong arguments");
                return;
            }

            StringBuilder fileData = new StringBuilder();

            Collator collator = Collator.getInstance(inputLocale);
            LinesEntity lines = new LinesEntity(inputLocale, collator);

            File outputFile = new File(args[args.length - 1]);
            File inputFile = new File(args[args.length - 2]);

            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile,
                    StandardCharsets.UTF_8))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    lines.addLine(line, line.length());
                    fileData.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            StatisticsCounter counter = new StatisticsCounter(inputLocale, fileData.toString(), lines);
            counter.work();

            HTMLPresenter presenter = new HTMLPresenter(counter);
            presenter.print(inputFile, outputFile, outputLocale, inputLocale);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Unsupported locale");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.err.println("Wrong arguments");
        }
    }
}

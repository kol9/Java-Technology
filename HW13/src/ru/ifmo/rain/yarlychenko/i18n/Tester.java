package ru.ifmo.rain.yarlychenko.i18n;

import org.junit.jupiter.api.*;
import ru.ifmo.rain.yarlychenko.i18n.Entities.LinesEntity;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Nikolay Yarlychenko
 */

@DisplayName("JUnit 5 Example")
public class Tester {


    @Test
    @DisplayName("Test 1")
    void singleWordTest() {
        String text = "Привет";
        StatisticsCounter counter = new StatisticsCounter(new Locale("ru"), text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();
        Assertions.assertEquals(1, counter.words.getNumberOfOccurrences());
        Assertions.assertEquals(0, counter.numbers.getNumberOfOccurrences());
        Assertions.assertEquals(0, counter.currencies.getNumberOfOccurrences());
        Assertions.assertEquals(0, counter.dates.getNumberOfOccurrences());
    }

    @Test
    @DisplayName("Test 2")
    void someArabicWordsDatesNumbersTest() {
        Locale in = new Locale("ar");

        String text = "خرجت كالمعتاد في الساعة 8 مساءً للبيتزا التي اشتريتها مقابل 300 هريفنياالأحد، ٣١ مايو ٢٠٢٠\n" +
                "٣١\u200F/٥\u200F/٢٠٢٠";
        StatisticsCounter counter = new StatisticsCounter(in, text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();
        Assertions.assertEquals(11, counter.words.getNumberOfUniqueElements());
        Assertions.assertEquals(5, counter.numbers.getNumberOfUniqueElements());
        Assertions.assertEquals("٢٬٠٢٠", counter.numbers.getMaximalValue(in));
        Assertions.assertEquals(19, counter.dates.getMaximalLength());
    }


    @Test
    @DisplayName("Test 3")
    void currencyTest() {
        Locale in = new Locale("ru", "RU");

        NumberFormat format = NumberFormat.getCurrencyInstance(in);

        String text = "рубли:\n" +
                "1 ₽ и 10 789,80 ₽ и 10 234,80 ₽ и 11 789,80 ₽ у 10 789,80 ₽ иначе 109 789,80 ₽ и\n" +
                "1 ₽ и 10 789,80 ₽ и 10 234,80 ₽ и 11 789,80 ₽ у 10 789,80 ₽ иначе 109 789,80 ₽ и\n" +
                "1 ₽ и 10 789,80 ₽ и 10 234,80 ₽ и 11 789,80 ₽ у 10 789,80 ₽ иначе 109 789,80 ₽ и\n" +
                "1 ₽ и 10 789,80 ₽ и 10 234,80 ₽ и 11 789,80 ₽ у 10 789,80 ₽ иначе 109 789,80 ₽ и\n" +
                "1 ₽ и 10 789,80 ₽ и 10 234,80 ₽ и 11 789,80 ₽ у 10 789,80 ₽ иначе 109 789,80 ₽ и";
        StatisticsCounter counter = new StatisticsCounter(in, text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();
        Assertions.assertEquals(30, counter.currencies.getNumberOfOccurrences());
        Assertions.assertEquals(5, counter.currencies.getNumberOfUniqueElements());
        Assertions.assertEquals("109 789,8 ₽", counter.currencies.getMaximalLengthElement(in));
        Assertions.assertEquals("1 ₽", counter.currencies.getMinimalValue(in));
        Assertions.assertEquals(format.format(25565.83), format.format(counter.currencies.getMeanValue()));
    }

    @Test
    @DisplayName("Test 4")
    void LongEnglishTextTest() {
        Locale in = new Locale("en", "US");


        String text = "Elon Reeve Musk FRS (born June 28, 1971) is an engineer, industrial designer and technology entrepreneur.[2][3][4] He is a citizen of South Africa, Canada, and the United States. He is the founder, CEO and chief engineer/designer of SpaceX;[5] early investor,[6][note 2] CEO and product architect of Tesla, Inc.;[9][10] founder of The Boring Company;[11] co-founder of Neuralink; and co-founder and initial co-chairman of OpenAI.[12] He was elected a Fellow of the Royal Society (FRS) in 2018.[13][14] In December 2016, he was ranked 21st on the Forbes list of The World's Most Powerful People,[15] and was ranked joint-first on the Forbes list of the Most Innovative Leaders of 2019.[16] As of May 2020, he has a net worth of $36.5 billion and is listed by Forbes as the 31st-richest person in the world.[17][1] He is the longest tenured CEO of any automotive manufacturer globally.[18]\n" +
                "\n" +
                "Born and raised in Pretoria, South Africa, Musk briefly attended the University of Pretoria before moving to Canada when he was 17 to attend Queen's University. He transferred to the University of Pennsylvania two years later, where he received a bachelor's degree in economics from the Wharton School and a bachelor's degree in physics from the College of Arts and Sciences. He began a Ph.D. in applied physics and material sciences at Stanford University in 1995 but dropped out after two days to pursue a business career. He subsequently co-founded (with his brother Kimbal) Zip2, a web software company, which was acquired by Compaq for $340 million in 1999. Musk then founded X.com, an online bank. It merged with Confinity in 2000, which had launched PayPal the previous year and was subsequently bought by eBay for $1.5 billion in October 2002.[9][19][20][21]\n" +
                "\n" +
                "In May 2002, Musk founded SpaceX, an aerospace manufacturer and space transport services company, of which he is CEO and lead designer. He joined Tesla Motors, Inc. (now Tesla, Inc.), an electric vehicle manufacturer, in 2004, the year after it was founded,[9] and became its CEO and product architect. In 2006, he helped create SolarCity, a solar energy services company (now a subsidiary of Tesla). In 2015, Musk co-founded OpenAI, a nonprofit research company that aims to promote friendly artificial intelligence. In July 2016, he co-founded Neuralink, a neurotechnology company focused on developing brain–computer interfaces. In December 2016, Musk founded The Boring Company, an infrastructure and tunnel construction company focused on tunnels optimized for electric vehicles.\n" +
                "\n" +
                "In addition to his primary business pursuits, Musk has envisioned a high-speed transportation system known as the Hyperloop. Musk has said the goals of SpaceX, Tesla, and SolarCity revolve around his vision to \"change the world and help humanity\".[22] His goals include reducing global warming through sustainable energy production and consumption, and lessening the risk of human extinction by establishing a human colony on Mars.[23][24][better source needed]\n";
        StatisticsCounter counter = new StatisticsCounter(in, text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();
        Assertions.assertEquals(446, counter.words.getNumberOfOccurrences());
        Assertions.assertEquals(44, counter.numbers.getNumberOfOccurrences());
        Assertions.assertEquals(3, counter.currencies.getNumberOfOccurrences());
        Assertions.assertEquals(1, counter.dates.getNumberOfOccurrences());
    }

    @Test
    @DisplayName("Test 5")
    void differentDateFormatsTest() {

        Locale in = new Locale("en", "US");

        String text = "May 31, 2020 yo Sunday, May 31, 2020 yo May 31, 2020 yo May 31, 2020 yo 5/31/20";
        StatisticsCounter counter = new StatisticsCounter(in, text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();

        Assertions.assertEquals(5, counter.dates.getNumberOfOccurrences());
        Assertions.assertEquals(1,counter.dates.getNumberOfUniqueElements());
    }

    @Test
    @DisplayName("Test 5")
    void differentNumbersTest() {

        Locale in = new Locale("en", "US");

        String text = "10,234.8 yo 11,790 yo 228 yo 1337 yo 11,5 8";
        StatisticsCounter counter = new StatisticsCounter(in, text,
                new LinesEntity(Locale.ENGLISH, Collator.getInstance()));
        counter.work();

        Assertions.assertEquals(6, counter.numbers.getNumberOfOccurrences());
        Assertions.assertEquals(6,counter.numbers.getNumberOfUniqueElements());
    }
}

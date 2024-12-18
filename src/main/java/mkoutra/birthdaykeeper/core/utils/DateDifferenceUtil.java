package mkoutra.birthdaykeeper.core.utils;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;

/**
 * A utility class that provides methods for date-related calculations.
 * Specifically, it includes functionality to determine the number of days
 * remaining until a given date (month/day) occurs next.
 * Note: This class cannot be instantiated as it is utility-based.
 */
public final class DateDifferenceUtil {

    // Private constructor to prevent instantiation
    private DateDifferenceUtil() {}

    /**
     * Calculates the number of days remaining until
     * the next occurrence of the given date (month/day).
     *
     * @param birthday  A {@link LocalDate} representing the user's birthday.
     *                  The year is ignored, and only the month and day are
     *                  used in calculations.
     * @return          The number of days until the next occurrence of the given date.
     *                  Returns 0 if the date is today.
     * @throws NullPointerException if the birthday is null.
     */
    public static long getDaysUntilNextBirthday(LocalDate birthday) {
        LocalDate today = LocalDate.now();

        // Extract the month and day from the birthday
        MonthDay birthMonthDay = MonthDay.of(birthday.getMonth(), birthday.getDayOfMonth());

        // Create the next birthday in the current year
        LocalDate nextBirthday = birthMonthDay.atYear(today.getYear());

        // If the next birthday has already passed this year, move to the next year
        if (nextBirthday.isBefore(today) || nextBirthday.isEqual(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }

        // Calculate the days until the next birthday
        return ChronoUnit.DAYS.between(today, nextBirthday);
    }
}

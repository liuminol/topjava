package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTo> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println();
        System.out.println();

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDay = new HashMap<>();
        List<MealTo> result = new ArrayList<>();

        for (Meal m : meals) {
            LocalDate key = m.getDateTime().toLocalDate();
            if (caloriesSumByDay.containsKey(key)) {
                caloriesSumByDay.merge(key, m.getCalories(), Integer::sum);
            } else {
                caloriesSumByDay.put(key, m.getCalories());
            }
        }

        for (Meal m : meals) {
            if (TimeUtil.isBetweenInclusive(m.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(new MealTo(m.getDateTime(), m.getDescription(), m.getCalories(),
                        caloriesPerDay < caloriesSumByDay.get(m.getDateTime().toLocalDate())));
            }
        }

        return result;
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDay = meals.stream().collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(),
                Collectors.summingInt(Meal::getCalories)));

        return meals.stream()
                .filter(m -> TimeUtil.isBetweenInclusive(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new MealTo(m.getDateTime(), m.getDescription(), m.getCalories(),
                        caloriesPerDay < caloriesSumByDay.get(m.getDateTime().toLocalDate())))
                .collect(Collectors.toList());
    }
}
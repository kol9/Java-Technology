package ru.ifmo.rain.yarlychenko.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Nikolay Yarlychenko
 */
public class StudentDB implements StudentGroupQuery {
    private static final Comparator<Student> alphabetOrderComparator =
            Comparator.comparing(Student::getLastName, String::compareTo)
                    .thenComparing(Student::getFirstName, String::compareTo)
                    .thenComparing(Student::getId);

    private Stream<String> mappedStudentStream(List<Student> students, Function<Student, String> f) {
        return students.stream().map(f);
    }

    private Stream<Student> sortedStudentStream(Collection<Student> students, Comparator<Student> cmp) {
        return students.stream().sorted(cmp);
    }

    private Stream<Student> filteredSortedStudentStream(Collection<Student> students, Predicate<Student> filterFunc) {
        return students.stream()
                .filter(filterFunc)
                .sorted(alphabetOrderComparator);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mappedStudentStream(students, Student::getFirstName).collect(Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mappedStudentStream(students, Student::getLastName).collect(Collectors.toList());
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return mappedStudentStream(students, Student::getGroup).collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mappedStudentStream(students,
                student -> student.getFirstName() + " " + student.getLastName())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return mappedStudentStream(students, Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream()
                .min(Student::compareTo)
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortedStudentStream(students, Student::compareTo).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortedStudentStream(students, alphabetOrderComparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return filteredSortedStudentStream(students,
                student -> student.getFirstName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return filteredSortedStudentStream(students,
                student -> student.getLastName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return filteredSortedStudentStream(students,
                student -> student.getGroup().equals(group))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return filteredSortedStudentStream(students,
                student -> group.equals(student.getGroup()))
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)));
    }


    private Stream<Map.Entry<String, List<Student>>> groupEntriesStream(Stream<Student> students) {
        return students
                .collect(Collectors.groupingBy(Student::getGroup, TreeMap::new, Collectors.toList()))
                .entrySet().stream();
    }

    private List<Group> getGroupsBy(Collection<Student> students, Function<List<Student>, List<Student>> f) {
        return groupEntriesStream(students.stream())
                .map(e -> new Group(e.getKey(),
                        f.apply(e.getValue())))
                .collect(Collectors.toList());
    }

    private String getLargestGroupBy(Stream<Map.Entry<String, List<Student>>> groups,
                                     Comparator<List<Student>> comparator) {
        return groups
                .max(Map.Entry.<String, List<Student>>comparingByValue(comparator)
                        .thenComparing(Map.Entry.comparingByKey(Collections.reverseOrder(String::compareTo))))
                .map(Map.Entry::getKey)
                .orElse("");
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsByName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getGroupsBy(students, this::sortStudentsById);
    }


    @Override
    public String getLargestGroup(Collection<Student> students) {
        return getLargestGroupBy(groupEntriesStream(students.stream()), Comparator.comparingInt(List::size));
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return getLargestGroupBy(groupEntriesStream(students.stream()),
                Comparator.comparingInt(list -> getDistinctFirstNames(list).size()));
    }
}

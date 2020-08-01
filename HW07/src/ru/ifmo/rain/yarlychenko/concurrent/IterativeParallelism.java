package ru.ifmo.rain.yarlychenko.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * @author Nikolay Yarlychenko
 */
public class IterativeParallelism implements ScalarIP {

    /**
     * Doing particular concurrent task with list
     *
     * @param threads      number or concurrent threads.
     * @param values       values to get maximum of.
     * @param taskFunction function, describes subtask with stream to do.
     * @param ansFunction  function, taking result of taskFunction job and calling itself for take a result of main task.
     * @param <T>          value type.
     * @param <R>          result type.
     * @return result of applying ansFunction on collecting stream
     * @throws IllegalArgumentException
     * @throws InterruptedException
     */
    private <T, R> R standardTask(int threads, List<? extends T> values,
                                  Function<? super Stream<? extends T>, ? extends R> taskFunction,
                                  Function<? super Stream<? extends R>, ? extends R> ansFunction) throws IllegalArgumentException, InterruptedException {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must be non-empty list");
        }
        if (threads <= 0) {
            throw new IllegalArgumentException("threads must be >= 0");
        }
        threads = Math.min(threads, values.size());
        final int blockSize = values.size() / threads;
        int tailSize = values.size() % threads;
        List<Thread> workers = new ArrayList<>();
        List<R> ans = new ArrayList<>(Collections.nCopies(threads, null));


        int lastR = 0;
        for (int i = 0; i < threads; i++) {
            int l = lastR;
            int r = l + blockSize + (tailSize-- <= 0 ? 0 : 1);
            lastR = r;
            int finalI = i;
            Thread worker = new Thread(() -> ans.set(finalI, taskFunction.apply(values.subList(l, r).stream())));
            workers.add(worker);
            worker.start();
        }
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new InterruptedException("executing thread was interrupted ");
            }
        }
        return ansFunction.apply(ans.stream());
    }

    /**
     * Returns maximum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return maximum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if not values are given.
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return standardTask(threads, values,
                stream -> stream.max(comparator).orElse(null),
                stream -> stream.max(comparator).orElse(null));
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return minimum of given values
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if not values are given.
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, Collections.reverseOrder(comparator));
    }

    /**
     * Returns whether all values satisfies predicate.
     *
     * @param <T>       value type.
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @return whether all values satisfies predicate or {@code true}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return standardTask(threads, values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(Boolean::booleanValue)
        );
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number or concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return whether any value satisfies predicate or {@code false}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return standardTask(threads, values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(Boolean::booleanValue));
    }

}

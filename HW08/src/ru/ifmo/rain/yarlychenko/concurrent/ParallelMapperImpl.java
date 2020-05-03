package ru.ifmo.rain.yarlychenko.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;


/**
 * @author Nikolay Yarlychenko
 */
public class ParallelMapperImpl implements ParallelMapper {

    /**
     * list of working threads.
     */
    final List<Thread> workers;
    /**
     * queue of tasks
     */
    final Queue<Runnable> tasks;

    /**
     * creates {@code threads} number of threads.
     *
     * @param threads number of threads to create.
     */
    public ParallelMapperImpl(int threads) {
        workers = new ArrayList<>();
        tasks = new ArrayDeque<>();

        for (int i = 0; i < threads; i++) {
            Worker worker = new Worker();
            worker.start();
            workers.add(worker);
        }
    }

    /**
     * Maps function {@code f} over specified {@code args}.
     * Mapping for each element performs in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted.
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        ResultCollector<R> collector = new ResultCollector<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            int finalI = i;
            synchronized (tasks) {
                tasks.add(() -> collector.setData(finalI, f.apply(args.get(finalI))));
                tasks.notifyAll();
            }
        }
        return collector.getRes();
    }

    /**
     * Stops all threads. All unfinished mappings leave in undefined state.
     */
    @Override
    public void close() {
        workers.forEach(Thread::interrupt);
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException ignored) {
            }
        }
    }


    /**
     * class for collecting result into array, and returning it asynchronously.
     *
     * @param <R> type of result.
     */
    private static class ResultCollector<R> {
        /**
         * array for result.
         */
        private final List<R> res;
        /**
         * counter of ready to return elements.
         */
        private int cnt;

        /**
         * creates result array, nullify counter.
         *
         * @param size size of creating array.
         */
        ResultCollector(final int size) {
            res = new ArrayList<>(Collections.nCopies(size, null));
            cnt = 0;
        }

        /**
         * Setting data on particular position asynchronously.
         *
         * @param pos  position, where need to set data asynchronously.
         * @param data data to set.
         */
        void setData(final int pos, R data) {
            synchronized (this) {
                res.set(pos, data);
                if (++cnt == res.size()) {
                    notify();
                }
            }
        }

        /**
         * returning result if and only it ready and all elements was changed.
         *
         * @return result array.
         * @throws InterruptedException when working thread interrupted.
         */
        synchronized List<R> getRes() throws InterruptedException {
            while (cnt < res.size()) {
                wait();
            }
            return res;
        }

    }

    /**
     * Worker class for work inside Thread loop.
     */
    class Worker extends Thread {
        /**
         * run function
         */
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    work();
                }
            } catch (InterruptedException ignored) {
            } finally {
                Thread.currentThread().interrupt();
            }

        }

        /**
         * pick task from head of queue, if that exists.
         *
         * @throws InterruptedException when working thread is interrupted.
         */
        void work() throws InterruptedException {
            Runnable task;
            synchronized (tasks) {
                while (tasks.isEmpty()) {
                    tasks.wait();
                }
                task = tasks.poll();
            }
            task.run();
        }
    }
}
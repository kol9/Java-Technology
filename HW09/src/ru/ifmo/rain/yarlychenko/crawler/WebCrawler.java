package ru.ifmo.rain.yarlychenko.crawler;

import info.kgeorgiy.java.advanced.crawler.CachingDownloader;
import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final int perHost;

    private final ExecutorService downloaders;
    private final ExecutorService extractors;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.perHost = perHost;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
    }

    void downloadDFS(String url, int depth, Phaser phaser, Set<String> downloaded,
                     ConcurrentMap<String, IOException> errors, Set<String> used) {
        phaser.register();
        downloaders.submit(() -> {
            try {
                final Document page = downloader.download(url);
                downloaded.add(url);
                if (depth > 1) {
                    phaser.register();
                    extractors.submit(() -> {
                        try {
                            List<String> list = page.extractLinks();
                            for (String s : list) {
                                if (!used.contains(s)) {
                                    used.add(s);
                                    downloadDFS(s, depth - 1, phaser, downloaded, errors, used);
                                }
                            }
                        } catch (IOException ignored) {
                        } finally {
                            phaser.arrive();
                        }
                    });
                }
            } catch (IOException e) {
                errors.put(url, e);
            } finally {
                phaser.arrive();
            }
        });
    }

    @Override
    public Result download(String url, int depth) {
        Set<String> downloaded = ConcurrentHashMap.newKeySet();
        final ConcurrentMap<String, IOException> errors = new ConcurrentHashMap<>();
        Set<String> used = ConcurrentHashMap.newKeySet();
        used.add(url);
        Phaser phaser = new Phaser(1);
        downloadDFS(url, depth, phaser, downloaded, errors, used);
        phaser.arriveAndAwaitAdvance();
        return new Result(new ArrayList<>(downloaded), errors);
    }

    @Override
    public void close() {
        downloaders.shutdownNow();
        extractors.shutdownNow();
    }

    public static void main(String[] args) {
        if (args == null || args.length < 2 || args.length > 5) {
            System.out.println("Expected more than two, and not more than 5 arguments");
        } else {
            try {
                try (Crawler crawler = new WebCrawler(new CachingDownloader(),
                        Integer.parseInt(args[2]), Integer.parseInt(args[3]), 1)) {
                    crawler.download(args[0], Integer.parseInt(args[1]));
                }
            } catch (NumberFormatException e) {
                System.err.println("Expected integer numbers in arguments");
            } catch (IOException e) {
                System.err.println("");
                e.printStackTrace();
            }
        }
    }
}

package ru.ifmo.rain.yarlychenko.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Nikolay Yarlychenko
 */
public class Walk {
    private static final int FNV_32_PRIME = 0x01000193;
    private static final int FNV_32_DEFAULT_VALUE = 0x811c9dc5;
    private static final int BLOCK_SIZE = 8192;

    public static int fileHashFNV(Path file) {
        int hash = FNV_32_DEFAULT_VALUE;

        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            byte[] b = new byte[BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(b, 0, BLOCK_SIZE)) != -1) {
                for (int i = 0; i < bytesRead; ++i) {
                    hash *= FNV_32_PRIME;
                    hash ^= (b[i] & 0xff);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File opening issue" + " " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Reading file issue" + " " + e.getMessage());
        }

        return hash;
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Wrong arguments, use this format: java Walk <input file> <output file>");
        } else {

            try {
                File inputFile = new File(args[0]);
                File outputFIle = new File(args[1]);

                if (outputFIle.getParent() != null && !Files.exists(outputFIle.getParentFile().toPath())) {
                    try {
                        Files.createDirectory(outputFIle.getParentFile().toPath());
                    } catch (IOException e) {
                        System.out.println("Output file parent creating issue" + " " + e.getMessage());
                    }
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile, StandardCharsets.UTF_8))) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFIle, StandardCharsets.UTF_8))) {
                        String path;
                        FNVFileVisitor fileVisitor = new FNVFileVisitor(writer);
                        while ((path = reader.readLine()) != null) {
                            try {
                                Files.walkFileTree(Paths.get(path), fileVisitor);
                            } catch (InvalidPathException e) {
                                System.out.println(e.getMessage());
                                writer.write(String.format("%08x", 0) + " " + path);
                                writer.newLine();
                            } catch (IOException e) {
                                System.out.println("Writing into output file issue" + e.getMessage());
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Input file not found" + " " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Reading input file issue" + " " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error thrown");
            }
        }
    }

    static class FNVFileVisitor extends SimpleFileVisitor<Path> {
        BufferedWriter writer;

        FNVFileVisitor(BufferedWriter writer) {
            this.writer = writer;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            int hash = fileHashFNV(file);
            writer.write(String.format("%08x", hash) + " " + file.toString());
            writer.newLine();
            return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            writer.write(String.format("%08x", 0) + " " + file.toString());
            writer.newLine();
            return FileVisitResult.CONTINUE;
        }
    }
}

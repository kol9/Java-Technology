import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Nikolay Yarlychenko
 */

public class RecursiveWalk {

    static class FNVFileVisitor extends SimpleFileVisitor<Path> {
        BufferedWriter writer;

        FNVFileVisitor(BufferedWriter writer) {
            this.writer = writer;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            int hash = Walk.fileHashFNV(file);
            writer.write(String.format("%08x", hash) + " " + file);
            writer.newLine();
            return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            writer.write(String.format("%08x", 0) + " " + file);
            writer.newLine();
            return FileVisitResult.CONTINUE;
        }
    }


    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Wrong arguments, use this format: java Walk <input file> <output file>");
        } else {

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
                    while ((path = reader.readLine()) != null) {
                        FNVFileVisitor fileVisitor = new FNVFileVisitor(writer);
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
        }
    }
}

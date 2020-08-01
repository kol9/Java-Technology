package ru.ifmo.rain.yarlychenko.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author Nikolay Yarlychenko
 * @version 0.0001
 * <p>
 * Class implements JarImpler interface
 */
public class JarImplementor extends Implementor implements JarImpler {
    /**
     * Compiles class, and stores in {@code tmpPath} path
     *
     * @param token   class to compile
     * @param tmpPath path to save compiled class
     * @throws ImplerException    when {@code} tmpPath is invalid path
     * @throws URISyntaxException when path is incorrect
     */
    void compile(Class<?> token, Path tmpPath) throws ImplerException, URISyntaxException {
        Path tokenClassPath;
        try {
            CodeSource codeSource = token.getProtectionDomain().getCodeSource();

            tokenClassPath = Path.of(codeSource.getLocation().toURI());
        } catch (InvalidPathException e) {
            throw new ImplerException("Failed to generate path", e);
        }

        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            String className = token.getSimpleName() + "Impl";
            final Path pathToPackage = tmpPath.resolve(token.getPackageName().replace('.', File.separatorChar));
            String[] args = {
                    "-classpath",
                    String.join(File.pathSeparator, tokenClassPath.toString(),
                            tmpPath.toString(), System.getProperty("java.class.path")),
                    pathToPackage.resolve(className + ".java").toString()
            };

            int resultCode = compiler.run(null, null, null, args);
            if (resultCode != 0) {
                throw new ImplerException("Can't compile the class, compiler's return code is " + resultCode + ".");
            }
        } catch (NullPointerException e) {
            System.out.println("Compiler receiving error");
        }
    }

    /**
     * Creates jar file for created {@code .class}-file
     *
     * @param token     type token for which {@code .jar}-file is generated
     * @param jarFile   target for the {@code .jar}-file
     * @param classFile path to the compiled implementation class
     * @throws ImplerException when can't write to the {@code .jar}-file
     */
    void createJar(Class<?> token, Path jarFile, Path classFile) throws ImplerException {
        final Manifest manifest = new Manifest();

        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (final JarOutputStream stream = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            final String name = token.getPackageName().replace('.', '/') + "/" +
                    token.getSimpleName() + "Impl" + ".class";
            stream.putNextEntry(new ZipEntry(name));
            Files.copy(Paths.get(classFile.toString(), name), stream);
        } catch (final IOException e) {
            throw new ImplerException(e.getMessage());
        }
    }


    /**
     * Creates {@code .jar} file implementing interface {@link Class}
     *
     * @param token   type token to create implementation for
     * @param jarFile target {@code .jar} file
     * @throws ImplerException when implementation can not be generated
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        try {
            Path tempDir = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "tmp");
            try {
                implement(token, tempDir);
                compile(token, tempDir);
                createJar(token, jarFile, tempDir);
            } catch (SecurityException e) {
                throw new ImplerException("Can't create jar-file: " + e.getMessage());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        } catch (IOException | SecurityException e) {
            throw new ImplerException("Can't create temporary directory: " + e.getMessage());
        }
    }


    /**
     * Main method of JarImplementor
     * @param args list of arguments
     */
    public static void main(String[] args) {
        if (args == null || (args.length != 2 && args.length != 3)) {
            System.out.println("Two or three arguments were expected");
            return;
        }

        for (String arg : args) {
            if (arg == null) {
                System.out.println("Non-null arguments were expected");
                return;
            }
        }
        JarImpler implementor = new JarImplementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else if (args[0].equals("-jar") || args[0].equals("--jar")) {
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            } else {
                System.out.println(args[0] + " is unknown argument, -jar expected.");
            }
        } catch (InvalidPathException e) {
            System.out.println("Invalid path in the second argument. " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid class in the first argument. " + e.getMessage());

        } catch (ImplerException e) {
            System.out.println("Implementation error. " + e.getMessage());
        }
    }
}

//    javac ru/ifmo/rain/yarlychenko/implementor/Implementor.java
//    java -cp . -p . -m info.kgeorgiy.java.advanced.implementor jar-class ru.ifmo.rain.yarlychenko.implementor.Implementor


package ru.ifmo.rain.yarlychenko.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Nikolay Yarlychenko
 * @version 0.0001
 * <p>
 * Class implements Impler interface
 * @see info.kgeorgiy.java.advanced.implementor.JarImpler
 * @see info.kgeorgiy.java.advanced.implementor.Impler
 */
public class Implementor implements Impler {
    /**
     * Getting correct absolute path for given class and his path
     *
     * @param token given Abstract class or Interface
     * @param root  path to given class
     * @return absolute path for implemented class
     * @throws ImplerException if <code>root</code> is incorrect path, or error in creating parent directory
     */
    private Path getPath(Class<?> token, Path root) throws ImplerException {
        Path path;
        try {
            String savePath = String.join(File.separator,
                    token.getPackageName().split("\\.")) +
                    File.separator + token.getSimpleName() + "Impl.java";
            path = Path.of(root.toString(), savePath);

        } catch (InvalidPathException e) {
            throw new ImplerException("Invalid path", e);
        }

        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new ImplerException("Can't create parent folder:" + e.getMessage());
            }
        }
        return path;
    }

    /**
     * Generates string with necessary class implementation
     *
     * @param token given Abstract class or Interface
     * @return String with necessary class implementation
     */
    private String generateImpl(Class<?> token) {
        CodeGenerator generator = new CodeGenerator(token);

        return generator
                .addPackage()
                .addHeader()
                .addMethods()
                .addFooter()
                .toString();
    }

    /**
     * Unicode format casting
     *
     * @param target String which should be formatted as Unicode
     * @return String in Unicode format
     */
    private String toUnicode(String target) {
        StringBuilder escapeBuilder = new StringBuilder();
        for (char c : target.toCharArray()) {
            if (c >= 128)
                escapeBuilder.append("\\u").append(String.format("%04X", (int) c));
            else
                escapeBuilder.append(c);
        }
        return escapeBuilder.toString();
    }


    /**
     * Produces code implementing class or interface specified by provided <var>token</var>.
     * <p>
     * Generated class classes name should be same as classes name of the type token with <var>Impl</var> suffix
     * added.
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @throws ImplerException when implementation cannot be generated.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (token.isPrimitive() || token == Enum.class ||
                Modifier.isFinal(token.getModifiers()) || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Token must be not private class or interface");
        }

        Path path = getPath(token, root);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(toUnicode(generateImpl(token)));
        } catch (IOException e) {
            throw new ImplerException("Can't write to source file", e);
        }
    }
}

/**
 * Class for generate implementation
 */
class CodeGenerator {
    /**
     * String constant for space symbol
     */
    private final static String SPACE = " ";
    /**
     * String constant for Tab symbol
     */
    private final static String TAB = "\t";
    /**
     * String constant for comma symbol
     */
    private final static String COMMA = ",";
    /**
     * String constant for line separator symbol
     */
    private final static String LINE_SEPARATOR = System.lineSeparator();
    /**
     * String builder for construct code implementation
     */
    private final StringBuilder builder;
    /**
     * Variable, taken given class
     */
    private Class<?> token;

    /**
     * Constructor for CodeGenerator
     * @param token given class to implement
     */
    public CodeGenerator(Class<?> token) {
        this.builder = new StringBuilder();
        this.token = token;
    }

    /**
     * Appending package name to {@link CodeGenerator#builder} if such exists
     *
     * @return current instance <code>this</code> of {@link CodeGenerator}
     */
    public CodeGenerator addPackage() {
        if (!token.getPackageName().equals("")) {
            builder.append("package").append(SPACE).append(token.getPackageName()).append(";").append(LINE_SEPARATOR);
        }
        builder.append(LINE_SEPARATOR);
        return this;
    }

    /**
     * Appending header to {@link CodeGenerator#builder}
     *
     * @return current instance <code>this</code> of {@link CodeGenerator}
     */
    public CodeGenerator addHeader() {
        builder.append("public class ").append(token.getSimpleName()).append("Impl").append(SPACE)
                .append("implements").append(SPACE).append(token.getCanonicalName())
                .append(SPACE).append("{").append(LINE_SEPARATOR);
        return this;
    }

    /**
     * Getting return type and name for arguments in methods or constructors in {@link CodeGenerator#token}
     *
     * @param executable given method or constructor to have argument's return type and name from
     * @return String with necessary format
     */
    private static String getReturnTypeAndName(Executable executable) {
        Method tmp = (Method) executable;
        return tmp.getReturnType().getCanonicalName() + SPACE + tmp.getName();
    }

    /**
     * Returns format for parameter
     *
     * @param parameter given parameter of constructor of method
     * @return necessary parameter format
     */
    private static String getParameter(Parameter parameter) {
        return (parameter.getType().getCanonicalName() + SPACE) + parameter.getName();
    }

    /**
     * Returns string describing arguments in format : {@code "(T arg0, E arg1, ...)"}
     *
     * @param executable given method or constructor
     * @return String in necessary format
     */
    private static String getArgs(Executable executable) {
        return Arrays.stream(executable.getParameters())
                .map(CodeGenerator::getParameter)
                .collect(Collectors.joining(COMMA + SPACE, "(", ")"));
    }

    /**
     * Appending exceptions line to {@link CodeGenerator#builder} if such exist
     *
     * @param executable given class or method
     * @return string with exceptions enumeration
     */
    private static String getExceptions(Executable executable) {
        StringBuilder res = new StringBuilder();
        Class<?>[] exceptions = executable.getExceptionTypes();
        if (exceptions.length > 0) {
            res.append(SPACE + "throws" + SPACE);
        }
        res.append(Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(COMMA + SPACE))
        );
        return res.toString();
    }

    /**
     * Returns string with body implementation of given method or constructor
     *
     * @param executable given class or method
     * @return string with necessary implementation
     */
    private static String getMethodBody(Executable executable) {
        return "return" + getDefaultReturnValue(((Method) executable).getReturnType());
    }

    /**
     * Getting default return value for {@link CodeGenerator#getMethodBody(Executable)}
     *
     * @param token given class or interface
     * @return default return value for particular return types;
     */
    private static String getDefaultReturnValue(Class<?> token) {
        if (token.equals(boolean.class)) {
            return " false";
        } else if (token.equals(void.class)) {
            return "";
        } else if (token.isPrimitive()) {
            return " 0";
        }
        return " null";
    }

    /**
     * Appending particular executable to {@link CodeGenerator#builder} if such exists
     *
     * @param executable given executable
     */
    private void generateMethod(Executable executable) {
        final int modifiers = executable.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.NATIVE & ~Modifier.TRANSIENT;
        builder.append(LINE_SEPARATOR).append(TAB).append(Modifier.toString(modifiers)).append(modifiers != 0 ? SPACE : "")
                .append(getReturnTypeAndName(executable))
                .append(getArgs(executable))
                .append(getExceptions(executable))
                .append(SPACE)
                .append("{")
                .append(LINE_SEPARATOR)
                .append(TAB).append(TAB)
                .append(getMethodBody(executable)) // body
                .append(";")
                .append(LINE_SEPARATOR)
                .append(TAB)
                .append("}")
                .append(LINE_SEPARATOR);
    }

    /**
     * Adding all methods of {@link CodeGenerator#token} in {@link CodeGenerator#builder}
     *
     * @return current instance <code>this</code> of {@link CodeGenerator}
     */
    public CodeGenerator addMethods() {
        HashSet<SignatureComparedMethod> methodsSet = Arrays.stream(token.getMethods())
                .map(SignatureComparedMethod::new)
                .collect(Collectors.toCollection(HashSet::new));
        while (token != null) {
            methodsSet.addAll(Arrays.stream(token.getDeclaredMethods())
                    .map(SignatureComparedMethod::new)
                    .collect(Collectors.toCollection(HashSet::new)));
            token = token.getSuperclass();
        }
        methodsSet.stream()
                .filter(m -> Modifier.isAbstract(m.getMethod().getModifiers()))
                .forEach(m -> generateMethod(m.getMethod()));

        return this;
    }

    /**
     * Comparing methods by signature
     */
    private static class SignatureComparedMethod {
        private final Method method;
        /**
         * prime number to calculate hash function of method
         */
        private static final int POW = 47;
        /**
         * prime number to calculate hash function of method
         */
        private static final int MOD = 1_000_000_007;

        /**
         * Constructor of SignatureComparedMethod
         *
         * @param method given method to calculate hash function
         */
        SignatureComparedMethod(Method method) {
            this.method = method;
        }

        /**
         * getter for mathod
         *
         * @return {@link #method}
         */
        Method getMethod() {
            return method;
        }

        /**
         * Calculates and returns hash function for {@link #method}
         *
         * @return hash function for {{@link #method}}
         */
        @Override
        public int hashCode() {
            int hash = method.getReturnType().hashCode() % MOD;
            hash = (hash + POW * method.getName().hashCode()) % MOD;
            hash = (hash + POW * POW * Arrays.hashCode(method.getParameterTypes()));
            return hash;
        }

        /**
         * if hashes are equal, compares by signature
         *
         * @param o object to equal
         * @return true, if they're equal, and false if they're not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SignatureComparedMethod other = (SignatureComparedMethod) o;
            return Objects.equals(method.getReturnType(), other.method.getReturnType()) &&
                    method.getName().equals(other.method.getName()) &&
                    Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes());
        }
    }

    /**
     * Adding all methods of {@link CodeGenerator#token} in {@link CodeGenerator#builder}
     *
     * @return current instance <code>this</code> of {@link CodeGenerator}
     */
    public CodeGenerator addFooter() {
        builder.append("}").append(LINE_SEPARATOR);
        return this;
    }

    /**
     * Converts {@link CodeGenerator#builder} value to String
     *
     * @return string with implementation
     * @see StringBuilder#toString()
     */
    public String toString() {
        return builder.toString();
    }
}
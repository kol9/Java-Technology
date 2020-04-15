package ru.ifmo.rain.yarlychenko.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class Implementor implements Impler {
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


    private String generateImpl(Class<?> token) {
        CodeGenerator generator = new CodeGenerator(token);

        return generator
                .addPackage()
                .addHeader()
                .addMethods()
                .addFooter()
                .toString();
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (token.isPrimitive() || token == Enum.class ||
                Modifier.isFinal(token.getModifiers()) || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Token must be not private class or interface");
        }

        Path path = getPath(token, root);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(generateImpl(token));
        } catch (IOException e) {
            throw new ImplerException("Can't write to source file", e);
        }
    }
}

class CodeGenerator {
    private final static String SPACE = " ";
    private final static String TAB = "\t";
    private final static String COMMA = ",";
    private final static String LINE_SEPARATOR = System.lineSeparator();

    private final StringBuilder builder;
    private Class<?> token;

    public CodeGenerator(Class<?> token) {
        this.builder = new StringBuilder();
        this.token = token;
    }

    public CodeGenerator addPackage() {
        if (!token.getPackageName().equals("")) {
            builder.append("package").append(SPACE).append(token.getPackageName()).append(";").append(LINE_SEPARATOR);
        }
        builder.append(LINE_SEPARATOR);
        return this;
    }

    public CodeGenerator addHeader() {
        builder.append("public class ").append(token.getSimpleName()).append("Impl").append(SPACE)
                .append("implements").append(SPACE).append(token.getCanonicalName())
                .append(SPACE).append("{").append(LINE_SEPARATOR);
        return this;
    }

    private static String getReturnTypeAndName(Executable executable) {
        Method tmp = (Method) executable;
        return tmp.getReturnType().getCanonicalName() + SPACE + tmp.getName();
    }

    private static String getParameter(Parameter parameter) {
        return (parameter.getType().getCanonicalName() + SPACE) + parameter.getName();
    }

    private static String getArgs(Executable executable) {
        return Arrays.stream(executable.getParameters())
                .map(CodeGenerator::getParameter)
                .collect(Collectors.joining(COMMA + SPACE, "(", ")"));
    }

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

    private static String getMethodBody(Executable executable) {
        return "return" + getDefaultReturnValue(((Method) executable).getReturnType());
    }

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

    private static class SignatureComparedMethod {
        private Method method;
        private static final int POW = 47;
        private static final int MOD = 1_000_000_007;

        SignatureComparedMethod(Method method) {
            this.method = method;
        }

        Method getMethod() {
            return method;
        }

        @Override
        public int hashCode() {
            int hash = method.getReturnType().hashCode() % MOD;
            hash = (hash + POW * method.getName().hashCode()) % MOD;
            hash = (hash + POW * POW * Arrays.hashCode(method.getParameterTypes()));
            return hash;
        }

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

    public CodeGenerator addFooter() {
        builder.append("}").append(LINE_SEPARATOR);
        return this;
    }

    public String toString() {
        return builder.toString();
    }
}
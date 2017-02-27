package co.andrewbates.grade.sandbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.sun.tools.javac.api.JavacTool;

import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class Sandbox implements AutoCloseable {
    private ClassLoader classLoader;
    private Path sandboxDir;
    private JavacTool compiler;

    static {
        System.setSecurityManager(new SecurityManager());
        URL.setURLStreamHandlerFactory(new SandboxStreamHandlerFactory());
    }

    @SuppressWarnings("deprecation")
    public Sandbox(Path... sandboxPaths) throws IOException {
        this.sandboxDir = Files.createTempDirectory(null);
        for (Path sandboxPath : sandboxPaths) {
            copy(sandboxPath, this.sandboxDir);
        }
        this.classLoader = new ClassLoader(this);
        this.compiler = new JavacTool();

        if (this.compiler == null) {
            throw new RuntimeException("The JRE does not include a compiler!");
        }
    }

    private void deleteAll(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void copy(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                copy(file);
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.equals(source)) {
                    copy(dir);
                }
                return FileVisitResult.CONTINUE;
            }

            private void copy(Path fileOrDir) throws IOException {
                Files.copy(fileOrDir, destination.resolve(source.relativize(fileOrDir)));
            }
        });
    }

    public void close() throws IOException {
        deleteAll(sandboxDir);
    }

    private List<File> getFiles(String extension) throws IOException {
        ArrayList<File> files = new ArrayList<File>();
        Files.walkFileTree(sandboxDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(extension)) {
                    files.add(file.toFile());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public List<File> getJavaFiles() throws IOException {
        return getFiles(".java");
    }

    public Path getDir() {
        return sandboxDir;
    }

    public ArrayList<Class<?>> compileFiles() throws CompileException, IOException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        List<File> javaFiles = getJavaFiles();
        String[] filenames = new String[javaFiles.size()];
        for (int i = 0; i < javaFiles.size(); i++) {
            filenames[i] = javaFiles.get(i).getAbsolutePath();
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int result = compiler.run(null, output, output, filenames);
        if (result != 0) {
            throw new CompileException(output.toString());
        }

        for (File file : getClassFiles()) {
            File classFile = new File(file.getAbsolutePath());
            classes.add(classLoader.loadClass(classFile));
        }

        return classes;
    }

    private List<File> getClassFiles() throws IOException {
        return getFiles(".class");
    }

    public void run(Runnable runnable) {
        Thread thread = new Thread(runnable);

        thread.setContextClassLoader(classLoader);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}

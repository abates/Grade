package co.andrewbates.grade.sandbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.sun.tools.javac.api.JavacTool;

import co.andrewbates.grade.model.Student;
import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class Sandbox implements AutoCloseable {
    protected ClassLoader classLoader;
    protected File sandboxDir;
    private JavacTool compiler;

    static {
        System.setSecurityManager(new SecurityManager());
        URL.setURLStreamHandlerFactory(new SandboxStreamHandlerFactory());
    }

    public Sandbox(Student student) throws IOException {
        throw new RuntimeException("Need to re-implement this!");
        // this(student.getDir());
    }

    @SuppressWarnings("deprecation")
    public Sandbox(File... sandboxDirs) throws IOException {
        this.sandboxDir = Files.createTempDirectory(null).toFile();
        for (File sandboxDir : sandboxDirs) {
            copy(sandboxDir, this.sandboxDir);
        }
        this.classLoader = new ClassLoader(this);
        this.compiler = new JavacTool();

        if (this.compiler == null) {
            throw new RuntimeException("The JRE does not include a compiler!");
        }
    }

    private void deleteAll(File dir) {
        if (dir.isFile()) {
            dir.delete();
        } else if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteAll(file);
            }
            dir.delete();
        }
    }

    private void copy(File source, File destination) throws IOException {
        if (source.isFile()) {
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }

            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                File newDestination = new File(destination, file.getName());
                copy(file, newDestination);
            }
        }
    }

    public void close() throws IOException {
        deleteAll(sandboxDir);
    }

    public List<File> getJavaFiles() {
        ArrayList<File> files = new ArrayList<File>();
        getJavaFiles(files, sandboxDir);
        return files;
    }

    private void getJavaFiles(List<File> list, File dir) {
        if (dir.isFile() && dir.getName().endsWith(".java")) {
            list.add(dir);
        } else if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                getJavaFiles(list, file);
            }
        }
    }

    public File getDir() {
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

    private List<File> getClassFiles() {
        ArrayList<File> files = new ArrayList<File>();
        getClassFiles(files, sandboxDir);
        return files;
    }

    private void getClassFiles(List<File> list, File dir) {
        if (dir.isFile() && dir.getName().endsWith(".class")) {
            list.add(dir);
        } else if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                getClassFiles(list, file);
            }
        }
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
}

package co.andrewbates.grade.sandbox;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import co.andrewbates.grade.model.Student;

public class TestSandbox extends Sandbox {
    class TestSuite extends RunListener implements Runnable {
        SecurityException securityException;
        Class<?>[] testClasses;
        TestResults results = new TestResults();

        TestSuite(Class<?>[] testClasses) {
            this.testClasses = testClasses;
        }

        @Override
        public void run() {
            try {
                Runner suite = new Suite(new RunnerBuilder() {
                    @Override
                    public Runner runnerForClass(Class<?> testClass) throws Throwable {
                        return new SandboxTestRunner(classLoader, testClass);
                    }
                }, testClasses);

                JUnitCore junit = new JUnitCore();
                junit.addListener(this);
                junit.run(suite);
            } catch (InitializationError e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void testFinished(Description description) throws Exception {
            results.add(new TestResult(description));
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            results.add(new TestResult(failure));
        }
    }

    public static class CompileException extends Exception {
        private static final long serialVersionUID = 1L;

        public CompileException(String message) {
            super(message);
        }

        public CompileException(Throwable cause) {
            super(cause);
        }
    }

    public TestSandbox(Student student, File testDir) throws IOException {
        throw new RuntimeException("Need to re-implement this");
        // this(student.getDir(), testDir);
    }

    public TestSandbox(File... sandboxDirs) throws IOException {
        super(sandboxDirs);
    }

    public TestResults runTests() throws CompileException, IOException, InitializationError {
        return runTests(getTestClasses(compileFiles()));
    }

    private List<Class<?>> getTestClasses(List<Class<?>> classes) {
        ArrayList<Class<?>> testClasses = new ArrayList<Class<?>>();
        for (Class<?> clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(org.junit.Test.class)) {
                    testClasses.add(clazz);
                    break;
                }
            }
        }
        return testClasses;
    }

    public TestResults runTests(List<Class<?>> testClasses) throws IOException, InitializationError {
        return runTests(testClasses.toArray(new Class<?>[testClasses.size()]));
    }

    public TestResults runTests(Class<?>[] testClasses) throws InitializationError {
        TestSuite suite = new TestSuite(testClasses);
        run(suite);

        TestResults results = suite.results;

        return results;
    }
}

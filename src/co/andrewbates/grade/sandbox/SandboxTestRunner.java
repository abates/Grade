package co.andrewbates.grade.sandbox;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class SandboxTestRunner extends BlockJUnit4ClassRunner {
    private ClassLoader classLoader;

    public SandboxTestRunner(ClassLoader classLoader, Class<?> klass) throws InitializationError {
        super(klass);
        this.classLoader = classLoader;
    }

    // Runs junit tests in a separate thread using the custom class loader
    @Override
    public void run(final RunNotifier notifier) {
        Runnable runnable = () -> {
            super.run(notifier);
        };
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(classLoader);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

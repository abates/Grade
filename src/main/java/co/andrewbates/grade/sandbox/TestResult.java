package co.andrewbates.grade.sandbox;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class TestResult {

    private Description description;
    private Failure failure;

    public TestResult(Description description) {
        this.description = description;
    }

    public TestResult(Failure failure) {
        this(failure.getDescription());
        this.failure = failure;
    }

    public boolean passed() {
        return failure == null;
    }

    public Description getDescription() {
        return description;
    }

    public Failure getFailure() {
        return failure;
    }

    public boolean failed() {
        return failure != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestResult)) {
            return false;
        }

        return description.equals(((TestResult) obj).getDescription());
    }

    @Override
    public String toString() {
        if (failure == null) {
            return "Passed";
        } else if (failure.getException() instanceof AssertionError) {
            return failure.getMessage();
        }
        return failure.getException().toString();
    }
}

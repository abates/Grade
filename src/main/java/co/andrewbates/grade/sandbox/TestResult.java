package co.andrewbates.grade.sandbox;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class TestResult {

    private String message;
    private Description description;
    private Failure failure;

    public TestResult(Description description) {
        this(description, "Passed");
    }

    public TestResult(Description description, String message) {
        this.description = description;
        this.message = message;
    }

    public TestResult(Failure failure) {
        this(failure.getDescription(), failure.getMessage());
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
        return message;
    }
}

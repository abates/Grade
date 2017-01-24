package co.andrewbates.grade.sandbox;

import java.util.List;

import org.junit.runner.notification.Failure;

public class TestResults {
    int testCount;
    List<Failure> failures;

    public List<Failure> getFailures() {
        return failures;
    }

    public int getTestCount() {
        return testCount;
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (Failure failure : failures) {
            builder.append(failure.getTestHeader() + "\n" + failure.getMessage());
            builder.append("\n");
        }
        return builder.toString();
    }
}

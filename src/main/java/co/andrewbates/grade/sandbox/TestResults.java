package co.andrewbates.grade.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

public class TestResults {
    int testCount;
    int failedCount;

    Map<Description, TestResult> results = new HashMap<>();
    List<Failure> failures = new ArrayList<>();

    public void add(TestResult result) {
        if (!results.containsKey(result.getDescription())) {
            if (result.failed()) {
                failedCount++;
                failures.add(result.getFailure());
            }
            results.put(result.getDescription(), result);
        }
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public int getTestCount() {
        return results.size();
    }

    public int getFailedCount() {
        return failedCount;
    }

    public int getPassedCount() {
        return results.size() - failedCount;
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        for (TestResult result : results.values()) {
            builder.append("==============================================\n");
            builder.append(result.getDescription());
            builder.append(":\n");
            builder.append(result.toString());
            builder.append("\n\n");
        }
        return builder.toString();
    }
}

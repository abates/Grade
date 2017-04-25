package co.andrewbates.grade.rubric;

import java.nio.file.Path;

import org.junit.runners.model.InitializationError;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.model.Submission;
import co.andrewbates.grade.sandbox.TestResults;
import co.andrewbates.grade.sandbox.TestSandbox;
import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class UnitTestCriteria implements Criteria {
    private Path testPath;

    public UnitTestCriteria(Path testPath) {
        this.testPath = testPath;
    }

    @Override
    public void grade(Submission submission) throws Exception {
        TestSandbox sandbox = null;
        try {
            sandbox = new TestSandbox(Main.database.getSubmissionPath(submission), testPath);
            TestResults results = sandbox.runTests();
            int count = results.getTestCount();
            int score = results.getPassedCount();
            String message = results.getMessage();
            submission.setScore(new Score("test", score, count, message));
        } catch (InitializationError | CompileException e) {
            submission.setScore(new Score("test", 0, 1, e.getMessage()));
        } finally {
            if (sandbox != null) {
                sandbox.close();
            }
        }
    }
}

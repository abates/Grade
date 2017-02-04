package co.andrewbates.grade.rubric;

import java.io.File;
import java.io.IOException;

import org.junit.runners.model.InitializationError;

import co.andrewbates.grade.model.Student;
import co.andrewbates.grade.sandbox.TestResults;
import co.andrewbates.grade.sandbox.TestSandbox;
import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class UnitTestCriteria implements Criteria {
    private File testDirectory;

    public UnitTestCriteria(File testDirectory) {
        this.testDirectory = testDirectory;
    }

    @Override
    public void grade(Student student) {
        TestSandbox sandbox = null;
        try {
            sandbox = new TestSandbox(student, testDirectory);
            TestResults results = sandbox.runTests();
            int count = results.getTestCount();
            int score = results.getPassedCount();
            String message = results.getMessage();
            // student.setGrade(new Score("test", score, count, message));
        } catch (InitializationError | CompileException | IOException e) {
            // student.setGrade(new Score("test", 0, 1, e.getMessage()));
        } finally {
            try {
                if (sandbox != null) {
                    sandbox.close();
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

}

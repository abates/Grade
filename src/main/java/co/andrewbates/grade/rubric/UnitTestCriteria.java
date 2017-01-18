package co.andrewbates.grade.rubric;

import java.io.IOException;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import co.andrewbates.grade.GradePreferences;
import co.andrewbates.grade.Student;
import co.andrewbates.grade.sandbox.TestSandbox;
import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class UnitTestCriteria implements Criteria {
    @Override
    public void grade(Student student) {
        TestSandbox sandbox = null;
        try {
            sandbox = new TestSandbox(student, GradePreferences.getTestsDirectory());
            List<Failure> failures = sandbox.runTests();
            if (failures.size() == 0) {
                student.setGrade(new Score("test", 1, 1));
            } else {
                student.setGrade(new Score("test", 0, 1));

            }
        } catch (InitializationError | CompileException | IOException e) {
            student.setGrade(new Score("test", 0, 1, e.getMessage()));
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

package co.andrewbates.grade.rubric;

import java.io.ByteArrayOutputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import co.andrewbates.grade.Student;
import co.andrewbates.grade.Submission;

public class CompileCriteria implements Criteria {
    JavaCompiler compiler;

    public CompileCriteria() {
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    public void grade(Student student) {
        int range = 0;
        int score = 0;

        for (Submission submission : student.getSubmissions()) {
            range++;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int result = compiler.run(null, buffer, buffer, submission.getFile().getAbsolutePath());
            if (result == 0) {
                score++;
                submission.setGrade(new GradeResult("compile", 1, 1, buffer.toString()));
            } else {
                submission.setGrade(new GradeResult("compile", 0, 1, buffer.toString()));

            }
        }
        student.setGrade(new GradeResult("compile", score, range, "Composite Score"));
    }
}

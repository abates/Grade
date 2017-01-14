package co.andrewbates.grade.rubric;

import java.io.IOException;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import co.andrewbates.grade.Student;
import co.andrewbates.grade.sandbox.Sandbox;
import co.andrewbates.grade.sandbox.TestSandbox.CompileException;

public class CompileCriteria implements Criteria {
    JavaCompiler compiler;

    public CompileCriteria() {
        compiler = ToolProvider.getSystemJavaCompiler();
    }

    public void grade(Student student) {
        try {
            Sandbox sandbox = new Sandbox(student);
            sandbox.compileFiles();
            student.setGrade(new Score("compile", 1, 1));
            sandbox.close();
        } catch (CompileException | IOException e) {
            student.setGrade(new Score("compile", 0, 1, e.getMessage()));
        }
    }
}

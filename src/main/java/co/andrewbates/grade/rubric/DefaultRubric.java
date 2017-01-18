package co.andrewbates.grade.rubric;

import java.io.File;

public class DefaultRubric extends Rubric {
    public DefaultRubric(File testDir) {
        addCriteria(new CompileCriteria());
        addCriteria(new UnitTestCriteria(testDir));
    }
}

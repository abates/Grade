package co.andrewbates.grade.rubric;

import java.nio.file.Path;

public class DefaultRubric extends Rubric {
    public DefaultRubric(Path testDir) {
        addCriteria(new CompileCriteria());
        addCriteria(new UnitTestCriteria(testDir));
    }
}

package co.andrewbates.grade.rubric;

public class DefaultRubric extends Rubric {
    public DefaultRubric() {
        addCriteria(new CompileCriteria());
        addCriteria(new UnitTestCriteria());
    }
}

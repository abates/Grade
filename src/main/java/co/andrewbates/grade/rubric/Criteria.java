package co.andrewbates.grade.rubric;

import co.andrewbates.grade.Student;

public interface Criteria {
    public void grade(Student student) throws Exception;
}

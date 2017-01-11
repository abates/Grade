package co.andrewbates.grade;

import java.util.HashMap;

import co.andrewbates.grade.rubric.GradeResult;

public class Gradeable {

    protected HashMap<String, GradeResult> results;

    public Gradeable() {
        super();
    }

    public void setGrade(GradeResult grade) {
        results.put(grade.getName(), grade);
    }

}
package co.andrewbates.grade.rubric;

import co.andrewbates.grade.model.Submission;

public interface Criteria {
    public void grade(Submission submission) throws Exception;
}

package co.andrewbates.grade.task;

import java.util.List;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.model.Submission;
import co.andrewbates.grade.rubric.Criteria;
import co.andrewbates.grade.rubric.Rubric;
import javafx.concurrent.Task;

public class GradeTask extends Task<Void> {
    private Submission submission;
    private Rubric rubric;

    public GradeTask(Submission submission, Rubric rubric) {
        this.submission = submission;
        this.rubric = rubric;
    }

    @Override
    protected Void call() throws Exception {
        submission.setStatus(Submission.Status.NOTGRADED);
        updateProgress(0.0, 1.0);
        List<Criteria> criteria = rubric.getCriteria();
        for (int i = 0; i < criteria.size(); i++) {
            criteria.get(i).grade(submission);
            updateProgress((double) i / criteria.size(), 1.0);
        }
        updateProgress(1.0, 1.0);
        Main.database.save(submission);
        succeeded();
        return null;
    }
}

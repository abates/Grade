package co.andrewbates.grade.task;

import java.util.List;

import co.andrewbates.grade.model.Student;
import co.andrewbates.grade.rubric.Criteria;
import co.andrewbates.grade.rubric.Rubric;
import javafx.concurrent.Task;

public class GradeTask extends Task<Void> {
    private Student student;
    private Rubric rubric;

    public GradeTask(Student student, Rubric rubric) {
        this.student = student;
        this.rubric = rubric;
    }

    @Override
    protected Void call() {
        updateProgress(0.0, 1.0);
        List<Criteria> criteria = rubric.getCriteria();
        for (int i = 0; i < criteria.size(); i++) {
            criteria.get(i).grade(student);
            updateProgress((double) i / criteria.size(), 1.0);
        }
        updateProgress(1.0, 1.0);
        succeeded();
        return null;
    }

}

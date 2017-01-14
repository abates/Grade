package co.andrewbates.grade.rubric;

import javax.swing.SwingWorker;

import co.andrewbates.grade.Student;

public class Workflow extends SwingWorker<Void, Void> {
    private Rubric rubric;
    private Student student;

    public Workflow(Student student, Rubric rubric) {
        this.student = student;
        this.rubric = rubric;
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (Criteria criteria : rubric.getCriteria()) {
            criteria.grade(student);
        }
        return null;
    }
}

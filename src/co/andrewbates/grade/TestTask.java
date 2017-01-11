package co.andrewbates.grade;

import javax.swing.SwingWorker;

public class TestTask extends SwingWorker<Void, Void> {
    Student student;

    public TestTask(Student student) {
        this.student = student;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}

package co.andrewbates.grade.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.andrewbates.grade.Student;
import co.andrewbates.grade.rubric.Rubric;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GradeAllTask extends Task<Void> {
    private ThreadPoolExecutor executor;
    private ObservableList<Student> students;
    private double done = 0;
    private Rubric rubric;

    public GradeAllTask(ObservableList<Student> students, Rubric rubric) {
        executor = new ThreadPoolExecutor(0, 10, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(students.size())) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                done++;
                updateProgress(done / students.size(), 1.0);
                if (done == students.size()) {
                    shutdown();
                }
            }
        };
        this.students = students;
        this.rubric = rubric;

    }

    @Override
    protected Void call() throws InterruptedException {
        for (Student student : students) {
            executor.execute(new GradeTask(student, rubric));
        }

        executor.awaitTermination(1, TimeUnit.DAYS);
        succeeded();
        return null;
    }

}

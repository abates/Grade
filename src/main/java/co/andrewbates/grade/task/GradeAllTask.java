package co.andrewbates.grade.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.andrewbates.grade.model.Submission;
import co.andrewbates.grade.rubric.Rubric;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class GradeAllTask extends Task<Void> {
    private ThreadPoolExecutor executor;
    private ObservableList<Submission> submissions;
    private double done = 0;
    private Rubric rubric;

    public GradeAllTask(ObservableList<Submission> submissions, Rubric rubric) {
        executor = new ThreadPoolExecutor(0, 10, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(submissions.size())) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                done++;
                updateProgress(done / submissions.size(), 1.0);
                if (done == submissions.size()) {
                    shutdown();
                }
            }
        };
        this.submissions = submissions;
        this.rubric = rubric;

    }

    @Override
    protected Void call() throws InterruptedException {
        for (Submission submission : submissions) {
            executor.execute(new GradeTask(submission, rubric));
        }

        executor.awaitTermination(1, TimeUnit.DAYS);
        succeeded();
        return null;
    }

}

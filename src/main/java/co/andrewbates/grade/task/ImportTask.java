package co.andrewbates.grade.task;

import java.io.File;
import java.io.IOException;

import co.andrewbates.grade.Main;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Offering;
import co.andrewbates.grade.model.Submission;
import javafx.concurrent.Task;

public class ImportTask extends Task<Void> {
    private StringBuilder output = new StringBuilder();
    private File folder;
    private Offering offering;
    private Assignment assignment;

    public ImportTask(File folder, Offering offering, Assignment assignment) {
        this.folder = folder;
        this.offering = offering;
        this.assignment = assignment;
    }

    @Override
    protected Void call() throws IOException {
        updateProgress(0, 1.0);
        File[] files = folder.listFiles();
        // TODO: This needs to be updated to use the NIO file walk utilities
        // it also should filter out anything that isn't a .java file
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            if (filename.indexOf("assignsubmission_file") > 0) {
                String[] tokens = files[i].getName().split("_");

                String studentName = tokens[0];

                Submission submission = getSubmission(studentName);
                Main.database.copyFileToSubmission(files[i], submission);
            } else if (files[i].isDirectory()) {
                String studentName = files[i].getName();
                Submission submission = getSubmission(studentName);
                for (File file : files[i].listFiles()) {
                    Main.database.copyFileToSubmission(file, submission);
                }
                log("Imported " + studentName);
            }
            updateProgress((double) i / (files.length - 1), 1.0);
        }
        succeeded();
        return null;
    }

    private Submission getSubmission(String studentName) throws IOException {
        Submission submission = Main.database.getSubmission(offering, assignment, studentName);

        if (submission == null) {
            submission = new Submission();
            submission.setStudentName(studentName);
            submission.setOfferingID(offering.getID());
            submission.setAssignmentID(assignment.getID());
            submission.setStatus(Submission.Status.NOTGRADED);
            Main.database.save(submission);
        }

        return submission;
    }

    private void log(String message) {
        output.append(message);
        output.append("\n");
        updateMessage(output.toString());
    }
}
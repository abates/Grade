package co.andrewbates.grade.task;

import java.io.File;

import co.andrewbates.grade.Main;
import javafx.concurrent.Task;

public class ImportTask extends Task<String> {
    StringBuilder output;
    File folder;

    public ImportTask(File folder) {
        this.folder = folder;
        output = new StringBuilder();
    }

    @Override
    protected String call() {
        updateProgress(0, 1.0);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            if (filename.indexOf("assignsubmission_file") > 0) {
                String[] tokens = files[i].getName().split("_");

                File destination = new File(folder.getAbsolutePath() + "/" + tokens[0]);
                if (!destination.exists()) {
                    if (destination.mkdirs()) {
                        log("Creating directory " + destination.getPath());
                        String studentName = tokens[0];
                        Main.students.findOrCreate(studentName, destination);
                    } else {
                        log("Failed to create " + destination.getAbsolutePath() + "\n");
                    }
                }

                if (destination.exists()) {
                    File newFile = new File(destination.getAbsolutePath() + "/" + tokens[4]);
                    if (!files[i].renameTo(newFile)) {
                        log("Failed to rename " + files[i].getName() + "\n");
                    }
                }
            } else if (files[i].isDirectory()) {
                String studentName = files[i].getName();
                Main.students.findOrCreate(studentName, files[i]);
                log("Imported " + studentName);
            }
            updateProgress((double) i / (files.length - 1), 1.0);
        }
        succeeded();
        return output.toString();
    }

    private void log(String message) {
        output.append(message);
        output.append("\n");
        updateValue(output.toString());
    }
}
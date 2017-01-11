package co.andrewbates.grade;

import java.io.File;

import javax.swing.SwingWorker;

public class ImportTask extends SwingWorker<Void, Void> {
    private File folder;
    private LogField logger;
    private StudentController students;

    public ImportTask(File folder, LogField logger, StudentController students) {
        this.folder = folder;
        this.logger = logger;
        this.students = students;
    }

    @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            if (filename.indexOf("assignsubmission_file") > 0) {
                String[] tokens = files[i].getName().split("_");

                File destination = new File(folder.getAbsolutePath() + "/" + tokens[0]);
                if (!destination.exists() && destination.mkdirs()) {
                    logger.append("Creating directory " + destination.getPath());
                } else {
                    logger.append("Failed to create " + destination.getAbsolutePath() + "\n");
                }

                if (destination.exists()) {
                    File newFile = new File(destination.getAbsolutePath() + "/" + tokens[4]);
                    if (files[i].renameTo(newFile)) {
                        importFile(newFile);
                    } else {
                        logger.append("Failed to rename " + files[i].getName() + "\n");
                    }
                }
            } else if (files[i].isDirectory()) {
                importDirectory(files[i]);
            }
            setProgress(100 * i / (files.length - 1));
        }

        return null;
    }

    private void importDirectory(File directory) {
        if (!directory.isDirectory()) {
            return;
        }

        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                importFile(file);
            }
        }
    }

    private void importFile(File file) {
        if (file.getName().endsWith(".java")) {
            String studentName = file.getParentFile().getName();
            Student student = students.findOrCreate(studentName, file.getParentFile());
            logger.append("Importing " + file.getName() + "\n");
            student.addSubmission(new Submission(file));
        }
    }
}
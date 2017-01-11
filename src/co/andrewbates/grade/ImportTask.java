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
                if (!destination.exists()) {
                    if (destination.mkdirs()) {
                        logger.append("Creating directory " + destination.getPath());
                        String studentName = tokens[0];
                        students.findOrCreate(studentName, destination);
                    } else {
                        logger.append("Failed to create " + destination.getAbsolutePath() + "\n");
                    }
                }

                if (destination.exists()) {
                    File newFile = new File(destination.getAbsolutePath() + "/" + tokens[4]);
                    if (!files[i].renameTo(newFile)) {
                        logger.append("Failed to rename " + files[i].getName() + "\n");
                    }
                }
            } else if (files[i].isDirectory()) {
                String studentName = files[i].getName();
                students.findOrCreate(studentName, files[i]);
                logger.append("Imported " + studentName);
            }
            setProgress(100 * i / (files.length - 1));
        }

        return null;
    }
}
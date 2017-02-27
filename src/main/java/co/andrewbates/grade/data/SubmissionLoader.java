package co.andrewbates.grade.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import co.andrewbates.grade.model.Submission;

public class SubmissionLoader extends BaseModelLoader<Submission> {
    public SubmissionLoader(Path baseDirectory) {
        super(Submission.class);
        setPath(baseDirectory);
    }

    public Path getPath(Submission submission) {
        return getPath().resolve(submission.getStudentName());
    }

    public void copyFile(File file, Submission submission) throws IOException {
        Path fileDir = getPath(submission).resolve("files");
        if (!fileDir.toFile().exists()) {
            fileDir.toFile().mkdirs();
        }
        Files.copy(file.toPath(), fileDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }
}

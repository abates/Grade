package co.andrewbates.grade;

import java.io.File;
import java.util.HashMap;

import co.andrewbates.grade.rubric.GradeResult;

public class Submission extends Gradeable {
    private File file;

    public Submission(File file) {
        this.file = file;
        this.results = new HashMap<String, GradeResult>();
    }

    public File getFile() {
        return file;
    }

    public File getClassFile() {
        return new File(file.getAbsolutePath().replaceFirst("\\.java$", "\\.class"));
    }

    public String toString() {
        return file.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Submission other = (Submission) obj;
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!file.equals(other.file)) {
            return false;
        }
        return true;
    }
}

package co.andrewbates.grade;

import java.io.File;
import java.util.HashSet;

public class Student extends Gradeable {
    private String name;
    private File dir;
    private HashSet<Submission> submissions;

    public Student(String name, File dir) {
        this.name = name;
        this.dir = dir;
        this.submissions = new HashSet<Submission>();
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return name;
    }

    public void addSubmission(Submission submission) {
        submissions.add(submission);
    }

    public Submission[] getSubmissions() {
        return submissions.toArray(new Submission[submissions.size()]);
    }

    public boolean isCompiled() {
        return false;
    }

    public boolean isValid() {
        return false;
    }

    public File getDir() {
        return dir;
    }
}

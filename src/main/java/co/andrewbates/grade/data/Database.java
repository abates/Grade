package co.andrewbates.grade.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.controlsfx.dialog.ExceptionDialog;

import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import co.andrewbates.grade.model.SchoolYear;
import co.andrewbates.grade.model.Submission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class Database {
    public class Loader extends Task<String> {
        private Path baseDirectory;
        private ModelLoader<?>[] loaders;

        Loader(Path baseDirectory) {
            this.baseDirectory = baseDirectory;
            // @formatter:off
            this.loaders = new ModelLoader[]{
                    courseLoader,
                    schoolYearLoader,
                    assignmentLoader,
                    offeringLoader,
            };
        }

        protected synchronized void addProgress(double amount) {
            updateProgress(getProgress() + amount/loaders.length, 1.0);
        }
        
        @Override
        protected String call() throws Exception {
            System.err.println("Loading from " + baseDirectory);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            updateProgress(0.0, loaders.length);
            for (int i=0; i<loaders.length; i++) {
                Task<Void> task = loaders[i].loadAll(baseDirectory);
                task.progressProperty().addListener((o, ov, nv) -> {
                    addProgress(nv.doubleValue());
                });
                
                task.messageProperty().addListener((o, ov, nv) -> {
                    updateValue(nv);
                });
                
                task.onFailedProperty().addListener(ev -> {
                    ExceptionDialog dialog = new ExceptionDialog(task.getException());
                    dialog.showAndWait();
                });
                executor.execute(task);
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            succeeded();
            return "Done";
        }
    }

    private static Database instance;
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    public static Loader load(Path baseDirectory) {
        Database instance = getInstance();
        return instance.new Loader(baseDirectory);
    }

    private CourseLoader courseLoader;

    private AssignmentLoader assignmentLoader;

    private SchoolYearLoader schoolYearLoader;

    private OfferingLoader offeringLoader;
    
    private Database() {
        courseLoader = new CourseLoader();
        schoolYearLoader = new SchoolYearLoader();
        assignmentLoader = new AssignmentLoader();
        offeringLoader = new OfferingLoader();
    }

    public ObservableList<Assignment> assignments(Course course) {
        return assignmentLoader.get(course);
    }

    public ObservableList<Course> courses() {
        return courseLoader.list();
    }

    public void delete(Assignment assignment) throws IOException {
        assignmentLoader.delete(assignment);
    }

    public void delete(Course course) throws IOException {
        Iterator<Assignment> assignment = assignments(course).iterator();
        for (;assignment.hasNext();) {
            assignment.next();
            assignment.remove();
        }
        courseLoader.delete(course);
    }
    
    public void delete(Offering offering) throws IOException {
        offeringLoader.delete(offering);
    }

    public ObservableList<Offering> offerings() {
        return offeringLoader.list();
    }
    
    public ObservableList<Offering> offerings(SchoolYear schoolYear) {
        return offeringLoader.offerings(schoolYear.getID());
    }

    public void save(Assignment assignment) throws IOException {
        assignmentLoader.save(assignment);
    }

    public void save(Course course) throws IOException {
        courseLoader.save(course);
    }
    
    public void save(Submission submission) throws IOException {
        getSubmissionLoader(submission).save(submission);
    }
    
    public void save(Model model) throws IOException {
        if (model instanceof Course) {
            save((Course)model);
        } else if (model instanceof Assignment) {
            save((Assignment)model);
        } else if (model instanceof SchoolYear) {
            save((SchoolYear)model);
        } else if (model instanceof Offering) {
            save((Offering)model);
        } else if (model instanceof Submission) {
            save((Submission)model);
        } else {
            throw new RuntimeException("Cannot handle type " + model.getClass());
        }
    }

    public void save(Offering offering) throws IOException {
        offeringLoader.save(offering);
    }
    
    public void save(SchoolYear schoolYear) throws IOException {
        schoolYearLoader.save(schoolYear);
    }

    public ObservableList<SchoolYear> schoolYears() {
        return schoolYearLoader.list();
    }
    
    public Course getCourse(UUID courseID) {
        return courseLoader.get(courseID);
    }

    public SchoolYear getSchoolYear(UUID yearID) {
        return schoolYearLoader.get(yearID);
    }
    
    public ObservableList<Submission> submissions(Offering offering, Assignment assignment) {
        SubmissionLoader loader = getSubmissionLoader(offering, assignment);
        try {
            new Thread(loader.loadAll()).start();
        } catch (DataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return loader.list();
    }

    public void copyFileToAssignment(File file, Assignment assignment) throws IOException {
        assignmentLoader.copyFile(file, assignment);
    }

    private SubmissionLoader getSubmissionLoader(Submission submission) {
        Offering offering = getOffering(submission.getOfferingID());
        Assignment assignment = getAssignment(submission.getAssignmentID());
        return getSubmissionLoader(offering, assignment);
    }
    
    private SubmissionLoader getSubmissionLoader(Offering offering, Assignment assignment) {
        Path path = offeringLoader.getPath(offering).resolve("assignments").resolve(assignment.getName());
        return new SubmissionLoader(path);
    }

    public void copyFileToSubmission(File file, Submission submission) throws IOException {
        getSubmissionLoader(submission).copyFile(file, submission);
    }

    public Submission getSubmission(Offering offering, Assignment assignment, String studentName) throws DataException {
        return getSubmissionLoader(offering, assignment).load(studentName);
    }

    public Offering getOffering(UUID offeringID) {
        return offeringLoader.get(offeringID);
    }

    public Assignment getAssignment(UUID assignmentID) {
        return assignmentLoader.get(assignmentID);
    }


    ObservableList<File> getJavaFiles(Path path) {
        File[] files = new File[] {};
        if (path.toFile().exists()) {
            files = path.toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".java");
                }
            });
        }
        return FXCollections.observableArrayList(files);
    }
    
    public ObservableList<File> getTestFiles(Assignment assignment) {
        
        return getJavaFiles(getTestPath(assignment));
    }

    public ObservableList<File> getSubmissionFiles(Submission submission) {
        return getJavaFiles(getSubmissionPath(submission));
    }

    public Path getTestPath(Assignment assignment) {
        return assignmentLoader.getPath(assignment).resolve("tests");
    }

    public Path getSubmissionPath(Submission submission) {
        return getSubmissionLoader(submission).getPath(submission).resolve("files");
    }
}

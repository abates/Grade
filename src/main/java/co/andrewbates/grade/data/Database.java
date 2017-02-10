package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.controlsfx.dialog.ExceptionDialog;

import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.SchoolYear;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class Database {
    HashMap<String, Class<?>> modelClasses;

    private CourseLoader courseLoader;
    private StudentLoader studentLoader;
    private AssignmentLoader assignmentLoader;
    private SchoolYearLoader schoolYearLoader;

    private static Database instance;

    public class Loader extends Task<String> {
        private Path baseDirectory;
        private ModelLoader<?>[] loaders;

        Loader(Path baseDirectory) {
            this.baseDirectory = baseDirectory;
            // @formatter:off
            this.loaders = new ModelLoader[]{
                    courseLoader,
                    studentLoader,
                    schoolYearLoader,
                    assignmentLoader,
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
                Task<Void> task = loaders[i].load(baseDirectory);
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

    private Database() {
        courseLoader = new CourseLoader();
        studentLoader = new StudentLoader();
        schoolYearLoader = new SchoolYearLoader();
        assignmentLoader = new AssignmentLoader();
    }

    public ObservableList<SchoolYear> schoolYears() {
        return schoolYearLoader.list();
    }

    public ObservableList<Assignment> assignments(Course course) {
        return assignmentLoader.get(course);
    }

    public void deleteAssignment(Assignment assignment) {
        assignmentLoader.delete(assignment);
    }

    public void create(Course course) throws IOException {
        courseLoader.create(course);
    }

    public void save(Course course) throws IOException {
        courseLoader.save(course);
    }

    public void delete(Course course) {
        courseLoader.delete(course);
    }

    public ObservableList<Course> courses() {
        return courseLoader.list();
    }

    public void create(Assignment assignment) throws IOException {
        assignmentLoader.create(assignment);
    }
    
    public void save(Assignment assignment) throws IOException {
        assignmentLoader.save(assignment);
    }
}

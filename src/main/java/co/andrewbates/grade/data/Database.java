package co.andrewbates.grade.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.andrewbates.grade.data.DatabaseEventHandler.DatabaseEvent;
import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import co.andrewbates.grade.model.Offering;
import co.andrewbates.grade.model.SchoolYear;
import co.andrewbates.grade.model.Submission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.event.EventType;

public class Database {
    private HashMap<Class<?>, DatabaseEventHandler> eventHandlers = new HashMap<>();

    private ModelLoader<Course> courseLoader;

    private ModelLoader<SchoolYear> schoolYearLoader;

    private ModelLoader<Assignment> assignmentLoader;

    private ModelLoader<Offering> offeringLoader;

    private ModelLoader<Submission> submissionLoader;

    public Database(Path basePath) {
        courseLoader = new ModelLoader<>(Course.class, basePath);
        schoolYearLoader = new ModelLoader<>(SchoolYear.class, basePath);
        assignmentLoader = new ModelLoader<>(Assignment.class, basePath);
        offeringLoader = new ModelLoader<>(Offering.class, basePath);
        submissionLoader = new ModelLoader<>(Submission.class, basePath);
    }

    public void copyFileToAssignment(File file, Assignment assignment) throws IOException {
        Path testDir = getTestPath(assignment);
        if (!testDir.toFile().exists()) {
            testDir.toFile().mkdirs();
        }
        Files.copy(file.toPath(), testDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }

    public void copyFileToSubmission(File file, Submission submission) throws IOException {
        Path fileDir = getSubmissionPath(submission);
        if (!fileDir.toFile().exists()) {
            fileDir.toFile().mkdirs();
        }
        Files.copy(file.toPath(), fileDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }

    public ObservableList<Course> courses() {
        return courseLoader.all();
    }

    protected void fireEvent(EventType<?> type, Model target) {
        DatabaseEventHandler handler = eventHandlers.get(target.getClass());
        if (handler != null) {
            handler.fire(type, target);
        }
    }

    public void delete(Model model) throws IOException {
        if (model instanceof Assignment) {
            for (Submission submission : getSubmissions((Assignment) model)) {
                delete(submission);
            }
            assignmentLoader.delete((Assignment) model);
        } else if (model instanceof Course) {
            courseLoader.delete((Course) model);
        } else if (model instanceof Offering) {
            Offering offering = (Offering) model;
            Course course = courseLoader.get(offering.getCourseID());
            for (Assignment assignment : getAssignments(course)) {
                for (Submission submission : getSubmissions(offering, assignment)) {
                    delete(submission);
                }
            }
            offeringLoader.delete((Offering) model);
        } else if (model instanceof SchoolYear) {
            for (Offering offering : getOfferings((SchoolYear) model)) {
                delete(offering);
            }
            schoolYearLoader.delete((SchoolYear) model);
        } else if (model instanceof Submission) {
            submissionLoader.delete((Submission) model);
        } else {
            throw new RuntimeException("Cannot handle type " + model.getClass());
        }
    }

    public ObservableList<Assignment> getAssignments(Course course) throws IOException {
        Stream<Assignment> assignments = assignmentLoader.stream().filter(v -> {
            return v.getCourseID().equals(course.getID());
        });
        return FXCollections.observableArrayList(assignments.collect(Collectors.toList()));
    }

    public Course getCourse(UUID courseID) throws IOException {
        return courseLoader.get(courseID);
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

    public ObservableList<Offering> getOfferings(Course course) throws IOException {
        Stream<Offering> offerings = offeringLoader.stream().filter(v -> {
            return v.getCourseID().equals(course.getID());
        });
        return FXCollections.observableArrayList(offerings.collect(Collectors.toList()));
    }

    public ObservableList<Offering> getOfferings(SchoolYear schoolYear) throws IOException {
        Stream<Offering> offerings = offeringLoader.stream().filter(v -> {
            return v.getSchoolYearID().equals(schoolYear.getID());
        });
        return FXCollections.observableArrayList(offerings.collect(Collectors.toList()));
    }

    public SchoolYear getSchoolYear(UUID yearID) throws IOException {
        return schoolYearLoader.get(yearID);
    }

    public Submission getSubmission(Offering offering, Assignment assignment, String studentName) throws IOException {
        Stream<Submission> submissions = submissionLoader.stream().filter(v -> {
            return v.getOfferingID().equals(offering.getID()) && v.getAssignmentID().equals(assignment.getID())
                    && v.getStudentName().equals(studentName);
        });
        Optional<Submission> o = submissions.findFirst();

        if (o.isPresent()) {
            return o.get();
        }
        return null;
    }

    public ObservableList<File> getSubmissionFiles(Submission submission) throws IOException {
        return getJavaFiles(getSubmissionPath(submission));
    }

    public Path getSubmissionPath(Submission submission) throws IOException {
        return submissionLoader.getPath(submission).resolve("files");
    }

    public ObservableList<Submission> getSubmissions(Assignment assignment) throws IOException {
        Stream<Submission> submissions = submissionLoader.stream().filter(v -> {
            return v.getAssignmentID().equals(assignment.getID());
        });

        return FXCollections.observableArrayList(submissions.collect(Collectors.toList()));
    }

    public ObservableList<Submission> getSubmissions(Offering offering, Assignment assignment) throws IOException {
        Stream<Submission> submissions = submissionLoader.stream().filter(v -> {
            return v.getOfferingID().equals(offering.getID()) && v.getAssignmentID().equals(assignment.getID());
        });

        return FXCollections.observableArrayList(submissions.collect(Collectors.toList()));
    }

    public ObservableList<File> getTestFiles(Assignment assignment) throws IOException {
        return getJavaFiles(getTestPath(assignment));
    }

    public Path getTestPath(Assignment assignment) throws IOException {
        return assignmentLoader.getPath(assignment).resolve("tests");
    }

    public Path getTestPath(Submission submission) throws IOException {
        return getTestPath(assignmentLoader.get(submission.getAssignmentID()));
    }

    public void addHandler(Class<? extends Model> clazz, EventType<DatabaseEvent> type,
            EventHandler<DatabaseEvent> handler) {
        DatabaseEventHandler databaseHandler = eventHandlers.get(type);
        if (databaseHandler == null) {
            databaseHandler = new DatabaseEventHandler();
            eventHandlers.put(clazz, databaseHandler);
        }
        databaseHandler.register(type, handler);
    }

    public void save(Model model) throws IOException {
        if (model instanceof Assignment) {
            assignmentLoader.save((Assignment) model);
        } else if (model instanceof Course) {
            courseLoader.save((Course) model);
        } else if (model instanceof Offering) {
            offeringLoader.save((Offering) model);
        } else if (model instanceof SchoolYear) {
            schoolYearLoader.save((SchoolYear) model);
        } else if (model instanceof Submission) {
            submissionLoader.save((Submission) model);
        } else {
            throw new RuntimeException("Cannot handle type " + model.getClass());
        }
    }

    public Task<ObservableList<SchoolYear>> schoolYearsTask() {
        return schoolYearLoader.allTask();
    }
}

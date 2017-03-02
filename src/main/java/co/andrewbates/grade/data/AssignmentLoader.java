package co.andrewbates.grade.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import co.andrewbates.grade.model.Assignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AssignmentLoader extends BaseModelLoader<Assignment> {
    public final String TESTDIR_PATH = "tests";

    Map<UUID, ObservableList<Assignment>> assignments = new HashMap<UUID, ObservableList<Assignment>>();

    Path dir;

    public AssignmentLoader() {
        super(Assignment.class);
    }

    private void addAssignment(Assignment assignment) {
        if (!assignments.containsKey(assignment.getCourseID())) {
            assignments.put(assignment.getCourseID(), FXCollections.observableArrayList());
        }
        assignments.get(assignment.getCourseID()).add(assignment);
    }

    @Override
    protected void initialize(Assignment assignment) {
        addAssignment(assignment);
    }

    public ObservableList<Assignment> get(BaseModel course) {
        ObservableList<Assignment> list = assignments.get(course.getID());
        if (list == null) {
            list = FXCollections.observableArrayList();
            assignments.put(course.getID(), list);
        }
        return list;
    }

    @Override
    public void save(Assignment assignment) throws IOException {
        if (assignment.getID() == null) {
            addAssignment(assignment);
        }
        super.save(assignment);
    }

    @Override
    public void delete(Assignment assignment) throws IOException {
        super.delete(assignment);
        if (assignments.containsKey(assignment.getCourseID())) {
            assignments.get(assignment.getCourseID()).remove(assignment);
        }
    }

    @Override
    public Path getPath() {
        return super.getPath().resolve("assignments");
    }

    public void copyFile(File file, Assignment assignment) throws IOException {
        Path testDir = getPath(assignment).resolve("tests");
        if (!testDir.toFile().exists()) {
            testDir.toFile().mkdirs();
        }
        Files.copy(file.toPath(), testDir.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }
}

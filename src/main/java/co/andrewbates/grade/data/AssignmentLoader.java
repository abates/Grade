package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import co.andrewbates.grade.model.Assignment;
import co.andrewbates.grade.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class AssignmentLoader extends BaseModelLoader<Assignment> {
    public final String TESTDIR_PATH = "tests";

    Map<UUID, ObservableList<Assignment>> assignments;

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

    public Task<Void> load(Path dir) throws DataException {
        this.dir = dir;
        final Task<Void> parent = super.load(dir);
        return new Task<Void>() {
            private void addProgress(double amount) {
                updateProgress(amount * 0.5, 1.0);
            }

            @Override
            protected Void call() throws Exception {
                Thread t = new Thread(parent);
                parent.progressProperty().addListener((o, ov, nv) -> {
                    addProgress(nv.doubleValue());
                });
                t.start();
                t.join();

                assignments = new HashMap<UUID, ObservableList<Assignment>>();
                ObservableList<Assignment> list = list();
                for (int i = 0; i < list.size(); i++) {
                    Assignment assignment = list.get(i);
                    addAssignment(assignment);
                    updateProgress(0.5 + (0.5 * i / list.size()), 1.0);
                }
                updateProgress(1.0, 1.0);
                succeeded();
                return null;
            }
        };
    }

    public ObservableList<Assignment> get(Course course) {
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
    public String getPath() {
        return "assignments";
    }
}

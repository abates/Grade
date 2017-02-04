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
    Map<UUID, ObservableList<Assignment>> assignments;

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
                    addAssignment(list.get(i));
                    updateProgress(0.5 + (0.5 * i / list.size()), 1.0);
                }
                updateProgress(1.0, 1.0);
                succeeded();
                return null;
            }
        };
    }

    public ObservableList<Assignment> get(Course course) {
        return assignments.get(course.getID());
    }

    @Override
    public void create(Assignment assignment) throws IOException {
        super.create(assignment);
        addAssignment(assignment);
    }

    @Override
    public void delete(Assignment assignment) {
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

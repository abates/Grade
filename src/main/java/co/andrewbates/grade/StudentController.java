package co.andrewbates.grade;

import java.io.File;
import java.util.Collection;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentController {
    ObservableList<String> studentNames = FXCollections.observableArrayList();
    TreeMap<String, Student> students = new TreeMap<String, Student>();

    public Collection<Student> all() {
        return students.values();
    }

    public Student add(Student student) {
        studentNames.add(student.getName());
        students.put(student.getName(), student);
        return student;
    }

    public Student find(String name) {
        return students.get(name);
    }

    public Student findOrCreate(String name, File dir) {
        Student student = students.get(name);
        if (student == null) {
            student = add(new Student(name, dir));
        }
        return student;
    }

    public ObservableList<String> studentNames() {
        return studentNames;
    }

    public ObservableList<Student> students() {
        return FXCollections.observableArrayList(students.values());
    }
}

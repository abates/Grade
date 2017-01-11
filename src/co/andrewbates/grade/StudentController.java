package co.andrewbates.grade;

import java.io.File;
import java.util.Collection;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

public class StudentController {
    DefaultListModel<String> studentNames = new DefaultListModel<String>();
    TreeMap<String, Student> students = new TreeMap<String, Student>();

    public Collection<Student> all() {
        return students.values();
    }

    public Student add(Student student) {
        studentNames.addElement(student.getName());
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

    public ListModel<String> getStudentNames() {
        return studentNames;
    }
}

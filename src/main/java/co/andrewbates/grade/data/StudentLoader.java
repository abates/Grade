package co.andrewbates.grade.data;

import co.andrewbates.grade.model.Student;

public class StudentLoader extends BaseModelLoader<Student> {
    public StudentLoader() {
        super(Student.class);
    }

    @Override
    public String getPath() {
        return "students";
    }
}

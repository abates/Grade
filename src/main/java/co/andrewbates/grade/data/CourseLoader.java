package co.andrewbates.grade.data;

import java.nio.file.Path;

import co.andrewbates.grade.model.Course;

public class CourseLoader extends BaseModelLoader<Course> {
    public CourseLoader() {
        super(Course.class);
    }

    @Override
    public Path getPath() {
        return super.getPath().resolve("courses");
    }

}

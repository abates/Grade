package co.andrewbates.grade.data;

import co.andrewbates.grade.model.Course;

public class CourseLoader extends BaseModelLoader<Course> {
    public CourseLoader() {
        super(Course.class);
    }

    @Override
    public String getPath() {
        return "courses";
    }

}

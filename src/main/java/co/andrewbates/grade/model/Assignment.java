package co.andrewbates.grade.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "assignments")
public class Assignment extends BaseModel {
    private static final long serialVersionUID = 1L;
    private long courseID;

    public long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    public String toString() {
        return getName();
    }
}

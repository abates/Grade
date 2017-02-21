package co.andrewbates.grade.model;

import co.andrewbates.grade.data.BaseModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Course extends BaseModel {
    private SimpleStringProperty name = new SimpleStringProperty();

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public String toString() {
        return getName();
    }
}

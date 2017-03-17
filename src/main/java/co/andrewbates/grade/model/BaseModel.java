package co.andrewbates.grade.model;

import java.io.Serializable;

import co.andrewbates.grade.data.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class BaseModel implements Model, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    private StringProperty nameProperty = new SimpleStringProperty();

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public final String getName() {
        return nameProperty().get();
    }

    public final void setName(String name) {
        nameProperty().set(name);
    }

    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Model o) {
        return getName().compareTo(((BaseModel) o).getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof BaseModel)) {
            return false;
        }

        BaseModel other = (BaseModel) obj;
        return id == other.getID();
    }
}

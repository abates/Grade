package co.andrewbates.grade.model;

import java.io.Serializable;
import java.util.UUID;

import co.andrewbates.grade.data.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class BaseModel implements Model, Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
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
    public int compareTo(Model obj) {
        if (this == obj) {
            return 0;
        }

        if (obj == null) {
            return -1;
        }

        BaseModel other = (BaseModel) obj;

        if (getName() == null && other.getName() == null) {
            return 0;
        }

        if (getName() == null && other.getName() != null) {
            return 1;
        }

        return getName().compareTo(other.getName());
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

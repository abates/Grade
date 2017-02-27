package co.andrewbates.grade.data;

import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class BaseModel implements Model {
    private UUID id;
    private SimpleStringProperty name = new SimpleStringProperty();

    public BaseModel() {
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public void setID(UUID id) {
        if (this.id != null) {
            throw new RuntimeException("IDs can only be set once");
        }
        this.id = id;
    }

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

    @Override
    public int compareTo(Model o) {
        return getName().compareTo(((BaseModel) o).getName());
    }

}

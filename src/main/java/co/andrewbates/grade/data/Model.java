package co.andrewbates.grade.data;

import java.util.UUID;

public interface Model extends Comparable<Model> {
    public UUID getID();

    public void setID(UUID id);

    public String getName();
}

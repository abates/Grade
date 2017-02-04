package co.andrewbates.grade.data;

import java.util.UUID;

public abstract class BaseModel implements Model {
    private UUID id;

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

}

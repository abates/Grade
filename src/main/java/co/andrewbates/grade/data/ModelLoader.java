package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;

import javafx.concurrent.Task;

public interface ModelLoader<T extends Model> {
    public Task<Void> load(Path basedir) throws DataException;

    public void delete(T object);

    public void create(T object) throws IOException;

    public void save(T object) throws IOException;

    public String getPath();

    public String getPath(T object);
}

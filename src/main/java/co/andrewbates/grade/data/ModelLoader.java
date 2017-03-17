package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;

import javafx.concurrent.Task;

public interface ModelLoader<T extends Model> {
    public Task<Void> loadAll(Path basedir) throws DataException;

    public void delete(T object) throws IOException;

    public void save(T object) throws IOException;

    public T get(long uuid);

    public Path getPath();

    public Path getPath(T object);

    public Class<T> modelClass();
}

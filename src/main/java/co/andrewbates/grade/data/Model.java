package co.andrewbates.grade.data;

public interface Model extends Comparable<Model> {
    public long getID();

    public void setID(long id);

    public String getName();
}

package co.andrewbates.grade.data;

import java.io.File;
import java.nio.file.Path;

import co.andrewbates.grade.model.SchoolYear;

public class SchoolYearLoader extends BaseModelLoader<SchoolYear> {
    File dir;

    public SchoolYearLoader() {
        super(SchoolYear.class);
    }

    @Override
    public Path getPath() {
        return super.getPath().resolve("years");
    }

}

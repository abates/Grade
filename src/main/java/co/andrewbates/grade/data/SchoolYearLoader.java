package co.andrewbates.grade.data;

import java.io.File;

import co.andrewbates.grade.model.SchoolYear;

public class SchoolYearLoader extends BaseModelLoader<SchoolYear> {
    File dir;

    public SchoolYearLoader() {
        super(SchoolYear.class);
    }

    @Override
    public String getPath() {
        return "years";
    }

}

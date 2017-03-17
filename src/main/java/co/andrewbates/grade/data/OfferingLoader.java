package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import co.andrewbates.grade.model.Offering;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OfferingLoader extends BaseModelLoader<Offering> {
    HashMap<Long, ObservableList<Offering>> offerings = new HashMap<Long, ObservableList<Offering>>();

    public OfferingLoader() {
        super(Offering.class);
    }

    @Override
    protected void initialize(Offering offering) {
        addOffering(offering);
    }

    private void addOffering(Offering offering) {
        offerings(offering.getSchoolYearID()).add(offering);
    }

    @Override
    public Path getPath() {
        return super.getPath().resolve("offerings");
    }

    @Override
    public void save(Offering offering) throws IOException {
        if (offering.getID() == 0) {
            addOffering(offering);
        }
        super.save(offering);
    }

    public ObservableList<Offering> offerings(long schoolYearID) {
        ObservableList<Offering> offerings = this.offerings.get(schoolYearID);
        if (offerings == null) {
            offerings = FXCollections.observableArrayList();
            this.offerings.put(schoolYearID, offerings);
        }
        return offerings;
    }
}

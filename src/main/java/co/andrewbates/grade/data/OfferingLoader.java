package co.andrewbates.grade.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

import co.andrewbates.grade.model.Offering;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OfferingLoader extends BaseModelLoader<Offering> {
    HashMap<UUID, ObservableList<Offering>> offerings = new HashMap<UUID, ObservableList<Offering>>();

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
        if (offering.getID() == null) {
            addOffering(offering);
        }
        super.save(offering);
    }

    public ObservableList<Offering> offerings(UUID schoolYearID) {
        ObservableList<Offering> offerings = this.offerings.get(schoolYearID);
        if (offerings == null) {
            offerings = FXCollections.observableArrayList();
            this.offerings.put(schoolYearID, offerings);
        }
        return offerings;
    }
}

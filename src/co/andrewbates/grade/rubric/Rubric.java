package co.andrewbates.grade.rubric;

import java.util.ArrayList;
import java.util.List;

public abstract class Rubric {
    private ArrayList<Criteria> criteria = new ArrayList<Criteria>();

    void addCriteria(Criteria criteria) {
        this.criteria.add(criteria);
    }

    public List<Criteria> getCriteria() {
        return criteria;
    }
}

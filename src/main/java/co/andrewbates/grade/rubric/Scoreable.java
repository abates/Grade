package co.andrewbates.grade.rubric;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Scoreable {
    ObservableMap<String, Score> scores = FXCollections.observableHashMap();

    public Scoreable() {
        super();
    }

    public void setGrade(Score score) {
        scores.put(score.getName(), score);
    }

    public ObservableList<Score> getScores() {
        return FXCollections.observableArrayList(scores.values());
    }

    public Score getScore(String criteriaName) {
        return scores.get(criteriaName);
    }
}
package co.andrewbates.grade;

import java.util.Collection;
import java.util.HashMap;

import co.andrewbates.grade.rubric.Score;

public class Scoreable {

    protected HashMap<String, Score> scores;

    public Scoreable() {
        super();
        scores = new HashMap<String, Score>();
    }

    public void setGrade(Score score) {
        scores.put(score.getName(), score);
    }

    public Collection<Score> getScores() {
        return scores.values();
    }

    public Score getScore(String criteriaName) {
        return scores.get(criteriaName);
    }

    public double getScore() {
        double score = 0;
        for (Score s : scores.values()) {
            score += s.getScore();
        }
        return score / scores.size();
    }
}
package co.andrewbates.grade.rubric;

public class ScoreItem {
    private int range;
    private int score;
    private String message;

    public ScoreItem(int score, int range) {
        this(score, range, "");
    }

    public ScoreItem(int score, int range, String message) {
        this.score = score;
        this.range = range;
        this.message = message;
    }

    public int getRange() {
        return range;
    }

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }

}

package co.andrewbates.grade.rubric;

public class Score {
    private String name;
    private int score;
    private int range;
    private String message;

    public Score(String name, int score, int range) {
        this(name, score, range, "");
    }

    public Score(String name, int score, int range, String message) {
        this.name = name;
        this.score = score;
        this.range = range;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getRange() {
        return range;
    }

    public String getMessage() {
        return message;
    }
}

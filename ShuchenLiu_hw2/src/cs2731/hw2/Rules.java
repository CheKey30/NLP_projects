package cs2731.hw2;

public class Rules {
    private float probability;
    private String to;
    private String from;
    public Rules(float prob, String from, String to){
        this.from = from;
        this.to = to;
        this.probability = prob;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}

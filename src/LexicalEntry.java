import java.util.ArrayList;
import java.util.List;

public class LexicalEntry {

    private String word;
    private List<Double> sentiments;

    public LexicalEntry() {

        this.word = null;
        this.sentiments = new ArrayList<Double>();

    }

    public LexicalEntry(String word, List<Double> sentiments) {

        this.word = word;
        this.sentiments = new ArrayList<Double>();

        try {
            this.sentiments.addAll(sentiments);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
            System.out.println("LexicalEntry: argument 'sentiments' is empty");
        }

    }

    public boolean equals(LexicalEntry entry) {
        return this.getWord().equals(entry.getWord());
    }

    public LexicalEntry union(LexicalEntry e2) {

        // TODO: handle multiple union (what if there are three or more of the same entry?)
        LexicalEntry union = new LexicalEntry();

        if(this.equals(e2)) {
            union.setWord(this.word);
        }
        else {
            union.setWord(e2.getWord());
        }

        for(int i = 0; i < 10; i++) {
            Double unionSentiment = this.sentiments.get(i) + e2.getSentiment(i);
            if(unionSentiment >= 1.0) {
                unionSentiment = (Double)1.0;
            }
            else {
                unionSentiment = (Double)0.0;
            }
            union.addSentiment(unionSentiment);
        }

        return union;
    }

    public Double getScore() {

        Double score = 0.0;
        for(int i = 0; i < sentiments.size(); i++) {
            if(i == 1 || i == 2 || i == 4 || i == 5 || i == 7) {
                score += (-1.0) * sentiments.get(i);
            }
            else {
                score += sentiments.get(i);
            }
        }
        return score;

    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getSentiment(int index) {
        return this.sentiments.get(index);
    }

    public List<Double> getSentiments() {
        return this.sentiments;
    }

    public void addSentiment(Double sentiment) {
        this.sentiments.add(sentiment);
    }

    public void setSentiments(ArrayList<Double> sentiments) {
        for(int i = 0; i < sentiments.size(); i++) {
            this.addSentiment(sentiments.get(i));
        }
    }

}

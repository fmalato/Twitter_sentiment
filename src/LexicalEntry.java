import java.util.ArrayList;
import java.util.List;

public class LexicalEntry {

    private String word;
    private List<Float> sentiments;

    public LexicalEntry() {

        this.word = null;
        this.sentiments = new ArrayList<Float>();

    }

    public LexicalEntry(String word, List<Float> sentiments) {

        this.word = word;
        this.sentiments = new ArrayList<Float>();

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
            float unionSentiment = this.sentiments.get(i) + e2.getSentiment(i);
            if(unionSentiment >= 1.0) {
                unionSentiment = (float)1.0;
            }
            else {
                unionSentiment = (float)0.0;
            }
            union.addSentiment(unionSentiment);
        }

        return union;
    }

    public float getScore() {

        float score = 0;
        for(int i = 0; i < sentiments.size(); i++) {
            score += sentiments.get(i);
        }
        return score;

    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Float getSentiment(int index) {
        return this.sentiments.get(index);
    }

    public void addSentiment(Float sentiment) {
        this.sentiments.add(sentiment);
    }

}

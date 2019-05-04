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

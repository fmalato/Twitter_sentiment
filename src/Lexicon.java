import java.util.ArrayList;
import java.util.List;

public class Lexicon extends ArrayList {

    private List<LexicalEntry> lexicon;

    public Lexicon() {

        this.lexicon = new ArrayList<LexicalEntry>();

    }

    public void add(LexicalEntry entry) {
        this.lexicon.add(entry);
    }

    public LexicalEntry get(int index) {
        return this.lexicon.get(index);
    }

    public int size() {
        return this.lexicon.size();
    }

    public void sort(LexicalComparator comp) {
        this.lexicon.sort(comp);
    }

    public List<LexicalEntry> getLexicon() {
        return this.lexicon;
    }

    public void removeDuplicates() {

        Lexicon newLexicon = new Lexicon();

        for (int i = 0; i < this.lexicon.size() - 1; i++) {
            if (this.lexicon.get(i).equals(this.lexicon.get(i + 1))) {
                newLexicon.add(this.lexicon.get(i).union(this.lexicon.get(i + 1)));
            }
        }
        newLexicon.add(this.lexicon.get(this.lexicon.size() - 1));

        this.lexicon = newLexicon.getLexicon();

    }

}

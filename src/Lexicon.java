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

    public boolean contains(LexicalEntry entry) {

        for(int i = 0; i < this.lexicon.size(); i++) {
            if((this.lexicon.get(i)).equals(entry)) {
                return true;
            }
        }
        return false;

    }

    public List<LexicalEntry> getLexicon() {
        return this.lexicon;
    }

    // TODO: make this work properly, dude
    public void removeDuplicates() {

        Lexicon newLexicon = new Lexicon();

        // Se due parole consecutive sono uguali, le unisco e aggiungo soltanto l'unione
        for (int i = 0; i < this.lexicon.size() - 1; i++) {
            LexicalEntry union = this.lexicon.get(i).union(this.lexicon.get(i + 1));
            if (this.lexicon.get(i).equals(this.lexicon.get(i + 1)) && !newLexicon.contains(union)) {
                newLexicon.add(union);
            }
            else if(!this.lexicon.get(i).equals(this.lexicon.get(i + 1))) {
                newLexicon.add(this.lexicon.get(i));
            }
        }
        // Aggiungo l'ultima parola
        newLexicon.add(this.lexicon.get(this.lexicon.size() - 1));

        this.lexicon = newLexicon.getLexicon();

    }

    public int binarySearch(String x) {
        int low = 0;
        int high = this.lexicon.size() - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;

            if (this.lexicon.get(mid).getWord().compareTo(x) < 0) {
                low = mid + 1;
            } else if (this.lexicon.get(mid).getWord().compareTo(x) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;
    }

}

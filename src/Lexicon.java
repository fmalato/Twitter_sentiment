import javax.swing.text.html.parser.Parser;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Lexicon extends ArrayList {

    private List<LexicalEntry> lexicon;

    public Lexicon() {

        this.lexicon = new ArrayList<LexicalEntry>();

    }

    public Lexicon(FileReader fr) {

        JSONParser jsonParser = new JSONParser();
        this.lexicon = new ArrayList<LexicalEntry>();
        try {
            Object obj = jsonParser.parse(fr);
            JSONObject jsonArray = (JSONObject)obj;
            for (Object key : jsonArray.keySet()) {
                LexicalEntry entry = new LexicalEntry();
                entry.setWord((String)key);
                entry.setSentiments((ArrayList<Double>)jsonArray.get(key));
                this.lexicon.add(entry);
            }
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
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

import java.util.Comparator;

public class LexicalComparator implements Comparator<LexicalEntry> {

    public int compare(LexicalEntry e1, LexicalEntry e2) {

        String word1 = e1.getWord();
        String word2 = e2.getWord();

        int l1 = word1.length();
        int l2 = word2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)word1.charAt(i);
            int str2_ch = (int)word2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }

        if (l1 != l2) {
            return l1 - l2;
        }

        else {
            return 0;
        }

    }

}

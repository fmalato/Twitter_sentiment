import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;


public class SentimentClassifier {

    private Lexicon itLexicon;
    private Lexicon engLexicon;

    public SentimentClassifier() throws IOException {
        File lexiconFile = new File("lexicon/NRC-Emotion-Lexicon-v0.92-In105Languages-Nov2017Translations.xlsx");
        FileInputStream fis = new FileInputStream(lexiconFile);

        this.engLexicon = new Lexicon();
        this.itLexicon = new Lexicon();

        try {

            this.itLexicon = new Lexicon(new FileReader("data/itLexicon.json"));
            System.out.println("File 'itLexicon.json' found in 'data/', itLexicon generated.");
            this.engLexicon = new Lexicon(new FileReader("data/engLexicon.json"));
            System.out.println("File 'engLexicon.json' found in 'data/', engLexicon generated.");

            engLexicon.sort(new LexicalComparator());
            itLexicon.sort(new LexicalComparator());
            itLexicon.removeDuplicates();

        } catch (FileNotFoundException e) {

            System.out.println("File not found, generating lexicon...");

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowItr = sheet.iterator();

            // La prima riga ha il nome dei campi
            rowItr.next();

            JSONObject itJsonRow = new JSONObject();
            JSONObject engJsonRow = new JSONObject();
            File itFile = new File("data/itLexicon.json");
            File engFile = new File("data/engLexicon.json");
            FileWriter fwIt = new FileWriter(itFile);
            FileWriter fwEng = new FileWriter(engFile);

            // Ciclo usato per l'estrazione dei dati necessari
            while (rowItr.hasNext()) {
                Row row = rowItr.next();
                Iterator<Cell> cellItr = row.cellIterator();

                int count = 0;
                LexicalEntry engEntry = new LexicalEntry();
                LexicalEntry itEntry = new LexicalEntry();

                while (cellItr.hasNext()) {

                    Cell cell = cellItr.next();

                    if (count == 0) {
                        engEntry.setWord(cell.toString());
                    }
                    if (count == 43) {
                        itEntry.setWord(cell.toString());
                    }
                    if (count > 104) {
                        engEntry.addSentiment(Double.parseDouble(cell.toString()));
                        itEntry.addSentiment(Double.parseDouble(cell.toString()));
                    }
                    if (count == 114) {
                        // TODO: fix LexicalEntry cannot be cast to class java.util.ArrayList
                        if (itJsonRow.containsKey(itEntry.getWord())) {
                            Object obj = itJsonRow.get(itEntry.getWord());
                            LexicalEntry e2 = new LexicalEntry(itEntry.getWord(), (ArrayList<Double>)obj);
                            itJsonRow.put(itEntry.getWord(), itEntry.union(e2).getSentiments());
                            System.out.println("Evitata ripetizione di " + itEntry.getWord());
                        }
                        else {
                            itJsonRow.put(itEntry.getWord(), itEntry.getSentiments());
                            engJsonRow.put(engEntry.getWord(), engEntry.getSentiments());
                        }
                    }
                    count++;

                }
                engLexicon.add(engEntry);
                itLexicon.add(itEntry);
            }

            try {

                fwIt.write(itJsonRow.toJSONString());
                fwIt.flush();
                fwEng.write(engJsonRow.toJSONString());
                fwEng.flush();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Poiché il lessico è basato sull'inglese, bisogna fare il sorting delle altre lingue
            itLexicon.sort(new LexicalComparator());
            // A volte capitano termini uguali in italiano per termini diversi in inglese
            itLexicon.removeDuplicates();

            workbook.close();
            fis.close();

        }
    }

    public float parseString (String s){

        // Suddivide la frase in parole ed elimina eventuale punteggiatura
        String[] words = s.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        float score = 0;
        for (String element : words) {
            int index = this.itLexicon.binarySearch(element);
            if (index != -1) {
                score += this.itLexicon.get(index).getScore();
            }
            else{
                int indexEng = this.engLexicon.binarySearch(element);
                if (indexEng != -1) {
                    score += this.itLexicon.get(indexEng).getScore();
                }
                else {
                    System.out.println(element + ": not found");
                }
            }
        }

        return score;

    }

}

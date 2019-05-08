import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SentimentClassifier {

    private Lexicon itLexicon;
    private Lexicon engLexicon;

    public SentimentClassifier() throws IOException {
        File lexiconFile = new File("lexicon/NRC-Emotion-Lexicon-v0.92-In105Languages-Nov2017Translations.xlsx");
        FileInputStream fis = new FileInputStream(lexiconFile);

        this.engLexicon = new Lexicon();
        this.itLexicon = new Lexicon();

        try {

            // TODO: implement file reader and understand file writer
            FileReader frIt = new FileReader("data/lexicon.json");

        } catch (FileNotFoundException e) {

            System.out.println("File not found, generating lexicon...");

            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowItr = sheet.iterator();

            // La prima riga ha il nome dei campi
            rowItr.next();

            JSONObject jsonRow = new JSONObject();

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
                        jsonRow.put("engWord", cell.toString());
                    }
                    if (count == 43) {
                        itEntry.setWord(cell.toString());
                        jsonRow.put("itWord", cell.toString());
                    }
                    if (count > 104) {
                        engEntry.addSentiment(Float.parseFloat(cell.toString()));
                        itEntry.addSentiment(Float.parseFloat(cell.toString()));
                        jsonRow.put("sentiment", Float.parseFloat(cell.toString()));
                    }
                    count++;

                }

                try (FileWriter file = new FileWriter("data/lexicon.json")) {

                    file.write(jsonRow.toJSONString());
                    file.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                engLexicon.add(engEntry);
                itLexicon.add(itEntry);

            }

            // Poiché il lessico è basato sull'inglese, bisogna fare il sorting delle altre lingue
            itLexicon.sort(new LexicalComparator());
            // A volte capitano termini uguali in italiano per termini diversi in inglese
            itLexicon.removeDuplicates();

        /*for(int i = 0; i < itLexicon.size(); i++) {
            System.out.print(itLexicon.get(i).getWord() + "; ");
            for(int j = 0; j < 10; j++) {
                System.out.print(itLexicon.get(i).getSentiment(j) + "; ");
            }
            System.out.println();
        }*/

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
        }

        return score;

    }

}

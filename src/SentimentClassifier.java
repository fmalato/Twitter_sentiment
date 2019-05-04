import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SentimentClassifier {

    private List<LexicalEntry> itLexicon;
    private List<LexicalEntry> engLexicon;

    public SentimentClassifier() throws IOException {
        File lexiconFile = new File("lexicon/NRC-Emotion-Lexicon-v0.92-In105Languages-Nov2017Translations.xlsx");
        FileInputStream fis = new FileInputStream(lexiconFile);

        this.engLexicon = new ArrayList<LexicalEntry>();

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowItr = sheet.iterator();

        Row row = rowItr.next();    // La prima riga ha il nome dei campi

        while(rowItr.hasNext()) {
            row = rowItr.next();
            Iterator<Cell> cellItr = row.cellIterator();

            int count = 0;
            LexicalEntry entry = new LexicalEntry();
            List<Float> entrySent = new ArrayList<Float>();

            while(cellItr.hasNext()) {
                Cell cell = cellItr.next();
                /*if(count == 0 || count == 43 || count > 105) {    // indici basati sul file .xlsx
                    System.out.print(cell.toString() + "; ");

                }*/
                if(count == 0) {
                    entry.setWord(cell.toString());
                }
                if(count > 105) {
                    entry.addSentiment(Float.parseFloat(cell.toString()));
                }

                // TODO: trovare un modo per fare il sorting di itLeixcon senza stravolgere i sentiments di ciascuna parola
                // TODO: risolvere il problema del salvataggio dell'ultimo sentimento posizionando bene cell.next()
                count++;
            }

            engLexicon.add(entry);
            System.out.print(entry.getWord() + "; ");
            for(int i = 0; i < 8; i++) {
                System.out.print(entry.getSentiment(i) + "; ");
            }
            System.out.println();
        }

        workbook.close();
        fis.close();

    }

}

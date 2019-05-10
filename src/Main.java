import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            SentimentClassifier classifier = new SentimentClassifier();
            float score = classifier.parseString("il poliziotto è coraggioso ma ha un pessimo carattere");
            float scoreEng = classifier.parseString("the officer is very drowsy and has a very bad attitude");
            System.out.println(score);
            System.out.println(scoreEng);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

}

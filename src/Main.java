import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        /*JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("jsonFiles/FlumeData-1556382859906.json"));
            JSONArray jsonArr = (JSONArray)obj;
            obj = parser.parse(new FileReader("jsonFiles/FlumeData-1556382859907.json"));
            JSONArray jsonArr2 = (JSONArray)obj;
            for(int i = 0; i < jsonArr2.size(); i++) {
                jsonArr.add(jsonArr2.get(i));
            }
            obj = parser.parse(new FileReader("jsonFiles/FlumeData-1556382859908.json"));
            JSONArray jsonArr3 = (JSONArray)obj;
            for(int i = 0; i < jsonArr3.size(); i++) {
                jsonArr.add(jsonArr3.get(i));
            }
            jsonArr.addAll(jsonArr3);
            for(int i = 0; i < jsonArr.size(); i++) {
                JSONObject jsonObj = (JSONObject) jsonArr.get(i);
                String text = (String) jsonObj.get("text");
                System.out.println("text " + i + ": " + text);
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }*/

        try {
            SentimentClassifier classifier = new SentimentClassifier();
            float score = classifier.parseString("il poliziotto è coraggioso ma ha un brutto carattere");
            float scoreEng = classifier.parseString("the police is very drowsy and has a very bad attitude");
            System.out.println(score);
            System.out.println(scoreEng);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

}

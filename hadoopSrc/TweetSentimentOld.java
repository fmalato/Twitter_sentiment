/*

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            SentimentClassifier classifier = new SentimentClassifier();
            float score = classifier.parseString("Alcune persone sono malvage");
            System.out.println(score);
        }c
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}

*/

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileInputStream;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TweetSentiment {

	public static Double getScore(ArrayList<Double> sentiments) {

        Double score = 0.0;
        for(int i = 0; i < sentiments.size(); i++) {
            if(i == 1 || i == 2 || i == 4 || i == 5 || i == 7) {
                score += (-1.0) * sentiments.get(i);
            }
            else {
                score += sentiments.get(i);
            }
        }
        return score;
    }


    public static float parseString (String s, JSONObject jsonObjectIt, JSONObject jsonObjectEng){

        // Suddivide la frase in parole ed elimina eventuale punteggiatura
        String[] words = s.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }

        float score = 0;
        ArrayList<Double> sentiments = null;
        for (String element : words) {

             try {
                 sentiments = (ArrayList<Double>) jsonObjectIt.get(element);

                 if (sentiments == null) {
                     sentiments = (ArrayList<Double>) jsonObjectEng.get(element);
                 }
                 score += getScore(sentiments);

             } catch (NullPointerException e) { }
        }

        return score;

    }


    public static class ClassificatorMapper extends Mapper<Object, Text, IntWritable, IntWritable> {

        private int score = 0;

        private Text word = new Text();
        private JSONParser parser = new JSONParser();
        private String tweetText = null;
    

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
        			
            try {
                JSONObject jsonObj = (JSONObject) parser.parse(value.toString());
                
                Object objIt = parser.parse(new FileReader("itLexicon.json"));
            	JSONObject jsonObjectIt = (JSONObject) objIt;

            	Object objEng = parser.parse(new FileReader("engLexicon.json"));
            	JSONObject jsonObjectEng = (JSONObject) objEng;
            	
                tweetText = (String) jsonObj.get("text");
                
				score = (int) parseString(tweetText, jsonObjectIt, jsonObjectEng);
				  
				} catch (Exception e) { 
            	e.printStackTrace();
            }
            
            context.write(new IntWritable(score), new IntWritable(1));
            
            }
        }

    public static class ClassificationCounterReducer extends Reducer<IntWritable,IntWritable,IntWritable,IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(IntWritable classification, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(classification, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Sentiment Count");
        job.setJarByClass(TweetSentiment.class);
        job.setMapperClass(ClassificatorMapper.class);
        job.setCombinerClass(ClassificationCounterReducer.class);
        job.setReducerClass(ClassificationCounterReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}






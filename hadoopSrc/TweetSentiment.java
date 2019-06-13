import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.io.FileInputStream;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class TweetSentiment extends Configured implements Tool {

    public static final Log logt = LogFactory.getLog(TweetSentiment.class);

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

            logt.info("Confronto: " + words[i] + " con " + QUERY);

            if (words[i].equalsIgnoreCase(QUERY)) {
                THEREISQUERY = true;
                logt.info("Ho trovato una parola ");
            }
        }

        if (THEREISQUERY == false)
            return 0;

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

    public static class ClassificatorMapper extends Mapper<Object, Text, Text, IntWritable> {

        public static final Log logm = LogFactory.getLog(ClassificatorMapper.class);

        private int score = 0;

        private Text word = new Text();
        private JSONParser parser = new JSONParser();
        private String tweetText = null;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        			
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

            Text classification = new Text("Neutral");

            if (score > 0 && score < 3)
                classification = new Text("Positive");
            if (score >= 3)
                classification = new Text("VeryPositive");

            if (score < 0 && score > -3)
                classification = new Text("Negative");
            if (score <= -3)
                classification = new Text("VeryNegative");
            
            if (THEREISQUERY == true) {
                logm.info("classificazione " + classification +  " 1");
                context.write(classification, new IntWritable(1));
                THEREISQUERY = false;
            }
            else {
                context.write(classification, new IntWritable(0));
            }
        }
    }

    public static class ClassificationCounterReducer extends TableReducer<Text,IntWritable,Text> {

        public static final Log log = LogFactory.getLog(ClassificationCounterReducer.class);

        
        private HTable hTable;
        protected void setup(Context context) throws IOException, InterruptedException {
            try {
                hTable = new HTable(configuration, QUERY);
            }
            catch (IOException e) {
                System.out.printf("Error getting table from HBase", e);
            }
        }   
        

        public void reduce(Text classification, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            
            // Leggo dal database prima di aggiornare la tabella
            Get g = new Get(Bytes.toBytes(classification.toString()));
            Result resultTable = hTable.get(g);

            byte [] oldByteValue = resultTable.getValue(Bytes.toBytes("number"),Bytes.toBytes("value"));
            
            int oldValue = Integer.parseInt(Bytes.toString(oldByteValue));

            log.info("Valore somma " + sum + " " + oldValue);

            sum += oldValue;

            Put put = new Put(Bytes.toBytes(classification.toString()));
            String result = Integer.toString(sum);

            put.addColumn( Bytes.toBytes("number"), Bytes.toBytes("value"), Bytes.toBytes(result) );

            context.write(classification, put);
        }
        
    }

    private static String OUTPUT_TABLE = "sentiment";
    private static String QUERY;
    private static boolean THEREISQUERY = false;
    private static Configuration configuration;
    //private static HTable hTable;

    public int run(String[] args) throws Exception  {

        if (args.length > 1)
            QUERY = args[1];

        configuration = getConf();

        Job job = Job.getInstance(getConf(), "Sentiment Count");

        job.setJarByClass(TweetSentiment.class);
        job.setMapperClass(ClassificatorMapper.class);
        //job.setCombinerClass(ClassificationCounterReducer.class);
        job.setReducerClass(ClassificationCounterReducer.class);

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH");
        Date date = new Date();
        String now = dateFormat.format(date);

        HBaseAdmin admin = new HBaseAdmin(getConf());

        HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(QUERY));

        tableDescriptor.addFamily(new HColumnDescriptor("number"));

        if (!admin.tableExists(TableName.valueOf(QUERY)))
           admin.createTable(tableDescriptor);
        
        //hTable = new HTable(getConf(), QUERY);

        TableMapReduceUtil.initTableReducerJob(
            QUERY,
            TweetSentiment.ClassificationCounterReducer.class,
            job);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new HBaseConfiguration(), new TweetSentiment(), args);
        System.exit(res);
    }

}
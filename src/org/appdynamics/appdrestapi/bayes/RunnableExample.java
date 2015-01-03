package org.appdynamics.appdrestapi.bayes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class RunnableExample {

	Map<String, Long> wordCountMap = new HashMap<String, Long>();
	Map<String, Long> wordCountMap2 = new HashMap<String, Long>();

	Map<String, Double> tokenProbabilityApp = new HashMap<String, Double>();
	Map<String, Double> tokenProbabilityOther = new HashMap<String, Double>();


	final BayesClassifier<String, String> bayes = new BayesClassifier<String, String>();

	public static void main(String[] args) {

		new RunnableExample().execute();

	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Executes the Runnable Example class. Compares the results of the Bayes
	 * Classifier class to the results of the DataSmart method.
	 */
	public void execute() {

	    File Mandrill = new File("resources/Mandrill.csv");
	    File Other = new File("resources/Other.csv");
	    File Test = new File("resources/Test.csv");

	    //Bayes Classifier learning
	    bayesLearn(Mandrill, "APP");
	    bayesLearn(Other, "OTHER");

	    //DataSmart learning
	    learnDS(wordCountMap, Mandrill);
	    learnDS(wordCountMap2, Other);
	    weightedProbability(wordCountMap, tokenProbabilityApp);
	    weightedProbability(wordCountMap2, tokenProbabilityOther);

	    //Results and comparisons
	    executeTestData(Test);

		/*
		 * Please note, that this particular classifier implementation will
		 * "forget" learned classifications after a few learning sessions. The
		 * number of learning sessions it will record can be set as follows:
		 */
		bayes.setMemoryCapacity(500); // remember the last 500 learned
										// classifications
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Parses a CSV file and returns a List of records from the CSV file.
	 * @param file The CSV file to be parsed.
	 * @return Will return a list of CSV records that have been parsed.
	 */
	private List<CSVRecord> parseCSV(File file) {
	    List<CSVRecord> records = null;
        try
        {
            CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(),
                CSVFormat.EXCEL);
            records = parser.getRecords();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return records;
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Creates a map of tokens from the CSV file.
	 */
	private void learnDS(Map<String, Long> map, File file) {
	    List<CSVRecord> records = parseCSV(file);
	    if (records != null) {
	        for (CSVRecord fileLine : records)
	        {
	            String line = fileLine.get(0);
	            line = cleanData(line);
	            String[] words = line.split("\\s");
	            for (int i = 0; i < words.length; i++) {
	                if (map.get(words[i]) == null) {
	                    if (words[i].length() > 3) {
	                        Long countValue = new Long(1);
	                        map.put(words[i], countValue);
	                    }
	                } else {
	                    if (words[i].length() > 3) {
	                        Long wordCount = map.get(words[i]);
	                        Long newWordCount = wordCount + 1;
	                        map.put(words[i], newWordCount);
	                    }
	                }
	            }
	        }
	    }
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Adds 1 to the count of every token.
	 * @param wordCountMap The map containing the token and its word count.
	 * @return Will return the new total amount of Features for the map's
	 *  category.
	 */
	private void additiveSmoothing(Map<String, Long> map) {
        for (Map.Entry<String, Long> wordCount : map.entrySet()) {
            String word = wordCount.getKey();
            Long count = wordCount.getValue() + 1;
            map.put(word, count);
        }
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Calculates the weighted probability for each token in the map.
	 * @param map The map that contains the features.
	 * @param probMap The map that will contain the probability of each feature.
	 */
	private void weightedProbability(Map<String, Long> map,
	    Map<String, Double> probMap) {
	    additiveSmoothing(map);
        for (Map.Entry<String, Long> wordCount : map.entrySet()) {
            String word = wordCount.getKey();
            Long count = wordCount.getValue();
            Double prob = new Double(Math.log(count / getTokenCount(map)));
            probMap.put(word, prob);
        }
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Place a description of your method here.
	 * @param map
	 * @return
	 */
	private Double getTokenCount(Map<String, Long> map) {
	    Double tCount = new Double(0);
	    for (Map.Entry<String, Long> wordCount : map.entrySet()) {
	        Long count = wordCount.getValue();
	        tCount = tCount + count;
	    }
	    return tCount;
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Place a description of your method here.
	 * @param map
	 * @return
	 */
	private Double sumProbability(Map<String, Double> map,
	    Map<String, Long> map2, String[] words) {
	    Double prob = new Double(0);
	    double tC = getTokenCount(map2);
        for (int x = 0; x < words.length; x++)
        {
            if (map.get(words[x]) != null
                && words[x].length() > 3)
            {
                prob = map.get(words[x]) + prob;
            }
            // If token is not in map
            else if (map.get(words[x]) == null
                && words[x].length() > 3)
            {
                Double tempProb =
                    new Double(Math.log(1 / tC));
                prob = tempProb + prob;
            }
        }
        return prob;
	}

	/*
	 * // ----------------------------------------------------------
	/**
	 * Place a description of your method here.
	 * @param file
	 */
	private void executeTestData(File file) {
	    List<CSVRecord> records = parseCSV(file);
        for (CSVRecord fileLine : records)
        {
            String line = fileLine.get(2);
            line = cleanData(line);
            String[] words = line.split("\\s");
            // Summing probabilities.
            Double appProb = sumProbability(tokenProbabilityApp, wordCountMap,
                words);
            Double otherProb = sumProbability(tokenProbabilityOther,
                wordCountMap2, words);


            //Results for BayesClassifier.
            System.out.println(
                bayes.classify(Arrays.asList(words)).getCategory() +
                " - Bayes Classifier result");
            //Results for DataSmart Method
            if (appProb > otherProb)
            {
                System.out.println("APP - DataSmart result [");
                System.out.println(appProb + " APP probability");
                System.out.println(otherProb + " OTHER probability ]");
            }
            else
            {
                System.out.println("OTHER - DataSmart result [");
                System.out.println(appProb + " APP probability");
                System.out.println(otherProb + " OTHER probability ]");
            }

        }
        System.out.println(new Integer(bayes.getCategoriesTotal()).toString());
        System.out.println(bayes.getCategories());
        System.out.println(bayes.getFeatures());
    }

	private void bayesLearn(File file, String category) {
	    List<CSVRecord> records = parseCSV(file);
	    for (CSVRecord fileLine : records) {

	        String line = fileLine.get(0);
	        line = cleanData(line);
	        String[] words = line.split("\\s");

	        bayes.learn(category, Arrays.asList(words));
	    }
	}

	private String cleanData(String line) {
	    String newLine = line.toLowerCase();
		newLine = newLine.replace(". ", " ");
		newLine = newLine.replace(": ", " ");
		newLine = newLine.replace("?", " ");
		newLine = newLine.replace("!", " ");
		newLine = newLine.replace(";", " ");
		newLine = newLine.replace(",", " ");
		return newLine;
	}

}

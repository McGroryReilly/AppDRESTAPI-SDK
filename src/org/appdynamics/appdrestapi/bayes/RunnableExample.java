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

	Double totalTokenCountApp = new Double(0);
	Double totalTokenCountOther = new Double(0);

	public static void main(String[] args) {

		new RunnableExample().execute();

	}

	public void execute() {

		bayesLearnApp();

		bayesLearnOther();



		testTweets();

		exeuteDataSmart();

		executeTestData();

		/*
		 * Please note, that this particular classifier implementation will
		 * "forget" learned classifications after a few learning sessions. The
		 * number of learning sessions it will record can be set as follows:
		 */
		bayes.setMemoryCapacity(500); // remember the last 500 learned
										// classifications
	}

	private void executeTestData() {
		// Now to parse the test data
		try {
			CSVParser parser4 = CSVParser.parse(new File("resources/Test.csv"),
					Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord> records4 = (List<CSVRecord>) parser4.getRecords();
			System.err.println(records4.size());
			for (CSVRecord fileLine : records4) {
				String line = fileLine.get(2);
				line = cleanData(line);
				String[] words = line.split("\\s");
				// Summing probabilities.
				double appProb = 0;
				double otherProb = 0;
				for (int x = 0; x < words.length; x++) {
					// App tokens
					if (tokenProbabilityApp.get(words[x]) != null
							&& words[x].length() > 3) {
						appProb = tokenProbabilityApp.get(words[x]) + appProb;
					}
					// If token is not in map
					else if (tokenProbabilityApp.get(words[x]) == null
							&& words[x].length() > 3) {
						Double tempProb = new Double(
								Math.log(1 / totalTokenCountApp));
						appProb = tempProb + appProb;
					}
					// Other tokens
					if (tokenProbabilityOther.get(words[x]) != null
							&& words[x].length() > 3) {
						otherProb = tokenProbabilityOther.get(words[x])
								+ otherProb;
					}
					// If token is not in map
					else if (tokenProbabilityOther.get(words[x]) == null
							&& words[x].length() > 3) {
						Double tempProb2 = new Double(
								Math.log(1 / totalTokenCountOther));
						otherProb = tempProb2 + otherProb;
					}
				}
				if (appProb > otherProb) {
					System.out.println("APP");
					System.out.println(appProb);
					System.out.println(otherProb);
				} else {
					System.out.println("OTHER");
					System.out.println(appProb);
					System.out.println(otherProb);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(new Integer(bayes.getCategoriesTotal()).toString());
		System.out.println(bayes.getCategories());
		System.out.println(bayes.getFeatures());
	}

	private void exeuteDataSmart() {
		// SECOND METHOD: DATASMART METHOD




		// Incrementing each token count by 1 in the App category
		for (Map.Entry<String, Long> wordCount : wordCountMap.entrySet()) {
			String word = wordCount.getKey();
			Long count = wordCount.getValue() + 1;
			wordCountMap.put(word, count);
			totalTokenCountApp = totalTokenCountApp + count;
		}
		// Calculating the probability for each token in the App category.
		for (Map.Entry<String, Long> wordCount : wordCountMap.entrySet()) {
			String word = wordCount.getKey();
			Long count = wordCount.getValue();
			Double natLogApp = new Double(Math.log(count / totalTokenCountApp));
			tokenProbabilityApp.put(word, natLogApp);
		}
		// Incrementing each token count by 1 in the Other category
		for (Map.Entry<String, Long> wordCount : wordCountMap2.entrySet()) {
			String word = wordCount.getKey();
			Long count = wordCount.getValue() + 1;
			wordCountMap2.put(word, count);
			totalTokenCountOther = totalTokenCountOther + count;
		}
		// Calculating the probability for each token in the Other category.
		for (Map.Entry<String, Long> wordCount : wordCountMap2.entrySet()) {
			String word = wordCount.getKey();
			Long count = wordCount.getValue();
			Double natLogOther = new Double(Math.log(count
					/ totalTokenCountOther));
			tokenProbabilityOther.put(word, natLogOther);
		}
		System.out.println("Total Token Count for App:  " + totalTokenCountApp);
		System.out.println("Total Token Count for Other:  "	+ totalTokenCountOther);
	}

	private void testTweets() {
		/*
		 * Testing using the test Tweets
		 */
		try {
			CSVParser parser3 = CSVParser.parse(new File("resources/Test.csv"),
					Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord> records3 = (List<CSVRecord>) parser3.getRecords();
			System.err.println(records3.size());
			for (CSVRecord fileLine : records3) {
				String line = fileLine.get(2);
				line = cleanData(line);
				String[] words = line.split("\\s");
				System.out.println(bayes.classify(Arrays.asList(words))
						.getCategory());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void bayesLearnOther() {
		/*
		 * Parsing the tweets about the "other" category.
		 */
		try {
			CSVParser parser2 = CSVParser.parse(
					new File("resources/Other.csv"), Charset.defaultCharset(),
					CSVFormat.EXCEL);
			List<CSVRecord> records2 = (List<CSVRecord>) parser2.getRecords();
			System.err.println(records2.size());
			for (CSVRecord fileLine : records2) {
				String line = fileLine.get(0);
				line = cleanData(line);

				String[] words = line.split("\\s");
				bayes.learn("OTHER", Arrays.asList(words));

				for (int i = 0; i < words.length; i++) {
					if (wordCountMap2.get(words[i]) == null) {
						if (words[i].length() > 3) {
							Long countValue = new Long(1);
							wordCountMap2.put(words[i], countValue);
						}
					} else {
						if (words[i].length() > 3) {
							Long wordCount = wordCountMap2.get(words[i]);
							Long newWordCount = wordCount + 1;
							wordCountMap2.put(words[i], newWordCount);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void bayesLearnApp() {
		try {
			CSVParser parser = CSVParser.parse(new File(
					"resources/Mandrill.csv"), Charset.defaultCharset(),
					CSVFormat.EXCEL);
			List<CSVRecord> records = (List<CSVRecord>) parser.getRecords();
			for (CSVRecord fileLine : records) {

				String line = fileLine.get(0);
				line = cleanData(line);
				String[] words = line.split("\\s");

				bayes.learn("APP", Arrays.asList(words));

				for (int i = 0; i < words.length; i++) {
					if (wordCountMap.get(words[i]) == null) {
						if (words[i].length() > 3) {
							Long countValue = new Long(1);
							wordCountMap.put(words[i], countValue);
						}
					} else {
						if (words[i].length() > 3) {
							Long wordCount = wordCountMap.get(words[i]);
							Long newWordCount = wordCount + 1;
							wordCountMap.put(words[i], newWordCount);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

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

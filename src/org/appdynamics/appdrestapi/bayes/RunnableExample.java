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

    public static void main(String[] args) {

    	Map<String, Long> wordCountMap = new HashMap<String, Long>();
    	Map<String, Long> wordCountMap2 = new HashMap<String, Long>();

    	//FIRST METHOD USING THE BAYESCLASSIFIER CLASS
        /*
         * Create a new classifier instance. The context features are
         * Strings and the context will be classified with a String according
         * to the featureset of the context.
         */
    	final BayesClassifier<String, String> bayes = new BayesClassifier<String, String>();

    	try {
			CSVParser parser = CSVParser.parse(new File("resources/Mandrill.csv") , Charset.defaultCharset(), CSVFormat.EXCEL);
			List<CSVRecord>  records  = (List<CSVRecord>)parser.getRecords();
			System.err.println(records.size());
			for (CSVRecord fileLine : records) {
				String line = fileLine.get(0);
				line = line.toLowerCase();
                line = line.replace(". ", " ");
                line = line.replace(": ", " ");
                line = line.replace("?", " ");
                line = line.replace("!", " ");
                line = line.replace(";", " ");
                line = line.replace(",", " ");

				String[] words = line.split("\\s");
				bayes.learn("APP", Arrays.asList(words));

				for (int i = 0; i < words.length; i++) {
					if (wordCountMap.get(words[i]) == null)
					{
					    if (words[i].length() > 3)
					    {
					        Long countValue = new Long(1);
					        wordCountMap.put(words[i], countValue);
					    }
					}else
					{
					    if (words[i].length() > 3)
					    {
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

    	/*
    	 * Parsing the tweets about the "other" category.
    	 */
    	try {
            CSVParser parser2 = CSVParser.parse(new File("resources/Other.csv") , Charset.defaultCharset(), CSVFormat.EXCEL);
            List<CSVRecord>  records2  = (List<CSVRecord>)parser2.getRecords();
            System.err.println(records2.size());
            for (CSVRecord fileLine : records2) {
                String line = fileLine.get(0);
                line = line.toLowerCase();
                line = line.replace(". ", " ");
                line = line.replace(": ", " ");
                line = line.replace("?", " ");
                line = line.replace("!", " ");
                line = line.replace(";", " ");
                line = line.replace(",", " ");

                String[] words = line.split("\\s");
                bayes.learn("OTHER", Arrays.asList(words));

                for (int i = 0; i < words.length; i++) {
                    if (wordCountMap2.get(words[i]) == null)
                    {
                        if (words[i].length() > 3)
                        {
                            Long countValue = new Long(1);
                            wordCountMap2.put(words[i], countValue);
                        }
                    }else
                    {
                        if (words[i].length() > 3)
                        {
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
            for ( Map.Entry<String, Long> wordCount  : wordCountMap2.entrySet()) {
                String word =  wordCount.getKey();
                Long count =  wordCount.getValue();
                //System.out.println("The word is " + word + " and the count is " + count);
            }

            /*
             * Testing using the test Tweets
             */
            try {
                CSVParser parser3 = CSVParser.parse(new File("resources/Test.csv") , Charset.defaultCharset(), CSVFormat.EXCEL);
                List<CSVRecord>  records3  = (List<CSVRecord>)parser3.getRecords();
                System.err.println(records3.size());
                for (CSVRecord fileLine : records3) {
                    String line = fileLine.get(2);
                    line = line.toLowerCase();
                    line = line.replace(". ", " ");
                    line = line.replace(": ", " ");
                    line = line.replace("?", " ");
                    line = line.replace("!", " ");
                    line = line.replace(";", " ");
                    line = line.replace(",", " ");


                    String[] words = line.split("\\s");
                    System.out.println(bayes.classify(Arrays.asList(words)).getCategory());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //SECOND METHOD: DATASMART METHOD


            Map<String, Double> tokenProbabilityApp = new HashMap<String, Double>();
            Map<String, Double> tokenProbabilityOther = new HashMap<String, Double>();
            Double totalTokenCountApp = new Double(0);
            Double totalTokenCountOther = new Double(0);
            //Incrementing each token count by 1 in the App category
            for ( Map.Entry<String, Long> wordCount : wordCountMap.entrySet()) {
                String word = wordCount.getKey();
                Long count =  wordCount.getValue() + 1;
                wordCountMap.put(word, count);
                totalTokenCountApp = totalTokenCountApp + count;
            }
            //Calculating the probability for each token in the App category.
            for ( Map.Entry<String, Long> wordCount : wordCountMap.entrySet()) {
                String word = wordCount.getKey();
                Long count =  wordCount.getValue();
                Double natLogApp = new Double(Math.log(count / totalTokenCountApp));
                tokenProbabilityApp.put(word, natLogApp);
            }
            //Incrementing each token count by 1 in the Other category
            for ( Map.Entry<String, Long> wordCount : wordCountMap2.entrySet()) {
                String word = wordCount.getKey();
                Long count =  wordCount.getValue() + 1;
                wordCountMap2.put(word, count);
                totalTokenCountOther = totalTokenCountOther + count;
            }
            //Calculating the probability for each token in the Other category.
            for ( Map.Entry<String, Long> wordCount : wordCountMap2.entrySet()) {
                String word = wordCount.getKey();
                Long count =  wordCount.getValue();
                Double natLogOther = new Double(Math.log(count / totalTokenCountOther));
                tokenProbabilityOther.put(word, natLogOther);
            }
            System.out.println(totalTokenCountApp);
            System.out.println(totalTokenCountOther);

            // Now to parse the test data
            try {
                CSVParser parser4 = CSVParser.parse(new File("resources/Test.csv") , Charset.defaultCharset(), CSVFormat.EXCEL);
                List<CSVRecord>  records4 = (List<CSVRecord>)parser4.getRecords();
                System.err.println(records4.size());
                for (CSVRecord fileLine : records4) {
                    String line = fileLine.get(2);
                    line = line.toLowerCase();
                    line = line.replace(". ", " ");
                    line = line.replace(": ", " ");
                    line = line.replace("?", " ");
                    line = line.replace("!", " ");
                    line = line.replace(";", " ");
                    line = line.replace(",", " ");


                    String[] words = line.split("\\s");
                    //Summing probabilities.
                    double appProb = 0;
                    double otherProb = 0;
                    for (int x = 0; x < words.length; x++)
                    {
                        //App tokens
                        if (tokenProbabilityApp.get(words[x]) != null && words[x].length() > 3)
                        {
                            appProb = tokenProbabilityApp.get(words[x]) + appProb;
                        }
                        //If token is not in map
                        else if (tokenProbabilityApp.get(words[x]) == null && words[x].length() > 3)
                        {
                            Double tempProb = new Double(Math.log(1 / totalTokenCountApp));
                            appProb = tempProb + appProb;
                        }
                        //Other tokens
                        if (tokenProbabilityOther.get(words[x]) != null && words[x].length() > 3)
                        {
                            otherProb = tokenProbabilityOther.get(words[x]) + otherProb;
                        }
                        //If token is not in map
                        else if (tokenProbabilityOther.get(words[x]) == null && words[x].length() > 3)
                        {
                            Double tempProb2 = new Double(Math.log(1 / totalTokenCountOther));
                            otherProb = tempProb2 + otherProb;
                        }
                    }
                    if (appProb > otherProb)
                    {
                        System.out.println("APP");
                        System.out.println(appProb);
                        System.out.println(otherProb);
                    }
                    else
                    {
                        System.out.println("OTHER");
                        System.out.println(appProb);
                        System.out.println(otherProb);
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        /*
         * The classifier can learn from classifications that are handed over
         * to the learn methods. Imagin a tokenized text as follows. The tokens
         * are the text's features. The category of the text will either be
         * positive or negative.
         */
         /*
        final String[] positiveText = "I love sunny days".split("\\s");
        bayes.learn("positive", Arrays.asList(positiveText));

        final String[] negativeText = "I hate rain".split("\\s");
        bayes.learn("negative", Arrays.asList(negativeText));

        final String[] negativeText1 = "Bob really hates rain".split("\\s");
        bayes.learn("negative", Arrays.asList(negativeText1));

        /*
         * Now that the classifier has "learned" two classifications, it will
         * be able to classify similar sentences. The classify method returns
         * a Classification Object, that contains the given featureset,
         * classification probability and resulting category.
         */
        /*
        final String[] unknownText1 = "today is a sunny day".split("\\s");
        final String[] unknownText2 = "there will be rain".split("\\s");
        final String[] unknownText3 = "rain is comming tomorrow".split("\\s");
        final String[] unknownText4 = "there won't be rain".split("\\s");
        final String[] unknownText5 = "there is no sun".split("\\s");
        final String[] unknownText6 = "sun is today".split("\\s");
        System.out.println(bayes.classify(Arrays.asList(unknownText1)).getCategory());
        System.out.println(bayes.classify(Arrays.asList(unknownText2)).getCategory());
        System.out.println(bayes.classify(Arrays.asList(unknownText3)).getCategory());
        System.out.println(bayes.classify(Arrays.asList(unknownText4)).getCategory());
        System.out.println(bayes.classify(Arrays.asList(unknownText5)).getCategory());
        System.out.println(bayes.classify(Arrays.asList(unknownText6)).getCategory());

        /*
         * The BayesClassifier extends the abstract Classifier and provides
         * detailed classification results that can be retrieved by calling
         * the classifyDetailed Method.
         *
         * The classification with the highest probability is the resulting
         * classification. The returned List will look like this.
         * [
         *   Classification [
         *     category=negative,
         *     probability=0.0078125,
         *     featureset=[today, is, a, sunny, day]
         *   ],
         *   Classification [
         *     category=positive,
         *     probability=0.0234375,
         *     featureset=[today, is, a, sunny, day]
         *   ]
         * ]
         */
        /*
        ((BayesClassifier<String, String>) bayes).classifyDetailed(Arrays.asList(unknownText1));
        Collection<Classification<String, String>> c1 = bayes.classifyDetailed(Arrays.asList(unknownText2));
        Collection<Classification<String, String>> c2 = bayes.classifyDetailed(Arrays.asList(unknownText3));
        Collection<Classification<String, String>> c3 = bayes.classifyDetailed(Arrays.asList(unknownText4));
        Collection<Classification<String, String>> c4 = bayes.classifyDetailed(Arrays.asList(unknownText5));
        Collection<Classification<String, String>> c5 = bayes.classifyDetailed(Arrays.asList(unknownText6));

        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);
        System.out.println(c4);
        System.out.println(c5);
        */

        System.out.println(new Integer(bayes.getCategoriesTotal()).toString());
        System.out.println(bayes.getCategories());
        System.out.println( bayes.getFeatures());



        /*
         * Please note, that this particular classifier implementation will
         * "forget" learned classifications after a few learning sessions. The
         * number of learning sessions it will record can be set as follows:
         */
        bayes.setMemoryCapacity(500); // remember the last 500 learned classifications
    }

}

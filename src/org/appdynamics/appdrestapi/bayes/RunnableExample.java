package org.appdynamics.appdrestapi.bayes;

import java.util.Arrays;
import java.util.Collection;




public class RunnableExample {

	
	
    public static void main(String[] args) {

        /*
         * Create a new classifier instance. The context features are
         * Strings and the context will be classified with a String according
         * to the featureset of the context.
         */
        final BayesClassifier<String, String> bayes =
                new BayesClassifier<String, String>();

        /*
         * The classifier can learn from classifications that are handed over
         * to the learn methods. Imagin a tokenized text as follows. The tokens
         * are the text's features. The category of the text will either be
         * positive or negative.
         */
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
        ((BayesClassifier<String, String>) bayes).classifyDetailed(Arrays.asList(unknownText1));
        Collection<Classification<String, String>> c1 = bayes.classifyDetailed(Arrays.asList(unknownText2));
        Collection<Classification<String, String>> c2 = bayes.classifyDetailed(Arrays.asList(unknownText3));
        Collection<Classification<String, String>> c3 = bayes.classifyDetailed(Arrays.asList(unknownText4));
        Collection<Classification<String, String>> c4 = bayes.classifyDetailed(Arrays.asList(unknownText5));
        Collection<Classification<String, String>> c5 = bayes.classifyDetailed(Arrays.asList(unknownText5));

        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);
        System.out.println(c4);
        System.out.println(c5);
        
        
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

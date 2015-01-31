package uk.ac.susx.tag.demographyframework;

import com.google.gson.Gson;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by thomas on 30/01/15.
 */
public class TestController {
	//TODO: load & classify data with classificationframework
	public static void main(String[] args) throws IOException {
		Gson gson = Utils.getGson();
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution.json"), gson);
		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);

		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingStream);
	}
}

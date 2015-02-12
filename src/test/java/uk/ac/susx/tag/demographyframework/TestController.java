package uk.ac.susx.tag.demographyframework;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import uk.ac.susx.tag.classificationframework.Evaluation;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.genderdetector.Country;
import uk.ac.susx.tag.genderdetector.GenderDetector;
import uk.ac.susx.tag.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by thomas on 30/01/15.
 */
public class TestController {
	public static void main(String[] args) throws IOException {
		Gson gson = Utils.getGson();

		// Individual vs. Institution
		/*
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution.json"), gson);
		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);
		*/
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution.json"), gson);
		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.jon"), gson);

		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false); // Exciting new pipeline builder

		System.out.println("Loading training data...");
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));


		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.json"), gson);
		System.out.println("==== EVAL NB - Individual vs. Institution =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		nb.writeJson(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/models/nb_individual_vs_institution.json"), pipeline);

		// Male vs. Female
		/*
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution.json"), gson);
		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);
		*/
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female.json"), gson);
		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.jon"), gson);

		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false); // Exciting new pipeline builder

		System.out.println("Loading training data...");
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female_gs.json"), gson);
		System.out.println("==== EVAL NB - Male vs. Female =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		nb.writeJson(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/models/nb_male_vs_female.json"), pipeline);

		// Gender Detector
		JsonReader reader = new JsonReader(new InputStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/models/nb_male_vs_female.json")))
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);
		System.out.println("GENDER GUESS: " + gd.guess("thomas"));
	}
}

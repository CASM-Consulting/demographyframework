package uk.ac.susx.tag.demographyframework;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.susx.tag.classificationframework.Evaluation;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.datastructures.ModelState;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.Country;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.GenderDetector;
import uk.ac.susx.tag.method51.twitter.demography.utils.Utils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 30/01/15.
 */
public class TestController {
	public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {

		//socialGradeClassification();
		//createSocialGradeModel();
		genderClassification();
		genderClassificationFromModel();
		//ageClassification();
		//createAgeModel();
		//employmentStatusClassification();
		//createEmploymentStatusModel();
	}

	private static void ageClassification() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine_train.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Profile Description and Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_and_tweets_dataset_coarse_train.json"), gson);
		System.out.println("==== EVAL NB - Tweets and Profile Description Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");
	}

	private static void createAgeModel() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/age_tweets_fine"));

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/age_tweets_coarse"));

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/age_profile_fine"));

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/age_profile_coarse"));
	}

	private static void createSocialGradeModel() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_codes.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/socialgrade_tweets_fine"));

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_coarse_codes.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/socialgrade_tweets_coarse"));

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_codes.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/socialgrade_profile_fine"));

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_codes.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/socialgrade_profile_coarse"));
	}

	private static void socialGradeClassification() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_train.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Profile Description and Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_and_tweets_dataset_coarse_train.json"), gson);
		System.out.println("==== EVAL NB - Tweets and Profile Description Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");
	}

	private static void employmentStatusClassification() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_fine_train.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Very Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_very_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_very_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets Very Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_fine_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Very Coarse Grained Classification - Profile Description and Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_very_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_very_coarse_train.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Very Coarse Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");
	}

	private static void createEmploymentStatusModel() throws IOException {
		Gson gson = Utils.getGson();

		// Fine Grained Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_fine.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/employment_status_tweets_fine"));

		// Coarse Grained Classification - Tweets
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/employment_status_tweets_coarse"));

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_fine.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/employment_status_profile_fine"));

		// Coarse Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/employment_status_profile_coarse"));
	}

	private static void genderClassificationFromModel() throws IOException, URISyntaxException, ClassNotFoundException {
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);

		System.out.println(gd.guess("thomas"));

		ModelState m = ModelState.load(new File(GenderDetector.class.getResource("models/male_vs_female").toURI()));
	}

	private static void genderClassification() throws IOException, URISyntaxException {
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
		List<Pair<String, String>> data = new ArrayList<>();

		//InputStream in = new FileInputStream("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/name_labelling_male_vs_female.json");
		InputStream in = new FileInputStream("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female_gs.json");
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			String name = null;
			String gender = null;
			while (reader.hasNext()) {
				String key = reader.nextName();
				switch (key) {
					//case "name":
					case "text":
						name = reader.nextString();
						break;
					case "label":
						gender = reader.nextString();
						break;
					case "id":
						reader.nextInt(); // ignore
						break;
				}
			}
			reader.endObject();
			data.add(Pair.of(name, gender));
		}
		reader.endArray();

		// Genderize the stuff
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);

		int trueCount = 0;
		int malePrecisionEnum = 0;
		int femalePrecisionEnum = 0;
		int malePrecisionDenom = 0;
		int femalePrecisionDenom = 0;
		int femaleRecallEnum = 0;
		int femaleRecallDenom = 0;
		int maleRecallEnum = 0;
		int maleRecallDenom = 0;

		int foundCount = 0;
		int notFoundCount = 0;

		for (Pair<String, String> p : data) {
			String predicted = gd.extractAndGuessString(p.getLeft()).toLowerCase();

			if (predicted.toLowerCase().equals("unknown")) {
				notFoundCount++;
				predicted = pipeline.labelString(nb.bestLabel(pipeline.extractFeatures(new Instance("", p.getLeft(), String.format("%d", p.getLeft().hashCode()))).getFeatures()));

				//predicted = nb.bestLabel();

			} else {
				foundCount++;
			}

			// Accuracy
			if (predicted.equals(p.getRight())) {
				trueCount++;
			}

			// Precision
			if (predicted.equals("male") && p.getRight().equals("male")) {
				malePrecisionEnum++;
			}
			if (predicted.equals("male")) {
				malePrecisionDenom++;
			}

			if (predicted.equals("female") && p.getRight().equals("female")) {
				femalePrecisionEnum++;
			}
			if (predicted.equals("female")) {
				femalePrecisionDenom++;
			}

			// Recall
			if (p.getRight().equals("male") && predicted.equals("male")) {
				maleRecallEnum++;
			}
			if (p.getRight().equals("male")) {
				maleRecallDenom++;
			}

			if (p.getRight().equals("female") && predicted.equals("female")) {
				femaleRecallEnum++;
			}
			if (p.getRight().equals("female")) {
				femaleRecallDenom++;
			}
		}

		double accuracy = trueCount / (double)data.size();
		double malePrecision = malePrecisionEnum / (double)malePrecisionDenom;
		double maleRecall = maleRecallEnum / (double)maleRecallDenom;
		double femalePrecision = femalePrecisionEnum / (double)femalePrecisionDenom;
		double femaleRecall = femaleRecallEnum / (double)femaleRecallDenom;
		double maleF1 = (2 * (malePrecision * maleRecall)) / (malePrecision + maleRecall);
		double femaleF1 = (2 * (femalePrecision * femaleRecall)) / (femalePrecision + femaleRecall);

		System.out.println("ACCURACY: " + accuracy);
		System.out.println("MALE PRECISION: " + malePrecision);
		System.out.println("MALE RECALL: " + maleRecall);
		System.out.println("FEMALE PRECISION: " + femalePrecision);
		System.out.println("FEMALE RECALL: " + femaleRecall);
		System.out.println("MALE F1:" + maleF1);
		System.out.println("FEMAIL F1:" + femaleF1);
		System.out.println("FOUND COUNT: " + foundCount);
		System.out.println("NOT FOUND COUNT: " + notFoundCount);
	}
}

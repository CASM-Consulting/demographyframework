package uk.ac.susx.tag.demographyframework;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.susx.tag.classificationframework.Evaluation;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifierPreComputed;
import uk.ac.susx.tag.classificationframework.datastructures.Document;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.datastructures.ModelState;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.exceptions.EvaluationException;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.Country;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.GenderDetector;
import uk.ac.susx.tag.method51.twitter.demography.utils.Utils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by thomas on 30/01/15.
 */
public class TestController {
	public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {

		//socialGradeClassification();
		//createSocialGradeModel();
		//genderClassification();
		//genderClassificationFromModel();
		//ageClassification();
		//createAgeModel();
		createGenderMaleVsFemaleModel();
		//employmentStatusClassification();
		//createEmploymentStatusModel();
		//presenceOfChildrenClassification();
		//createPresenceOfChildrenModel();
		//genderClassificationDynamicPriors();

		// Cross Validation
		//	- Gender
		//	- Age
		//crossValidateAgeModel();
		//	- Social Grade
		//	- Employment Status
		//	- Presence of Children
	}



	private static void createPresenceOfChildrenModel() throws IOException {
		Gson gson = Utils.getGson();

		// Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/tweets_dataset.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/presence_of_children_tweets"));

		// Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/profile_description_dataset.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/presence_of_children_description"));
	}

	private static void presenceOfChildrenClassification() throws IOException {
		Gson gson = Utils.getGson();

		// Classification - Tweets
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/tweets_dataset_train.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/tweets_dataset_test.json"), gson);
		System.out.println("==== EVAL NB - Tweets =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Fine Grained Classification - Profile Description
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/profile_description_dataset_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/profile_description_dataset_test.json"), gson);
		System.out.println("==== EVAL NB - Profile Description Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");
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

		// Fine Grained Classification - Train on tweets, classify profile descriptions
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Training: Tweets Fine Grained; Classification Profile Descriptions Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Train on tweets, classify profile descriptions
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Training: Tweets Coarse Grained; Classification Profile Descriptions Coarse Grained =======");
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

	private static void createGenderMaleVsFemaleModel() throws IOException {
		Gson gson = Utils.getGson();

		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/gender_labelling_male_vs_female_my_tsb_merged.json"), gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		m.save(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/gender_merged"));
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

		// Fine Grained Classification - Train on tweets, classify profile descriptions
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_test.json"), gson);
		System.out.println("==== EVAL NB - Training: Tweets Fine Grained; Classification Profile Descriptions Fine Grained =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		// Coarse Grained Classification - Train on tweets, classify profile descriptions
		trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_coarse_train.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_test.json"), gson);
		System.out.println("==== EVAL NB - Training: Tweets Coarse Grained; Classification Profile Descriptions Coarse Grained =======");
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

	private static void genderClassificationDynamicPriors() throws URISyntaxException, IOException, ClassNotFoundException {
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);
		Gson gson = Utils.getGson();

		ModelState m = ModelState.load(new File(GenderDetector.class.getResource("models/male_vs_female").toURI()));

		// Classify TSB Users with pre-trained model without dynamic priors
		NaiveBayesClassifierPreComputed nb = (NaiveBayesClassifierPreComputed)m.classifier.getPrecomputedClassifier();
		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_dataset_test.json"), gson);
		System.out.println("==== EVAL NB - pre-trained, no dynamic priors =======");
		System.out.println(new Evaluation(nb, m.pipeline, goldStandardStream.iterableOverProcessedInstances(m.pipeline)));
		System.out.println("====================");

		/*
		The below results are somewhat expected, the trained model has a prior bias of 67:33 towards male whereas on the evaluation dataset
		its roughly the other way round. Hence, while recall on male is very high (the majority of users being classified as male), the precision
		is rather low, for female its the other way round, we need a lot more evidence to classify a user as female, hence results are quite
		precise, however at the cost of missing many cases.
		==== EVAL NB - pre-trained, no dynamic priors =======
		female

		  Precision : 0.881
		  Recall    : 0.421
		  FB1       : 0.57

		male

		  Precision : 0.503
		  Recall    : 0.912
		  FB1       : 0.649

		Accuracy    : 0.613

		Confusion Matrix (rows = actual label, columns = predicted label)

		  X		fem mal
		  fem	512	705
		  mal	69	714

		====================
		 */

		// Classify TSB Users with pre-trained model with dynamic priors
		int batchSize = 20;
		nb = (NaiveBayesClassifierPreComputed)m.classifier.getPrecomputedClassifier();
		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset_test.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset_test.json"), gson);

		Int2IntOpenHashMap dynamicPriors = new Int2IntOpenHashMap();
		dynamicPriors.defaultReturnValue(0);

		List<ProcessedInstance> l = new LinkedList<>();

		System.out.println("Starting Priors: " + nb.getLabelPriors());

		int totalDocuments = 0;
		int totalCorrect = 0;
		for (Instance doc : goldStandardStream.iterableOverInstances()) {
			String[] parts = doc.text.split(" ABCDEFGHIJKLMNOPQRSTUVWXYZ "); // Hacky-whacky FTW!!!
			String name = parts[0];

			String gender = gd.extractAndGuessString(name).toLowerCase();
			if (!gender.equals("unknown")) {
				dynamicPriors.addTo(m.pipeline.labelIndex(gender), 1);

				int sum = dynamicPriors.values().stream().reduce(0, (a, b) -> a + b);
				int min = dynamicPriors.values().stream().reduce(Integer.MAX_VALUE, (a, b) -> b < a ? b : a);
				if (sum % batchSize == 0 && (dynamicPriors.size() > 1 && min > 0)) { // <-- make sure we've observed EVERY target label...
					Int2DoubleMap priors = new Int2DoubleOpenHashMap();
					for (int idx : dynamicPriors.keySet()) {
						priors.put(idx, Math.log((double)dynamicPriors.get(idx)) - Math.log((double)sum));
					}
					System.out.println("Current Priors: " + nb.getLabelPriors());
					nb.setLabelPriors(priors);
				}
			}
			doc.text = parts[1];

			ProcessedInstance p = m.pipeline.extractFeatures(doc);

			l.add(p);
			/*
			String systemLabel = m.pipeline.labelString(nb.bestLabel(p.features));
			String goldLabel = m.pipeline.labelString(p.getLabel());

			if (systemLabel.equals(goldLabel))
				totalCorrect++;
			totalDocuments++;
			*/
		}

		System.out.println("==== EVAL NB - pre-trained, dynamic priors =======");
		System.out.println(new Evaluation(nb, m.pipeline, l));
		System.out.println("====================");



		// Classify TSB Users with pre-trained model with dynamic priors, starting from uniform priors
	}

	private static void genderClassificationFromModel() throws IOException, URISyntaxException, ClassNotFoundException {
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);

		System.out.println(gd.guess("thomas"));

		ModelState m = ModelState.load(new File(GenderDetector.class.getResource("models/male_vs_female").toURI()));
	}

	private static void genderClassification() throws IOException, URISyntaxException {
		Gson gson = Utils.getGson();

		// Individual vs. Institution
		JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/polly/GenderLabelling/individual_vs_institution.json"), gson);
		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);

		//JsonListStreamReader trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution.json"), gson);
		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.jon"), gson);

		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false); // Exciting new pipeline builder

		System.out.println("Loading training data...");
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));


		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.json"), gson);
		JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);
		System.out.println("==== EVAL NB - Individual vs. Institution =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		//nb.writeJson(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/models/nb_individual_vs_institution.json"), pipeline);

		// Male vs. Female

		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/polly/GenderLabelling/individual_vs_institution.json"), gson);

		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female.json"), gson);
		//JsonListStreamReader goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_individual_vs_institution_gs.jon"), gson);

		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false); // Exciting new pipeline builder

		System.out.println("Loading training data...");
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female_gs.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/polly/GenderLabelling/individual_vs_institution_gs.jon"), gson);
		System.out.println("==== EVAL NB - Male vs. Female =======");
		System.out.println(new Evaluation(nb, pipeline, goldStandardStream.iterableOverProcessedInstances(pipeline)));
		System.out.println("====================");

		//nb.writeJson(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/models/nb_male_vs_female.json"), pipeline);

		// Gender Detector
		List<Pair<String, String>> data = new ArrayList<>();

		//InputStream in = new FileInputStream("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/name_labelling_male_vs_female.json");
		//InputStream in = new FileInputStream("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/gender_labelling_male_vs_female_gs.json");
		InputStream in = new FileInputStream("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/gender_labelling_male_vs_female_gs.json");
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

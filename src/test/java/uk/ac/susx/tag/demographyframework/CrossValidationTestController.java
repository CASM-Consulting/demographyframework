package uk.ac.susx.tag.demographyframework;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import uk.ac.susx.tag.classificationframework.Util;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.crossvalidation.KFoldCrossValidation;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by thomas on 24/04/15.
 */
public class CrossValidationTestController {

	public static void main(String[] args) throws IOException {
		//crossValidateAgeModel();
		crossValidateSocialGradeModel();
		crossValidateEmploymentStatusModel();
		crossValidatePresenceOfCheildrenModel();
	}

	private static void crossValidatePresenceOfCheildrenModel() throws IOException {
		Gson gson = Util.getGson();

		File f;
		JsonListStreamReader trainingStream;
		FeatureExtractionPipeline pipeline;
		List<ProcessedInstance> trainingData;

		// Classification - Profile Description
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/profile_description_dataset.json");
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/profile_description_dataset.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Presence of Children: Profile Description");

		// Classification - Tweets
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/tweets_per_user_dataset.json");
		//File f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/presence_of_children/tweets_dataset.json");

		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Presence of Children: Tweets");

	}

	private static void crossValidateEmploymentStatusModel() throws IOException {
		Gson gson = Util.getGson();

		File f;
		JsonListStreamReader trainingStream;
		FeatureExtractionPipeline pipeline;
		List<ProcessedInstance> trainingData;

		// Fine Grained Classification - Profile Description
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_fine.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_fine.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Employment Status Classification: Fine Grained - Profile Description:");

		// Coarse Grained Classification - Profile Description
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_coarse.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/profile_description_dataset_coarse.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Employment Status Classification: Coarse Grained - Profile Description:");

		// Fine Grained Classification - Tweets
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_fine.json");
		//File f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_fine.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Employment Status Classification: Fine Grained - Tweets:");

		// Coarse Grained Classification - Tweets
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_coarse.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/employment_status/tweets_dataset_coarse.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Employment Status Classification: Coarse Grained - Tweets:");
	}

	private static void crossValidateSocialGradeModel() throws IOException {
		Gson gson = Util.getGson();

		File f;
		JsonListStreamReader trainingStream;
		FeatureExtractionPipeline pipeline;
		List<ProcessedInstance> trainingData;

		// Fine Grained Classification - Tweets
		//File f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_codes.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_fine_codes.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Social Grade Classification: Fine Grained - Tweets:");

		// Coarse Grained Classification - Tweets
		// f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/tweets_dataset_coarse_codes.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets//socialclass/tweets_dataset_coarse_codes.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Social Grade Classification: Coarse Grained - Tweets:");

		// Fine Grained Classification - Profile Description
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_codes.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_fine_codes.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Social Grade Classification: Fine Grained - Profile Description:");

		// Coarse Grained Classification - Profile Description
		// f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_codes.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/socialclass/profile_description_dataset_coarse_codes.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Social Grade Classification: Coarse Grained - Profile Description:");
	}

	private static void crossValidateAgeModel() throws IOException{
		Gson gson = Util.getGson();

		// Fine Grained Classification - Tweets
		//File f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine.json");
		File f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_fine.json");
		JsonListStreamReader trainingStream = new JsonListStreamReader(f, gson);
		FeatureExtractionPipeline pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		List<ProcessedInstance> trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Age Classification: Fine Grained - Tweets:");

		// Coarse Grained Classification - Tweets
		//f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_coarse.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Age Classification: Coarse Grained - Tweets:");

		// Fine Grained Classification - Profile Description
		// f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_fine.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Age Classification: Fine Grained - Profile Description:");

		// Coarse Grained Classification - Profile Description
		// f = new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse.json");
		f = new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_coarse.json");
		trainingStream = new JsonListStreamReader(f, gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		crossValidateClassifier(pipeline, trainingData, "Age Classification: Fine Grained - Profile Description:");
	}

	private static void crossValidateClassifier(FeatureExtractionPipeline pipeline, List<ProcessedInstance> data, String title) {
		System.out.println("Starting Cross Validation...");
		KFoldCrossValidation<NaiveBayesClassifier> kf = new KFoldCrossValidation<>(NaiveBayesClassifier.class, pipeline, data);

		kf.crossValidateClassifier(10);

 		System.out.println(title);
		System.out.println("\tAccuracy: " + kf.getAccuracy());
		System.out.println("\t----------------------------------------------");
		System.out.println("\tWeighted Precision: " + kf.getWeightedPrecision());
		System.out.println("\tWeighted Recall: " + kf.getWeightedRecall());
		System.out.println("\tWeighted F1-Score: " + kf.getWeightedF1Score());
		System.out.println("\t----------------------------------------------");
		System.out.println("\tMacro Precision: " + kf.getMacroPrecision());
		System.out.println("\tMacro Recall: " + kf.getMacroRecall());
		System.out.println("\tMacro F1-Score: " + kf.getMacroF1Score());
		System.out.println("================================================");
	}

}

package uk.ac.susx.tag.demographyframework;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.susx.tag.classificationframework.Util;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.Country;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.GenderDetector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thk22 on 28/05/2015.
 *
 * This is a very ugly and very quick class for evaluating the Age Model with the 2-4 digit high Precision thingy.
 * I was quite hungover when that was written...
 */
public class AgeEvaluation {
	private static final String REGEX_TEMPLATE = "^\\w*\\w{1}([0-9]{%d})$";

	// Metrics
	private Double[] precisionsPerClass;
	private Double[] recallsPerClass;
	private Double[] f1ScoresPerClass;
	private double weightedPrecision = 0.;
	private double weightedRecall = 0.;
	private double weightedF1Score = 0.;
	private double macroPrecision = 0.;
	private double macroRecall = 0.;
	private double macroF1Score = 0.;
	private double accuracy;

	private Double[] precisionsPerClassGazeteer;
	private Double[] recallsPerClassGazeteer;
	private Double[] f1ScoresPerClassGazeteer;
	private double weightedPrecisionGazeteer = 0.;
	private double weightedRecallGazeteer = 0.;
	private double weightedF1ScoreGazeteer = 0.;
	private double macroPrecisionGazeteer = 0.;
	private double macroRecallGazeteer = 0.;
	private double macroF1ScoreGazeteer = 0.;
	private double accuracyGazeteer;

	private Double[] precisionsPerClassNB;
	private Double[] recallsPerClassNB;
	private Double[] f1ScoresPerClassNB;
	private double weightedPrecisionNB = 0.;
	private double weightedRecallNB = 0.;
	private double weightedF1ScoreNB = 0.;
	private double macroPrecisionNB = 0.;
	private double macroRecallNB = 0.;
	private double macroF1ScoreNB = 0.;
	private double accuracyNB;

	ArrayList<Integer> predicted = new ArrayList<>();
	ArrayList<Integer> actual = new ArrayList<>();
	ArrayList<Integer> predictedGazeteer = new ArrayList<>();
	ArrayList<Integer> actualGazeteer = new ArrayList<>();
	ArrayList<Integer> predictedNB = new ArrayList<>();
	ArrayList<Integer> actualNB = new ArrayList<>();
	private int predictedDocs = 0;
	private int gazeteeredDocs = 0;

	public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, URISyntaxException {
		AgeEvaluation ae = new AgeEvaluation();
		Gson gson = Util.getGson();

		JsonListStreamReader trainingStream = null;
		FeatureExtractionPipeline pipeline = null;
		List<Instance> trainingData = null;
		JsonListStreamReader goldStandardStream = null;

		// Cross Validation over Age Profile Descriptions, coarse, incl 2-4 digit - IM
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_coarse.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ae.crossValidateTheStuff(ae, trainingData, pipeline, "Age Classification - Profile Descriptions; Coarse Grained; incl 2-4 digits", "ageClassificationProfileDescriptionCoarseIncl24digits.csv", false);

		// Cross Validation over Age Profile Descriptions, coarse, incl 2-4 digit - 4dCI
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_coarse.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_coarse.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ae.crossValidateTheStuff(ae, trainingData, pipeline, "Age Classification - Profile Descriptions; Coarse Grained; incl 2-4 digits", "ageClassificationProfileDescriptionCoarseIncl24digits.csv", false);


		// Cross Validation over Age Profile Descriptions, fine, incl 2-4 digit
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_fine.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/profile_description_dataset_incl_screen_name_fine.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ae.crossValidateTheStuff(ae, trainingData, pipeline, "Age Classification - Profile Descriptions; Fine Grained; incl 2-4 digits", "ageClassificationProfileDescriptionFineIncl24digits.csv", true);

		// Cross Validation over Age Tweets, coarse, incl 2-4 digit
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_incl_screen_name_coarse.json"), gson);
		//trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_incl_screen_name_coarse.json"), gson);
		//pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		//trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		//ae.crossValidateTheStuff(ae, trainingData, pipeline, "Age Classification - Tweets; Coarse Grained; incl 2-4 digits", "ageClassificationTweetsCoarseIncl24digits.csv", false);

		// Cross Validation over Age Tweets, fine, incl 2-4 digit
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_incl_screen_name_fine.json"), gson);
		//trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/age/tweets_dataset_incl_screen_name_fine.json"), gson);
		//pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		//trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		//ae.crossValidateTheStuff(ae, trainingData, pipeline, "Age Classification - Tweets; Fine Grained; incl 2-4 digits", "ageClassificationTweetsFineIncl24digits.csv", true);

	}

	public void crossValidateTheStuff(AgeEvaluation ae, List<Instance> trainingData, FeatureExtractionPipeline pipeline, String title, String fname, boolean fineGrained) throws URISyntaxException, InstantiationException, IllegalAccessException, IOException {
		ae.crossValidateAgeClassifier(trainingData, pipeline, NaiveBayesClassifier.class, 10, fineGrained);

		System.out.println(title);
		System.out.println("\tAccuracy: " + ae.accuracy);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tWeighted Precision: " + ae.weightedPrecision);
		System.out.println("\tWeighted Recall: " + ae.weightedRecall);
		System.out.println("\tWeighted F1-Score: " + ae.weightedF1Score);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tMacro Precision: " + ae.macroPrecision);
		System.out.println("\tMacro Recall: " + ae.macroRecall);
		System.out.println("\tMacro F1-Score: " + ae.macroF1Score);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tPredicted Docs: " + ae.predictedDocs);
		System.out.println("\tGazeteered Docs: " + ae.gazeteeredDocs);
		System.out.println("\tRatio: " + ((double)ae.gazeteeredDocs / ((double)(ae.gazeteeredDocs + ae.predictedDocs))) + " gazeteered");
		System.out.println("\tG A Z E T E E R   P E R F O R M A N C E");
		System.out.println("\tGazeteer Accuracy: " + ae.accuracyGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tGazeteer Weighted Precision: " + ae.weightedPrecisionGazeteer );
		System.out.println("\tGazeteer Weighted Recall: " + ae.weightedRecallGazeteer );
		System.out.println("\tGazeteer Weighted F1-Score: " + ae.weightedF1ScoreGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tGazeteer Macro Precision: " + ae.macroPrecisionGazeteer );
		System.out.println("\tGazeteer Macro Recall: " + ae.macroRecallGazeteer );
		System.out.println("\tGazeteer Macro F1-Score: " + ae.macroF1ScoreGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tN B   P E R F O R M A N C E");
		System.out.println("\tNB Leftover Accuracy: " + ae.accuracyNB);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tNB Leftover Weighted Precision: " + ae.weightedPrecisionNB );
		System.out.println("\tNB Leftover Weighted Recall: " + ae.weightedRecallNB );
		System.out.println("\tNB Leftover Weighted F1-Score: " + ae.weightedF1ScoreNB );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tNB Leftover Macro Precision: " + ae.macroPrecisionNB );
		System.out.println("\tNB Leftover Macro Recall: " + ae.macroRecallNB );
		System.out.println("\tNB Leftover Macro F1-Score: " + ae.macroF1ScoreNB );
		System.out.println("\t----------------------------------------------");

		//String basePath = "/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_results/polly/";
		//try {
		//	ae.saveConfusionMatrixAsCsv(basePath + fname, actual, predicted);
		//	ae.saveConfusionMatrixAsCsv(basePath + "Gazeteer" + fname, actualGazeteer, predictedGazeteer);
		//	ae.saveConfusionMatrixAsCsv(basePath + "NBLeftover" + fname, actualNB, predictedNB);
		//} catch (IOException ex) {
		//	ex.printStackTrace();
		//}

		ae.accuracy = 0.;
		ae.weightedPrecision = 0.;
		ae.weightedRecall = 0.;
		ae.weightedF1Score = 0.;
		ae.macroPrecision = 0.;
		ae.macroRecall = 0.;
		ae.macroF1Score = 0.;
		ae.gazeteeredDocs = 0;
		ae.predictedDocs = 0;
		ae.actual = new ArrayList<>();
		ae.predicted = new ArrayList<>();

		ae.accuracyGazeteer = 0.;
		ae.weightedPrecisionGazeteer = 0.;
		ae.weightedRecallGazeteer = 0.;
		ae.weightedF1ScoreGazeteer = 0.;
		ae.macroPrecisionGazeteer = 0.;
		ae.macroRecallGazeteer = 0.;
		ae.macroF1ScoreGazeteer = 0.;
		ae.actualGazeteer = new ArrayList<>();
		ae.predictedGazeteer = new ArrayList<>();

		ae.accuracyNB = 0.;
		ae.weightedPrecisionNB = 0.;
		ae.weightedRecallNB = 0.;
		ae.weightedF1ScoreNB = 0.;
		ae.macroPrecisionNB = 0.;
		ae.macroRecallNB = 0.;
		ae.macroF1ScoreNB = 0.;
		ae.actualNB = new ArrayList<>();
		ae.predictedNB = new ArrayList<>();
	}

	public void saveConfusionMatrixAsCsv(String path, ArrayList<Integer> act, ArrayList<Integer> pred) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');

		assert(act.size() == pred.size());

		for (int i = 0; i < act.size(); i++) {
			String[] row = {act.get(i).toString(), pred.get(i).toString()};
			writer.writeNext(row);
		}
		writer.close();
	}

	// TEMPORARY TODO: REMOVE ME
	protected List<Integer> createIndexSet(int length) {
		List<Integer> indexSet = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			indexSet.add(i);
		}

		return indexSet;
	}

	protected Int2ObjectArrayMap<List<Instance>> createInstFoldMap(List<Instance> dataset, List<Integer> indexSet, int folds) {
		Int2ObjectArrayMap<List<Instance>> foldMap = new Int2ObjectArrayMap<>(); // maybe do indexSet.size()

		for (int idx : indexSet) {
			int foldIdx = idx % folds;

			List<Instance> fold = foldMap.getOrDefault(foldIdx, new ArrayList<>());
			fold.add(dataset.get(idx));
			foldMap.put(foldIdx, fold);
		}

		return foldMap;
	}

	protected Pair<List<Instance>, List<Instance>> createInstTrainTestSplit(Int2ObjectArrayMap<List<Instance>> foldMap, int currTestFold, int folds) {
		List<Instance> trainData = new LinkedList<>();

		for (int i = 0; i < currTestFold; i++) {
			trainData.addAll(foldMap.get(i));
		}

		for (int i = currTestFold + 1; i < folds; i++) {
			trainData.addAll(foldMap.get(i));
		}

		return Pair.of(trainData, foldMap.get(currTestFold));
	}

	private int estimateAgeFromScreenName(String screenName, int numDigits, Range<Integer> targetRange, Set<Integer> excludeYears) {
		String regex = String.format(REGEX_TEMPLATE, numDigits);

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(screenName);

		int birthYear = -1;

		if (m.matches()) {
			int matchedYear = Integer.parseInt(m.group(1));

			if (targetRange.contains(matchedYear) && !excludeYears.contains(matchedYear)) {
				birthYear = matchedYear;
			}
		}

		int yearPadding = 0;
		if (birthYear != -1 && numDigits == 2) {
			yearPadding = (targetRange.lowerEndpoint().intValue() > birthYear) ? 2000 : 1900;
		}

		return (birthYear != -1) ? Calendar.getInstance().get(Calendar.YEAR) - (birthYear + yearPadding) : birthYear;
	}

	private String annotateAgeGroup(Map<String, Range<Integer>> targetBins, int age) {
		String ageGroup = "unknown";
		if (age >= 0) {
			for (String k : targetBins.keySet()) {
				if (targetBins.get(k).contains(age)) {
					ageGroup = k;
					break;
				}
			}
		}
		return ageGroup;
	}

	public void crossValidateAgeClassifier(List<Instance> instances, FeatureExtractionPipeline pipeline, Class<NaiveBayesClassifier> khlavKalash, int numFolds, boolean fineGrained) throws IOException, URISyntaxException, IllegalAccessException, InstantiationException {
		// Create IndexSet and shuffle it
		List<Integer> indexSet = createIndexSet(instances.size());
		Collections.shuffle(indexSet, new Random(42));

		// Create actual foldmap
		Int2ObjectArrayMap<List<Instance>> foldMap = createInstFoldMap(instances, indexSet, numFolds);

		int numLabels = fineGrained ? 5 : 2;//pipeline.getLabelIndexer().size();

		// Overall
		int totalDocuments = 0;
		int totalCorrect = 0;
		int[] truePositives = new int[numLabels];
		int[] falsePositives = new int[numLabels];
		int[] docsOfClass = new int[numLabels];

		// Gazeteer Performance
		int totalDocumentsGazeteer = 0;
		int totalCorrectGazeteer = 0;
		int[] truePositivesGazeteer = new int[numLabels];
		int[] falsePositivesGazeteer = new int[numLabels];
		int[] docsOfClassGazeteer = new int[numLabels];

		// NB Performance
		int totalDocumentsNB = 0;
		int totalCorrectNB = 0;
		int[] truePositivesNB = new int[numLabels];
		int[] falsePositivesNB = new int[numLabels];
		int[] docsOfClassNB = new int[numLabels];

		Range twoDigitAgeRange = Range.closed(50, 98);
		Range fourDigitAgeRange = Range.closed(1950, 2003);
		Set<Integer> twoDigitExcludeYears = new HashSet<>();
		Set<Integer> fourDigitExcludeYears = new HashSet<>();
		fourDigitExcludeYears.add(2000);
		Map<String, Range<Integer>> coarseBucket = new HashMap<>();
		coarseBucket.put("Under 30", Range.closed(0, 29));
		coarseBucket.put("Over 30", Range.closed(30, 130));
		Map<String, Range<Integer>> fineBucket = new HashMap<>();
		fineBucket.put("0-15", Range.closed(0, 15));
		fineBucket.put("16-24", Range.closed(16, 24));
		fineBucket.put("25-34", Range.closed(25, 34));
		fineBucket.put("35-49", Range.closed(35, 49));
		fineBucket.put("50+", Range.closed(50, 130));

		// RunRunRun!!!
		for (int i = 0; i < numFolds; i++) {

			// Partition
			Pair<List<Instance>, List<Instance>> currTrainTestSplit = createInstTrainTestSplit(foldMap, i, numFolds);

			List<Instance> trainData = currTrainTestSplit.getLeft();
			List<Instance> evaluationData = currTrainTestSplit.getRight();

			// Create new classifier instance & train
			try {
				// Create new classifier instance & train
				NaiveBayesClassifier classifier = khlavKalash.newInstance();

				List<ProcessedInstance> processedTrainData = new LinkedList<>();
				for (Instance instance : trainData) {
					processedTrainData.add(pipeline.extractFeatures(instance));
				}

				classifier.train(processedTrainData);

				// Evaluate
				for (Instance doc : evaluationData) {
					// Gazeteer stuff
					String[] parts = doc.text.split(" ABCDEFGHIJKLMNOPQRSTUVWXYZ "); // Hacky-whacky FTW!!!
					String name = parts[0].trim();

					int age = estimateAgeFromScreenName(name, 4, fourDigitAgeRange, fourDigitExcludeYears);
					age = (age == -1) ? estimateAgeFromScreenName(name, 2, twoDigitAgeRange, twoDigitExcludeYears) : age;

					String ageGroup = annotateAgeGroup((fineGrained ? fineBucket : coarseBucket), age);

					int predictedLabel;
					int gsLabel;

					if (ageGroup.equals("unknown")) {
						doc.text = parts[1];

						ProcessedInstance p = pipeline.extractFeatures(doc);

						predictedLabel = classifier.bestLabel(p.features);
						gsLabel = p.getLabel();

						predicted.add(predictedLabel);
						actual.add(gsLabel);

						predictedDocs++;

						predictedNB.add(predictedLabel);
						actualNB.add(gsLabel);

						totalDocumentsNB++;
						docsOfClassNB[gsLabel]++;

						if (predictedLabel == gsLabel) {
							truePositivesNB[predictedLabel]++;
							totalCorrectNB++;
						} else {
							falsePositivesNB[predictedLabel]++;
						}

					} else {
						predictedLabel = pipeline.labelIndex(ageGroup);
						gsLabel = pipeline.labelIndex(doc.label);

						gazeteeredDocs++;

						predictedGazeteer.add(predictedLabel);
						actualGazeteer.add(gsLabel);

						totalDocumentsGazeteer++;
						docsOfClassGazeteer[gsLabel]++;

						if (predictedLabel == gsLabel) {
							truePositivesGazeteer[predictedLabel]++;
							totalCorrectGazeteer++;
						} else {
							falsePositivesGazeteer[predictedLabel]++;
						}
					}

					totalDocuments++;
					docsOfClass[gsLabel]++;

					// Accuracy, Precision, Recall & F1-Score tallies
					if (predictedLabel == gsLabel) {
						truePositives[predictedLabel]++;
						totalCorrect++;
					} else {
						falsePositives[predictedLabel]++;

					}
				}

				//calculateMeasuresPerFold(truePositivesPerFold, falsePositivesPerFold, totalCorrectPerFold, docsOfClassPerFold, totalDocumentsPerFold, numLabels, posLabel);

			} catch (InstantiationException e) { // If any of these exceptions are thrown, sth is seriously wrong!
				e.printStackTrace();
			} catch (IllegalAccessException e) { // So die quickly and let the client figure out what he/she did wrong!
				e.printStackTrace();
			}
		}

		// Calculate all the stuff
		calculateMeasures(truePositives, falsePositives, totalCorrect, docsOfClass, totalDocuments, numLabels, -1);
		calculateMeasuresGazeteer(truePositivesGazeteer, falsePositivesGazeteer, totalCorrectGazeteer, docsOfClassGazeteer, totalDocumentsGazeteer, numLabels, -1);
		calculateMeasuresNB(truePositivesNB, falsePositivesNB, totalCorrectNB, docsOfClassNB, totalDocumentsNB, numLabels, -1);
		//finalisePerFoldMeasures();
	}

	// Validated against sklearn's measures (for the binary case when pos_label=None + for the case with given pos_label)
	protected void calculateMeasures(int[] truePositives, int[] falsePositives, int totalCorrect, int[] docsOfClass, int totalDocuments, int numLabels, int posLabel) {
		precisionsPerClass = new Double[numLabels];
		recallsPerClass = new Double[numLabels];
		f1ScoresPerClass = new Double[numLabels];

		// Per-Class Scores
		for (int i = 0; i < numLabels; i++) {
			Double currPrecision = truePositives[i] / (double) (truePositives[i] + falsePositives[i]);
			Double currRecall = truePositives[i] / (double) docsOfClass[i];

			precisionsPerClass[i] = (!currPrecision.isNaN()) ? currPrecision : 0.;
			recallsPerClass[i] = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * precisionsPerClass[i] * recallsPerClass[i] / (precisionsPerClass[i] + recallsPerClass[i]);
			f1ScoresPerClass[i] = (!currF1.isNaN()) ? currF1 : 0.;
		}

		// If we have a posLabel, we simply use its metrics (thats what sklearn does as well), otherwise we
		// calculate all measures on a per-class basis
		if (posLabel != -1) { // only pos class
			Double currPrecision = truePositives[posLabel] / (double)(truePositives[posLabel] + falsePositives[posLabel]);
			Double currRecall = truePositives[posLabel] / (double)(docsOfClass[posLabel]);

			weightedPrecision = (!currPrecision.isNaN()) ? currPrecision : 0.;
			weightedRecall = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * weightedPrecision * weightedRecall / (weightedPrecision + weightedRecall);
			weightedF1Score = (!currF1.isNaN()) ? currF1 : 0.;

			macroPrecision = weightedPrecision;
			macroRecall = weightedRecall;
			macroF1Score = weightedF1Score;

		} else { // measures per class
			for (int i = 0; i < numLabels; i++) {
				// Weighted Scores
				weightedPrecision += precisionsPerClass[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedRecall += recallsPerClass[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedF1Score += f1ScoresPerClass[i] * (docsOfClass[i] / (double) totalDocuments);

				// Macro Scores
				macroPrecision += precisionsPerClass[i] / (double) numLabels;
				macroRecall += recallsPerClass[i] / (double) numLabels;
				macroF1Score += f1ScoresPerClass[i] / (double) numLabels;
			}
		}

		// Accuracy
		accuracy = totalCorrect / (double)totalDocuments;
	}

	// Validated against sklearn's measures (for the binary case when pos_label=None + for the case with given pos_label)
	protected void calculateMeasuresGazeteer(int[] truePositives, int[] falsePositives, int totalCorrect, int[] docsOfClass, int totalDocuments, int numLabels, int posLabel) {
		precisionsPerClassGazeteer = new Double[numLabels];
		recallsPerClassGazeteer = new Double[numLabels];
		f1ScoresPerClassGazeteer = new Double[numLabels];

		// Per-Class Scores
		for (int i = 0; i < numLabels; i++) {
			Double currPrecision = truePositives[i] / (double) (truePositives[i] + falsePositives[i]);
			Double currRecall = truePositives[i] / (double) docsOfClass[i];

			precisionsPerClassGazeteer[i] = (!currPrecision.isNaN()) ? currPrecision : 0.;
			recallsPerClassGazeteer[i] = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * precisionsPerClassGazeteer[i] * recallsPerClassGazeteer[i] / (precisionsPerClassGazeteer[i] + recallsPerClassGazeteer[i]);
			f1ScoresPerClassGazeteer[i] = (!currF1.isNaN()) ? currF1 : 0.;
		}

		// If we have a posLabel, we simply use its metrics (thats what sklearn does as well), otherwise we
		// calculate all measures on a per-class basis
		if (posLabel != -1) { // only pos class
			Double currPrecision = truePositives[posLabel] / (double)(truePositives[posLabel] + falsePositives[posLabel]);
			Double currRecall = truePositives[posLabel] / (double)(docsOfClass[posLabel]);

			weightedPrecisionGazeteer = (!currPrecision.isNaN()) ? currPrecision : 0.;
			weightedRecallGazeteer = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * weightedPrecisionGazeteer * weightedRecallGazeteer / (weightedPrecisionGazeteer + weightedRecallGazeteer);
			weightedF1ScoreGazeteer = (!currF1.isNaN()) ? currF1 : 0.;

			macroPrecisionGazeteer = weightedPrecisionGazeteer;
			macroRecallGazeteer = weightedRecallGazeteer;
			macroF1ScoreGazeteer = weightedF1ScoreGazeteer;

		} else { // measures per class
			for (int i = 0; i < numLabels; i++) {
				// Weighted Scores
				weightedPrecisionGazeteer += precisionsPerClassGazeteer[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedRecallGazeteer += recallsPerClassGazeteer[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedF1ScoreGazeteer += f1ScoresPerClassGazeteer[i] * (docsOfClass[i] / (double) totalDocuments);

				// Macro Scores
				macroPrecisionGazeteer += precisionsPerClassGazeteer[i] / (double) numLabels;
				macroRecallGazeteer += recallsPerClassGazeteer[i] / (double) numLabels;
				macroF1ScoreGazeteer += f1ScoresPerClassGazeteer[i] / (double) numLabels;
			}
		}

		// Accuracy
		accuracyGazeteer = totalCorrect / (double)totalDocuments;
	}

	// Validated against sklearn's measures (for the binary case when pos_label=None + for the case with given pos_label)
	protected void calculateMeasuresNB(int[] truePositives, int[] falsePositives, int totalCorrect, int[] docsOfClass, int totalDocuments, int numLabels, int posLabel) {
		precisionsPerClassNB = new Double[numLabels];
		recallsPerClassNB = new Double[numLabels];
		f1ScoresPerClassNB = new Double[numLabels];

		// Per-Class Scores
		for (int i = 0; i < numLabels; i++) {
			Double currPrecision = truePositives[i] / (double) (truePositives[i] + falsePositives[i]);
			Double currRecall = truePositives[i] / (double) docsOfClass[i];

			precisionsPerClassNB[i] = (!currPrecision.isNaN()) ? currPrecision : 0.;
			recallsPerClassNB[i] = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * precisionsPerClassNB[i] * recallsPerClassNB[i] / (precisionsPerClassNB[i] + recallsPerClassNB[i]);
			f1ScoresPerClassNB[i] = (!currF1.isNaN()) ? currF1 : 0.;
		}

		// If we have a posLabel, we simply use its metrics (thats what sklearn does as well), otherwise we
		// calculate all measures on a per-class basis
		if (posLabel != -1) { // only pos class
			Double currPrecision = truePositives[posLabel] / (double)(truePositives[posLabel] + falsePositives[posLabel]);
			Double currRecall = truePositives[posLabel] / (double)(docsOfClass[posLabel]);

			weightedPrecisionNB = (!currPrecision.isNaN()) ? currPrecision : 0.;
			weightedRecallNB = (!currRecall.isNaN()) ? currRecall : 0.;

			Double currF1 = 2 * weightedPrecisionNB * weightedRecallNB / (weightedPrecisionNB + weightedRecallNB);
			weightedF1ScoreNB = (!currF1.isNaN()) ? currF1 : 0.;

			macroPrecisionNB = weightedPrecisionNB;
			macroRecallNB = weightedRecallNB;
			macroF1ScoreNB = weightedF1ScoreNB;

		} else { // measures per class
			for (int i = 0; i < numLabels; i++) {
				// Weighted Scores
				weightedPrecisionNB += precisionsPerClassNB[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedRecallNB += recallsPerClassNB[i] * (docsOfClass[i] / (double) totalDocuments);
				weightedF1ScoreNB += f1ScoresPerClassNB[i] * (docsOfClass[i] / (double) totalDocuments);

				// Macro Scores
				macroPrecisionNB += precisionsPerClassNB[i] / (double) numLabels;
				macroRecallNB += recallsPerClassNB[i] / (double) numLabels;
				macroF1ScoreNB += f1ScoresPerClassNB[i] / (double) numLabels;
			}
		}

		// Accuracy
		accuracyNB = totalCorrect / (double)totalDocuments;
	}
}

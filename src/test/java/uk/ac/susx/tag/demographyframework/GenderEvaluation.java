package uk.ac.susx.tag.demographyframework;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
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

/**
 * Created by thk22 on 28/05/2015.
 *
 * This is a very ugly and very quick class for evaluating the Gender Model with Gazeteer.
 * I was quite hungover when that was written...
 */
public class GenderEvaluation {

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
		GenderEvaluation ge = new GenderEvaluation();
		Gson gson = Util.getGson();

		JsonListStreamReader trainingStream = null;
		FeatureExtractionPipeline pipeline = null;
		List<Instance> trainingData = null;
		JsonListStreamReader goldStandardStream = null;

//		// Cross Validation over Male vs. Female Model (TSB labelled, incl Gazeteer)
//		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
//		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
//		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
//		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());
//
//		ge.crossValidateTheStuff(ge, trainingData, pipeline, "Gender Classification TSB labelled (incl Gazeteer)", "genderClassificationMaleVsFemaleTSBLabelledInclGazeteer.csv");
//
//		// Cross Validation over Male vs. Female Model (my labelled, incl Gazeteer)
//		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
//		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
//		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
//		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());
//
//		ge.crossValidateTheStuff(ge, trainingData, pipeline, "Gender Classification my labelled (incl Gazeteer)", "genderClassificationMaleVsFemalemyLabelledInclGazeteer.csv");

		// Cross Validation over Male vs. Female Model (IM, incl Gazeteer)
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/gender_labelling_male_vs_female_my_tsb_merged_gazeteer_enabled.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/gender/profile_description_incl_name_dataset_IM.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ge.crossValidateTheStuff(ge, trainingData, pipeline, "Gender Classification IM (incl Gazeteer)", "genderClassificationMaleVsFemaleIMInclGazeteer.csv");

		// Cross Validation over Male vs. Female Model (TK, incl Gazeteer)
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/gender_labelling_male_vs_female_my_tsb_merged_gazeteer_enabled.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/gender/profile_description_incl_name_dataset_TK.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ge.crossValidateTheStuff(ge, trainingData, pipeline, "Gender Classification TK (incl Gazeteer)", "genderClassificationMaleVsFemaleTKInclGazeteer.csv");

		// Cross Validation over Male vs. Female Model (Merged, incl Gazeteer)
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/gender_labelling_male_vs_female_my_tsb_merged_gazeteer_enabled.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/gender/profile_description_incl_name_dataset_merged.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverInstances());

		ge.crossValidateTheStuff(ge, trainingData, pipeline, "Gender Classification Merged (incl Gazeteer)", "genderClassificationMaleVsFemaleMergedInclGazeteer.csv");
	}

	public void crossValidateTheStuff(GenderEvaluation ge, List<Instance> trainingData, FeatureExtractionPipeline pipeline, String title, String fname) throws URISyntaxException, InstantiationException, IllegalAccessException, IOException {
		ge.crossValidateGenderClassifier(trainingData, pipeline, NaiveBayesClassifier.class, 10);

		System.out.println(title);
		System.out.println("\tAccuracy: " + ge.accuracy);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tWeighted Precision: " + ge.weightedPrecision);
		System.out.println("\tWeighted Recall: " + ge.weightedRecall);
		System.out.println("\tWeighted F1-Score: " + ge.weightedF1Score);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tMacro Precision: " + ge.macroPrecision);
		System.out.println("\tMacro Recall: " + ge.macroRecall);
		System.out.println("\tMacro F1-Score: " + ge.macroF1Score);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tPredicted Docs: " + ge.predictedDocs);
		System.out.println("\tGazeteered Docs: " + ge.gazeteeredDocs);
		System.out.println("\tRatio: " + ((double)ge.gazeteeredDocs / ((double)(ge.gazeteeredDocs + ge.predictedDocs))) + " gazeteered");
		System.out.println("\tG A Z E T E E R   P E R F O R M A N C E");
		System.out.println("\tGazeteer Accuracy: " + ge.accuracyGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tGazeteer Weighted Precision: " + ge.weightedPrecisionGazeteer );
		System.out.println("\tGazeteer Weighted Recall: " + ge.weightedRecallGazeteer );
		System.out.println("\tGazeteer Weighted F1-Score: " + ge.weightedF1ScoreGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tGazeteer Macro Precision: " + ge.macroPrecisionGazeteer );
		System.out.println("\tGazeteer Macro Recall: " + ge.macroRecallGazeteer );
		System.out.println("\tGazeteer Macro F1-Score: " + ge.macroF1ScoreGazeteer );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tN B   P E R F O R M A N C E");
		System.out.println("\tNB Leftover Accuracy: " + ge.accuracyNB);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tNB Leftover Weighted Precision: " + ge.weightedPrecisionNB );
		System.out.println("\tNB Leftover Weighted Recall: " + ge.weightedRecallNB );
		System.out.println("\tNB Leftover Weighted F1-Score: " + ge.weightedF1ScoreNB );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tNB Leftover Macro Precision: " + ge.macroPrecisionNB );
		System.out.println("\tNB Leftover Macro Recall: " + ge.macroRecallNB );
		System.out.println("\tNB Leftover Macro F1-Score: " + ge.macroF1ScoreNB );
		System.out.println("\t----------------------------------------------");
		System.out.println("\tLabel Map:");
		for (int labelIndex : pipeline.getLabelIndexer().getIndices()) {
			System.out.println(String.format("\t\tidx=%d; label=%s", labelIndex, pipeline.labelString(labelIndex)));
		}
		System.out.println("\t----------------------------------------------");

		//String basePath = "/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_results/polly/";
		String basePath = "/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/gender/";
		try {
			ge.saveConfusionMatrixAsCsv(basePath + fname, actual, predicted);
			ge.saveConfusionMatrixAsCsv(basePath + "Gazeteer" + fname, actualGazeteer, predictedGazeteer);
			ge.saveConfusionMatrixAsCsv(basePath + "NBLeftover" + fname, actualNB, predictedNB);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		ge.accuracy = 0.;
		ge.weightedPrecision = 0.;
		ge.weightedRecall = 0.;
		ge.weightedF1Score = 0.;
		ge.macroPrecision = 0.;
		ge.macroRecall = 0.;
		ge.macroF1Score = 0.;
		ge.gazeteeredDocs = 0;
		ge.predictedDocs = 0;
		ge.actual = new ArrayList<>();
		ge.predicted = new ArrayList<>();

		ge.accuracyGazeteer = 0.;
		ge.weightedPrecisionGazeteer = 0.;
		ge.weightedRecallGazeteer = 0.;
		ge.weightedF1ScoreGazeteer = 0.;
		ge.macroPrecisionGazeteer = 0.;
		ge.macroRecallGazeteer = 0.;
		ge.macroF1ScoreGazeteer = 0.;
		ge.actualGazeteer = new ArrayList<>();
		ge.predictedGazeteer = new ArrayList<>();

		ge.accuracyNB = 0.;
		ge.weightedPrecisionNB = 0.;
		ge.weightedRecallNB = 0.;
		ge.weightedF1ScoreNB = 0.;
		ge.macroPrecisionNB = 0.;
		ge.macroRecallNB = 0.;
		ge.macroF1ScoreNB = 0.;
		ge.actualNB = new ArrayList<>();
		ge.predictedNB = new ArrayList<>();
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

	public void crossValidateGenderClassifier(List<Instance> instances, FeatureExtractionPipeline pipeline, Class<NaiveBayesClassifier> khlavKalash, int numFolds) throws IOException, URISyntaxException, IllegalAccessException, InstantiationException {
		// Create IndexSet and shuffle it
		List<Integer> indexSet = createIndexSet(instances.size());
		Collections.shuffle(indexSet, new Random(42));

		// Create actual foldmap
		Int2ObjectArrayMap<List<Instance>> foldMap = createInstFoldMap(instances, indexSet, numFolds);

		int numLabels = 2;//pipeline.getLabelIndexer().size();

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

		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);

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

					String gender = gd.extractAndGuessString(name).toLowerCase();

					int predictedLabel;
					int gsLabel;

					if (gender.equals("unknown")) {
						doc.text = parts[1];

						ProcessedInstance p = pipeline.extractFeatures(doc);

						predictedLabel = classifier.bestLabel(p.features);
						gsLabel = p.getLabel();

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
						predictedLabel = pipeline.labelIndex(gender);
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

					predicted.add(predictedLabel);
					actual.add(gsLabel);

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

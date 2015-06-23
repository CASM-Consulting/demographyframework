package uk.ac.susx.tag.demographyframework;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import uk.ac.susx.tag.classificationframework.Evaluation;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifier;
import uk.ac.susx.tag.classificationframework.classifiers.NaiveBayesClassifierPreComputed;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.datastructures.ModelState;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.Country;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.GenderDetector;
import uk.ac.susx.tag.method51.twitter.demography.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by thk22 on 28/05/2015.
 */
public class GenderDynamicPriorsEvaluation {

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
	ArrayList<Integer> predicted = new ArrayList<>();
	ArrayList<Integer> actual = new ArrayList<>();

	public static void main(String[] args) {
		new GenderDynamicPriorsEvaluation();
	}

	public GenderDynamicPriorsEvaluation() {
		try {
			genderClassificationDynamicPriors();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void evaluateWithGazeteer(NaiveBayesClassifierPreComputed nb, JsonListStreamReader goldStandardStream, FeatureExtractionPipeline pipeline, boolean useDynamicPriors, int batchSize, String title, String fname) throws IOException, URISyntaxException {
		GenderDetector gd = new GenderDetector(Country.CountryCode.UK);

		Int2IntOpenHashMap dynamicPriors = new Int2IntOpenHashMap();
		dynamicPriors.defaultReturnValue(0);

		int numLabels = 2;//pipeline.getLabelIndexer().size();

		// Overall
		int totalDocuments = 0;
		int totalCorrect = 0;
		int[] truePositives = new int[numLabels];
		int[] falsePositives = new int[numLabels];
		int[] docsOfClass = new int[numLabels];

		for (Instance doc : goldStandardStream.iterableOverInstances()) {
			String[] parts = doc.text.split(" ABCDEFGHIJKLMNOPQRSTUVWXYZ "); // Hacky-whacky FTW!!!
			String name = parts[0].trim();

			int predictedLabel;
			int gsLabel;

			String gender = gd.extractAndGuessString(name).toLowerCase();
			if (!gender.equals("unknown")) {
				dynamicPriors.addTo(pipeline.labelIndex(gender), 1);

				int sum = dynamicPriors.values().stream().reduce(0, (a, b) -> a + b);
				int min = dynamicPriors.values().stream().reduce(Integer.MAX_VALUE, (a, b) -> b < a ? b : a);
				if ((sum % batchSize == 0 && (dynamicPriors.size() > 1 && min > 0)) && useDynamicPriors) { // <-- make sure we've observed EVERY target label...
					Int2DoubleMap priors = new Int2DoubleOpenHashMap();
					for (int idx : dynamicPriors.keySet()) {
						priors.put(idx, Math.log((double) dynamicPriors.get(idx)) - Math.log((double) sum));
					}
					//System.out.println("Current Priors: " + nb.getLabelPriors());
					nb.setLabelPriors(priors);
				}
				predictedLabel = pipeline.labelIndex(gender);
				gsLabel = pipeline.labelIndex(doc.label);
			} else {
				doc.text = parts[1];

				ProcessedInstance p = pipeline.extractFeatures(doc);

				predictedLabel = nb.bestLabel(p.features);
				gsLabel = p.getLabel();
			}

			actual.add(gsLabel);
			predicted.add(predictedLabel);

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
		calculateMeasures(truePositives, falsePositives, totalCorrect, docsOfClass, totalDocuments, numLabels, -1);

		System.out.println(title);
		System.out.println("\tAccuracy: " + accuracy);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tWeighted Precision: " + weightedPrecision);
		System.out.println("\tWeighted Recall: " + weightedRecall);
		System.out.println("\tWeighted F1-Score: " + weightedF1Score);
		System.out.println("\t----------------------------------------------");
		System.out.println("\tMacro Precision: " + macroPrecision);
		System.out.println("\tMacro Recall: " + macroRecall);
		System.out.println("\tMacro F1-Score: " + macroF1Score);
		System.out.println("\t----------------------------------------------");

		//String basePath = "/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_results/polly/";
		//try {
		//	saveConfusionMatrixAsCsv(basePath + fname);
		//} catch (IOException ex) {
		//	ex.printStackTrace();
		//}

		accuracy = 0.;
		weightedPrecision = 0.;
		weightedRecall = 0.;
		weightedF1Score = 0.;
		macroPrecision = 0.;
		macroRecall = 0.;
		macroF1Score = 0.;
		actual = new ArrayList<>();
		predicted = new ArrayList<>();
	}

	public void saveConfusionMatrixAsCsv(String path) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');

		assert(actual.size() == predicted.size());

		for (int i = 0; i < actual.size(); i++) {
			String[] row = {actual.get(i).toString(), predicted.get(i).toString()};
			writer.writeNext(row);
		}
		writer.close();
	}

	// Validated against sklearn's measures (for the binary case when pos_label=None + for the case with given pos_label)
	private void calculateMeasures(int[] truePositives, int[] falsePositives, int totalCorrect, int[] docsOfClass, int totalDocuments, int numLabels, int posLabel) {
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

	private void genderClassificationDynamicPriors() throws URISyntaxException, IOException, ClassNotFoundException {
		Gson gson = Utils.getGson();

		JsonListStreamReader trainingStream = null;
		FeatureExtractionPipeline pipeline = null;
		List<ProcessedInstance> trainingData = null;
		JsonListStreamReader goldStandardStream = null;

		// Train on my labelled data, test on TSB labelled data (excl Gazeteer)
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		ModelState m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		//m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/genderMy"));
		m.save(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/genderMy"));

		m = ModelState.load(new File(GenderDetector.class.getResource("models/male_vs_female").toURI()));

		// Classify TSB Users with pre-trained model without dynamic priors
		NaiveBayesClassifierPreComputed preComputedNB = (NaiveBayesClassifierPreComputed) m.classifier.getPrecomputedClassifier();

		System.out.println("~~~~~~ O R I G I N A L   P R I O R S   M Y   D A T A S E T ~~~~~~");
		for (int label : preComputedNB.getLabelPriors().keySet()) {
			System.out.println("\t" + pipeline.labelString(label) + ": " + Math.exp(preComputedNB.getLabelPriors().get(label)));
		}
		System.out.println("===================================================================\n");

		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph//resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		evaluateWithGazeteer(preComputedNB, goldStandardStream, pipeline, false, 20, "Train on MY, test on TSB; no dynamic priors", "genderTrainMyEvalTSBNoDynPriors.csv");

		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		evaluateWithGazeteer(preComputedNB, goldStandardStream, pipeline, true, 20, "Train on MY, test on TSB; dynamic priors", "genderTrainMyEvalTSBInclDynPriors.csv");

		// Train on TSB data, test on my labelled data (excl Gazeteer)
		//trainingStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		trainingStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender_tsb/profile_description_only_dataset.json"), gson);
		pipeline = uk.ac.susx.tag.classificationframework.Util.buildBasicPipeline(true, false);
		trainingData = Lists.newLinkedList(trainingStream.iterableOverProcessedInstances(pipeline));

		nb = new NaiveBayesClassifier();
		nb.train(trainingData);

		m = new ModelState(nb, ModelState.getSourceInstanceList(trainingData), pipeline);
		//m.save(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/_datasets/polly/genderTSB"));
		m.save(new File("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/genderTSB"));

		preComputedNB = (NaiveBayesClassifierPreComputed) m.classifier.getPrecomputedClassifier();

		System.out.println("~~~~~~ O R I G I N A L   P R I O R S   T S B   D A T A S E T ~~~~~~");
		for (int label : preComputedNB.getLabelPriors().keySet()) {
			System.out.println("\t" + pipeline.labelString(label) + ": " + Math.exp(preComputedNB.getLabelPriors().get(label)));
		}
		System.out.println("===================================================================\n");

		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		evaluateWithGazeteer(preComputedNB, goldStandardStream, pipeline, false, 20, "Train on TSB, test on MY; no dynamic priors", "genderTrainTSBEvalMyNoDynPriors.csv");

		//goldStandardStream = new JsonListStreamReader(new File("/Volumes/LocalDataHD/thk22/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		goldStandardStream = new JsonListStreamReader(new File("/Users/thomas/DevSandbox/InfiniteSandbox/tag-lab/demograph/resources/datasets/gender/profile_description_only.json"), gson);
		evaluateWithGazeteer(preComputedNB, goldStandardStream, pipeline, true, 20, "Train on TSB, test on MY; dynamic priors", "genderTrainTSBEvalMyInclDynPriors.csv");
	}
}

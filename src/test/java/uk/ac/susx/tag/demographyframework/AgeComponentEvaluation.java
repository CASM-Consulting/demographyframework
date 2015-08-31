package uk.ac.susx.tag.demographyframework;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.IntSet;
import uk.ac.susx.tag.classificationframework.Util;
import uk.ac.susx.tag.classificationframework.datastructures.Instance;
import uk.ac.susx.tag.classificationframework.datastructures.ModelState;
import uk.ac.susx.tag.classificationframework.datastructures.ProcessedInstance;
import uk.ac.susx.tag.classificationframework.featureextraction.pipelines.FeatureExtractionPipeline;
import uk.ac.susx.tag.classificationframework.jsonhandling.JsonListStreamReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thomas on 13/08/15.
 */
public class AgeComponentEvaluation {
	private static final String REGEX_TEMPLATE = "^\\w*\\w{1}([0-9]{%d})$";
	private Map<String, Range<Integer>> targetBins;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		AgeComponentEvaluation ace = new AgeComponentEvaluation();
		String[] modelPaths = {
			"/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_models/age/profile_description_coarse_4dCI",
			"/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_models/age/profile_description_fine_4dCI"
		};
		String[] testPaths = {
			"/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/age/profile_description_dataset_coarse_incl_screen_name_im.json",
			"/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/age/profile_description_dataset_fine_incl_screen_name_im.json"
		};

		for (int i = 0; i < testPaths.length; i++) {
			ace.runEvaluation(modelPaths[i], testPaths[i]);
		}
	}

	public void runEvaluation(String modelPath, String testPath) throws IOException, ClassNotFoundException {
		String[] parts = modelPath.split("/");
		String name = parts[parts.length - 1];

		ModelState m = ModelState.load(new File(modelPath));

		// Load Test data
		Gson gson = Util.getGson();
		JsonListStreamReader testStream = new JsonListStreamReader(new File(testPath), gson);
		Iterable<ProcessedInstance> testData = testStream.iterableOverProcessedInstances(m.pipeline);

		Range<Integer> targetRange4Digits = Range.closed(1950, 2003);
		Range<Integer> targetRange2Digits = Range.closed(50, 98);
		Set<Integer> excludeYears4Digits = new HashSet<>();
		excludeYears4Digits.add(2000);
		Set<Integer> excludeYears2Digits = new HashSet<>();


		if (name.contains("coarse")) {
			targetBins = createTargetBins(TargetAgeClassification.OVER_30_VS_UNDER_30);
		} else {
			targetBins = createTargetBins(TargetAgeClassification.POLLY_5_WAY_SPLIT);
		}

		// Counts
		int totalDocs = 0;
		int totalCorrect = 0;
		ArrayList<Integer> totalPredicted = new ArrayList<>();
		ArrayList<Integer> totalActual = new ArrayList<>();

		int total4DigitDocs = 0;
		int fourDigitCorrect = 0;
		ArrayList<Integer> fourDigitPredicted = new ArrayList<>();
		ArrayList<Integer> fourDigitActual = new ArrayList<>();

		int total2DigitDocs = 0;
		int twoDigitCorrect = 0;
		ArrayList<Integer> twoDigitPredicted = new ArrayList<>();
		ArrayList<Integer> twoDigitActual = new ArrayList<>();

		int totalClassifiedDocs = 0;
		int classifiedCorrect = 0;
		ArrayList<Integer> classifierPredicted = new ArrayList<>();
		ArrayList<Integer> classifierActual = new ArrayList<>();

		int totalDigitDocs = 0;
		int digitCorrect = 0;
		ArrayList<Integer> digitPredicted = new ArrayList<>();
		ArrayList<Integer> digitActual = new ArrayList<>();

		for (ProcessedInstance p : testData) {
			String[] pparts = p.source.text.split(" ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
			String userName = pparts[0].trim();
			String userDesc = pparts[1].trim();

			totalDocs++;

			// 4 Digit Lookup
			int age = estimateAgeFromScreenName(userName, 4, targetRange4Digits, excludeYears4Digits);

			int predictedLabel = -1;

			if (age > 0) {
				predictedLabel = m.pipeline.labelIndex(annotateAgeGroup(age));

				total4DigitDocs++;
				fourDigitCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
				fourDigitActual.add(p.getLabel());
				fourDigitPredicted.add(predictedLabel);

				totalDigitDocs++;
				digitCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
				digitActual.add(p.getLabel());
				digitPredicted.add(predictedLabel);

			} else {

				// 2 Digit Lookup
				age = estimateAgeFromScreenName(userName, 2, targetRange2Digits, excludeYears2Digits);
				if (age > 0) {
					predictedLabel = m.pipeline.labelIndex(annotateAgeGroup(age));

					total2DigitDocs++;
					twoDigitCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
					twoDigitActual.add(p.getLabel());
					twoDigitPredicted.add(predictedLabel);

					totalDigitDocs++;
					digitCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
					digitActual.add(p.getLabel());
					digitPredicted.add(predictedLabel);
				} else {

					/*
					// Chris Inskips Regex Lookup
					String[] regexes = {
						"(in\\sa)(?<age>([^\\w][1-9][0-9])|^([1-9][0-9]))\\s?(((yr|year)s?\\s?((old)|(of\\sage)|(young)))|(y\\/o)|(y\\.o))",
						"((([^\\w]i)|(^i))((\\'?m)|(\\sam)))?(?<age>([^\\w][1-9][0-9])|^([1-9][0-9]))\\s?(((yr|year)s?\\s?((old)|(of\\sage)|(young)))|(y\\/o)|(y\\.o))",
						"(([^\\w]i)|(^i))((\\'?m)|(\\sam))\\s(?<age>[1-9][0-9]?)($|\\s|[^\\w^%^\\']($|\\s))",
						"(([^\\w]born)|(^born))\\s([io]n\\s)?(?<dob>(19[2-9][0-9])|(\\'[0-9][0-9])|(200[0-4]))",
						"(([^\\w]aged?)|(^(aged?)))\\s?:?\\s(?<age>[1-9][0-9])"
					};

					//System.out.println(userDesc);
					for (String regex : regexes) {
						System.out.println(regex);
						Pattern pp = Pattern.compile(regex);
						Matcher mm = pp.matcher("asdf age 19 adsfa");

						if (mm.matches()) {
							System.out.println("FOUND STUFF!");
						}
					}*/

					Instance i = new Instance("", userDesc, String.format("%d", userDesc.hashCode()));

					predictedLabel = m.classifier.bestLabel(m.pipeline.extractFeatures(i).getFeatures());

					totalClassifiedDocs++;
					classifiedCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
					classifierActual.add(p.getLabel());
					classifierPredicted.add(predictedLabel);
				}

			}

			totalActual.add(p.getLabel());
			totalPredicted.add(predictedLabel);

			totalCorrect += (predictedLabel == p.getLabel()) ? 1 : 0;
		}

		// Store Confusion Matrices
		saveConfusionMatrixAsCsv("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/age/total_" + name + ".csv", totalActual, totalPredicted);
		saveConfusionMatrixAsCsv("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/age/twoDigit_" + name + ".csv", twoDigitActual, twoDigitPredicted);
		saveConfusionMatrixAsCsv("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/age/fourDigit_" + name + ".csv", fourDigitActual, fourDigitPredicted);
		saveConfusionMatrixAsCsv("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/age/digit_" + name + ".csv", digitActual, digitPredicted);
		saveConfusionMatrixAsCsv("/Users/thomas/DevSandbox/EpicDataShelf/tag-lab/polly/_clean/_results/age/classifier_" + name + ".csv", classifierActual, classifierPredicted);

		System.out.println("Total Docs: " + totalDocs);
		System.out.println("Total Correct: " + totalCorrect);
		System.out.println("Total Accuracy: " + totalCorrect / (float)totalDocs);
		System.out.println("-----------------------------");
		System.out.println("4 Digit Docs: " + total4DigitDocs);
		System.out.println("4 Digit Correct: " + fourDigitCorrect);
		System.out.println("4 Digit Accuracy: " + fourDigitCorrect / (float)total4DigitDocs);
		System.out.println("-----------------------------");
		System.out.println("2 Digit Docs: " + total2DigitDocs);
		System.out.println("2 Digit Correct: " + twoDigitCorrect);
		System.out.println("2 Digit Accuracy: " + twoDigitCorrect / (float)total2DigitDocs);
		System.out.println("-----------------------------");
		System.out.println("Classified Docs: " + totalClassifiedDocs);
		System.out.println("Classified Correct: " + classifiedCorrect);
		System.out.println("Classified Accuracy: " + classifiedCorrect / (float)totalClassifiedDocs);
		System.out.println("-----------------------------");
		System.out.println("Digit Docs: " + totalDigitDocs);
		System.out.println("Digit Correct: " + digitCorrect);
		System.out.println("Digit Accuracy: " + digitCorrect / (float)totalDigitDocs);
		System.out.println("-----------------------------");

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

	private String annotateAgeGroup(int age) {
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

	private Map<String, Range<Integer>> createTargetBins(TargetAgeClassification t) {
		Map<String, Range<Integer>> m = new HashMap<>();
		switch (t) {
			case OVER_30_VS_UNDER_30: {
				m.put("Under 30", Range.closed(0, 29));
				m.put("Over 30", Range.closed(30, 130));
				break;
			}
			case POLLY_5_WAY_SPLIT: {
				m.put("0-15", Range.closed(0, 15));
				m.put("16-24", Range.closed(16, 24));
				m.put("25-34", Range.closed(25, 34));
				m.put("35-49", Range.closed(35, 49));
				m.put("50+", Range.closed(50, 130));
				break;
			}
		}

		return m;
	}

	public enum TargetAgeClassification {
		OVER_30_VS_UNDER_30(0),
		POLLY_5_WAY_SPLIT(1);

		private int value;

		private static Map<Integer, TargetAgeClassification> map = new HashMap<>();

		static {
			for (TargetAgeClassification t : TargetAgeClassification.values()) {
				map.put(t.value, t);
			}
		}

		private TargetAgeClassification(final int value) {
			this.value = value;
		}

		public static TargetAgeClassification valueOf(int targetAgeClassification) {
			return map.get(targetAgeClassification);
		}
	}
}

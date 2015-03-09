package uk.ac.susx.tag.method51.twitter.demography.genderdetector;

/**
 * Created by thk22 on 16/01/2015.
 */

public class Binomy {

	public static final double DEFAULT_PROPORTION_THRESHOLD = 0.99;
	public static final double DEFAULT_LOWER_CONFIDENCE_BOUND = 0.75;
	public static final double MAGIC_STATISTICS_NUMBER = 1.96;

	private boolean confidence;

	public Binomy(int maleCount, int femaleCount, double proportionThreshold, double lowerConfidenceBound, double z) {
		int total = maleCount + femaleCount;

		double rawMaleProportion = (total > 0) ? maleCount / (double)total : 0.;
		double rawFemaleProportion = (total > 0) ? femaleCount / (double)total : 0.;

		double exp = Math.pow(z, 2.);
		double nt = total + exp;

		double observed = Math.max(maleCount, femaleCount);
		double estimated = (observed + (exp / 2)) / nt;
		double interval = z * Math.sqrt(estimated * (1 - estimated) / nt);
		double lower = Math.max(rawMaleProportion, rawFemaleProportion) - interval;

		confidence = rawMaleProportion > proportionThreshold || rawFemaleProportion > proportionThreshold && lower > lowerConfidenceBound;
	}

	public Binomy(int maleCount, int femaleCount) {
		this(maleCount, femaleCount, DEFAULT_PROPORTION_THRESHOLD, DEFAULT_LOWER_CONFIDENCE_BOUND, MAGIC_STATISTICS_NUMBER);
	}

	public boolean enoughConfidence() {
		return this.confidence;
	}

	public static void main(String[] args) {
		Binomy b = new Binomy(47, 8900);

		System.out.println(b.enoughConfidence());
	}
}

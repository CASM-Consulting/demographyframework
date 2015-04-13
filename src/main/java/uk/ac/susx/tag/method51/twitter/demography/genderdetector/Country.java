package uk.ac.susx.tag.method51.twitter.demography.genderdetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thk22 on 16/01/2015.
 */

public class Country {

	public static enum CountryCode {
		UK,
		US,
		AR,
		UY;
	}

	private CountryCode countryCode;
	private boolean applyBinomy;
	private final List<CountryCode> binomyCountries;

	public Country(CountryCode countryCode, boolean applyBinomy) {
		this.countryCode = countryCode;
		this.applyBinomy = applyBinomy;

		this.binomyCountries = new ArrayList<CountryCode>();
		this.binomyCountries.add(CountryCode.UK);
		this.binomyCountries.add(CountryCode.US);
	}

	public CountryCode getCountryCode() {
		return countryCode;
	}

	public GenderDetector.Gender guess(String[] csvLine) {
		return (binomyCountries.contains(countryCode) && applyBinomy) ? binomy(csvLine) : GenderDetector.Gender.valueOf(csvLine[4].toUpperCase());
	}

	private GenderDetector.Gender binomy(String[] csvLine) {
		int maleCount = Integer.parseInt(csvLine[2]);
		int femaleCount = Integer.parseInt(csvLine[3]);

		Binomy b = new Binomy(maleCount, femaleCount);

		// malev uses an if statment in the python code, TODO: check if there are inconsistencies between the gender in the file and the counts
		return (b.enoughConfidence()) ? GenderDetector.Gender.valueOf(csvLine[4].toUpperCase()) : GenderDetector.Gender.UNKNOWN;
	}
}

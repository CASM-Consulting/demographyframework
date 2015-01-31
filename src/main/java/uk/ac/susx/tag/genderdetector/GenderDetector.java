package uk.ac.susx.tag.genderdetector;

import au.com.bytecode.opencsv.CSVReader;
import uk.ac.susx.tag.genderdetector.Country.CountryCode;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by thomas on 29/01/15.
 */
public class GenderDetector {
	public static enum Gender {
		MALE,
		FEMALE,
		UNKNOWN;
	}

	private Country country;

	public GenderDetector(CountryCode countryCode, boolean applyBinomy) {
		this.country = new Country(countryCode, applyBinomy);
	}

	public GenderDetector(CountryCode countryCode) {
		this(countryCode, false);
	}

	public GenderDetector() {
		this(CountryCode.UK, false);
	}

	public Gender guess(String name) throws IOException, URISyntaxException {
		name = normaliseName(name);

		String p = String.format("%s/%s_index/%s.csv", GenderDetector.class.getResource("/data").toURI().getPath(), country.getCountryCode().toString().toLowerCase(), name.substring(0, 1));

		CSVReader reader = new CSVReader(new FileReader(p));

		// Skip header
		reader.readNext();

		String[] line;

		while ((line = reader.readNext()) != null) {
			if (line[0].equals(name)) break;
		}

		return (line != null) ? country.guess(line) : Gender.UNKNOWN;
	}

	public String guessString(String name) throws IOException, URISyntaxException {
		Gender g = guess(name);

		return g.toString();
	}

	private String normaliseName(String name) {
		return name.substring(0, 1).toUpperCase().trim() + name.substring(1).toLowerCase().trim();
	}
}

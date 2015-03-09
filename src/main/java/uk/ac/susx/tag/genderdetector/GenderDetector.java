package uk.ac.susx.tag.genderdetector;

import au.com.bytecode.opencsv.CSVReader;
import uk.ac.susx.tag.genderdetector.Country.CountryCode;
import uk.ac.susx.tag.utils.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

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
	private String countryFileBasePath;

	public GenderDetector(CountryCode countryCode, boolean applyBinomy, String countryFileBasePath) {
		this.country = new Country(countryCode, applyBinomy);
		this.countryFileBasePath = countryFileBasePath;
	}

	public GenderDetector(CountryCode countryCode, boolean applyBinomy) throws URISyntaxException {
		this(countryCode, applyBinomy, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public GenderDetector(CountryCode countryCode) throws URISyntaxException {
		this(countryCode, true, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public GenderDetector() throws URISyntaxException {
		this(CountryCode.UK, true, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public Gender guess(String name) {
		Gender g = Gender.UNKNOWN;
		try {
			name = normaliseName(name);

			String p = String.format("%s/%s_index/%s.csv", this.countryFileBasePath, country.getCountryCode().toString().toLowerCase(), name.substring(0, 1));

			CSVReader reader = new CSVReader(new FileReader(p));

			// Skip header
			reader.readNext();

			String[] line;

			while ((line = reader.readNext()) != null) {
				if (line[0].equals(name)) break;
			}

			g =  (line != null) ? country.guess(line) : Gender.UNKNOWN;
		} catch(IOException ex) {
			// TODO: Something useful
		} catch (StringIndexOutOfBoundsException ex) {
			// TODO: Something useful
		}
		return g;
	}

	public String guessString(String name) {
		Gender g = guess(name);

		return g.toString();
	}

	public Gender extractAndGuess(String twitterNameField) {
		Gender g = Gender.UNKNOWN;
		List<String> names = Utils.extractName(twitterNameField);
		Iterator<String> iter = names.iterator();

		while (g.equals(Gender.UNKNOWN) && iter.hasNext()) {
			g = guess(iter.next());
		}
		return g;
	}

	public String extractAndGuessString(String twitterNameField) {
		Gender g = extractAndGuess(twitterNameField);

		return g.toString();
	}

	private String normaliseName(String name) throws StringIndexOutOfBoundsException { // Empty names
		return name.substring(0, 1).toUpperCase().trim() + name.substring(1).toLowerCase().trim();
	}
}

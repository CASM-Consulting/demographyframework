package uk.ac.susx.tag.method51.twitter.demography.genderdetector;

import au.com.bytecode.opencsv.CSVReader;
import uk.ac.susx.tag.method51.twitter.demography.genderdetector.Country.CountryCode;
import uk.ac.susx.tag.method51.twitter.demography.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by thomas on 29/01/15.
 */
public class GenderDetector {
	public static enum Gender {
		MALE,
		FEMALE,
		UNKNOWN;
	}

	private static final String MR_PREFIX = "mr";
	private static final String MRS_PREFIX = "mrs";
	private static final String MS_PREFIX = "ms";

	private Country country;
	private String countryFileBasePath;
	private boolean useMrsMrFeature;
	private Map<String, String[]> nameLookup;

	public GenderDetector(CountryCode countryCode, boolean applyBinomy, String countryFileBasePath, boolean useMrsMrFeature) throws IOException{
		this.country = new Country(countryCode, applyBinomy);
		this.countryFileBasePath = countryFileBasePath;
		this.useMrsMrFeature = useMrsMrFeature;
		this.nameLookup = cacheNameLookup();
	}
	public GenderDetector(CountryCode countryCode, boolean applyBinomy, String countryFileBasePath) throws IOException {
		this(countryCode, applyBinomy, countryFileBasePath, true);
	}

	public GenderDetector(CountryCode countryCode, boolean applyBinomy) throws URISyntaxException, IOException {
		this(countryCode, applyBinomy, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public GenderDetector(CountryCode countryCode) throws URISyntaxException, IOException {
		this(countryCode, true, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public GenderDetector() throws URISyntaxException, IOException {
		this(CountryCode.UK, true, GenderDetector.class.getResource("data").toURI().getPath());
	}

	public Gender guess(String name) {
		String[] stats = nameLookup.getOrDefault(name.toLowerCase(), null);

		Gender g = (stats != null) ? country.guess(stats) : Gender.UNKNOWN;

		// Apply Mrs/Ms/Mr prefix Feature
		g = (useMrsMrFeature && g == Gender.UNKNOWN) ? (name.toLowerCase().equals(MR_PREFIX) ? Gender.MALE : (name.toLowerCase().equals(MRS_PREFIX)) ? Gender.FEMALE : (name.toLowerCase().equals(MS_PREFIX)) ? Gender.FEMALE : Gender.UNKNOWN) : g;
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

	private Map<String, String[]> cacheNameLookup() throws IOException {
		String p = String.format("%s/%sprocessed.csv", countryFileBasePath, country.getCountryCode().toString().toLowerCase());

		Map<String, String[]> lookup = new HashMap<>();

		CSVReader reader = new CSVReader(new FileReader(p));

		// Skip header
		reader.readNext();

		String[] line;

		while ((line = reader.readNext()) != null) {
			String name = line[0];
			lookup.put(name.toLowerCase(), line);
		}

		return lookup;
	}
}

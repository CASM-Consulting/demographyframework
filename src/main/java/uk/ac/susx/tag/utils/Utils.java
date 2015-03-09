package uk.ac.susx.tag.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.susx.tag.genderdetector.Country;
import uk.ac.susx.tag.genderdetector.GenderDetector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 30/01/15.
 */
public final class Utils {

	private static Gson gson = null;
	public static Gson getGson() {
		if (gson == null) gson = getNewGson();
		return gson;
	}
	private static Gson getNewGson() {
		GsonBuilder gsonBuilder =
				new GsonBuilder()
						.setPrettyPrinting();
		return gsonBuilder.create();
	}

	public static List<String> extractName(String twitterName, List<String> splitters) {
		List<String> candidates = new ArrayList<String>();
		String[] parts;

		for (String splitter : splitters) {
			parts = twitterName.split(splitter);
			for (String p : parts) {
				if (!candidates.contains(p)) candidates.add(p); // Implemented as a list (instead of a set) to maintain the order of the entries
			}
		}

		return candidates;
	}

	public static List<String> extractName(String twitterName) {
		List<String> splitters = new ArrayList<String>();
		splitters.add("\\s+");
		splitters.add("-");
		splitters.add(".");
		splitters.add(",");

		return Utils.extractName(twitterName, splitters);
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		List<String> l = Utils.extractName("Thomas Kober");

		for (String s : l) {
			System.out.println("EXTRACTED NAME: " + s);
		}

		System.out.println("-----");
		l = Utils.extractName("Thomas");
		for (String s : l) {
			System.out.println("EXTRACTED NAME: " + s);
		}

		System.out.println(Utils.class.getResource("/genderdetector/data").toURI().getPath());

		GenderDetector gd = new GenderDetector(Country.CountryCode.UK, true);
		System.out.println("GENDER GUESS: " + gd.guess("thomas"));
	}
}

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import static io.restassured.RestAssured.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import java.util.Comparator;

public class TestMovieAPI {

	public void testPost(String movieName, Map<String, String> movieData) {

		given()
		.contentType("application/json")
		.body(movieData)
		.when()
		.post("https://splunk.mocklab.io/movies?q=batman")
		.then();
	}

	@Test
	public String testGet() throws URISyntaxException, ParseException {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");

		ResponseBody responseBody = given()
				.headers(headers)
				.when()
				.get("https://splunk.mocklab.io/movies?q=batman")
				.body();
		String body = responseBody.asString();
		return body;

	}

	public boolean checkOriginalTitle(String body) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(body);
		JSONArray array = (JSONArray) o.get("results");
		ArrayList<JSONObject> list = new ArrayList<>();
		boolean OrigionalTitle = true;
		for (int i = 0; i < array.size(); i++) {
			list.add((JSONObject) array.get(i));
		}
		ArrayList<String> titles = new ArrayList<>();
		for (JSONObject obj : list) {
			titles.add((String) obj.get("title"));
		}
		ArrayList<String> original_title = new ArrayList<>();
		for (JSONObject obj : list) {
			original_title.add((String) obj.get("original_title"));
		}

		for (int i = 0; i < titles.size(); i++) {
			if (!original_title.get(i).contains(titles.get(i))) {
				OrigionalTitle = false;
				System.out.println(titles.get(i).toString());
			}
		}

		Assert.assertTrue(OrigionalTitle);
		return OrigionalTitle;
	}

	public int checkOverlap(String body) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(body);
		JSONArray array = (JSONArray) o.get("results");
		ArrayList<JSONObject> list = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			list.add((JSONObject) array.get(i));
		}
		ArrayList<String> titles = new ArrayList<>();
		for (JSONObject obj : list) {
			titles.add((String) obj.get("title"));
		}
		int overlaps = 0;
		for (int i = 0; i < titles.size() - 1; i++) {
			for (int j = i + 1; j < titles.size(); j++) {
				if (!titles.get(i).equals(titles.get(j))) {
					if (titles.get(i).contains(titles.get(j)))
						overlaps++;
					else if (titles.get(j).contains(titles.get(i)))
						overlaps++;
				}
			}
		}
		Assert.assertTrue(overlaps >= 2);
		return overlaps;

	}

	// return whether there is a palindrome in the titles.
	@SuppressWarnings("unchecked")
	public boolean checkPalindrome(String body) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(body);
		JSONArray array = (JSONArray) o.get("results");
		ArrayList<JSONObject> list = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			list.add((JSONObject) array.get(i));
		}
		ArrayList<String> titles = new ArrayList<>();
		for (JSONObject obj : list) {
			titles.add((String) obj.get("title"));
		}
		boolean palindrome = false;
		// System.out.println("titles : " + titles);
		for (String tt : titles) {
			// System.out.println(tt);
			String[] words = tt.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			for (String word : words) {
				// System.out.println(word);
				if (WordPalindrome(word))
					palindrome = true;
			}
		}

		Assert.assertTrue(palindrome);
		return palindrome;

	}

	private boolean WordPalindrome(String word) {
		char[] chars = word.toCharArray();
		int i = 0, j = chars.length - 1;
		while (j >= i) {
			if (chars[i] != chars[j])
				return false;
			i++;
			j--;
		}
		return true;
	}

	// The number of movies whose sum of "genre_ids" > 400 should be no more than 7.
	public int CheckGenIds(String body) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(body);
		JSONArray array = (JSONArray) o.get("results");
		ArrayList<JSONObject> list = new ArrayList<>();
		for (int i = 0; i < array.size(); i++) {
			list.add((JSONObject) array.get(i));
		}
		int countsum = 0;
		for (JSONObject obj : list) {
			int gsum = 0;
			@SuppressWarnings("unchecked")
			ArrayList<Long> gids = (ArrayList<Long>) obj.get("genre_ids");
			// System.out.println(gids);

			for (int i = 0; i < gids.size(); i++) {
				gsum += gids.get(i);
			}
			if (gsum > 400)
				countsum++;

		}
		Assert.assertTrue(countsum <= 7);
		return countsum;
	}

	public String sortJson(String body) {
		// sort movies by genre_ids and ids
		JSONParser parser = new JSONParser();
		try {
			JSONObject o = (JSONObject) parser.parse(body);
			JSONArray array = (JSONArray) o.get("results");
			ArrayList<JSONObject> list = new ArrayList<>();

			for (int i = 0; i < array.size(); i++) {
				list.add((JSONObject) array.get(i));
			}

			Collections.sort(list, new MyJSONComparator());

			String sortedbody = new Gson().toJson(list);
			return sortedbody;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Sorting failed" + body;
	}

	public boolean checkDups(String body) {
		boolean isduplicate = true;
		JsonPath json = new JsonPath(body);
		List<Object> list = json.getList("results.poster_path");
		Set<Object> set = new HashSet<>();
		for (Object path : list) {
			set.add(path);
		}
		if (set.size() != list.size())
			isduplicate = false;
		Assert.assertTrue(isduplicate);
		return isduplicate;
	}

	public boolean checkValidPath(String body) {
		boolean validPoster = true;
		JsonPath json = new JsonPath(body);
		List<Object> list = json.getList("results.poster_path");
		for (Object path : list) {
			if (!path.toString().contains("https://www.dropbox.com/")) {
				validPoster = false;
				//System.out.println(path.toString());
			}
		}

		Assert.assertTrue(validPoster);
		return validPoster;
	}
}

class MyJSONComparator implements Comparator<JSONObject> {

	public int compare(JSONObject o1, JSONObject o2) {
		JSONArray g1 = ((JSONArray) o1.get("genre_ids"));
		JSONArray g2 = ((JSONArray) o2.get("genre_ids"));
		Integer i1 = Integer.parseInt(o1.get("id").toString());
		Integer i2 = Integer.parseInt(o2.get("id").toString());

		int c = i1.compareTo(i2);
		if (g1.size() == 0 & g2.size() != 0)
			return -1;
		else if (g2.size() == 0 & g1.size() != 0)
			return 1;
		else
			return c;

	}

}

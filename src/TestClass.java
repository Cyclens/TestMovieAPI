import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.ParseException;

public class TestClass {

	public static void main(String[] args) throws URISyntaxException, ParseException {
		TestMovieAPI test = new TestMovieAPI();
		
		String movieName = "Superman";
		Map<String, String> movie = new HashMap<>();
		movie.put("name", movieName);
		movie.put("description", "the best movie ever made");
		
		test.testPost(movieName, movie);

		// Perform test on the response status code
		test.basicPingTest();
		
		// Perform the test based on the requirements and get the JSON body of the response
		String body = test.testGet();

		// Print out the Json after sorting with genre_ids and id
		System.out.println("The sorted body of response: " + test.sortJson(body));

		// Check if there are movies using the same poster
		System.out.println("There are movies using the same poster: " + test.checkDups(body));

		// Check if all poster_path links are valid
		System.out.println("All poster_path links are valid: " + test.checkValidPath(body));

		// Check if the number of movies whose sum of "genre_ids" > 400 should be no more than 7
		System.out.println("There are " + test.CheckGenIds(body) + " movies whose sum of genre_ids > 400");

		// Check if there is at least one movie in the database whose title has a palindrome in it
		System.out.println("At least one movie whose title has a palindrome: " + test.checkPalindrome(body));

		// Check if there are at least two movies in the database whose title contain the title of another movie
		System.out.println(
				"There are " + test.checkOverlap(body) + " movies whos title contain the title of another movie\n");
		
		// Check if the title of each movie match its original title
		System.out.println("The title and original_title are match: " + test.checkOriginalTitle(body));
	}
}

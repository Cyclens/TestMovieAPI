# [Splunk REST API Testing](https://github.com/Cyclens/TestMovieAPI/new/master?readme=1)

This project is used to test Splunk movie API with Rest-Assured and TestNG.

## Prerequisite

1.	[Apache Maven](https://maven.apache.org/).
2.	[REST Assured v3.1.0](https://github.com/rest-assured/rest-assured).

## Test Properties

1.	Sort the JSON response body by genre_ids and id;
2.	Test for duplicate images;
3.	Test the validation of poster_path links;
4.	Test the number of movies whose sum of "genre_ids" > 400 being no more than 7;
5.	Test for palindrome in titles;
6.	Test for titles to see if one contain the other;
7.	Test if the title and original title are match for each movie.

## How to Perform the Test

1.	Initial the TestMovieAPI.java class as:
```java
TestMovieAPI test = new TestMovieAPI();
```

2.	Post new data onto the server by:
```java
String movieName = "Superman";
Map<String, String> movie = new HashMap<>();
movie.put("name", movieName);
movie.put("description", "the best movie ever made");
test.testPost(movieName, movie);
```

3.	Perform the test based on the requirements and get the JSON body of the response
```java
String body = test.testGet();
```

4.	Print out the Json after sorting with genre_ids and id
```java
System.out.println("The sorted body of response: " + test.sortJson(body));
```

5.	Check if there are movies using the same poster
```java
System.out.println("There are movies using the same poster: " + test.checkDups(body));
```

6.	Check if all poster_path links are valid
```java
System.out.println("All poster_path links are valid: " + test.checkValidPath(body));
```

7.	Check if the number of movies whose sum of "genre_ids" > 400 should be no more than 7
```java
System.out.println("There are " + test.CheckGenIds(body) + " movies whose sum of genre_ids > 400");
```

8.	heck if there is at least one movie in the database whose title has a palindrome in it
```java
System.out.println("At least one movie whose title has a palindrome: " + test.checkPalindrome(body));
```

9.	Check if there are at least two movies in the database whose title contain the title of another movie
```java
System.out.println("There are " + test.checkOverlap(body) + " movies whos title contain the title of another movie\n");
```

10.	Check if the title of each movie match its original title
```java
System.out.println("The title and original_title are match: " + test.checkOriginalTitle(body));
```
11. Check if the response code is 200
```java
test.basicPingTest();
```

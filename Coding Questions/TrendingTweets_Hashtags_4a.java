/*4a Question 4
a)
Input:
Tweets table:
Write a solution to find the top 3 trending hashtags in February 2024. Every tweet may
contain several hashtags.
Return the result table ordered by count of hashtag, hashtag in descending order.
The result format is in the following example.
Explanation:
#HappyDay: Appeared in tweet IDs 13, 14, and 17, with a total count of 3 mentions.
#TechLife: Appeared in tweet IDs 16 and 18, with a total count of 2 mentions.
#WorkLife: Appeared in tweet ID 15, with a total count of 1 mention.
Note: Output table is sorted in descending order by hashtag_count and hashtag respectively
 */

 import java.util.*;
 import java.util.regex.*;
 
 public class TrendingTweets_Hashtags_4a  {
 
     public static void main(String[] args) {
         // Create a Scanner object for input and a map to store hashtag counts
         Scanner inputScanner = new Scanner(System.in);
         HashMap<String, Integer> hashtagFrequency = new HashMap<>();
         
         // Define a regex pattern to detect hashtags in the tweet text
         Pattern hashtagRegex = Pattern.compile("#\\w+");
 
         // Request input for 7 tweets from the user
         System.out.println("Please enter details for 7 tweets (Each field will be entered separately):");
 
         for (int i = 1; i <= 7; i++) {
             System.out.println("\nTweet " + i + ":");
             
             // Capture details for each tweet
             System.out.print("Enter User ID: ");
             int userId = Integer.parseInt(inputScanner.nextLine().trim());
             System.out.print("Enter Tweet ID: ");
             int tweetId = Integer.parseInt(inputScanner.nextLine().trim());
             System.out.print("Enter Tweet Text: ");
             String tweetText = inputScanner.nextLine().trim();
             System.out.print("Enter Tweet Date (YYYY-MM-DD): ");
             String tweetDate = inputScanner.nextLine().trim();
 
             // Use regex to find and count hashtags in the tweet
             Matcher matcher = hashtagRegex.matcher(tweetText);
             while (matcher.find()) {
                 String hashtag = matcher.group().toLowerCase(); // Normalize to lowercase
                 // Increment the hashtag count in the map
                 hashtagFrequency.put(hashtag, hashtagFrequency.getOrDefault(hashtag, 0) + 1);
             }
         }
 
         // Close the scanner to free resources
         inputScanner.close();
 
         // Sort hashtags first by frequency (descending) then alphabetically (descending)
         List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagFrequency.entrySet());
         sortedHashtags.sort((entry1, entry2) -> {
             // Compare based on frequency in descending order
             int frequencyComparison = entry2.getValue().compareTo(entry1.getValue());
             if (frequencyComparison != 0) {
                 return frequencyComparison;
             }
             // If frequencies are the same, compare by hashtag name in descending order
             return entry2.getKey().compareTo(entry1.getKey());
         });
 
         // Display the top 3 trending hashtags
         System.out.println("\n+------------+-------+");
         System.out.println("| Hashtag    | Count |");
         System.out.println("+------------+-------+");
 
         // Print the top 3 hashtags or fewer if there aren't enough
         for (int i = 0; i < Math.min(3, sortedHashtags.size()); i++) {
             System.out.printf("| %-10s | %5d |\n", sortedHashtags.get(i).getKey(), sortedHashtags.get(i).getValue());
         }
 
         System.out.println("+------------+-------+");
     }
 }
 
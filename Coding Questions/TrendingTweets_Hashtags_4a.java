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
 
 public class TrendingTweets_Hashtags_4a {
     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);
         Map<String, Integer> hashtagCount = new HashMap<>();
         Pattern pattern = Pattern.compile("#[A-Za-z0-9]+");
 
         System.out.println("Enter 7 tweets (each with user_id, tweet_id, tweet text, date):");
 
         for (int i = 1; i <= 7; i++) {
             System.out.println("\nTweet " + i + ":");
 
             System.out.print("Enter user_id: ");
             int userId = Integer.parseInt(scanner.nextLine().trim());
 
             System.out.print("Enter tweet_id: ");
             int tweetId = Integer.parseInt(scanner.nextLine().trim());
 
             System.out.print("Enter tweet text: ");
             String tweet = scanner.nextLine().trim();
 
             System.out.print("Enter tweet_date (YYYY-MM-DD): ");
             String tweetDate = scanner.nextLine().trim();
 
             if (!tweetDate.startsWith("2024-02")) {
                 continue;
             }
 
             Matcher matcher = pattern.matcher(tweet);
             while (matcher.find()) {
                 String hashtag = matcher.group();
                 hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
             }
         }
         scanner.close();
 
         List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
         sortedHashtags.sort((a, b) -> {
             int countCompare = b.getValue().compareTo(a.getValue());
             return countCompare != 0 ? countCompare : b.getKey().compareTo(a.getKey());
         });
 
         System.out.println("\n+------------+-------+");
         System.out.println("| Hashtag    | Count |");
         System.out.println("+------------+-------+");
         int limit = Math.min(3, sortedHashtags.size());
         for (int i = 0; i < limit; i++) {
             Map.Entry<String, Integer> entry = sortedHashtags.get(i);
             System.out.printf("| %-10s | %5d |\n", entry.getKey(), entry.getValue());
         }
         System.out.println("+------------+-------+");
     }
 }
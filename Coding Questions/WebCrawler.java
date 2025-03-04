import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WebCrawler {
    // Thread pool to manage worker threads.
    private final ExecutorService executorService;
    // BlockingQueue to hold URLs to be crawled.
    private final BlockingQueue<String> urlQueue;
    // Map to store crawled page content: URL -> Content.
    private final ConcurrentHashMap<String, String> crawledData;
    // Set to keep track of visited URLs.
    private final Set<String> visitedUrls;
    // Number of worker threads.
    private final int numThreads;

    public WebCrawler(int numThreads) {
        this.numThreads = numThreads;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.urlQueue = new LinkedBlockingQueue<>();
        this.crawledData = new ConcurrentHashMap<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
    }
    //  Adds a URL to the crawling queue if it has not been visited yet.
    public void addUrl(String url) {
        if (visitedUrls.add(url)) { // Only add if URL is not already visited.
            urlQueue.offer(url);
        }
    }

    // * Starts the crawling process by launching worker threads.

    public void startCrawling() {
        // Submit worker tasks.
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(new Worker());
        }
        
        // Shutdown the executor after work is done.
        executorService.shutdown();
        try {
            // Wait for all threads to finish (adjust timeout as needed).
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Crawling interrupted: " + e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    //  * Worker class that continuously polls the URL queue,fetches web page content, processes it, and adds any new URLs.

    private class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                String url = null;
                try {
                    // Poll for a URL; if no URL is available within 10 seconds, assume work is done.
                    url = urlQueue.poll(10, TimeUnit.SECONDS);
                    if (url == null) {
                        // No URL found within timeout period; worker exits.
                        break;
                    }
                    // Fetch the web page content.
                    String content = fetchContent(url);
                    // Store the fetched content.
                    crawledData.put(url, content);
                    // Process the content (e.g., extract new URLs).
                    List<String> newUrls = extractUrls(content);
                    for (String newUrl : newUrls) {
                        addUrl(newUrl);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing URL " + url + ": " + e.getMessage());
                }
            }
        }
    }

    //  * Fetches the content of a web page from the given URL.(In a real application, consider using a robust HTTP client.)

    private String fetchContent(String urlString) throws IOException {
        StringBuilder content = new StringBuilder();
        URL urlObj = new URL(urlString);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObj.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    //  * Dummy method to extract URLs from the content.* In a production crawler, use an HTML parser (like Jsoup) to extract <a href="..."> links.

    private List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        // Dummy implementation: In real use, parse the content and add discovered URLs.
        // For example, you might search for patterns like href="http://..."
        return urls;
    }
        //  * Returns the map of crawled data.
    public ConcurrentHashMap<String, String> getCrawledData() {
        return crawledData;
    }

    public static void main(String[] args) {
        // Create a web crawler with 10 threads.
        WebCrawler crawler = new WebCrawler(10);
        // Seed the crawler with initial URLs.
        crawler.addUrl("https://schoolworkspro.com/modules/st5008cem-programming-for-developers");
        // crawler.addUrl("https://rahulpajiyar.com.np/");
        // Start the crawling process.
        crawler.startCrawling();
        // Output the number of crawled pages.
        System.out.println("Crawled pages: " + crawler.getCrawledData().size());

        // Print fetched contents for each URL.
        for (Map.Entry<String, String> entry : crawler.getCrawledData().entrySet()) {
            System.out.println("URL: " + entry.getKey());
            System.out.println("Content:\n" + entry.getValue());
            System.out.println("---------------------------------------------------");
        }
    }
}
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class WebCrawler {

    // Thread-safe data structures
    private final BlockingQueue<String> linkQueue = new LinkedBlockingQueue<>();
    private final Set<String> processedLinks = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, String> scrapedData = new ConcurrentHashMap<>();

    // Thread pool
    private final ExecutorService threadPool;

    // Constructor to initialize the thread pool
    public WebCrawler(int threadsCount) {
        this.threadPool = Executors.newFixedThreadPool(threadsCount);
    }

    // Method to begin the crawling process
    public void beginCrawl(String startingUrl) {
        // Add starting URL to the queue and mark it as processed
        linkQueue.add(startingUrl);
        processedLinks.add(startingUrl);

        // Assign tasks to the thread pool
        for (int i = 0; i < 10; i++) {
            threadPool.submit(new CrawlWorker());
        }

        // Shutdown the thread pool once all tasks are finished
        threadPool.shutdown();
        try {
            if (threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Crawling finished.");
            } else {
                System.err.println("Timeout reached before crawling was completed.");
            }
        } catch (InterruptedException e) {
            System.err.println("Crawl process interrupted: " + e.getMessage());
        }

        // Display the scraped data
        System.out.println("Scraped Data:");
        scrapedData.forEach((url, content) -> System.out.println(url + " -> " + content));
    }

    // Worker class to process each URL
    private class CrawlWorker implements Runnable {
        @Override
        public void run() {
            while (true) {
                String urlToProcess = linkQueue.poll();
                if (urlToProcess == null) {
                    break; // No more URLs to process
                }

                try {
                    // Get page content from the URL
                    String pageData = fetchContent(urlToProcess);
                    scrapedData.put(urlToProcess, pageData);

                    // Find new URLs on the page
                    Set<String> discoveredUrls = findUrls(pageData, urlToProcess);
                    for (String newUrl : discoveredUrls) {
                        synchronized (processedLinks) {
                            if (!processedLinks.contains(newUrl)) {
                                processedLinks.add(newUrl);
                                linkQueue.add(newUrl);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to access URL: " + urlToProcess + " - " + e.getMessage());
                }
            }
        }

        // Method to fetch the content from a given URL
        private String fetchContent(String url) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error: " + responseCode);
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                scanner.useDelimiter("\\Z");
                return scanner.next();
            }
        }

        // Method to find all URLs from the page content
        private Set<String> findUrls(String content, String base) {
            Set<String> links = new HashSet<>();
            String pattern = "<a\\s+(?:[^>]*?\\s+)?href=\"([^\"]*)\"";
            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(content);

            while (matcher.find()) {
                String link = matcher.group(1);
                if (link.startsWith("http")) {
                    links.add(link);
                } else if (link.startsWith("/")) {
                    // Resolve relative URLs
                    try {
                        URL resolvedUrl = new URL(new URL(base), link);
                        links.add(resolvedUrl.toString());
                    } catch (Exception e) {
                        System.err.println("Error resolving URL: " + link);
                    }
                }
            }
            return links;
        }
    }

    // Main method to initialize and start crawling
    public static void main(String[] args) {
        // Initialize the crawler with 10 threads
        WebCrawler crawler = new WebCrawler(10);

        // Begin crawling starting from a seed URL
        System.out.println("Starting the crawl process...");
        crawler.beginCrawl("https://www.example.com");
        System.out.println("Crawl process completed.");
    }
}

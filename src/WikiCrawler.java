import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/25/16.
 */
public class WikiCrawler{
	
	final static String RAW_BASE_URL = "https://en.wikipedia.org/w/index.php?action=raw&title=";
	final static String BASE_URL = "https://en.wikipedia.org";
	final static String RAW_PAGE = "&action=raw";
	
	private String seedURL = null;
	private String[] keywords = null;
	private String fileName = null;
	private Integer maxPage = null;
	private Integer totalRequests = 0;
	
	private Set<String> visited = new HashSet<String>();
	private Set<String> confirmed = new HashSet<String>();
	private Queue<LinkElement> unconfirmedList = new LinkedList<LinkElement>();
	private Queue<LinkElement> queue = new LinkedList<LinkElement>(); 
	
	class LinkElement{
		String link;
		String parentLink;
		
		LinkElement(String link, String parentLink){
			this.link = link;
			this.parentLink = parentLink;
		}
	}
	
	public WikiCrawler() {
		
	}
	
	public WikiCrawler(String seedURL, String[] keywords, Integer max, String fileName) {
		this.seedURL = seedURL;
		this.keywords = keywords;
		this.maxPage = max;
		this.fileName = fileName;
	}
	
	public void crawl(){
		long startTime = System.currentTimeMillis();
		// List<String> output = new ArrayList<String>();
		LinkedHashMap<String,Set<String>> output = new LinkedHashMap<String,Set<String>>();
		// we do not use the queue for the seed url to check if we are starting with an incorrect url
		String page = fetchPage(BASE_URL + seedURL);
		if(!pageContainsAllKeywords(page)) {
			System.out.println("Seed url does not contain keywords. Enter correct seed url");
		}
		confirmed.add(seedURL);
		List<String> links = extractLinks(page);
		for (String link : links){
			if(!visited.contains(link)) {
				queue.add(new LinkElement(link,seedURL));
				visited.add(link);
			}
		}
		while(!queue.isEmpty() && confirmed.size() < maxPage) {
			LinkElement linkElement = queue.remove();
			System.out.println("Requesting page : Current Node Size - " + confirmed.size() + " . Fetching page - " + linkElement.link + " . Parent page - " + linkElement.parentLink);
			page = fetchPage(BASE_URL + linkElement.link);
			if(page != null && pageContainsAllKeywords(page)){
				confirmed.add(linkElement.link);
				if (!output.containsKey(linkElement.parentLink)) {
					output.put(linkElement.parentLink, new LinkedHashSet<String>());
				}
				output.get(linkElement.parentLink).add(linkElement.link);
				links = extractLinks(page);
				for (String link : links){
					if (!link.equals(linkElement.link)){
						if(!visited.contains(link)) {
							queue.add(new LinkElement(link,linkElement.link));
							visited.add(link);
						} else if(confirmed.contains(link)) {
							if (!output.containsKey(linkElement.link)) {
								output.put(linkElement.link, new LinkedHashSet<String>());
							}
							output.get(linkElement.link).add(link);
						} else {
							unconfirmedList.add(new LinkElement(link, linkElement.link));
						}
					}
				}
			}
		}
		// re-visit those unconfirmed links
		for(LinkElement element : unconfirmedList) {
			if (confirmed.contains(element.link)){
				if(output.containsKey(element.parentLink)){
					if (!output.get(element.parentLink).contains(element.link)) {
						output.get(element.parentLink).add(element.link);
					}
				}
			}
		}
		
		// write to file
		try {
			File file = new File(fileName);
			if(file.createNewFile()){
				System.out.println("File created.");
			}
			FileWriter writer = new FileWriter(file);
			String newLine = System.getProperty("line.separator");
			writer.write(confirmed.size() + newLine);
			for(Map.Entry<String,Set<String>> entry : output.entrySet()) {
				  String key = entry.getKey();
				  Set<String> value = entry.getValue();
				  for (String v : value) {
					  writer.append(key + " " + v + newLine);
				  }
				}
			writer.flush();
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Crawling completed. Total requests - " + totalRequests + ". Total time - " + (endTime - startTime));
	}
	
	private boolean pageContainsAllKeywords(String page) {
		String pageLowerCase = page.toLowerCase();
		for (int i=0; i<keywords.length; i++){
			if(!pageLowerCase.contains(keywords[i].toLowerCase())) {
				return false;
			}
		}
		return true;
	}
	
	private String fetchPage(String uri){
		String page = null;
		try {
			if ((totalRequests % 100 == 0) && (totalRequests != 0)) {
				Thread.sleep(5000);
			}
			URL url = new URL(uri);
			InputStream inputStream = url.openStream();
			totalRequests++;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder builder = new StringBuilder("");
			String line = reader.readLine();
			while(line != null) {
				builder.append(line);
				line = reader.readLine();
			}
			page = builder.toString();
			page = page.substring(page.indexOf("<p>"), page.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		visited.add(uri);
		return page;
	}
	
	private List<String> extractLinks(String webpage){
		List<String> links = new ArrayList<String>();
		Pattern linkPattern = Pattern.compile("<a[^>]* href=\"(/wiki/.*?)\" .*?>(.*?)</a>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = linkPattern.matcher(webpage);
		while(matcher.find()){
			String link = webpage.substring(matcher.start(1),matcher.end(1));
			if(!link.contains("#") && ! link.contains(":")){
				links.add(link);
			}
		}
		return links;
	}
	
	public static void main(String[] args){
		String[] keywords = {"tennis", "grand slam"};
		WikiCrawler crawler = new WikiCrawler("/wiki/Tennis", keywords, 100, "test.txt");
		crawler.crawl();
	}
}

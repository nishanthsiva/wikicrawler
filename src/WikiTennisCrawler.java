
/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/02/16.
 */
public class WikiTennisCrawler {
	public static void main(String[] args){
		String[] keywords = {"tennis", "grand slam"};
		WikiCrawler crawler = new WikiCrawler("/wiki/Tennis", keywords, 1000, "WikiTennisGraph.txt");
		crawler.crawl();
	}
}

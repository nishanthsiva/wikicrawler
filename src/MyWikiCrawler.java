/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/02/16.
 */
public class MyWikiCrawler {
	public static void main(String[] args){
		String[] keywords = {"video", "game","play"};
		WikiCrawler crawler = new WikiCrawler("/wiki/Video_game", keywords, 1000, "MyWikiGraph.txt");
		crawler.crawl();
	}
}

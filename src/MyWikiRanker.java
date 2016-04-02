import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/27/16.
 */
public class MyWikiRanker {

    public static Set<String> convertArrayToSet(String[] stringArray){
        Set<String> stringSet = new TreeSet<>();
        for(String s:stringArray){
            stringSet.add(s);
        }
        return stringSet;
    }

    public static double exactJaccard(Set<String> file1Terms, Set<String> file2Terms){

        double jaccardSim = 0.0;

        Set<String> termIntersection = new TreeSet<>();
        termIntersection.addAll(file1Terms);
        termIntersection.retainAll(file2Terms);

        Set<String> unionSet = new TreeSet<>();
        unionSet.addAll(file1Terms);
        unionSet.addAll(file2Terms);
        jaccardSim = (double)termIntersection.size()/(unionSet.size());


        return jaccardSim;
    }

    public static void main(String arg[]){
        String[] keywords = {"video", "game","play"};
        WikiCrawler crawler = new WikiCrawler("/wiki/Video_game", keywords, 1000, "MyWikiGraph.txt");
        crawler.crawl();

        PageRank pageRank = new PageRank("MyWikiGraph.txt",0.01);
        System.out.println("Highest Page Rank  - "+pageRank.topKPageRank(1)[0]);
        System.out.println("Highest In Degree  - "+pageRank.topKInDegree(1)[0]);
        System.out.println("Highest Out Degree  - "+pageRank.topKOutDegree(1)[0]);

        Set<String> top100Ranks = convertArrayToSet(pageRank.topKPageRank(100));
        Set<String> top100InDegree = convertArrayToSet(pageRank.topKInDegree(100));
        Set<String> top100OutDegree = convertArrayToSet(pageRank.topKOutDegree(100));

        System.out.println("Jaccard Similarity of top 100 pageRanks and top 100 In Degrees = "+exactJaccard(top100Ranks,top100InDegree));
        System.out.println("Jaccard Similarity of top 100 pageRanks and top 100 Out Degrees = "+exactJaccard(top100Ranks,top100OutDegree));
        System.out.println("Jaccard Similarity of top 100 In Degrees and top 100 Out Degrees = "+exactJaccard(top100InDegree,top100OutDegree));

        System.out.println();
        pageRank = new PageRank("MyWikiGraph.txt",0.005);
        System.out.println("Highest Page Rank  - "+pageRank.topKPageRank(1)[0]);
        System.out.println("Highest In Degree  - "+pageRank.topKInDegree(1)[0]);
        System.out.println("Highest Out Degree  - "+pageRank.topKOutDegree(1)[0]);

        top100Ranks = convertArrayToSet(pageRank.topKPageRank(100));
        top100InDegree = convertArrayToSet(pageRank.topKInDegree(100));
        top100OutDegree = convertArrayToSet(pageRank.topKOutDegree(100));

        System.out.println("Jaccard Similarity of top 100 pageRanks and top 100 In Degrees = "+exactJaccard(top100Ranks,top100InDegree));
        System.out.println("Jaccard Similarity of top 100 pageRanks and top 100 Out Degrees = "+exactJaccard(top100Ranks,top100OutDegree));
        System.out.println("Jaccard Similarity of top 100 In Degrees and top 100 Out Degrees = "+exactJaccard(top100InDegree,top100OutDegree));


    }
}

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/25/16.
 */
public class PageRank {
    private Logger LOGGER = Logger.getLogger(PageRank.class.getName());
    private final String CLASS_NAME = PageRank.class.getName();

    private String filename;
    private double approxParam;
    private final double beta = 0.85;
    private HashMap<String,Double> pageRanks;
    private HashMap<String,Integer> inDegrees;
    private HashMap<String,Integer> outDegrees;
    private HashMap<String,Set<String>> pageLinks;
    private HashMap<String,Integer> pageIndex;
    private String[] sortedPageRank;
    private String[] sortedInDegree;
    private String[] sortedOutDegree;

    private int numEdges;

    public PageRank(String filename, double approxParam){
        this.filename = filename;
        this.approxParam = approxParam;

        this.pageRanks = new HashMap<>();
        this.inDegrees = new HashMap<>();
        this.outDegrees = new HashMap<>();
        this.pageLinks = new HashMap<>();
        this.pageIndex = new HashMap<>();
        calculatePageRanks();
        createSortedPageRank();
        createSortedDegrees();
    }

    private void createSortedDegrees() {
        final String METHOD_NAME = "createSortedDegrees";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);
        SortedSet<Map.Entry<String, Integer>> sortedset = new TreeSet<Map.Entry<String, Integer>>(
                new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> e1,
                                       Map.Entry<String, Integer> e2) {
                        if(e2.getValue().compareTo(e1.getValue()) == 0){
                            return e1.getKey().compareTo(e2.getKey());
                        }else
                            return e2.getValue().compareTo(e1.getValue());
                    }
                });
        sortedset.addAll(this.inDegrees.entrySet());
        Iterator<Map.Entry<String,Integer>> iterator = sortedset.iterator();
        this.sortedInDegree = new String[this.inDegrees.size()];
        int i=0;
        while(iterator.hasNext()){
            this.sortedInDegree[i++] = iterator.next().getKey();
        }
        sortedset.clear();
        sortedset.addAll(this.outDegrees.entrySet());
        iterator = sortedset.iterator();
        this.sortedOutDegree = new String[this.outDegrees.size()];
        i=0;
        while(iterator.hasNext()){
            this.sortedOutDegree[i++] = iterator.next().getKey();
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    private void createSortedPageRank() {
        final String METHOD_NAME = "createSortedPageRank";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        SortedSet<Map.Entry<String, Double>> sortedset = new TreeSet<Map.Entry<String, Double>>(
                new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> e1,
                                       Map.Entry<String, Double> e2) {
                        if(e2.getValue().compareTo(e1.getValue()) == 0){
                            return e1.getKey().compareTo(e2.getKey());
                        }else
                            return e2.getValue().compareTo(e1.getValue());
                    }
                });
        sortedset.addAll(this.pageRanks.entrySet());
        Iterator<Map.Entry<String,Double>> iterator = sortedset.iterator();
        this.sortedPageRank = new String[this.pageRanks.size()];
        int i=0;
        while(iterator.hasNext()){
            this.sortedPageRank[i++] = iterator.next().getKey();
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }


    public int inDegreeOf(String pageName){
        return inDegrees.get(pageName);
    }

    public int outDegreeOf(String pageName){
        return outDegrees.get(pageName);
    }

    public double pageRankOf(String pageName){
        return pageRanks.get(pageName);
    }

    public int numEdges(){
        return numEdges;
    }

    public String[] topKPageRank(int k){
        final String METHOD_NAME = "topKPageRank";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        int size = this.sortedPageRank.length<k?this.sortedPageRank.length:k;
        String[] topKPages = new String[size];
        for(int i=0;i<size;i++) {
           topKPages[i] = this.sortedPageRank[i];
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return topKPages;
    }

    public String[] topKInDegree(int k){
        final String METHOD_NAME = "topKInDegree";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        int size = this.sortedInDegree.length<k?this.sortedInDegree.length:k;
        String[] topKInDegree = new String[size];
        for(int i=0;i<size;i++) {
            topKInDegree[i] = this.sortedInDegree[i];
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return topKInDegree;
    }

    public String[] topKOutDegree(int k){
        final String METHOD_NAME = "topKOutDegree";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        int size = this.sortedOutDegree.length<k?this.sortedOutDegree.length:k;
        String[] topKOutDegree = new String[size];
        for(int i=0;i<size;i++) {
            topKOutDegree[i] = this.sortedOutDegree[i];
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return topKOutDegree;
    }

    private void parseGraph(){
        final String METHOD_NAME = "parseGraph";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);
        try {
            FileReader fileReader = new FileReader(new File(this.filename));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            double nodes = 0;
            if(bufferedReader.ready()){
                nodes = Integer.parseInt(bufferedReader.readLine());
                LOGGER.log(Level.FINE,"Nodes found = "+nodes);
            }
            while(bufferedReader.ready()){
                String edge = bufferedReader.readLine();
                String[] edgeNodes = edge.split(" ");
                if(edgeNodes.length == 2){
                    this.numEdges++;

                    if(!this.inDegrees.containsKey(edgeNodes[0])){
                        this.inDegrees.put(edgeNodes[0],0);
                    }

                    if(!this.outDegrees.containsKey(edgeNodes[1])){
                        this.outDegrees.put(edgeNodes[1],0);
                    }

                    if(this.inDegrees.containsKey(edgeNodes[1])){
                        int temp = this.inDegrees.get(edgeNodes[1]);
                        this.inDegrees.put(edgeNodes[1],temp+1);
                    }else{
                        this.inDegrees.put(edgeNodes[1],1);
                    }

                    if(this.outDegrees.containsKey(edgeNodes[0])){
                        int temp = this.outDegrees.get(edgeNodes[0]);
                        this.outDegrees.put(edgeNodes[0],temp+1);
                    }else{
                        this.outDegrees.put(edgeNodes[0],1);
                    }

                    if(this.pageLinks.containsKey(edgeNodes[0])){
                        this.pageLinks.get(edgeNodes[0]).add(edgeNodes[1]);
                    }else{
                        Set<String> links = new TreeSet<>();
                        links.add(edgeNodes[1]);
                        this.pageLinks.put(edgeNodes[0],links);
                    }

                    this.pageRanks.put(edgeNodes[0],new Double(1/nodes));
                    this.pageRanks.put(edgeNodes[1],new Double(1/nodes));

                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING,e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,e.getMessage());
            e.printStackTrace();
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);

    }

    private void calculatePageRanks() {
        final String METHOD_NAME = "calculatePageRanks";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        parseGraph();
        createPageIndex();
        int n=1;
        double norm = 0.0;
        boolean converged = false;
        HashMap<String,Double> currentRank = this.pageRanks;
        HashMap<String,Double> nextRank;
        while(!converged){
            LOGGER.log(Level.FINE,"Sum of Page Ranks = "+getSumOfPageRanks(currentRank));
            nextRank = computeNextP(currentRank);
            norm = normalize(nextRank,currentRank);
            if(norm <= this.approxParam){
                converged = true;
                this.pageRanks = nextRank;
                break;
            }
            currentRank = nextRank;
            n++;
        }
        LOGGER.log(Level.FINE,n+" step! norm value= "+norm);
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    private double getSumOfPageRanks(HashMap<String,Double> currentRank){
        double pgr = 0.0;
        for(String page : currentRank.keySet()){
            pgr += currentRank.get(page);
        }
        return pgr;
    }

    private double normalize(HashMap<String, Double> nextRank, HashMap<String, Double> currentRank) {
        final String METHOD_NAME = "normalize";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        Iterator<String> keysIterator = nextRank.keySet().iterator();
        double normalize = 0;
        while(keysIterator.hasNext()){
            String page = keysIterator.next();
            normalize+= Math.abs(nextRank.get(page)-currentRank.get(page));
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return normalize;
    }

    private HashMap<String,Double> computeNextP(HashMap<String,Double> prevRank){
        final String METHOD_NAME = "computeNextP";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        HashMap<String,Double> nextRank = new HashMap<>();
        initializeNextP(nextRank,prevRank);
        Iterator<String> pageIterator = nextRank.keySet().iterator();
        while(pageIterator.hasNext()){
            String page = pageIterator.next();
            if(this.outDegrees.get(page) != 0){
                Set<String> linksInPage = this.pageLinks.get(page);
                Iterator<String> linksIterator = linksInPage.iterator();
                while(linksIterator.hasNext()){
                    String link = linksIterator.next();
                    double currentRank = nextRank.get(link);
                    currentRank += beta*(prevRank.get(page)/linksInPage.size());
                    nextRank.put(link,currentRank);
                }
            }else{
                Set<String> allPages = this.pageLinks.keySet();
                Iterator<String> allPagesIterator = allPages.iterator();
                while(allPagesIterator.hasNext()){
                    String nextPage = allPagesIterator.next();
                    double currentRank = nextRank.get(nextPage);
                    currentRank += beta*(prevRank.get(page)/allPages.size());
                    nextRank.put(nextPage,currentRank);
                }

            }
        }

        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return nextRank;
    }

    private void initializeNextP(HashMap<String, Double> nextRank, HashMap<String, Double> prevRank) {
        final String METHOD_NAME = "initializeNextP";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        Iterator<String> keySet = prevRank.keySet().iterator();
        double val = (1-beta)/prevRank.size();
        while(keySet.hasNext()){
            nextRank.put(keySet.next(),val);
        }

        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    private void createPageIndex(){
        final String METHOD_NAME = "createPageIndex";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        Set<String> pages = this.pageRanks.keySet();
        Iterator<String> pageIterator = pages.iterator();
        int index =0;
        while(pageIterator.hasNext()){
            this.pageIndex.put(pageIterator.next(),index++);
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    public static void main(String[] args){
        PageRank pageRank = new PageRank("WikiTennisGraph.txt",0.005);
        System.out.println(pageRank.topKPageRank(15).length);
        double pgr = 0.0;
        System.out.println("Top 15 page Ranks - ");
        for(String s :pageRank.topKPageRank(15)){
            pgr += pageRank.pageRankOf(s);
            System.out.println(s+"\t"+pageRank.pageRankOf(s));
        }
        System.out.println("Top 15 In Degrees - ");
        for(String s: pageRank.topKInDegree(15)){
            System.out.println(s+"\t"+pageRank.inDegreeOf(s));
        }
        System.out.println("Top 15 Out Degrees - ");
        for(String s: pageRank.topKOutDegree(15)){
            System.out.println(s+"\t"+pageRank.outDegreeOf(s));
        }

    }
}

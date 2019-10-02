import java.io.*;
import java.util.*;

public class Ngram {

    // read files line by line, regard each line as a sentence
    public ArrayList<String> getSentences(String path){
        ArrayList<String> sentences = new ArrayList<>();
        File file = new File(path);
        if(file.exists()){
            try{
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(file));
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line!=null){
                    sentences.add(line);
                    line = br.readLine();
                }
                br.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            return sentences;
        }else {
            System.out.println("This file does not exist");
            return null;
        }

    }

    // count ngram words for different model, all counts would be saved in one txt file
    public NgramCount getNgramCount(int k, String language, String path) {
        NgramCount ngramCount = new NgramCount();
        HashMap<String, Integer> map = new HashMap<>();
        ArrayList<String> sentences = getSentences(path);
        // split the whole document into sentences and analysis each sentence
        for(String curr: sentences){
            for(int i=0;i<=curr.length()-k;i++){
                String key = curr.substring(i,i+k);
                // use a hashmap to save the result
                map.put(key,map.getOrDefault(key,0)+1);
            }
        }
        // write the map into a txt file
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("result/"+ language + ".txt",true));
            for (String key : map.keySet()) {
                writer.write(key + "_" + map.get(key));
                writer.newLine();
            }
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        ngramCount.type = map.size();
        int count = 0;
        for(String x: map.keySet()){
            count+= map.get(x);
        }
        ngramCount.token = count;
        return ngramCount;
    }

    // read count result from txt file and save it as a hashmap
    public HashMap<String, Integer> getNgram(String language) {
        HashMap<String, Integer> map = new HashMap<>();
        File file = new File("result/"+ language + ".txt");
        if (file.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(file));
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null) {
                    String[] word = line.split("_");
                    map.put(word[0], Integer.valueOf(word[1]));
                    line = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("The file does not exist");
        }
        return map;
    }

    // predict the language of the test document, meanwhile calculate the perplexity of english models
    public void predict(HashMap<String, Integer> englishCount, HashMap<String, Integer> spanishCount, HashMap<String, Integer> germanCount,int k,String path, NgramCount volE, NgramCount volG, NgramCount volS) {
        // count the number of english/german/spanish sentences in test document
        System.out.println(k+"-gram model: ");
        int english = 0, german = 0, spanish = 0;
        String res = "";
        double perp = 0;
        ArrayList<String > sentences = getSentences(path);
        int n = sentences.size();
        // analysis sentence by sentence
        for(int i =0; i<sentences.size();i++){
            String line = sentences.get(i);
            // get the probability of each sentence to be english/spanish/german
            double probE = getProb(englishCount,line,k,volE);
            double probS = getProb(spanishCount,line,k,volS);
            double probG = getProb(germanCount,line,k,volG);

            double pp = Math.pow(probE,-((double) 1/(double) line.length()));
            System.out.println("perplexity of the "+(i+1)+" sentence: "+pp);
            perp += pp;

            // regard each sentence as the language with largest probability
            if(probE>=probS && probE>=probG){
                english++;
            }else if(probG>=probE && probG>=probS){
                german++;
            }else {
                spanish++;
            }
        }

        //take the mean value of all sentences' perplexity as the perplexity of this test document of this model
//        System.out.println(perp);
//        System.out.println(n);


        // regard this document as the language with most sentences
        if(english>=german && english>=spanish){
            res = "English";
        }else if(german>=english && german>=spanish){
            res = "German";
        }else {
            res = "Spanish";
        }
        System.out.println("This article is "+ res);
        System.out.println(k+"gram perplexity: "+(perp/(n==0?1:n)));
    }

    // predict the language of test document by LaPlace smoothing trigram model meanwhile get the perplexity of this model
    public void laPlacePredict(HashMap<String, Integer> englishCount, HashMap<String, Integer> spanishCount, HashMap<String, Integer> germanCount, int k, String path, NgramCount volE, NgramCount volG, NgramCount volS) {
        System.out.println("Laplace smoothing trigram model: ");
        int english = 0, german = 0, spanish = 0;
        String res = "";
        double perp = 0.0;
        ArrayList<String> sentences = getSentences(path);
        int n = sentences.size();
        for(int i =0; i<sentences.size();i++){
            String line = sentences.get(i);
            double probE = getLaPlaceProb(englishCount,line,k,volE);
            double probS = getLaPlaceProb(spanishCount,line,k,volS);
            double probG = getLaPlaceProb(germanCount,line,k,volG);
            double pp = Math.pow(probE,-((double)1/(double) line.length()));
            System.out.println("perplexity of the "+(i+1)+" sentence: "+pp);
            perp += pp;
            if(probE>=probS && probE>=probG){
                english++;
            }else if(probG>=probE && probG>=probS){
                german++;
            }else {
                spanish++;
            }
        }

        if(english>=german && english>=spanish){
            res = "English";
        }else if(german>=english && german>=spanish){
            res = "German";
        }else {
            res = "Spanish";
        }
        System.out.println("This article is "+res);
        System.out.println("Laplace trigram perplexity: "+(perp/(n==0?1:n)));
    }

    // predict the language of test document by backoff smoothing trigram model meanwhile get the perplexity of this model
    public void backoffPredict(HashMap<String, Integer> englishCount, HashMap<String, Integer> spanishCount, HashMap<String, Integer> germanCount, int k, String path, NgramCount volE, NgramCount volG, NgramCount volS) {
        int english = 0, german = 0, spanish = 0;
        String res = "";
        double perp = 0.0;
        ArrayList<String> sentences = getSentences(path);
        int n = sentences.size();
         for (int i =0;i<sentences.size();i++){
             String line = sentences.get(i);
            double probE = getBackoffProb(englishCount,line,k,volE);
            double probS = getBackoffProb(spanishCount,line,k,volS);
            double probG = getBackoffProb(germanCount,line,k,volG);


            double pp = Math.pow(probE,-((double)1/(double) line.length()));
            System.out.println("perplexity of the "+(i+1)+" sentence: "+pp);
            perp += pp;
            if(probE>=probS && probE>=probG){
                english++;
            }else if(probG>=probE && probG>=probS){
                german++;
            }else {
                spanish++;
            }
        }


        if(english>=german && english>=spanish){
            res = "English";
        }else if(german>=english && german>=spanish){
            res = "German";
        }else {
            res = "Spanish";
        }

        System.out.println("This article is "+res);
        System.out.println("backoff trigram perplexity: "+ (perp/(n==0?1:n)));
    }

    // predict the language of test document by Linear Interpolation smoothing trigram model meanwhile get the perplexity of this model
    public void linearInterPredict(HashMap<String, Integer> englishCount, HashMap<String, Integer> spanishCount, HashMap<String, Integer> germanCount, int k, String path, NgramCount volE, NgramCount volG, NgramCount volS) {
        int english = 0, german = 0, spanish = 0;
        String res = "";
        double perp = 0.0;
        ArrayList<String> sentences = getSentences(path);
        int n = sentences.size();
        for (int i =0;i<sentences.size();i++){
            String line = sentences.get(i);
            double probE = getLinearInterProb(englishCount,line,k,volE);
            double probS = getLinearInterProb(spanishCount,line,k,volS);
            double probG = getLinearInterProb(germanCount,line,k,volG);


            double pp = Math.pow(probE,-((double)1/(double) line.length()));
            System.out.println("perplexity of the "+(i+1)+" sentence: "+pp);
            perp += pp;
            if(probE>=probS && probE>=probG){
                english++;
            }else if(probG>=probE && probG>=probS){
                german++;
            }else {
                spanish++;
            }
        }


        if(english>=german && english>=spanish){
            res = "English";
        }else if(german>=english && german>=spanish){
            res = "German";
        }else {
            res = "Spanish";
        }
        System.out.println("This article is "+res);
        System.out.println("Linear interpolation trigram perplexity: "+(perp/(n==0?1:n)));

    }

    // predict the language of test document by absolute discount smoothing trigram model meanwhile get the perplexity of this model
    public void absDisPredict(HashMap<String, Integer> englishCount, HashMap<String, Integer> spanishCount, HashMap<String,Integer> germanCount, int k, String path, NgramCount volE, NgramCount volG, NgramCount volS) {
        int english = 0, german = 0, spanish = 0;
        String res = "";
        double perp = 0.0;
        ArrayList<String> sentences = getSentences(path);
        int n = sentences.size();
        for (int i =0;i<sentences.size();i++){
            String line = sentences.get(i);
            double probE = getAbsDisProb(englishCount,line,k,volE);
            double probS = getAbsDisProb(spanishCount,line,k,volS);
            double probG = getAbsDisProb(germanCount,line,k,volG);


            double pp = Math.pow(probE,-((double)1/(double) line.length()));
            System.out.println("perplexity of the "+(i+1)+" sentence: "+pp);
            perp += pp;
            if(probE>=probS && probE>=probG){
                english++;
            }else if(probG>=probE && probG>=probS){
                german++;
            }else {
                spanish++;
            }
        }


        if(english>=german && english>=spanish){
            res = "English";
        }else if(german>=english && german>=spanish){
            res = "German";
        }else {
            res = "Spanish";
        }
        System.out.println("This article is "+res);
        System.out.println("Absolute discount smoothing trigram perplexity: "+(perp/(n==0?1:n)));
    }

    public double getProb(HashMap<String, Integer> count, String word,int k, NgramCount vol){
        double prob = 1;
        if(k==1){
            for(int i=0;i<=word.length()-k;i++){
                prob *= ((double)count.getOrDefault(word.substring(i,i+k),0)/(double) vol.token);
            }
        }else {
            for(int i=0;i<=word.length()-k;i++){
                prob*=((double)count.getOrDefault(word.substring(i,i+k),0)/(double)count.getOrDefault(word.substring(i,i+k-1),1));
            }
        }
        return prob;
    }

    public double getLaPlaceProb(HashMap<String, Integer> count, String word, int k, NgramCount vol){
        double prob = 1;
        for(int i=0;i<=word.length()-k;i++){
            prob*=((float)(count.getOrDefault(word.substring(i,i+k),0)+1)/(float)(count.getOrDefault(word.substring(i,i+k-1),1)+vol.type));
        }
        return prob;
    }

    public double getBackoffProb(HashMap<String, Integer> count, String word, int k, NgramCount vol){
        double prob = 1;
        for(int i=0;i<=word.length()-k;i++){
            if(count.containsKey(word.substring(i,i+k))){
                prob*=((double)count.getOrDefault(word.substring(i,i+k),0)/(double)count.getOrDefault(word.substring(i,i+k-1),1));
            }else if(count.containsKey(word.substring(i+1,i+k))){
                prob*=((double)count.getOrDefault(word.substring(i+1,i+k),0)/(double)count.getOrDefault(word.substring(i+1,i+k-1),1));
            }else{
                prob*=((double)count.getOrDefault(word.substring(i+2,i+k),0)/(double)vol.token);
            }

        }
        return prob;
    }

    public double getLinearInterProb(HashMap<String, Integer> count, String word, int k, NgramCount vol){
        double prob = 1f;
        double lambda = (1.0/3.0);
        for(int i=0;i<=word.length()-k;i++){
            prob*=
                    (lambda*(
                            ((double)count.getOrDefault(word.substring(i,i+k),0)/(double)count.getOrDefault(word.substring(i,i+k-1),1))
                            +((double)count.getOrDefault(word.substring(i+1,i+k),0)/(double)count.getOrDefault(word.substring(i+1,i+k-1),1))
                            +((double)count.getOrDefault(word.substring(i+2,i+k),0)/(double)vol.token)
                    ));
        }
        return prob;
    }

    public double getAbsDisProb(HashMap<String,Integer> count, String word, int k, NgramCount vol){
        double prob = 1f;
        for(int i=0; i<=word.length()-k;i++){
            double tri = ((Math.max(count.getOrDefault(word.substring(i,i+k),0)-0.75,0))/((double)count.getOrDefault(word.substring(i,i+k-1),1)));
            double lambda1 = ((0.75/((double)count.getOrDefault(word.substring(i,i+k-1),1)))*getTriType(count,word.substring(i,i+k-1)));
            double bi = ((Math.max(count.getOrDefault(word.substring(i+1,i+k),0)-0.75,0))/(double)count.getOrDefault(word.substring(i+1,i+k-1),1));
            double lambda2 = ((0.75/((double)count.getOrDefault(word.substring(i+1,i+k-1),1)))*getTriType(count,word.substring(i+1,i+k-1)));
            double uni = ((Math.max(count.getOrDefault(word.substring(i+2,i+k),0)-0.75,0))/(double)vol.token);
            double lambda3 = ((0.75/(double)vol.token))*vol.type;
            prob *= tri+lambda1*bi+lambda2*uni+lambda3;
        }
        return prob;
    }

    public void getThProbs(HashMap<String, Integer> englishCount, NgramCount volE,String path) {
        ArrayList<String> words = new ArrayList<>();
        String th = "th";
        for(String key : englishCount.keySet()){
            if(key.length()==1){
                words.add(th+key);
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("vocabulary, trigram, LaPlace trigram, backoff trigram, linear interpolation trigram, absolute discount trigram");
            writer.newLine();
            double c1=0,c2=0,c3=0,c4=0,c5=0;
            double s1=0,s2=0,s3=0,s4=0,s5=0;
            for(String curr: words){
                c1 = getProb(englishCount,curr,3,volE);
                c2 = getLaPlaceProb(englishCount,curr,3,volE);
                c3 = getBackoffProb(englishCount,curr,3,volE);
                c4 = getLinearInterProb(englishCount,curr,3,volE);
                s1+=c1;
                s2+=c2;
                s3+=c3;
                s4+=c4;
                String res = "";
                res+=curr+", ";
                res+=c1+", ";
                res+=c2+", ";
                res+=c3+", ";
                res+=c4;
                writer.write(res);
                writer.newLine();
            }
            s1 = (double)Math.round(s1*100)/100;
            s2 = (double)Math.round(s2*100)/100;
            s3 = (double)Math.round(s3*100)/100;
            s4 = (double)Math.round(s4*100)/100;
            writer.write("sum, "+s1+", "+s2+", "+s3+", "+s4);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public int getTriType(HashMap<String,Integer> map, String formerTwo){
        int count = 0;
        if(formerTwo.length()==2){
            for(String key:map.keySet()){
                if(key.length()==3 && key.substring(0,2).equals(formerTwo)){
                    count++;
                }
            }
        }else {
            for(String key:map.keySet()){
                if(key.length()==2 && key.substring(0,1).equals(formerTwo)){
                    count++;
                }
            }
        }
        return count;
    }
}

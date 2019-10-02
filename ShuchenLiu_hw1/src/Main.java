import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        Ngram ngram = new Ngram();

        //count ngram words
        NgramCount volE = new NgramCount();
        NgramCount volS = new NgramCount();
        NgramCount volG = new NgramCount();
        for(int k=1;k<=3;k++){
            if(k==1){
                volE = ngram.getNgramCount(k,"English",Path.English);
                volS = ngram.getNgramCount(k,"Spanish",Path.Spanish);
                volG = ngram.getNgramCount(k,"German",Path.German);
            }else {
                ngram.getNgramCount(k,"English",Path.English);
                ngram.getNgramCount(k,"Spanish",Path.Spanish);
                ngram.getNgramCount(k,"German",Path.German);
            }
        }

        // read count from generated files
        HashMap<String,Integer> englishCount = ngram.getNgram("English");
        HashMap<String,Integer> spanishCount = ngram.getNgram("Spanish");
        HashMap<String,Integer> germanCount = ngram.getNgram("German");

        //unigram model
        ngram.predict(englishCount,spanishCount,germanCount,1,Path.test,volE,volG,volS);

        //bigram model
        ngram.predict(englishCount,spanishCount,germanCount,2,Path.test,volE,volG,volS);

        //trigram model
        ngram.predict(englishCount,spanishCount,germanCount,3,Path.test,volE,volG,volS);

        //LaPlace smoothing trigram model
        ngram.laPlacePredict(englishCount,spanishCount,germanCount,3,Path.test,volE,volG,volS);

        //backoff smoothing trigram model
        ngram.backoffPredict(englishCount,spanishCount,germanCount,3,Path.test,volE,volG,volS);

        //linear interpolation trigram model
        ngram.linearInterPredict(englishCount,spanishCount,germanCount,3,Path.test,volE,volG,volS);

        //Absolute discount smoothing trigram model
        ngram.absDisPredict(englishCount,spanishCount,germanCount,3,Path.test,volE,volG,volS);

        //calculate the probability of each th*, the result is in result/thProb.txt
        ngram.getThProbs(englishCount,volE,Path.ThProb);
    }
}

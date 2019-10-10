package cs2731.hw2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProbCKY {
    public static void main(String[] args) {
        if(args.length<3){
            System.out.println("Error, There must be three arguments");
            return;
        }
        String path = args[0];
        String sentence = args[1];
        String gt=args[2];
        sentence = sentence.toLowerCase();
        gt = gt.toLowerCase();
        CKY probCKY = new CKY();

        //read pcfg.txt
        List<Rules> grammar = new ArrayList<>();
        HashSet<String> newElements = new HashSet<>();
        try{
            probCKY.getGrammar(path,grammar,newElements);
        }catch (IOException e){
            e.printStackTrace();
        }

//        for(Rules r:grammar){
//            System.out.println(r.getFrom()+"->"+r.getTo()+" "+r.getProbability());
//        }

        // dp processing
        List<Node> nodes = probCKY.getPrase(sentence,grammar);

        // parse sentence in different ways and analyze
        if(nodes.size()==0){
            System.out.println("Sentence rejected");
        }else {
            System.out.println("Sentence accepted");
            List<ParsedSentence> res = new ArrayList<>();
            for(Node node:nodes){
                ParsedSentence ps = new ParsedSentence();
                ps.result = probCKY.getParsedSentence(node, newElements);
                float[] pr = probCKY.getPR(ps.result,sentence,gt,sentence);
                ps.precision = pr[0];
                ps.recall = pr[1];
                ps.prob = node.prob;
                res.add(ps);
            }
            System.out.println(res.size()+" possible parses");
            float total=0f;
            for(ParsedSentence ps : res){
                System.out.println("One parse: "+ps.result);
                System.out.println("Precision: "+ps.precision);
                System.out.println("Recall: "+ps.recall);
                System.out.println("Probability: "+ps.prob);
                total+=ps.prob;
            }
            System.out.println("Probability of this sentence: "+total);
        }
    }
}

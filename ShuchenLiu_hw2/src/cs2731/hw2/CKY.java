package cs2731.hw2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CKY {

    public void getGrammar(String path, List<Rules> rules, HashSet<String> newElements) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(path));
        String line = bf.readLine();
        int count =0;
        while (line != null){
            line = line.toLowerCase();
            String[] s = line.split("->");
            if(s.length>1){
                String[] tmp = s[0].split(" ");
                String[] tmp2 = s[1].split(" ");
                if(tmp2.length>2){
                    List<Rules> m = getAllByTo(tmp2[0]+" "+tmp2[1],rules);
                    String from = "";
                    for(Rules r:m){
                        if(newElements.contains(r.getFrom())){
                            from = r.getFrom();
                        }
                    }
                    if(m.isEmpty() || from.equals("")){
                        rules.add(new Rules(1,"x"+count,tmp2[0]+" "+tmp2[1]));
                        rules.add(new Rules(Float.parseFloat(tmp[0]),tmp[1],"x"+count+" "+tmp2[2]));
                        newElements.add("x"+count);
                        count++;
                    }else{
                        rules.add(new Rules(1,tmp[1],from +" "+tmp2[2]));
                    }
                }else {
                    rules.add(new Rules(Float.parseFloat(tmp[0]),tmp[1],s[1]));
                }

            }
            line = bf.readLine();
        }
    }


    public List<Rules> getAllByTo(String to, List<Rules> rules){
        List<Rules> res = new ArrayList<>();
        for(Rules r: rules){
            if(r.getTo().equals(to)){
                res.add(r);
            }
        }
        return res;
    }

    public List<Node> getPrase(String sentence, List<Rules> grammar) {
        String[] words = sentence.split(" ");
        int m = words.length;
        Cell[][] dp = new Cell[m][m];
        for(int i=0; i<m;i++){
            for(int j = i;j>=0;j--){
                if(i==j){
                    List<Node> nodes = new ArrayList<>();
                    Node node = new Node();
                    node.name = words[i];
                    node.prob = 1;
                    node.right = null;
                    node.left = null;
                    node.range = new int[]{j,i+1};
                    buildPartTree(nodes,words[i],grammar,node);
                    if(!nodes.isEmpty()){
                        Cell cell = new Cell();
                        cell.nodes = nodes;
                        dp[j][i] = cell;
                    }

                }else {
                    List<Node> res = new ArrayList<>();
                    for(int l=i-1;l>=0;l--){
                        int k = l+1;
                        if(dp[k][i]==null || dp[j][l]==null){
                            continue;
                        }
                        List<Node> l1 = dp[j][l].nodes;
                        List<Node> l2 = dp[k][i].nodes;
                        HashSet<String> candidates = new HashSet<>();
                        Cell cell = new Cell();
                        for(Node n1:l1){
                            for(Node n2:l2){
                                List<Rules> rules = getAllByTo(n1.name+" "+n2.name,grammar);
                                if(rules.isEmpty()){
                                    continue;
                                }
                                for(Rules r: rules){
                                    buildPartTree2(res,r.getFrom(),grammar,new int[]{j,i+1},n1.prob*n2.prob*r.getProbability(),n1,n2);
                                }

                            }
                        }
                        if(!res.isEmpty()){
                            cell.nodes = res;
                            dp[j][i] = cell;
                        }
                    }
                }
            }
        }
        List<Node> finalRes = new ArrayList<>();
        if(dp[0][m-1]==null){
            return finalRes;
        }
        for(Node n:dp[0][m-1].nodes){
            if(n.name.equals("s")){
                finalRes.add(n);
            }
        }
        return finalRes;
    }

    public String getParsedSentence(Node node, HashSet<String> set){
        if(node.left==null && node.right==null){
            return " "+node.name;
        }
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        if(node.left !=null){
            left.append(getParsedSentence(node.left,set));
        }
        if(node.right !=null){
            right.append(getParsedSentence(node.right,set));
        }
        if(!set.contains(node.name)){
            sb.append("[").append(node.name).append(left).append(right).append("]");
        }else {
            sb.append(left).append(right);
        }

        return sb.toString();
    }

    public float[] getPR(String sentence, String sentence1, String gt, String gt1){
        HashSet<String> w1 = getWords(sentence1);
        HashSet<String> w2 = getWords(gt1);
        List<String> r1 = getParts(sentence,w1);
        List<String> r2 = getParts(gt,w2);
        int correct = 0;
        for(String s1:r1){
            for(String s2:r2){
                if(s1.equals(s2)){
                    correct++;
                }
            }
        }
        float precision = (float)correct/(float)r1.size();
        float recall = (float)correct/(float)r2.size();
        return new float[]{precision,recall};
    }

    public List<String> getParts(String sentence, HashSet<String> set){
        sentence = sentence.toLowerCase();
        List<String> res = new ArrayList<>();
        char[] chars = sentence.toCharArray();
        Stack<String> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(char x: chars){
            if(x=='[' || x ==' '){
                if(sb.length()>0){
                    stack.add(sb.append("-").append("(").append(count).toString());
                }
                sb.delete(0,sb.length());
            }
            if(Character.isAlphabetic(x)){
                sb.append(x);
            }
            if(x==']'){
                if(set.contains(sb.toString())){
                    count++;
                }
                res.add(stack.pop() + count + ")");
                sb.delete(0,sb.length());
            }
        }

        return res;
    }

    public void buildPartTree(List<Node> nodes, String to, List<Rules> grammar,Node node){
        List<Rules> rules = getAllByTo(to,grammar);
        if(rules.isEmpty()){
            return;
        }

        for(Rules r: rules){
            Node node1 = new Node();
            node1.prob = node.prob*r.getProbability();
            node1.range = node.range;
            node1.name = r.getFrom();
            node1.left = node;
            nodes.add(node1);
            buildPartTree(nodes,r.getFrom(),grammar,node1);
        }
    }

    public void buildPartTree2(List<Node> nodes, String to, List<Rules> grammar, int[] range, float prob,Node left, Node right){
        Node node = new Node();
        node.left = left;
        node.right = right;
        node.name = to;
        node.prob = prob;
        node.range = range;
        nodes.add(node);
        buildPartTree(nodes,to,grammar,node);
    }

    public HashSet<String> getWords(String sentence){
        HashSet<String> set = new HashSet<>();
        String[] s = sentence.split(" ");
        for(String str:s){
            set.add(str);
        }
        return set;
    }

}

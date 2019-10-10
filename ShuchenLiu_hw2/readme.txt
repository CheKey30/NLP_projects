There are some assumptions I made in this project.
1. I lowercased every letter not only in the sentence but also in rules. For example, "NP" would be "np".

2. About the binarization, when a rule has more than 2 non-terminals on the right, I would use a new symbol 
   to represent it and add a new rule. for example, for rule "vp->verb np pp" it would be "vp->x1 pp" and "x1->verb np".

3. I am not sure whether there would be more than 3 non-terminals on the right like "a->b c d e". In my program, 
   this situation is not handled. So the grammar rules should have 1 to 3 non-terminals on the right.

4. The test sentence should not include non-letter characters such as punctuation. 

The following is about how to run the program.
1. In order to run this program correctly, you should first unzip this file and then enter the folder "ShuchenLiu_hw2"
   through the terminal.

2. After that, you should make sure that the "pcfg.txt" file and "ShucheLiu_hw2.jar" are in the same folder(should be "ShuchenLiu_hw2" here).

3. Then you can type the following command in the terminal to run the program and all results would be typed in the terminal.
  java -cp ShuchenLiu_hw2.jar cs2731.hw2.ProbCKY pcfg.txt "A test sentence ." "the gold standard s-expression"

4. To do the blind test, please put the new grammar txt file under the "ShuchenLiu_hw2" folder and change the "pcfg.txt"
to the new name when you type the command line.

Java version: JDK 1.11




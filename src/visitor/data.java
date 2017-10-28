package visitor;

import java.util.HashMap;

public class data {
    /* finding line number of label and label in line*/
    static HashMap<String,Integer> LabelLine = new HashMap<>();
    static HashMap<Integer,String> LineLabel = new HashMap<>();
    static HashMap<String,ControlFlowGraph> hashGraphs = new HashMap<>();
}

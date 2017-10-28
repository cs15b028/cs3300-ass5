package visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StatementNode {
    public ArrayList<String> predecessors;
    /*contains the line numbers of successors*/
    public ArrayList<Integer> successors;
    public Set<String> sin;
    public Set<String> sout;
    public Set<String> sin1;
    public Set<String> sout1;
    public Set<String> suse;
    public ArrayList<String> in;
    public ArrayList<String> out;
    public ArrayList<String> use;
    public int line;
    public String def;

    public StatementNode(ArrayList<Integer> successors, ArrayList<String> in, ArrayList<String> out,
                         ArrayList<String> use, String def, int line)
    {
        this.successors = successors;
        this.in = in;
        this.out = out;
        this.use = use;
        this.def = def;
        this.line = line;
        this.sin = new HashSet<>(in);
        this.sout = new HashSet<>(out);
        this.suse = new HashSet<>(use);
        this.sin1 = new HashSet<>(in);
        this.sout1 = new HashSet<>(out);
    }
    public void print()
    {
        System.out.print("in : ");
        System.out.println(sin);
        System.out.print("out : ");
        System.out.println(sout);
        System.out.println();
//        System.out.print("use :");
//        System.out.println(use);
//        System.out.print("successor : ");
//        System.out.println(successors);
//        System.out.println("def is : " + def );
//        System.out.println("stmt is " + line);
//        System.out.println();
    }
}

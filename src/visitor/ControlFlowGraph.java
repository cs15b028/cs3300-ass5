package visitor;

import javafx.util.Pair;

import java.util.*;

public class ControlFlowGraph {
    public String name;
    public HashMap<Integer,StatementNode> hashNodes;
    public HashMap<String, Pair<Integer,Integer>> ranges = new HashMap<>();
    public ArrayList<String> regs = new ArrayList<>(Arrays.asList("t0","t1","t2","t3","t4","t5","t6","t7","t8", "t9 ",
            "s0","s1","s2","s3","s4", "s5", "s6","s7"));

    public HashMap<interval,String> isusing = new HashMap<>();
    public ArrayList<interval> intervals = new ArrayList<>();
    public ArrayList<interval> active = new ArrayList<>();
    public HashMap<String,Integer> stacklocation = new HashMap<>();
    public HashMap<String,String> registers = new HashMap<>();
    int count = 0;

    void RegisterAllocation()
    {
        for(String str : ranges.keySet())
        {
            interval i = new interval(str,ranges.get(str).getKey(),ranges.get(str).getValue());
            intervals.add(i);
        }
        intervals.sort(new compareIntervals());
        for(interval i : intervals)
        {
            expireOldIntervals(i);
            if(active.size() == regs.size())
            {
                spillatInterval(i);
            }
            else
            {
                isusing.put(i,regs.get(regs.size() - 1));
                regs.remove(regs.size() - 1);
                active.add(i);
                active.sort(new reversecompareIntervals());
            }
        }
        for(interval i : isusing.keySet())
        {
            registers.put(i.temp,isusing.get(i));
        }
    }

    private void spillatInterval(interval i) {
        interval last = active.get(active.size() - 1);
        if(last.end > i.end)
        {
            isusing.put(i,isusing.get(last));
            stacklocation.put(last.temp,count);
            count++;
            active.remove(last);
            active.add(i);
            active.sort(new reversecompareIntervals());
        }
        else
        {
            stacklocation.put(i.temp,count);
            count++;
        }
    }

    private void expireOldIntervals(interval i) {
        for(int j = 0;j < active.size();j++)
        {
            if(active.get(j).end >= i.start)
            {
                return;
            }
            else
            {
                regs.add(isusing.get(active.get(j)));
                active.remove(active.get(j));
            }
        }
    }

    ControlFlowGraph(String name, HashMap<Integer,StatementNode> hashNodes)
    {
        this.name = name;
        int size = hashNodes.size();
        //making the successors of the last node as empty array list
        hashNodes.get(size).successors = new ArrayList<>();
        this.hashNodes = hashNodes;
    }
    public void print()
    {
        System.out.println(name);
        for(Integer i : hashNodes.keySet())
        {
            hashNodes.get(i).print();
        }
        System.out.println(ranges);
        System.out.println(registers);
        System.out.println(stacklocation);
    }
    void computeRanges()
    {
        for(int i : hashNodes.keySet())
        {
            for(String str : hashNodes.get(i).sin)
            {
                if(!ranges.containsKey(str))
                {
                    Pair p = new Pair(hashNodes.get(i).line,hashNodes.get(i).line);
                    ranges.put(str,p);
                }
                else
                {
                    Pair p = new Pair(ranges.get(str).getKey(),hashNodes.get(i).line);
                    ranges.put(str,p);
                }
            }
        }
    }
    void compute()
    {
        boolean flag = true;
        while(flag) {
            for(Integer i : hashNodes.keySet())
            {
                hashNodes.get(i).sout1 = new HashSet(hashNodes.get(i).sout);
                hashNodes.get(i).sin1 = new HashSet(hashNodes.get(i).sin);

                Set use = new HashSet(hashNodes.get(i).use);
                Set out_def = new HashSet(hashNodes.get(i).sout);
                out_def.remove(hashNodes.get(i).def);
                use.addAll(out_def);
                hashNodes.get(i).sin = use;
                ArrayList<Integer> successors = hashNodes.get(i).successors;
                for(int j = 0;j < successors.size();j++)
                {
                    hashNodes.get(i).sout.addAll(hashNodes.get(successors.get(j)).sin);
                }
            }
            flag = false;
            for(Integer i : hashNodes.keySet())
            {
                Set sout1 = hashNodes.get(i).sout1;
                Set sout = hashNodes.get(i).sout;
                if(!sout.containsAll(sout1) || !sout1.containsAll(sout))
                {
                    flag = true;
                }
                Set sin = hashNodes.get(i).sin;
                Set sin1 = hashNodes.get(i).sin1;
                if(!sin.containsAll(sin1) || !sin1.containsAll(sin))
                {
                    flag = true;
                }
            }
        }
    }
}
class compareIntervals implements Comparator<interval>{
    @Override
    public int compare(interval i1,interval i2)
    {
        return i1.start - i2.start;
    }
}
class reversecompareIntervals implements Comparator<interval>{
    @Override
    public int compare(interval i1,interval i2)
    {
        return i1.end - i2.end;
    }
}
class interval{
    public String temp;
    public int start;
    public int end;
    interval(String temp,int start,int end)
    {
        this.end = end;
        this.start = start;
        this.temp = temp;
    }
}

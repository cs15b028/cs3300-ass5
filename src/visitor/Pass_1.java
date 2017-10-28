package visitor;

import syntaxtree.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class Pass_1<R,A> extends GJDepthFirst <R,A>{

    public HashMap<Integer,StatementNode> hashNodes = new HashMap<>();
    public R visit(NodeList n, A argu) {
        R _ret=null;
        int _count=0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeListOptional n, A argu) {
        if ( n.present() ) {
            R _ret=null;
            int _count=0;
            ArrayList<R> list = new ArrayList<>();
            for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                list.add(e.nextElement().accept(this,argu));
                _count++;
            }
            return (R)list;
        }
        else
            return null;
    }

    public R visit(NodeOptional n, A argu) {
        if ( n.present() )
            return n.node.accept(this,argu);
        else
            return null;
    }

    public R visit(NodeSequence n, A argu) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
        }
        return _ret;
    }

    public R visit(NodeToken n, A argu) { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> "MAIN"
     * f1 -> StmtList()
     * f2 -> "END"
     * f3 -> ( Procedure() )*
     * f4 -> <EOF>
     */
    public R visit(Goal n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        ControlFlowGraph cfg = new ControlFlowGraph("main",hashNodes);
        data.hashGraphs.put("main",cfg);
        cfg.compute();
        cfg.computeRanges();
        cfg.RegisterAllocation();
        cfg.print();
        hashNodes = new HashMap<>();
        lineNumber = 1;
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> ( ( Label() )? Stmt() )*
     */
    public R visit(StmtList n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Label()
     * f1 -> "["
     * f2 -> IntegerLiteral()
     * f3 -> "]"
     * f4 -> StmtExp()
     */
    public R visit(Procedure n, A argu) {
        R _ret=null;
        String name = (String)n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        lineNumber = 1;
        ControlFlowGraph cfg = new ControlFlowGraph(name,hashNodes);
        cfg.compute();
        cfg.computeRanges();
        cfg.RegisterAllocation();
        cfg.print();
        data.hashGraphs.put(name,cfg);
        hashNodes = new HashMap<>();
        return _ret;
    }

    /**
     * f0 -> NoOpStmt()
     *       | ErrorStmt()
     *       | CJumpStmt()
     *       | JumpStmt()
     *       | HStoreStmt()
     *       | HLoadStmt()
     *       | MoveStmt()
     *       | PrintStmt()
     */
    public R visit(Stmt n, A argu) {
        R _ret=null;
        StatementNode node = (StatementNode)n.f0.accept(this,argu);
        hashNodes.put(node.line, node);
        lineNumber++;
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "NOOP"
     */
    public R visit(NoOpStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        ArrayList<Integer> successors = new ArrayList<>();
        successors.add(lineNumber + 1);
        StatementNode node = new StatementNode(successors,new ArrayList<>(),new ArrayList<>(),
                new ArrayList<>(),null,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "ERROR"
     */
    public R visit(ErrorStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "CJUMP"
     * f1 -> Temp()
     * f2 -> Label()
     */
    public R visit(CJumpStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String use1 = (String)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        ArrayList<Integer> successors = new ArrayList<>();
        successors.add(lineNumber + 1);
        successors.add(data.LabelLine.get(n.f2.f0.toString()));
        ArrayList<String> use = new ArrayList<>();
        use.add(use1);
        StatementNode node = new StatementNode(successors,new ArrayList<>(),new ArrayList<>(),
                use,null,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "JUMP"
     * f1 -> Label()
     */
    public R visit(JumpStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        ArrayList<Integer> successors = new ArrayList<>();
        successors.add(data.LabelLine.get(n.f1.f0.toString()));
        StatementNode node = new StatementNode(successors,new ArrayList<>(),new ArrayList<>(),
                new ArrayList<>(),null,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "HSTORE"
     * f1 -> Temp()
     * f2 -> IntegerLiteral()
     * f3 -> Temp()
     */
    public R visit(HStoreStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String def = (String)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String use1 = (String)n.f3.accept(this, argu);

        ArrayList<Integer> successors = new ArrayList<>();
        successors.add(lineNumber + 1);
        ArrayList<String> use = new ArrayList<>();
        use.add(use1);
        StatementNode node = new StatementNode(successors,new ArrayList<>(),new ArrayList<>(),
                use,def,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "HLOAD"
     * f1 -> Temp()
     * f2 -> Temp()
     * f3 -> IntegerLiteral()
     */
    public R visit(HLoadStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String def = (String)n.f1.accept(this, argu);
        String use1 = (String)n.f2.accept(this, argu);
        n.f3.accept(this, argu);

        ArrayList<Integer> successors = new ArrayList<>();
        successors.add(lineNumber + 1);
        ArrayList<String> use = new ArrayList<>();
        use.add(use1);
        StatementNode node = new StatementNode(successors,new ArrayList<>(),new ArrayList<>(),
                use,def,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "MOVE"
     * f1 -> Temp()
     * f2 -> Exp()
     */
    public R visit(MoveStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String def = (String)n.f1.accept(this, argu);
        ArrayList<String> use = (ArrayList<String>)n.f2.accept(this, argu);
        ArrayList<Integer> successor = new ArrayList<>();
        successor.add(lineNumber + 1);
        StatementNode node = new StatementNode(successor,new ArrayList<>(),new ArrayList<>(),use,def,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> "PRINT"
     * f1 -> SimpleExp()
     */
    public R visit(PrintStmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String use1 = (String)n.f1.accept(this, argu);
        ArrayList<String> use = new ArrayList<>();
        if(use1 != null)
            use.add(use1);
        ArrayList<Integer> successor = new ArrayList<>();
        successor.add(lineNumber + 1);
        StatementNode node = new StatementNode(successor,new ArrayList<>(),new ArrayList<>(),use,null,lineNumber);
        return (R)node;
    }

    /**
     * f0 -> Call()
     *       | HAllocate()
     *       | BinOp()
     *       | SimpleExp()
     */
    public R visit(Exp n, A argu) {
        R _ret=null;
        if(n.f0.which != 3)
        {
            return n.f0.accept(this, argu);
        }
        else
        {
            String use1 = (String)n.f0.accept(this,argu);
            ArrayList<String> use = new ArrayList<>();
            if(use1 != null)
            {
                use.add(use1);
            }
            return (R)use;
        }
    }

    /**
     * f0 -> "BEGIN"
     * f1 -> StmtList()
     * f2 -> "RETURN"
     * f3 -> SimpleExp()
     * f4 -> "END"
     */
    public R visit(StmtExp n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String use1 = (String)n.f3.accept(this, argu);
        ArrayList<String> use = new ArrayList<>();
        if(use1 != null)
            use.add(use1);
        StatementNode node = new StatementNode(new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),use,null,lineNumber);
        lineNumber++;
        hashNodes.put(node.line,node);
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "CALL"
     * f1 -> SimpleExp()
     * f2 -> "("
     * f3 -> ( Temp() )*
     * f4 -> ")"
     */
    public R visit(Call n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String methodname = (String)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        ArrayList<String> args = (ArrayList<String>)n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        if(methodname != null)
            args.add(0,methodname);
        /* returns arraylist of used variables */
        return (R)args;
    }

    /**
     * f0 -> "HALLOCATE"
     * f1 -> SimpleExp()
     */
    public R visit(HAllocate n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String use1 = (String)n.f1.accept(this, argu);
        ArrayList<String> use = new ArrayList<>();
        if(use1 != null)
            use.add(use1);
        return (R)use;
    }

    /**
     * f0 -> Operator()
     * f1 -> Temp()
     * f2 -> SimpleExp()
     */
    public R visit(BinOp n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        String use1 = (String)n.f1.accept(this, argu);
        String use2 = (String)n.f2.accept(this, argu);

        ArrayList<String> use = new ArrayList<>();
        use.add(use1);
        if(use2 != null)
            use.add(use2);
        return (R)use;
    }

    /**
     * f0 -> "LE"
     *       | "NE"
     *       | "PLUS"
     *       | "MINUS"
     *       | "TIMES"
     *       | "DIV"
     */
    public R visit(Operator n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Temp()
     *       | IntegerLiteral()
     *       | Label()
     */
    public R visit(SimpleExp n, A argu) {
        R _ret=null;
        if(n.f0.which == 1 || n.f0.which == 2)
            return null;
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "TEMP"
     * f1 -> IntegerLiteral()
     */
    public R visit(Temp n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return (R)("TEMP " + n.f1.f0.toString());
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public R visit(IntegerLiteral n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R)n.f0.toString();
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public R visit(Label n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return (R)n.f0.toString();
    }

}

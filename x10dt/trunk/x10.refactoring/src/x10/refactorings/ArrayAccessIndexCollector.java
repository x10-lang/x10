/**
 * 
 */
package x10.refactorings;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ext.x10.ast.X10Call;
import polyglot.types.ClassType;
import polyglot.types.Type;
import polyglot.visit.NodeVisitor;

public class ArrayAccessIndexCollector extends NodeVisitor {
    private List<Expr> fResult;

    public ArrayAccessIndexCollector() {
        fResult = new ArrayList<Expr>();
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        // PORT1.7 Array accesses now look like ordinary method calls
//      if (n instanceof X10ArrayAccess1)
//          fResult.add(((X10ArrayAccess1) n).index());
        if (n instanceof X10Call) {
            X10Call call = (X10Call) n;
            Receiver rcvr = call.target();
            Type rcvrType = rcvr.type();
            if (rcvrType instanceof ClassType) {
                ClassType rcvrClassType = (ClassType) rcvrType;
                if (rcvrClassType.name().toString().equals("")) {
                    
                }
            }
        }
        else if (n instanceof ArrayAccess)
            fResult.add(((ArrayAccess) n).index());
        return n;
    }

    public List<Expr> getResult() {
        return fResult;
    }
}

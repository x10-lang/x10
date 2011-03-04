package x10.visit;

import polyglot.ast.FlagsNode_c;
import polyglot.ast.Node;
import polyglot.ast.LocalDecl;
import polyglot.ast.FieldDecl;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import x10.ast.AnnotationNode_c;
import x10.ast.X10Formal_c;

public class PositionInvariantChecker extends NodeVisitor
{
    private final Job job;
    private final String previousGoalName;

    public PositionInvariantChecker(Job job, String previousName) {
        this.job = job;
        previousGoalName = previousName;
    }

    public Node visitEdgeNoOverride(Node parent, Node n) {
        if (Report.should_report("PositionInvariantChecker", 2))
            Report.report(2, "Checking invariants for: " + n);
        String m = checkInvariants(parent, n);
        if (m!=null) {
        	String msg;
        	if (parent != null) {
        		msg = "After goal "+previousGoalName+": "+
        			m+("!")+
        			(" parentPos=")+(parent.position())+
                    (" nPos=")+(n.position())+
                    (" parent=")+(parent)+
                    (" n=")+(n).toString();
        	} else {
        		msg = "After goal "+previousGoalName+": "+
    			m+("!")+
                (" nPos=")+(n.position())+
                (" n=")+(n).toString();
        	}
            job.compiler().errorQueue().enqueue(ErrorInfo.INVARIANT_VIOLATION_KIND,msg,n.position());
        } else {
            n.del().visitChildren(this); // if there is an error, I don't recurse to the children
        }
        return n;
    }
    private String checkInvariants(Node parent, Node n) {
        if (parent == null) return null;
        if (n == null) return "Cannot visit null";
        Position pPos = parent.position();
        Position nPos = n.position();
        if ((pPos == null) || (nPos == null))
            return "Positions must never be null";
        if (nPos.isCompilerGenerated()) return null;
        if (pPos.isCompilerGenerated()) {
            //myAssert(nPos.isCompilerGenerated()) : "If your parent is COMPILER_GENERATED, then you must be COMPILER_GENERATED";
            // todo: take from some ancestor
            return null;
        }
        if (!(equals(pPos.file(), nPos.file())))
            return "Positions must have the same file";
        if (!(equals(pPos.path(), nPos.path())))
            return "Positions must have the same path";

        /*
        todo: remove this.

        left to fix:
        Desugaring problems:
        1) AmbMacroTypeNode_c (Macro expansion)
        2) val p(i,j) = ...
        3) public var x,y;

        E.g., after "ReassembleAST" stage in Array.x10:
        original source is:  Point(reg.rank)
        parent=x10.array.Point{self.rank==reg.rank}
        n={[x10.array.Point.self.rank == reg.rank]}
        parentPos=C:\cygwin\home\Yoav\intellij\sourceforge\x10.runtime\src-x10\x10\array\Array.x10:132,12-26
        nPos=C:\cygwin\home\Yoav\intellij\sourceforge\x10.runtime\src-x10\x10\lang\_.x10:73,45-58

        A simple example that causes it:
        public class Hello {
          public val i=3, j=4;
          def m() { for (var i:Int=1,j:Int=2; i<10; i++); }
        }
         */
        if (parent instanceof LocalDecl || parent instanceof FieldDecl ||
                parent instanceof X10Formal_c) { // e.g., "for (val p(i,j): Point(2) in r) {"
            if (n instanceof FlagsNode_c) return null;
            if (n instanceof AnnotationNode_c) return null;
            //if (n instanceof ClosureCall_c) return; //val q1(m,n) = [0,1] as Point;
        }


        String s;
        if ((s=checkNumbers(pPos.line(), nPos.line(), true))!=null) return s;
        if ((s=checkNumbers(pPos.endLine(), nPos.endLine(), false))!=null) return s;
        if ((s=checkNumbers(pPos.offset(), nPos.offset(), true))!=null) return s;
        if ((s=checkNumbers(pPos.endOffset(), nPos.endOffset(), false))!=null) return s;
        if (pPos.line() == nPos.line())
            if ((s=checkNumbers(pPos.column(), nPos.column(), true))!=null) return s;
        if (pPos.endLine() == nPos.endLine())
            if ((s=checkNumbers(pPos.endColumn(), nPos.endColumn(), false))!=null) return s;
        return null;
    }

    public String checkNumbers(int pNum, int nNum, boolean isBeginning) {
        if (nNum<0 || pNum<0)
            return "We have unknown numbers";
        if (isBeginning ? pNum>nNum : pNum<nNum)
            return "Illegal containment of positions";
        return null;
    }

    public static boolean equals(Object o1, Object o2) {
        return (o1 == o2) || ((o1 != null) && (o2 != null) && (o1.equals(o2)));
    }
}

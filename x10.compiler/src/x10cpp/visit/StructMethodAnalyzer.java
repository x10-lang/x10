package x10cpp.visit;

import polyglot.ast.Block_c;
import polyglot.ast.BooleanLit;
import polyglot.ast.Branch;
import polyglot.ast.Conditional;
import polyglot.ast.Eval_c;
import polyglot.ast.FlagsNode_c;
import polyglot.ast.FloatLit;
import polyglot.ast.Id_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.NumLit;
import polyglot.frontend.Job;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.X10Call_c;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.X10ConstructorCall_c;
import x10.ast.X10ConstructorDecl_c;
import x10.ast.X10FieldAssign_c;
import x10.ast.X10Formal_c;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10NodeFactory;
import x10.ast.X10Special_c;
import x10.types.X10ClassType;
import x10.types.X10MethodInstance;
import x10.types.X10TypeSystem;
import x10.util.Synthesizer;

public class StructMethodAnalyzer extends ContextVisitor {
    private final X10TypeSystem xts;
    private final X10ClassType myContainer;
    
    // GACK:  This is ridiculous.  This should be a simple boolean field,
    //        but polyglot insists on doing copying/cloning under the covers
    //        and a simple instance field which is set to false deep in leaveCall
    //        still remains true up at the "top" of the visit.
    boolean[] canGoInHeaderStream = new boolean[] { true };
    
    public StructMethodAnalyzer(Job job, TypeSystem ts, NodeFactory nf, X10ClassType container) {
        super(job, ts, nf);
        myContainer = container;
        xts = (X10TypeSystem) ts;
    }
    
    public boolean canGoInHeaderStream() { return canGoInHeaderStream[0]; }

    public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
        // Boilerplate to get to the body of the constructor/method
        if (n instanceof X10ConstructorDecl_c || n instanceof X10MethodDecl_c ||
                n instanceof Block_c || n instanceof Eval_c) {
            return n;
        }
        
        // Wrapper expressions/statements that by themselves are not a problem
        if (n instanceof Conditional || n instanceof Branch) {
            return n;
        }
        
        // Trivial nodes that will never by themselves prevent us from putting the body in the struct method class.
        if (n instanceof FlagsNode_c || n instanceof Id_c || 
                n instanceof X10Formal_c || n instanceof X10Local_c || 
                n instanceof X10CanonicalTypeNode_c || n instanceof X10Special_c) {
            return n;
        }

        // Literals of built-in struct types and of null are fine.
        if (n instanceof BooleanLit || n instanceof FloatLit || n instanceof NullLit || n instanceof NumLit) {
            return n;
        }

        // Constructor call.
        // We can allow calls to the same class as we are analyzing and to the trivial x10.lang.Struct 
        // constructor.  Any other constructor call will require us to set canBeInlined to false.
        if (n instanceof X10ConstructorCall_c) {
            StructType container = ((X10ConstructorCall_c)n).constructorInstance().container();
            if (!(container.typeEquals(xts.Struct(), context) || container.typeEquals(myContainer, context))) {
                canGoInHeaderStream[0] = false;
            }
            return n;
        }
        
        // Only allow field assignments to fields of the current container; since those decls must be available.
        if (n instanceof X10FieldAssign_c) {
            X10FieldAssign_c fa = (X10FieldAssign_c)n;
            if (!fa.fieldInstance().container().typeEquals(myContainer, context)) {
                canGoInHeaderStream[0] = false;
                System.out.println("field assignment to another type");
            }
            return n;
        }
        
        // Only allow calls to methods of the current container and to methods of built-in numeric types.
        if (n instanceof X10Call_c) {
            StructType methodType = ((X10Call_c)n).methodInstance().container();
            if (!(methodType.typeEquals(myContainer, context) ||
                    xts.typeBaseEquals(xts.Boolean(), methodType, context) || 
                    xts.typeBaseEquals(xts.Byte(), methodType, context) || 
                    xts.typeBaseEquals(xts.UByte(), methodType, context) || 
                    xts.typeBaseEquals(xts.Char(), methodType, context) || 
                    xts.typeBaseEquals(xts.Short(), methodType, context) || 
                    xts.typeBaseEquals(xts.UShort(), methodType, context) || 
                    xts.typeBaseEquals(xts.Int(), methodType, context) || 
                    xts.typeBaseEquals(xts.UInt(), methodType, context) || 
                    xts.typeBaseEquals(xts.Float(), methodType, context) || 
                    xts.typeBaseEquals(xts.Long(), methodType, context) || 
                    xts.typeBaseEquals(xts.ULong(), methodType, context) || 
                    xts.typeBaseEquals(xts.Double(), methodType, context))) {
                canGoInHeaderStream[0] = false;
            }
            return n;    
        }
        
        
        // Discovering any other AST in the body means that we don't want to put the method in the header file.
        canGoInHeaderStream[0] = false;
        
        return n;
    }
}

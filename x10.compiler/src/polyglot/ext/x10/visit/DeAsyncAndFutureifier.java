package polyglot.ext.x10.visit;

import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ext.x10.RuntimeConstants;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.Future;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.ParsedClassType;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that transforms incoming code that contains X10 async and
 * future calls to the corresponding Java code that use the X10 Runtime
 * facilities instead.  Note that at this point a place is treated as
 * an object!  Hence what was "<P> or P@T" must have been transformed
 * into arguments of type 'Place' at this point.  Similarly, the 
 * token 'here' should be transformed into 'Runtime.here()'.
 * <p>
 * 
 * Also the code assumes that all variables accessed from within the
 * asyncs/futures are 'final' in the Java code (javac is supposed to
 * produce the appropriate closures).
 * 
 * @author Christian Grothoff, based on code from Filip Pizlo
 */
public class DeAsyncAndFutureifier 
    extends X10Transmogrifier 
    implements RuntimeConstants {
    
    public DeAsyncAndFutureifier(TypeSystem ts,
    	    	    	    	 NodeFactory nf) {
	super(ts,nf);
    }
    
    public void visit(Async async) {
        ParsedClassType container = ts.createClassType();
        container.position(Position.COMPILER_GENERATED);
        container.kind(ClassType.ANONYMOUS);
        // container.outer(FIXME); // we may want to (have to?) set the outer class here to the current class...
        container.superType(ts.getActivityType());

        // generate 'new Activity() { public void run() { ... } };'
        MethodInstance run_type = ts.methodInstance(Position.COMPILER_GENERATED, 
                                                    container,
                                                    Flags.PUBLIC.Final(),
                                                    ts.Void(),
                                                    ACTIVITY_RUN, 
                                                    new LinkedList(), 
                                                    new LinkedList());
        List members = new LinkedList();
        members.add(nf.MethodDecl(CG,
                                  Flags.PUBLIC.Final(),
                                  nf.CanonicalTypeNode(CG, ts.Void()),
                                  ACTIVITY_RUN, 
                                  new LinkedList(),
                                  new LinkedList(),
                                  nf.Block(CG, async.body())).methodInstance(run_type));
        New nw = nf.New(CG,
                        nf.CanonicalTypeNode(CG, ts.getActivityType()),
                        new LinkedList(),
                        nf.ClassBody(CG,members));
        ConstructorInstance ci = ts.defaultConstructor(CG, 
                                                       container);
        container.addConstructor(ci);
        Expr result = nw.anonType(container).constructorInstance(ci).type(container);
   
        // generate 'place.runAsync(new container())';
        Call call = nf.Call(CG,
                            async.place(),
                            RuntimeConstants.PLACE_RUN_ACTIVATION_ASYNC,
                            result);
        LinkedList argTypes
            = new LinkedList();
        argTypes.add(ts.getActivityType());
        MethodInstance mi
            = ts.methodInstance(CG,
                                ts.getActivityType(),
                                Flags.PUBLIC.Final(),
                                ts.Void(),
                                RuntimeConstants.PLACE_RUN_ACTIVATION_ASYNC,
                                argTypes,
                                new LinkedList());
        this.next = nf.Eval(CG,
                            call.methodInstance(mi).type(ts.Void()));
    }

    public void visit(Future future) {
        ParsedClassType container = ts.createClassType();
        container.position(Position.COMPILER_GENERATED);
        container.kind(ClassType.ANONYMOUS);
        // container.outer(FIXME); // we may want to (have to?) set the outer class here to the current class...
        container.superType(ts.getFutureActivityType());

        // generate 'new Activity.FutureActivity() { private Object x10_result_; public void run()
        //           { x10_result_ = body; } public void getResult() { return x10_result_; } };'
        
        // add 'private Object x10_result_' field.
        Field myField = nf.Field(Position.COMPILER_GENERATED,
                                 nf.AmbReceiver(Position.COMPILER_GENERATED, "this"),
                                 "x10_result_");
        container.addField(myField.fieldInstance().type(ts.Object()).flags(Flags.PRIVATE));
        FieldAssign assign 
            = nf.FieldAssign(Position.COMPILER_GENERATED, 
                             myField, 
                             polyglot.ast.Assign.ASSIGN, 
                             future.body());
        
        List members = new LinkedList();
        // create method "run() { this.x10_result_ = future.body(); }" 
        MethodInstance run_type = ts.methodInstance(Position.COMPILER_GENERATED, 
                                                    container,
                                                    Flags.PUBLIC.Final(),
                                                    ts.Void(),
                                                    ACTIVITY_RUN, 
                                                    new LinkedList(), 
                                                    new LinkedList());
        Stmt assignStmt = nf.Eval(Position.COMPILER_GENERATED,
                                  assign.type(ts.Object()));        
        members.add(nf.MethodDecl(CG,
                                  Flags.PUBLIC.Final(),
                                  nf.CanonicalTypeNode(CG, ts.Void()),
                                  ACTIVITY_RUN, 
                                  new LinkedList(),
                                  new LinkedList(),
                                  nf.Block(Position.COMPILER_GENERATED,
                                           assignStmt)).methodInstance(run_type));

        // create method "getResult() { return this.x10_result_; }" 
        MethodInstance get_type = ts.methodInstance(Position.COMPILER_GENERATED, 
                                                    container,
                                                    Flags.PUBLIC.Final(),
                                                    ts.Object(),
                                                    ACTIVITY_GET_RESULT, 
                                                    new LinkedList(), 
                                                    new LinkedList());

        Return returnExpr
            = nf.Return(Position.COMPILER_GENERATED,
                        myField.type(ts.Object()));
        members.add(nf.MethodDecl(CG,
                                  Flags.PUBLIC.Final(),
                                  nf.CanonicalTypeNode(CG, ts.Object()),
                                  ACTIVITY_GET_RESULT, 
                                  new LinkedList(),
                                  new LinkedList(),
                                  nf.Block(Position.COMPILER_GENERATED,
                                           returnExpr)).methodInstance(get_type));

        // create statement 'new FutureActivity() { }'
        New nw = nf.New(CG,
                        nf.CanonicalTypeNode(CG, ts.getFutureActivityType()),
                        new LinkedList(), 
                        nf.ClassBody(CG, members));
        ConstructorInstance ci = ts.defaultConstructor(CG, 
                                                       container);
        container.addConstructor(ci);
        Expr result = nw.anonType(container).constructorInstance(ci).type(container);
   
        // generate expression 'place.runFuture(result)';
        Call call = nf.Call(CG,
                            future.place(),
                            RuntimeConstants.PLACE_RUN_ACTIVATION_ASYNC,
                            result);
        LinkedList argTypes
            = new LinkedList();
        argTypes.add(ts.getActivityType());
        MethodInstance mi
            = ts.methodInstance(CG,
                                ts.getActivityType(),
                                Flags.PUBLIC.Final(),
                                ts.getFutureType(),
                                RuntimeConstants.PLACE_RUN_ACTIVATION_FUTURE,
                                argTypes,
                                new LinkedList());
        this.next = call.methodInstance(mi).type(ts.getFutureType());
    }


    /**
     * Return value from leave, set by the 'visit' methods (replacement tree).
     */
    private Node next;

    public Node leave(Node old,Node n,NodeVisitor v) {
        if (n instanceof Async)
            visit((Async)n);
        else if (n instanceof Future)
            visit((Future)n);
        return next;
    }
} // end of DeAsyncAndFutureifier


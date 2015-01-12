/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */


package x10.compiler.ws.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.For;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; 
import x10.util.CollectionFactory;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.Finish;
import x10.ast.PlacedClosure;
import x10.ast.StmtSeq;
import x10.ast.When;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl;
import x10.compiler.ws.WSTransformState;
import x10.types.ClosureDef;
import x10.types.MethodInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10Def;
import x10.types.X10MethodDef;
import polyglot.types.Context;
import polyglot.types.TypeSystem;

/**
 * @author Haichuan
 * 
 * This class contains some utility functions, such as forming the standard name
 *
 */
public class WSUtil {
    
    //------------- Section I: Debug & Log Info Mation -------------
    static boolean turnOffAllDebugMsg = false;
    static boolean turnOffAllInfoMsg = false;    
    static public void debug(String msg, Node node){
        if(!turnOffAllDebugMsg){
            System.out.println("[WS_DEBUG] " + msg);
            node.prettyPrint(System.out);
            System.out.println();
        }
    }
    
    static public void debug(String msg){
        if(!turnOffAllDebugMsg){
            System.out.println("[WS_DEBUG] " + msg);
        }
    }
    
    static public void info(String msg){
        if(!turnOffAllInfoMsg){
            System.out.println("[WS_INFO] " + msg);
        }
    }
    
    static public void err(String msg, Node n) throws SemanticException{
        System.err.println("[WS_ERR]" + msg);
        if(n != null){
            System.err.println("        ");
            n.prettyPrint(System.err);
            System.err.println();
            throw new SemanticException(msg, n.position());
        }
        else{
            throw new SemanticException(msg);
        }
    }
    
    //------------------ Section II: Naming Management
    
    static Map<ClassType, Map<String, Integer>> container2MethodNameMap;
    //              classType         methodName, number
    static Map<String, Integer> dupNameCountMap;
    
    
    private static String getMethodName(MethodDef methodDef){
        return methodDef.name().toString();
    }
    
    /**
     * Check whether the current name is used, if used, add the count number to it.
     * @param name
     * @return
     */
    public static String checkDupName(String name){
        if(dupNameCountMap == null){
            dupNameCountMap = new HashMap<String, Integer>();
        }
        
        if(dupNameCountMap.containsKey(name)){
            //the name is used,
            int count = dupNameCountMap.get(name);
            String newName = name + count;
            dupNameCountMap.put(name, count+1);
            dupNameCountMap.put(newName, 1); //in the newname is not used by user
            return newName;
        }
        else{
            dupNameCountMap.put(name, 1);
            return name;
        }
    }
    
    /**
     * @param f The dividable For
     * @return the name for the divide-and-conquer method
     */
    public static String getDividableForMethodName(For f){
        return "_$dcFor"; //divide-and-conquer for
    }
    
    /**
     * Form the closure's name by line & column number. Should be identical for a inner class
     * @param closure
     * @return
     */
    public static String getClosureBodyClassName(Closure closure){
    	Position pos = closure.position();
    	String posStr =  "L" + pos.line() + "_" + pos.column();   	
        String tempName = "_$Closure_" + posStr;
        return tempName;
    }
    
    public static String getMethodBodyClassName(MethodDef methodDef){
        if(container2MethodNameMap == null){
            container2MethodNameMap = CollectionFactory.newHashMap();
        }
        String tempName = "_$" + getMethodName(methodDef);
        ClassType classType = (ClassType) methodDef.container().get();
       
        Map<String, Integer> nameNumberMap = container2MethodNameMap.get(classType);
        if(nameNumberMap == null){
            nameNumberMap = CollectionFactory.newHashMap();
            nameNumberMap.put(tempName, 1);
            container2MethodNameMap.put(classType, nameNumberMap);
            return tempName;
        }
        
        if(nameNumberMap.containsKey(tempName)){
            int num = nameNumberMap.get(tempName);
            nameNumberMap.put(tempName, num + 1);
            return tempName+"$"+num;            
        }
        else{
            nameNumberMap.put(tempName, 1);
            return tempName;
        }

    }
    
    public static String getMethodFastPathName(MethodDef methodDef){
        return getMethodName(methodDef) + "_F";
    }

    public static String getMethodSlowPathName(MethodDef methodDef){
        return getMethodName(methodDef) + "_S";
    }

    public static String getAsyncStmtClassName(String parentName){
        return parentName + "A";
    }
    
    public static String getRemoteRemoteClassName(String parentName){
        return parentName + "R";
    }
    
    public static String getFinishStmtClassName(String parentName){
        return parentName + "F";
    }
    
    public static String getBlockFrameClassName(String parentName){
        return parentName + "B";
    }

    public static String getExceptionFrameClassName(String parentName){
        return parentName + "E";
    }
    
    public static String getIFBlockClassName(String parentName, boolean condition){
        return parentName + "IF" +
               (condition ? "T" : "F" );
    }
    
    public static String getLoopClassName(String parentName){
        return parentName + "L";
    }
    
    public static String getSwitchClassName(String parentName){
        return parentName + "S";
    }

    public static String getWhenClassName(String parentName){
        return parentName + "W";
    }

    /**
     * Recursively scan the block of statements to find all local declaration
     * @param block
     * @return
     */
    public static List<LocalDecl> recursiveScanForLocals(Block block){
        ArrayList<LocalDecl> locals = new ArrayList<LocalDecl>();
        LocalDeclVisitor ldVisitor = new LocalDeclVisitor(locals);
        block.visit(ldVisitor);
        return locals;
    }
    
    
    /**
     * Check the x10def's annotation contains "WS" or not
     * @param def
     * @return true if the def contains "WS" annotation
     */
    protected static boolean isWSTarget(X10Def def) {
        try {
            Type t = def.typeSystem().systemResolver().findOne(QName.make("x10.compiler.WS"));
            return !def.annotationsMatching(t).isEmpty();
        } catch (SemanticException e) {
            return false;
        }
    }
    
    /**
     * Check whether the code node contains concurrent construct or not
     * Concurrent construct includes:
     *     async/finish/etc.
     * NOTE: if it contains a closure/inner class /etc, and the closure has some concurrent, 
     * the current node will not be visited as a concurrent node
     * @param node
     * @return
     */
    public static boolean containsConcurrentConstruct(Node node){
        if(node == null){
            return false; //no data
        }
        
        if (node instanceof X10MethodDecl && isWSTarget(((X10MethodDecl) node).methodDef())) {
            return true;
        }
        
        if(node instanceof Closure){ //need remove closure's shell, other wise the visitor will not work
            node = ((Closure)node).body();
        }
        
        ConcurrentConstructBlockVisitor ccbv = new ConcurrentConstructBlockVisitor();
        node.visit(ccbv);
        return ccbv.isConcurrent();
    }
    
    /**
     * Check whether one code node is complex code block or not.
     * A complex code block is one code block containing async/finish/method invocation
     * @param block
     * @return
     */
    public static boolean isComplexCodeNode(Node node, WSTransformState wsState){
        
        if(node == null){
            return false; //blank node
        }
        
        if(containsConcurrentConstruct(node)){
            return true; //if it contains async/finish/etc.
        }
        
        //check whether it contains method call to other target method
        if(node instanceof Closure){ //need remove closure's shell, other wise the visitor will not work
            node = ((Closure)node).body();
        }
        
        MethodCallVisitor cbv = new MethodCallVisitor(wsState); 
        node.visit(cbv);
        
        return cbv.isComplex();
    }
    
    
    /**
     * Calculate how many concurrent calls invoked during the input code node
     * @param node the node to search
     * @param wsState the WorkStealing state with concurrent call maps
     * @return the number of concurrent call invocations
     */
    public static int calcConcurrentCallNums(Node node, WSTransformState wsState){
        if(node == null){
            return 0; //blank node
        }
        
        if(node instanceof Closure){ //need remove closure's shell, other wise the visitor will not work
            node = ((Closure)node).body();
        }
        
        //check whether it contains method call to other target method
        MethodCallVisitor cbv = new MethodCallVisitor(wsState); 
        node.visit(cbv);
        
        return cbv.getConcurrentCallNums();
    }
    
    /**
     * Scan the block of statements, and check whether it contains a return statement
     * @param node
     * @return
     */
    public static boolean hasReturnStatement(Node node){
        ScanReturnStatementVisitor srsv = new ScanReturnStatementVisitor();
        
        if(node instanceof Closure){ //need remove closure's shell, other wise the visitor will not work
            node = ((Closure)node).body();
        }
        
        node.visit(srsv);
        return srsv.isHasReturn();
    }

    /**
     * Scan the block of statements to find all local declaration
     * @param block
     * @return
     */
    public static List<LocalDecl> scanForLocals(Block block){
        ArrayList<LocalDecl> locals = new ArrayList<LocalDecl>();
        for (Stmt s : block.statements()){
            if (s instanceof LocalDecl) {
                locals.add((LocalDecl) s);
            }
        }
        return locals;
    }
    
    
    /**
     * Find all callees from an input node, e.g. methoddecl
     * @param node
     * @return all callees' methoddef
     */
    public static List<ProcedureDef> scanForCallees(Node node){
        MethodCallFindingVisitor mcfv = new MethodCallFindingVisitor();
        
        if(node instanceof Closure){ //need remove closure's shell, other wise the visitor will not work
            node = ((Closure)node).body();
        }
        node.visit(mcfv);
        return mcfv.getCallees();
    }
    
    
    /**
     * Identify whether one statement is a local assign
     * If it is a local assign, it may need move() in async frame
     * @param s
     * @return the local
     */
    static public LocalAssign identifyLocalAssign(Stmt s){
        if(s == null || !(s instanceof Eval)){
            return null;
        }
        Expr e = ((Eval)s).expr();
        if(e instanceof LocalAssign){
            return ((LocalAssign)e);
        }

        return null;
    }
    
    /**
     * Identify receiver = aCall() statement
     * @param s
     * @return Pair<Assign, Call> pair. If null, not such an expression
     */
    static public Pair<Assign, Call> identifyAssignByCall(Stmt s){
        if(s == null){
            return null;
        }
        Pair<Assign, Call> result = null;
        if(s instanceof Eval){
            
            Expr expr = ((Eval)s).expr();
            if(expr instanceof Assign){
                Assign assignExpr = (Assign)expr;
                Expr rightExpr = assignExpr.right();
                if(rightExpr instanceof Call){
                    result = new Pair<Assign, Call>(assignExpr, (Call)rightExpr);
                }
            }
        }
        return result;
    }
    
    
    /**
     * Transform original call's def to ws call's def
     * e.g. foo(abc:int) -> foo_F(w:Worker, up:Frame, abc:int);
     * @param methodDef original method def
     * @param wts WSTransformState
     * @return
     */
    static public X10MethodDef createWSCallMethodDef(MethodDef methodDef, TypeSystem ts) {
    	
        X10ClassType containerClassType = (X10ClassType) methodDef.container().get();
        X10ClassDef containerClassDef = containerClassType.x10Def();
        
        List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>();
        formalTypes.add(Types.ref(ts.Worker()));
        formalTypes.add(Types.ref(ts.Frame()));
        formalTypes.add(Types.ref(ts.FinishFrame()));
        for (Ref<? extends Type> f : methodDef.formalTypes()) {
            formalTypes.add(f); //all formals are added in
        }
        
        TypeSystem xts = methodDef.typeSystem();
        
        X10MethodDef mDef = (X10MethodDef) xts.methodDef(methodDef.position(), methodDef.errorPosition(),
                Types.ref(containerClassDef.asType()),                
                methodDef.flags(), 
                methodDef.returnType(), 
                Name.make(WSUtil.getMethodFastPathName(methodDef)), 
                formalTypes, Collections.<Ref<? extends Type>>emptyList());
        mDef.setTypeParameters(methodDef.typeParameters());

        List<LocalDef> formalNames = new ArrayList<LocalDef>();
        Name workerName = Name.make("worker");
        LocalDef workerLDef = methodDef.typeSystem().localDef(methodDef.position(), Flags.FINAL, Types.ref(xts.Worker()), workerName);
        Name upName = Name.make("up");
        LocalDef upLDef = methodDef.typeSystem().localDef(methodDef.position(), Flags.FINAL, Types.ref(ts.Frame()), upName);
        Name ffName = Name.make("ff");
        LocalDef ffLDef = methodDef.typeSystem().localDef(methodDef.position(), Flags.FINAL, Types.ref(ts.FinishFrame()), ffName);
        formalNames.add(workerLDef);
        formalNames.add(upLDef);
        formalNames.add(ffLDef);
        for (LocalDef f : methodDef.formalNames()) {
            formalNames.add(f); //all formals are added in
        }
        mDef.setFormalNames(formalNames);
        return mDef;
    }
    
    static public MethodInstance createWSMethodInstance(MethodInstance mi, TypeSystem ts) {
    	X10MethodDef mDef = createWSCallMethodDef(mi.def(), ts);
    	MethodInstance m = mDef.asInstance();
    	m = (MethodInstance) m.container(mi.container());

    	List<Type> formalTypes = new ArrayList<Type>();
        formalTypes.add((ts.Worker()));
        formalTypes.add((ts.Frame()));
        formalTypes.add((ts.FinishFrame()));
        for (Type f : mi.formalTypes()) {
            formalTypes.add(f); //all formals are added in
        }
        m = m.formalTypes(formalTypes);
        
        List<LocalInstance> formalNames = new ArrayList<LocalInstance>();
        formalNames.add(ts.localDef(mi.position(), Flags.FINAL, Types.ref(ts.Worker()), Name.make("worker")).asInstance());
        formalNames.add(ts.localDef(mi.position(), Flags.FINAL, Types.ref(ts.Frame()), Name.make("up")).asInstance());
        formalNames.add(ts.localDef(mi.position(), Flags.FINAL, Types.ref(ts.FinishFrame()), Name.make("ff")).asInstance());
        for (LocalInstance f : mi.formalNames()) {
            formalNames.add(f); //all formals are added in
        }
        m = (MethodInstance) m.formalNames(formalNames);
        
        m = (MethodInstance) m.typeParameters(mi.typeParameters());
        
        return m;
    }

    /**
     * 
     * Replace original call, e.g. fib(n) with generated WS call
     *  --> fib_fast(worker, this, this, 1, n);
     * The newArgs are worker/this/this/1
     * @param xnf node factory
     * @param aCall Original call
     * @param methodDef the new methodDef
     * @param newArgTypes additional arguments's types
     * @param newArgs additional arguments, including worker/frame/upframe
     * @return new method call
     */
    public static X10Call replaceMethodCallWithWSMethodCall(NodeFactory xnf, X10Call aCall, MethodInstance mi, 
                                                  List<Expr> newArgs) {
    	
        ArrayList<Expr> args = new ArrayList<Expr>(newArgs);
        args.addAll(aCall.arguments());
        
        //for the name
        Name name = mi.name(); //new name
        
        //build new call
        aCall = (X10Call) aCall.methodInstance(mi);
        aCall = (X10Call) aCall.name(xnf.Id(aCall.name().position(), name));
        aCall = (X10Call) aCall.arguments(args);
        return aCall;
    }
    
    
    /**
     * Identify receiver = async aCall() statement
     * In the WS step, the code is transformed into a future.
     * The assign is the assign, but the call is just the return part of the future
     * @param s
     * @return Pair<Assign, Call> pair. If null, not such an expression
     * /
    static public Pair<Assign, Call> identifyAssignByAsyncCall(Stmt s, Context context){
        TypeSystem xts = (TypeSystem) context.typeSystem();
        Pair<Assign, Call> result = null;
        if(s instanceof Eval){
            
            Expr expr = ((Eval)s).expr();
            if(expr instanceof Assign){
                Assign assignExpr = (Assign)expr;
                Expr rightExpr = assignExpr.right();
                if(rightExpr instanceof Call){
                    Call aCall = (Call)rightExpr;
                    Name forceName = Name.make("force");
                    Type futureType = xts.load("x10.util.Future");
                    MethodInstance futureMI = null;
                    try {
                        futureMI = xts.findMethod(futureType, xts.MethodMatcher(futureType, forceName, Collections
                                .<Type> emptyList(), Collections.<Type> emptyList(), context));
                        MethodDef futureMD = futureMI.def();
                        if(futureMD.equals(aCall.methodInstance().def())){
                            //this call is a async, now we need find the return call
                           Receiver r = aCall.target();
                           Stmt s1 = ((Future)r).body().statements().get(0);
                           //s1 should be return
                           Call bCall = (Call)((Return)s1).expr();
                           result = new Pair<Assign, Call>(assignExpr, bCall);
                            
                        }

                    } catch (SemanticException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
        return result;
    }
    */
    
    
    /**
     * Used to transform a seq stmt into a block
     * If the input is not a StmtSeq, just return
     * @param xnf
     * @param s
     * @return only one stmt, maybe a block
     */
    static public Stmt seqStmtsToOneStmt(NodeFactory xnf, Stmt s){
        if(s instanceof StmtSeq){
            return xnf.Block(s.position(),((StmtSeq)s).statements());
        }
        else{
            return s;
        }
    }
    
    /**
     * Used to transform a stmt into a Block
     * If the input is not a block, wrap it into a block.
     * @param xnf
     * @param s
     * @return only one stmt, should be a block, cannot be a StmtSeq
     */
    static public Block stmtToBlock(NodeFactory xnf, Stmt s){
        if(s instanceof StmtSeq){
            return xnf.Block(s.position(), ((StmtSeq)s).statements());
        }
        else if(s instanceof Block){
            return (Block)s;
        }
        else {
            return xnf.Block(s.position(), s);
        }
    }

    /**
     * @param stmt a construct's body, such as if/for/async
     * @return a list of stmts
     */
    static public List<Stmt> unwrapBodyBlockToStmtList(Stmt stmt){
        if(stmt instanceof Block){
            List<Stmt> stmts = new ArrayList<Stmt>();
            for(Stmt s : ((Block)stmt).statements()){
                stmts.addAll(unwrapToStmtList(s));
            }
            return stmts;
        }
        else{
            return Collections.singletonList(stmt);
        }
    }
    
    /**
     * @param stmt that may contain StmtSeq
     * @return stmts without StmtSeq
     */
    static public List<Stmt> unwrapToStmtList(Stmt stmt){
        if(stmt instanceof Block){
            if(stmt instanceof StmtSeq){
                List<Stmt> stmts = new ArrayList<Stmt>();
                for(Stmt s : ((StmtSeq)stmt).statements()){
                    stmts.addAll(unwrapToStmtList(s));
                }
                return stmts;
            }
            else{
                return Collections.singletonList(stmt); //pure block not unwrapp
            }
        }
        //other case
        return Collections.singletonList(stmt);
    }
    
    /**
     * Unroll block until it either a single stmt, or a block with more than one stmts
     * If the top level is StmtSeq, still return StmtSeq
     * @param block
     * @return
     */
    static public Stmt unwrapToOneStmt(Stmt stmt){
        if(!(stmt instanceof Block)){
            return stmt; //non block just return;
        }
        List<Stmt> blockSS = ((Block)stmt).statements();
        if(blockSS.size() > 1){
            return stmt; //return current block
        }
        else{ //size == 1;
            stmt = blockSS.get(0);
            if(stmt instanceof Block){
                return unwrapToOneStmt((Block)stmt);
            }
            else{
                return stmt;
            }
        }
    }
    
    /**
     * @param c the current Context
     * @return the current context's container class context;
     */
    static public Context popToClassContext(Context c){
        while (c != null && !c.isClass()) {
            c = c.pop();
        }
        return c;
    }
    
    
    
    /**
     * Set all special's qualifier with the outer class type node if the special's qualifier is null
     * @param s
     * @param outerDef
     * @param xnf
     * @return
     */
    static public Stmt setSpeicalQualifier(Stmt s, ClassDef outerDef, NodeFactory xnf){
        SpecialQualifierSetter sqs = new SpecialQualifierSetter(xnf, outerDef);
        
        if (s == null) return null;
        return (Stmt) s.visit(sqs);

    }
    
    static class ScanReturnStatementVisitor extends NodeVisitor{
        private boolean hasReturn;
        ScanReturnStatementVisitor(){
            
        }
        
        private int noVisitDepth;//if noVisitDepth > 0, not visit
        
        @Override
        public NodeVisitor enter(Node n) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth++;
            }
            return super.enter(n);
        }
        
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth--;
                return n;
            }
            
            if(noVisitDepth > 0){
                return n;
            }
            
            if(n instanceof Return){
                hasReturn = true;
            }
            return n;
        }

        public boolean isHasReturn() {
            return hasReturn;
        }
    }
    
    
    static class ConcurrentConstructBlockVisitor extends NodeVisitor{

        private boolean isConcurrent;
        private int noVisitDepth;//if noVisitDepth > 0, not visit
        
        @Override
        public NodeVisitor enter(Node n) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth++;
            }
            return super.enter(n);
        }
        
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth--;
                return n;
            }
            
            if(noVisitDepth > 0){
                return n;
            }
            
            if (n instanceof Finish
                    //|| n instanceof Future //Future is translated from Async
                    || n instanceof PlacedClosure // is An abstraction for at(p) Expr
                    || n instanceof AtStmt  // at stmt
                    || n instanceof Async  //direct async
                    || n instanceof When) {
                isConcurrent = true;
            }
            return n;
        }

        public boolean isConcurrent() {
            return isConcurrent;
        }
    }
    
    /**
     * 
     * Locate all callees from one node;
     * @author Haichuan
     *
     */
    static class MethodCallFindingVisitor extends NodeVisitor{
        private List<ProcedureDef> callees;
        
        public MethodCallFindingVisitor(){
            callees = new ArrayList<ProcedureDef>();
        }
        
        private int noVisitDepth;//if noVisitDepth > 0, not visit
        
        @Override
        public NodeVisitor enter(Node n) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth++;
            }
            return super.enter(n);
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth--;
                return n;
            }
            
            if(noVisitDepth > 0){
                return n;
            }
            
            if(n instanceof Call){
                Call aCall = (Call)n;
                callees.add(aCall.methodInstance().def());                
            }
            else if(n instanceof ClosureCall){
                ClosureCall aCall = (ClosureCall)n;
                
                System.out.println("[WS_WARNING]Cannot build closure call relationship");
                System.out.println("            ClosureDef = "+aCall.closureInstance().def());
                System.out.println("            Target = " + aCall.target());                
                callees.add(aCall.closureInstance().def());
            }
            else if(n instanceof ConstructorCall){
                ConstructorCall aCall = (ConstructorCall)n;
                callees.add(aCall.constructorInstance().def());
            }
            else if(n instanceof New){
                New aCall = ((New)n);
                callees.add(aCall.constructorInstance().def());                                
            }
            return n;
        }

        protected List<ProcedureDef> getCallees() {
            return callees;
        }
    }
    
    static class MethodCallVisitor extends NodeVisitor{
        
        MethodCallVisitor(WSTransformState wsState){
            this.wsState = wsState;
        }
        
        private WSTransformState wsState;
        private int complexCallNum;
        
        private int noVisitDepth;//if noVisitDepth > 0, not visit
        
        @Override
        public NodeVisitor enter(Node n) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth++;
            }
            return super.enter(n);
        }
        
        @Override        
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            if(n instanceof Closure
                    || n instanceof ClassBody){
                noVisitDepth--;
                return n;
            }
            
            if(noVisitDepth > 0){
                return n;
            }

            //FIXME: need consider call to closure/constructor/new with concurrent
            if(n instanceof Call){
                

                if((wsState != null)){
                    Call aCall = (Call)n;
                    if(wsState.isConcurrentCallSite(aCall)){
                        complexCallNum++;
                    }
                }
                else{
                    //no wsState, always treat it as a complex node
                    complexCallNum++;
                }
            }
            
            return n;
        }

        public int getConcurrentCallNums(){
            return complexCallNum;
        }
        
        public boolean isComplex() {
            return complexCallNum > 0;
        }
    }
    
    
    /**
     * @author Haibo
     * 
     * This class is used to collect local variable declarations.
     *
     */
    static class LocalDeclVisitor extends NodeVisitor {
        protected ArrayList<LocalDecl> lDeclList;
        public LocalDeclVisitor(ArrayList<LocalDecl> lDeclList){
            this.lDeclList = lDeclList;
        }
        
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
            if (n instanceof LocalDecl) {
                lDeclList.add((LocalDecl) n);
            }
           
            return n;
        }
    }

    
    /**
     * @author Haichuan
     * Set all special's qualifier to outer class if the special's qualifier is null
     */
    static class SpecialQualifierSetter extends NodeVisitor{
        //protected ClassDef outerDef;
        TypeNode  tn ;
        public SpecialQualifierSetter(NodeFactory xnf, ClassDef outerDef){
           tn = xnf.CanonicalTypeNode(Position.COMPILER_GENERATED, outerDef.asType());
        }
        
        @Override
        public Node leave(Node parent, Node old, Node n, NodeVisitor v){
            if(n instanceof Special){
                
                Special s = (Special)n;
                if(s.qualifier() == null){
                    return s.qualifier(tn);   
                }
            }
            return n;
        }
    }
}

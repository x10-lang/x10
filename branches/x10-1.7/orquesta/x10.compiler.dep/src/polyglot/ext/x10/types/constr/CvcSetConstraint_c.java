package polyglot.ext.x10.types.constr;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.types.ClassType;
import polyglot.types.PrimitiveType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Pair;

public class CvcSetConstraint_c extends TypeObject_c implements SimpleConstraint {
    protected CvcSetConstraintSystem eval;
    protected C_Term t;

    protected CvcSetConstraint_c(TypeSystem ts, C_Term t, CvcSetConstraintSystem eval) {
        super(ts);
        this.t = t;
        this.eval = eval;
    }
    
    public ConstraintSystem constraintSystem() {
        return eval;
    }
    
    public void internRecursively(C_Var var, Constraint container) throws Failure {
        Type t = var.type();
        if (t instanceof X10Type) {
            Constraint c = X10TypeMixin.depClause((X10Type) t);
            for (SimpleConstraint sc : c.conjuncts()) {
                if (sc.constraintSystem() == eval)
                    addIn(sc);
            }
        }
    }
    
    @Override
    public CvcConstraint_c copy() {
        CvcConstraint_c c = (CvcConstraint_c) super.copy();
        c.t = t.copy(); 
        return c;
    }
    
    @Override
    public X10TypeSystem typeSystem() {
        return (X10TypeSystem) super.typeSystem();
    }
    
    public Set<C_Var> vars() {
        List<C_Var> vars = new ArrayList<C_Var>();
        t.collectVars(vars);
        return new LinkedHashSet<C_Var>(vars);
    }

    String flip(String op) {
        if (op.equals("<=")) return ">=";
        if (op.equals(">=")) return "<=";
        if (op.equals("<")) return ">";
        if (op.equals(">")) return "<";
        if (op.equals("==")) return "==";
        if (op.equals("!=")) return "!=";
        return null;
    }
    
    C_Term neg(C_Term t) {
        if (t instanceof C_Lit) {
            C_Lit l = (C_Lit) t;
            if (l.val() instanceof Integer) {
                Integer i = (Integer) l.val();
                return new C_Lit_c(-i, l.type());
            }
        }
        return new C_UnaryTerm_c("-", t, t.type());
    }

    public SimpleConstraint unaryOp(Unary.Operator op) {
        if (op == Unary.POS) {
            return this;
        }
        if (op == Unary.NEG) {
            if (t instanceof C_BinaryTerm) {
                C_BinaryTerm b = (C_BinaryTerm) t;
                C_Term left = b.left();
                C_Term right = b.right();
                String bop = b.op();
                if (flip(bop) != null) {
                    if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                        CvcConstraint_c n = (CvcConstraint_c) copy();
                        n.t = new C_BinaryTerm_c(flip(bop), left, neg(right), b.type());
                        return n;
                    }
                    if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                        CvcConstraint_c n = (CvcConstraint_c) copy();
                        n.t = new C_BinaryTerm_c(flip(bop), neg(left), right, b.type());
                        return n;
                    }
                }
            }
        }
        return null;
    }


    // TODO: should just use existentials:
    // self OP1 x1  BIN  self OP2 x2
    // -->
    // \exists a, b. self = a BIN b && a OP1 x1 && b OP2 x2
    public SimpleConstraint binaryOp(Binary.Operator op, Constraint other, Constraint me) {
        C_Term result = binaryOp(t, op, other, me);
        if (result != null) {
            try {
                return eval.constraintForTerm(result);
            }
            catch (SemanticException f) {
            }
        }
        return null;
    }
    
    public C_Term binaryOp(C_Term t, Binary.Operator op, Constraint other, Constraint me) {
        if (t instanceof C_BinaryTerm) {
            C_BinaryTerm b = (C_BinaryTerm) t;
            C_Term left = b.left();
            C_Term right = b.right();
            String bop = b.op();
            if (bop.equals("&&")) {
                C_Term l = binaryOp(left, op, other, me);
                C_Term r = binaryOp(right, op, other, me);
                if (l == null) return r;
                if (r == null) return l;
                return new C_BinaryTerm_c(bop, l, r, b.type());
            }

            if (flip(bop) != null) {
                C_Term t2 = null;

                if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                    t2 = binaryOp(bop, left, right, op, other, me);
                }
                if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                    t2 = binaryOp(flip(bop), right, left, op, other, me);
                }
                
                if (t2 != null)
                    return t2;
            }
        }
        return null;
    }

    private C_Term binaryOp(String bop, C_Term self, C_Term bound, Operator op, Constraint c, Constraint me) {
        for (SimpleConstraint sc : c.conjuncts()) {
            if (sc instanceof CvcSetConstraint_c) {
                CvcSetConstraint_c oc = (CvcSetConstraint_c) sc;
//                if (oc.t instanceof C_BinaryTerm) {
//                    C_Term t2 = binaryOp(bop, self, bound, op, (C_BinaryTerm) oc.t, me);   
//                    if (t2 != null)
//                        return t2;
//                }
                if (c.selfVar() != null
						&& (op == Binary.ADD || op == Binary.SUB
								|| op == Binary.MUL || op == Binary.DIV || op == Binary.MOD)) {
                    C_Var v1 = me.selfVar();
                    C_Var v2 = c.selfVar();
                    if (v1 == null) v1 = me.genEQV(self.type(), false);
//                    C_EQV v2 = me.genEQV(c.selfVar().type(), false);
                    try {
                        PrimitiveType selfType = ts.promote(v1.type(), v2.type());
                        C_Special_c newSelf = new C_Special_c(selfType, null, C_Special.SELF);
                        C_Term t2 = new C_BinaryTerm_c("==",
                                                       newSelf,
                                                       new C_BinaryTerm_c(op.toString(), v1, v2, selfType),
                                                       ts.Boolean());
                        t2 = new C_BinaryTerm_c("&&",
                                                t2,
                                                new C_BinaryTerm_c("&&",
                                                                     t.substitute(v1, C_Special.Self, true, new HashSet<C_Term>()),
                                                                     oc.t.substitute(v2, C_Special.Self, true, new HashSet<C_Term>()), ts.Boolean()),
                                                                     ts.Boolean());
                        if (me.selfVar() != null)
                            t2 = new C_BinaryTerm_c("&&", t2, new C_BinaryTerm_c("==", v1, me.selfVar().substitute(v1, C_Special.Self, true, new HashSet<C_Term>()), ts.Boolean()), ts.Boolean());
                        if (c.selfVar() != null)
                            t2 = new C_BinaryTerm_c("&&", t2, new C_BinaryTerm_c("==", v2, c.selfVar().substitute(v2, C_Special.Self, true, new HashSet<C_Term>()), ts.Boolean()), ts.Boolean());
                        
                        if (op == Binary.DIV) {
                            C_Term t3;
                            t3 = new C_BinaryTerm_c("=>", // HACK! use => since CVC supports it.
                                                    new C_BinaryTerm_c(">=", v2, new C_Lit_c(0, v2.type()), ts.Boolean()),
                                                    new C_BinaryTerm_c("<=", newSelf, v1, ts.Boolean()), ts.Boolean());
                            t2 = new C_BinaryTerm_c("&&", t2, t3, ts.Boolean());
                        }
                        
                        return t2;
                    }
                    catch (Failure f) {
                        return null;
                    }
                    catch (SemanticException e) {
                        return null;
                    }
                }
                
                if (c.selfVar() != null && op == Binary.MOD) {
                    try {
                    PrimitiveType selfType = ts.promote(self.type(), c.selfVar().type());
                    return new C_BinaryTerm_c("&&",
                                              new C_BinaryTerm_c(">=", new C_Special_c(selfType, null, C_Special.SELF), new C_Lit_c(0, selfType), ts.Boolean()),
                                              new C_BinaryTerm_c("<", new C_Special_c(selfType, null, C_Special.SELF), c.selfVar(), ts.Boolean()),
                                              ts.Boolean());
                    }
                    catch (SemanticException e) {
                        return null;
                    }
                }
                

            }
        }
        return null;
    }

    private C_Term binaryOp(String bop, C_Term self, C_Term bound, Operator op, C_BinaryTerm b, Constraint me) {
        C_Term left = b.left();
        C_Term right = b.right();
        String bop2 = b.op();
        if (bop2.equals("&&")) {
            C_Term l = left instanceof C_BinaryTerm ? binaryOp(bop, self, bound, op, (C_BinaryTerm) left, me) : null;
            C_Term r = right instanceof C_BinaryTerm ? binaryOp(bop, self, bound, op, (C_BinaryTerm) right, me) : null;
            if (l == null) return r;
            if (r == null) return l;
            return new C_BinaryTerm_c(bop2, l, r, b.type());
        }
        if (flip(bop2) != null) {
            if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                return binaryOp(bop, self, bound, bop2, right, op, me);
            }
            if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                return binaryOp(bop, self, bound, flip(bop2), left, op, me);
            }
        }
        return null;
    }

        // (self bop bond) op (self bop2 bound2)
        // e.g.
        // (self <= n) + (self <= m) --> (self <= n+m)
    private C_Term binaryOp(String bop, C_Term self, C_Term bound, String bop2, C_Term bound2, Operator op, Constraint me) {
        X10TypeSystem xts = typeSystem();
        if (bop.equals(bop2)) {
            if (op == Binary.ADD) {
                C_BinaryTerm t = new C_BinaryTerm_c(bop, self, optimize(new C_BinaryTerm_c("+", bound, bound2, bound2.type())), ts.Boolean());
                return t;
            }
            if (op == Binary.MUL) {
                // self >= 0 * self >= 0 --> self >= 0
                if (bound instanceof C_Lit && ((C_Lit) bound).val().equals(0)) {
                    C_BinaryTerm t = new C_BinaryTerm_c(bop, self, optimize(new C_BinaryTerm_c("*", bound, bound2, bound2.type())), ts.Boolean());
                    return t; 
               }
            }
        }
        
        return null;
    }
    
//    Map<Constraint,Boolean> cache = null;
//
//    public boolean entailedBy(Constraint c) {
//        Boolean result = cache != null ? cache.get(c) : null;
//        if (result != null) {
//            System.out.println("CACHED");
//            return result;
//        }
//        result = entailedByWork(c);
//        if (cache == null)
//            cache = new HashMap<Constraint,Boolean>();
//        cache.put(c, result);
//        return result;
//    }
    
    public boolean entailedBy(Constraint other, Constraint me) {
        List<CvcSetConstraint_c> otherConjuncts = new ArrayList<CvcSetConstraint_c>();
        List<CvcSetConstraint_c> myConjuncts = new ArrayList<CvcSetConstraint_c>();
        
        for (SimpleConstraint c2 : me.conjuncts()) {
            if (c2 instanceof CvcSetConstraint_c) {
                CvcSetConstraint_c oc = (CvcSetConstraint_c) c2;
                myConjuncts.add(oc);
            }
        }
        
        for (SimpleConstraint c2 : other.conjuncts()) {
//            if (c2 == this)
//                return true;
            if (c2 instanceof CvcSetConstraint_c) {
                CvcSetConstraint_c oc = (CvcSetConstraint_c) c2;
                otherConjuncts.add(oc);
            }
        }
        
        if (myConjuncts.isEmpty()) {
            return true;
        }
        
        System.out.println("other = " + otherConjuncts);
        System.out.println("me = " + myConjuncts);
        
        assert myConjuncts.size() == 1;
        assert myConjuncts.get(0) == this;
        assert otherConjuncts.size() <= 1;
        
        CvcSetConstraint_c otherCvc = otherConjuncts.size() == 1 ? otherConjuncts.get(0) : null;
        
        Pair<C_Term, C_Term> key = new Pair<C_Term, C_Term>(otherCvc != null ? otherCvc.t : typeSystem().TRUE(), this.t);
		
        Boolean b = cache.get(key);
        if (b != null)
        	return b;
        
        b = entailedByCvc(me, otherCvc);
        
        cache.put(key, b);
        
        return b;
    }
    
    static HashMap<Pair<C_Term, C_Term>, Boolean> cache = 
    	new HashMap<Pair<C_Term, C_Term>, Boolean> ();
    
	protected boolean entailedByCvc(Constraint me, CvcSetConstraint_c otherCvc) {
		// Check for the trivial case.
        if (otherCvc != null && otherCvc.t.equals(t)) {
        	return true;
        }
        
        StringBuffer sb = new StringBuffer();

        Map<C_Var,String> varMap = new HashMap<C_Var, String>();

        // Preamble for CVC program.
        preamble(sb);


        // Declare variables.  Create a new hash set to eliminate duplicates.
//        declareVars(sb, varMap, t, c, "self");

        Map<String,String> decls = new HashMap<String,String>();
        List<String> asserts = new ArrayList<String>();
        Map<String,String> eqvDecls = new HashMap<String,String>();
        List<String> eqvAsserts = new ArrayList<String>();
        
        Constraint c = me;

        try {
        	if (otherCvc != null)
        		declareVars(decls, asserts, varMap, otherCvc.t, c, "self", new HashSet<Key>(), true, false);     

        	declareVars(decls, asserts, varMap, this.t, c, "self", new HashSet<Key>(), false, false);     
        	declareVars(eqvDecls, eqvAsserts, varMap, this.t, c, "self", new HashSet<Key>(), true, false);     

        	eqvDecls.keySet().removeAll(decls.keySet());
        	eqvAsserts.removeAll(asserts);

        	for (String s : decls.keySet()) {
        		sb.append(s);
        		sb.append(": ");
        		sb.append(decls.get(s));
        		sb.append(";\n");
        	}

        	for (String s : asserts) {
        		sb.append("ASSERT ");
        		sb.append(s);
        		sb.append(";\n");
        	}

        	// Assert c.
        	if (otherCvc != null) {
        		sb.append("% " + otherCvc.t + "\n");
        		sb.append("ASSERT ");
        		toCvc(sb, varMap, otherCvc.t, c, "self");
        		sb.append(";\n");
        	}

        	// Assert my other conjuncts
        	//        for (CvcConstraint_c oc : myConjuncts) {
        	//            if (oc == this) continue;
        	//            sb.append("% " + oc.t + "\n");
        	//            sb.append("ASSERT ");
        	//            toCvc(sb, varMap, oc.t, c, "self");
        	//            sb.append(";\n");
        	//        }

        	// Query this.
        	sb.append("% " + t + "\n");
        	sb.append("QUERY ");
        	if (! eqvDecls.isEmpty()) {
        		sb.append("EXISTS (");
        		String str = "";
        		for (String s : eqvDecls.keySet()) {
        			sb.append(str);
        			sb.append(s);
        			sb.append(": ");
        			sb.append(eqvDecls.get(s));
        			str = ", ";
        		}
        		sb.append("): ");
        		str = "(";
        		for (String s : eqvAsserts) {
        			sb.append(str);
        			sb.append(s);
        			str = " AND ";
        		}
        		if (! str.equals("("))
        			sb.append(") => ");
        	}
        	sb.append("(");
        	sb.append("(");
        	toCvc(sb, varMap, t, c, "self");
        	sb.append(")");

        	//        if (me.selfVar() != null) {
        	//        	C_Term selfBinding = new C_BinaryTerm_c("==", new C_Special_c(X10Special.SELF, me.selfVar().type()), me.selfVar(), ts.Boolean());
        	//        	sb.append(" AND ");
        	//        	toCvc(sb, varMap, selfBinding, c, "self");
        	//        	sb.append("");
        	//        }

        	sb.append(")");
        	sb.append(";\n");

        }
        catch (Failure f) {

        	System.out.println(f.getMessage());

        	return false;
        }

        return checkValid(sb.toString());
	}

    protected String toName(C_Var v, String self, Map<C_Var, String> varMap) {
        if (v instanceof C_Special) {
            C_Special cs = (C_Special) v;
            if (cs.kind() == C_Special.SELF) {
                return self;
            }
        }

        String name = varMap.get(v);
        if (name != null)
            return name;
        
        name = makeName(v, self, varMap);
        varMap.put(v, name);
        return name;
    }

    protected String makeName(C_Var v, String self, Map<C_Var, String> varMap) {
//        if (v instanceof C_EQV) {
//            return "_" + fresh++;
//        }
    	
    	assert v.type() != null;

        if (v instanceof C_Field) {
            C_Field f = (C_Field) v;
            return toName(f.receiver(), self, varMap) + "__" + f.name();
        }
        
        if (v instanceof C_Type) {
            C_Type t = (C_Type) v;
            if (t.type() instanceof X10NamedType) {
                X10NamedType xt = (X10NamedType) t.type();
                return xt.fullName().replace(".", "__");
            }
            else { 
                assert false;
            }
        }
        
        if (v instanceof C_Special) {
            C_Special cs = (C_Special) v;
            if (cs.kind() == C_Special.SELF) {
                return self;
            }
            // super and this are the same variable; call it "this"
            if (cs.qualifier() != null) {
                X10NamedType xt = (X10NamedType) cs.qualifier();
                return xt.fullName().replace(".", "__") + "__" + "this";
            }
            else {
                return "this";
            }
        }
        
        return v.toString();
    }
    
    String toType(Type t) {
        if (t != null && t.isIntOrLess()) {
            return "INT";
        }
        if (t != null && t.isBoolean()) {
        	return "MyBoolean";
        }
        if (t != null && typeSystem().isRegion(t)) {
        	return "Region";
        }
        if (t != null && typeSystem().isPoint(t)) {
        	return "Point";
        }
        return "Atom";
    }

    public void preamble(StringBuffer sb) {
    	sb.append(    
//    	"posInteger: TYPE = SUBTYPE(LAMBDA (x:INT): x > 0);           \n" +
//    	"__witness : INT;                                             \n" +
//    	"ASSERT __witness > 0;                                        \n" +
//    	"                                                             \n" +
    			"mod:(INT, INT) -> INT;                               \n" +
    			"ASSERT FORALL(a,b,m,q: INT):                             \n" +
    			"    (a >= 0 AND b > 0 AND q = a/b AND (a - q * b) = m) <=> mod(a,b) = m;\n" +
    			"" +
    			"Atom : TYPE;\n" +
    			"\n" +
    			"DATATYPE\n" +
    			"    MyBoolean = true\n" +
    			"          | false\n" +
    			"END;\n" +
    			"\n" +
    			"DATATYPE\n" +
    			"    Point = point1(point11: INT)\n" +
    			"          | point2(point21: INT, point22: INT)\n" +
//    			"          | pointAdd(pointAdd1: Point, pointAdd2: Point)\n" +
    			"END;\n" +
    			"\n" +
    			"DATATYPE\n" +
    			"    Region = rect(topeft: Point, bottomRight: Point)\n" +
    			"        | union(union1: Region, union2: Region)\n" +
    			"        | intersect(intersect1: Region, intersect2: Region)\n" +
    			"        | minus(minus1: Region, minus2: Region)\n" +
    			"        | shift(i: INT, shiftdown1: Region, shiftdown2: Point)\n" +
    			"END;\n" +
    			"\n" +
    			"contains: (Point, Region) -> BOOLEAN;\n" +
    			"lessThan: (Point, Point) -> BOOLEAN;\n" +
    			"subset: (Region, Region) -> BOOLEAN;\n" +
    			"\n" +
//    			"ASSERT FORALL (x1,x2,y1,y2: INT):\n" +
//    			"        subset(range(x1, x2), range(y1,y2)) <=> y1 <= x1 AND x2 <= y2;\n" +
//    			"\n" +
//    			"ASSERT FORALL (x1,x2,y1,y2: Point):\n" +
//    			"        subset(rect(x1, x2), rect(y1,y2)) <=> subset(x1,y1) AND subset(x2,y2);\n" +
//    			"\n" +
    			"ASSERT FORALL (r1,r2: Region):\n" +
    			"        subset(r1, r2) <=> FORALL (x: Point): contains(x,r1) => contains(x,r2);\n" +
    			"\n" +
    			"ASSERT FORALL (r: Region):\n" +
    			"        subset(r, r);\n" +
    			"\n" +
    			"ASSERT FORALL (p1, p2: INT):\n" +
    			"        lessThan(point1(p1), point1(p2)) <=> p1 <= p2;\n" +
    			"\n" +
//    			"ASSERT FORALL (x1, x2, x3: INT):\n" +
//    			"        lessThan(pointAdd(point1(x1),point1(x2)), point1(x3)) <=> x1+x2 <= x3;\n" +
    			"\n" +
    			"ASSERT FORALL (x1, y1, x2, y2: INT):\n" +
    			"        lessThan(point2(x1,y1), point2(x2,y2)) <=> x1 <= x2 AND y1 <= y2;\n" +
    			"\n" +
    			"ASSERT FORALL (x,x1,x2: Point):\n" +
    			"        contains(x,rect(x1,x2)) <=> lessThan(x1,x) AND lessThan(x,x2);\n" +
    			"\n" +
    			"ASSERT FORALL (x:Point, r1,r2: Region):\n" +
    			"        contains(x,union(r1,r2)) <=> contains(x,r1) OR contains(x,r2);\n" +
    			"\n" +
    			"ASSERT FORALL (x:Point, r1,r2: Region):\n" +
    			"        contains(x,intersect(r1,r2)) <=> contains(x,r1) AND contains(x,r2);\n" +
    			"\n" +
    			"ASSERT FORALL (x:Point, r1,r2: Region):\n" +
    			"        contains(x,minus(r1,r2)) <=> contains(x,r1) AND NOT contains(x,r2);\n" +
    			"\n" +
    			"ASSERT FORALL (i,x1,x2:INT, r: Region):\n" +
    			"        contains(point1(x1),shift(i,r,point1(x2))) <=> contains(point1(x1+i*x2),r);\n" +
    			"\n" +
    			"ASSERT FORALL (i,x1,y1,x2,y2:INT, r: Region):\n" +
    			"        contains(point2(x1,y1),shift(i,r,point2(x2,y2))) <=> contains(point2(x1+i*x2,y1+i*y2),r);\n" +
    			"\n" +
    	"");
    }
    
    static class Key {
    	C_Term t;
    	String self;
    
    	Key(C_Term t, String self) {
    		this.t = t;
    		this.self = self;
    	}
    	
    	public boolean equals(Object o) {
    		if (o instanceof Key) {
    			Key k = (Key) o;
    			return t.equals(k.t) && self.equals(k.self);
    		}
    		return false;
    	}
    	
    	public int hashCode() {
    		return t.hashCode() + self.hashCode();
    	}
    	
    	public String toString() {
    		return "(" + t + " self=" + self + ")";
    	}
    }
    
    protected void declareVars(Map<String,String> decls, List<String> asserts, Map<C_Var, String> varMap, C_Term t, Constraint c, String self, Set<Key> visited, boolean includeEQVs, boolean includeOnlyEQVs) throws Failure {
        Key key = new Key(t, self);
		if (visited.contains(key)) {
            return;
        }
        visited.add(key);
        
        if (t instanceof C_Lit) {
            C_Lit e = (C_Lit) t;
        }
        else if (t instanceof C_Var) {
        	String type = toType(t.type());
        	if (t instanceof C_Type) {
        		type = "Atom";
        	}
            String name = toName((C_Var) t, self, varMap);

            boolean include = true;
          
            if (! includeEQVs) {
                C_Var[] xs = ((C_Var) t).vars();
                boolean recurse = true;
                for (C_Var x : xs) {
                    if (x instanceof C_EQV) {
                        include = false;
                        break;
                    }
                }
            }

            if (include) {
            	if (name.equals("this"))
                decls.put(name, type);
            	else
            		decls.put(name, type);
            		
                if (t instanceof C_Special && name.equals("this")) {
                    C_Special cs = (C_Special) t;
                    if (cs.type() != null) {
                        // If type is not null, add constraint that the qualified type is equal to the unqualified type.
                        X10NamedType xt = (X10NamedType) cs.type();
                        String qualified = xt.fullName().replace(".", "__") + "__" + "this";
                        String qtype = toType(xt);
                        if (! type.equals(qtype))
                        	decls.put("this", qtype);
                        decls.put(qualified, qtype);
                        asserts.add("(this = " + qualified + ")");
                    }
                }

                if (t instanceof C_Field) {
                    C_Field field = (C_Field) t;
                    declareVars(decls, asserts, varMap, field.receiver(), c, self, visited, includeEQVs, includeOnlyEQVs);

                    String targetName = toName(field.receiver(), self, varMap);

                    // Make sure the type of the target is an atom -- or region.
//                    decls.put(targetName, "Atom");
                    
                    for (Key otherKey : visited) {
                    	C_Term other = otherKey.t;
                        if (other instanceof C_Field && ! field.equals(other)) {
                            C_Field otherField = (C_Field) other;
                            String otherName = toName(otherField, self, varMap);
                            String otherTargetName = toName(otherField.receiver(), self, varMap);
                            
                            if (decls.get(otherName) != null && otherTargetName != null && decls.get(otherTargetName) != null) {
                            	if (decls.get(otherName).equals(decls.get(name)) && decls.get(otherTargetName).equals(decls.get(targetName))){
                            		// Make sure the type of the target is an atom.
                            		if (field.fieldInstance().def() == otherField.fieldInstance().def()) {
//                            			decls.put(otherTargetName, "Atom");
                            			String eq = " = ";
                            			if (decls.get(otherName).equals("BOOLEAN"))
                            				eq = " <=> ";
//                            			if (decls.get(otherName).equals("MyBoolean"))
//                            				eq = " <=> ";
                            			String implies = "((" + targetName + " = " + otherTargetName + ") => (" + name + eq + otherName + "))";
                            			System.out.println("FIELD CONSTRAINT " + implies);
                            			asserts.add(implies);
                            		}
                            	}
                            }
                        }
                    }
                }

                if (t.type() instanceof X10Type && X10TypeMixin.isConstrained((X10Type) t.type())) {
                    Constraint tc = X10TypeMixin.realClause((X10Type) t.type());
                    for (SimpleConstraint sc : tc.conjuncts()) {
                        if (sc instanceof CvcSetConstraint_c) {
                            CvcSetConstraint_c oc = (CvcSetConstraint_c) sc;
                            declareVars(decls, asserts, varMap, oc.t, c, name, visited, includeEQVs, includeOnlyEQVs);
                            StringBuffer sb = new StringBuffer();
                            toCvc(sb, varMap, oc.t, c, name);
                            asserts.add(sb.toString());
                        }
                    }
                }
            }
        }
        else if (t instanceof C_UnaryTerm) {
            C_UnaryTerm e = (C_UnaryTerm) t;
            declareVars(decls, asserts, varMap, e.arg(), c, self, visited, includeEQVs, includeOnlyEQVs);
        }
        else if (t instanceof C_BinaryTerm) {
            C_BinaryTerm e = (C_BinaryTerm) t;
            declareVars(decls, asserts, varMap, e.left(), c, self, visited, includeEQVs, includeOnlyEQVs);
            declareVars(decls, asserts, varMap, e.right(), c, self, visited, includeEQVs, includeOnlyEQVs);
        }
    }
    
    protected void toCvc(StringBuffer sb, Map<C_Var, String> varMap, C_Term t, Constraint c, String self) throws Failure {
        if (t instanceof C_Lit) {
            C_Lit e = (C_Lit) t;
            if (e.val() instanceof Boolean) {
            	if (e.val().equals(true)) {
            		sb.append("TRUE");
            	}
            	else {
            		sb.append("FALSE");
            	}
            }
            else {
            	sb.append(e.val());
            }
//            sb.append(e.val());
        }
        else if (t instanceof C_Var) {
            String i = toName((C_Var) t, self, varMap);
            if (t instanceof C_Special && ((C_Special) t).kind() == C_Special.SELF) {
            	i = self;
            }
            assert i != null : "no var for " + t + " key=" + t;
            if (t.type() != null && t.type().isBoolean())
				sb.append("is_true(" + i + ")");
			else
				sb.append(i);
        }
        else if (t instanceof C_Call) {
        	C_Call e = (C_Call) t;
			List<C_Term> args = e.args();
			if (e.mi().name().equals("contains") && typeSystem().isRegion(e.receiver().type()) && args.size() == 1 && typeSystem().isRegion(args.get(0).type())) {
        		// region.contains(region)
				sb.append("subset(");
        		toCvc(sb, varMap, args.get(0), c, self);
        		sb.append(", ");
        		toCvc(sb, varMap, e.receiver(), c, self);
        		sb.append(")");
        		return;
        	}
			else if (e.mi().name().equals("region") && e.receiver().toString().equals("x10.lang.region.factory") && args.size() == 2) {
				if (typeSystem().isRegion(args.get(0).type()) && typeSystem().isRegion(args.get(1).type())) {
					sb.append("rect(");
					toCvc(sb, varMap, args.get(0), c, self);
					sb.append(", ");
					toCvc(sb, varMap, args.get(1), c, self);
					sb.append(")");
				}
			}
			else if (e.mi().name().equals("region") && e.receiver().toString().equals("x10.lang.region.factory") && args.size() == 3) {
				if (args.get(0).type().isInt() && args.get(1).type().isInt()) {
					sb.append("rect(point1(");
					toCvc(sb, varMap, args.get(0), c, self);
					sb.append("), point1");
					toCvc(sb, varMap, args.get(1), c, self);
					sb.append("))");
				}
				return;
			}
			else if (e.mi().name().equals("contains") && typeSystem().isRegion(e.receiver().type()) && args.size() == 1 && typeSystem().isPoint(args.get(0).type())) {
				// region.contains(region)
				sb.append("contains2(");
				toCvc(sb, varMap, args.get(0), c, self);
				sb.append(", ");
				toCvc(sb, varMap, e.receiver(), c, self);
				sb.append(")");
				return;
			}
			else if (e.mi().name().equals("contains") && typeSystem().isRegion(e.receiver().type()) && args.size() == 1 && args.get(0).type().isInt()) {
				// region.contains(region)
				sb.append("contains(");
				toCvc(sb, varMap, args.get(0), c, self);
				sb.append(", ");
				toCvc(sb, varMap, e.receiver(), c, self);
				sb.append(")");
				return;
			}

			throw new Failure("Bad term " + t);
        }
        else if (t instanceof C_UnaryTerm) {
            C_UnaryTerm e = (C_UnaryTerm) t;
            if (e.op().equals("-")) {
                sb.append("-");                
            }
            else if (e.op().equals("!")) {
                sb.append("NOT");
            }
            else {
                sb.append(e.op());
            }
            sb.append("(");
            toCvc(sb, varMap, e.arg(), c, self);
            sb.append(")");
        }
        else if (t instanceof C_BinaryTerm) {
            C_BinaryTerm e = (C_BinaryTerm) t;
     
            String op = e.op();
            C_Term left = e.left();
			C_Term right = e.right();
			
			if (left.type() == null || right.type() == null) {
				sb.append("TRUE");
			}
			
			if (typeSystem().isRegion(left.type()) && typeSystem().isRegion(right.type())) {
				sb.append("(");
				
				if (op.equals("||")) {
					sb.append("union(");
					toCvc(sb, varMap, left, c, self);
					sb.append(", ");
					toCvc(sb, varMap, right, c, self);
					sb.append(")");
				}
				else if (op.equals("&&")) {
					sb.append("intersect(");
					toCvc(sb, varMap, left, c, self);
					sb.append(", ");
					toCvc(sb, varMap, right, c, self);
					sb.append(")");
				}
				else if (op.equals("-")) {
					sb.append("minus(");
					toCvc(sb, varMap, left, c, self);
					sb.append(", ");
					toCvc(sb, varMap, right, c, self);
					sb.append(")");
				}
				else if (op.equals("==")) {
					toCvc(sb, varMap, left, c, self);
					sb.append(" = ");
					toCvc(sb, varMap, right, c, self);
				}
				else {
					throw new Failure("Bad term " + t);
				}
				
				sb.append(")");
				return;
			}
			
			if (typeSystem().isRegion(left.type()) && typeSystem().isPoint(right.type())) {
				sb.append("(");

				if (op.equals("-")) {
					sb.append("shift(-1, ");
					toCvc(sb, varMap, left, c, self);
					sb.append(", ");
					toCvc(sb, varMap, right, c, self);
					sb.append(")");
				}
				else if (op.equals("+")) {
					sb.append("shift(1, ");
					toCvc(sb, varMap, left, c, self);
					sb.append(", ");
					toCvc(sb, varMap, right, c, self);
					sb.append(")");
				}
				else {
					throw new Failure("Bad term " + t);
				}
				
				sb.append(")");
            	return;
            }

			if (typeSystem().isRegion(left.type()) || typeSystem().isRegion(right.type())) {
				sb.append("TRUE");
				return;
			}
			

			if (op.equals("%")) {
				sb.append("mod(");
				toCvc(sb, varMap, left, c, self);
				sb.append(", ");
				toCvc(sb, varMap, right, c, self);
				sb.append(")");
				return;
			}

			sb.append("(");
            toCvc(sb, varMap, left, c, self);

			if (op.equals("&&") || op.equals("&")) {
                sb.append(" AND ");
            }
            else if (op.equals("||") || op.equals("|")) {
                sb.append(" OR ");
            }
            else if (op.equals("==")) {
            	if (left.type() != null && right.type() != null && left.type().isBoolean() && right.type().isBoolean())
            		sb.append(" <=> ");
            	else
            		sb.append(" = ");
            }
            else if (op.equals("!=")) {
                sb.append(" /= ");
            }
            else {
                sb.append(" ");
                sb.append(op);
                sb.append(" ");
            }

            toCvc(sb, varMap, right, c, self);

            sb.append(")");
        }
        else {
			throw new Failure("Bad term " + t);
        }
    }

    private boolean checkValid(String query) {

        try {
            ErrorQueue eq = Globals.Compiler().errorQueue();

            String cvc;
            
            cvc = System.getProperty("cvc.bin.path");

            if (cvc == null) {
                cvc = "/Users/nystrom/work/cvc3-1.2.1/bin/cvc3";
            }
            
            String file = "tmp.cvc";

            Runtime runtime = Runtime.getRuntime();
            
            ArrayList<String> cmd = new ArrayList<String>();
            cmd.add(cvc);
            cmd.add(file);
            cmd.add("-timeout");
            cmd.add("30");
            
            boolean result = false;
            
            try {
                FileWriter tmp = new FileWriter(file);
                new PrintWriter(tmp).println(query);
                tmp.close();

                Process proc = runtime.exec(cmd.toArray(new String[cmd.size()]));
                LineNumberReader err = new LineNumberReader(new BufferedReader(new InputStreamReader(proc.getErrorStream())));
                LineNumberReader out = new LineNumberReader(new BufferedReader(new InputStreamReader(proc.getInputStream())));

                System.out.println(query);

                while (true) {
                    String line = out.readLine();
//                    eq.enqueue(ErrorInfo.WARNING,
//                               "cvc3 stdout: " + line);
                    
                    System.out.println(line);

                    if (line == null) {
                        result = false;
                        break;
                    }
                    
                    if (line.startsWith("Valid.")) {
                        result = true;
                        break;
                    }
                    else if (line.startsWith("Invalid.")) {
                        result = false;
                        break;
                    }
                    else if (line.startsWith("Unknown.")) {
                        result = false;
                        break;
                    }
                }
           
                while (out.ready()) {
                    String line = out.readLine();
                    System.out.println("cvc3 stdout: " + line);
                }
                
                while (err.ready()) {
                    String line = err.readLine();
                    System.out.println("cvc3 stderr: " + line);
                }

                proc.waitFor();

                if (proc.exitValue() > 0) {
                    eq.enqueue(ErrorInfo.WARNING,
                               "Non-zero return code: " + proc.exitValue());
                }
            }
            catch (IOException e) {
                eq.enqueue(ErrorInfo.WARNING, e.getMessage());
            }
            catch (InterruptedException e) {
                eq.enqueue(ErrorInfo.WARNING, e.getMessage());
            }
            
            return result;
        }
        finally {
        }
    }

    public String toString() {
        return t.toString();
    }

    public C_Var find(String varName) {
        return null;
    }

    public SimpleConstraint substitute(C_Var y, C_Root x, boolean propagate, Constraint container, HashSet<C_Term> visited) throws Failure {
        CvcSetConstraint_c sc = (CvcSetConstraint_c) super.copy();
        sc.t = t.substitute(y, x, propagate, visited);
        return sc;
    }
    
    public boolean contains(C_Term me, C_Term other) {
        if (me.equals(other)) return true;
        if (me instanceof C_BinaryTerm) {
            C_BinaryTerm e = (C_BinaryTerm) me;
            if (e.op().equals("&&")) {
                if (contains(e.left(), other) || contains(e.right(), other)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public C_Term optimize(C_Term t) {
        if (t instanceof C_BinaryTerm) {
            C_BinaryTerm b = (C_BinaryTerm) t;
            C_Term l = optimize(b.left());
            C_Term r = optimize(b.right());
            String op = b.op();
            if ((op.equals("==") || op.equals("<=") || op.equals(">=")) && l.equals(r))
                return typeSystem().TRUE();
            if ((op.equals("!=") || op.equals("<") || op.equals(">")) && l.equals(r))
                return typeSystem().TRUE().not();
            if (l instanceof C_Lit && r instanceof C_Lit) {
                Object lv = ((C_Lit) l).val();
                Object rv = ((C_Lit) l).val();
                if (op.equals("+") && lv instanceof Integer && rv instanceof Integer) {
                    return new C_Lit_c((Integer) lv + (Integer) rv, t.type());
                }
                if (op.equals("-") && lv instanceof Integer && rv instanceof Integer) {
                    return new C_Lit_c((Integer) lv - (Integer) rv, t.type());
                }
                if (op.equals("*") && lv instanceof Integer && rv instanceof Integer) {
                    return new C_Lit_c((Integer) lv * (Integer) rv, t.type());
                }
            }
        }
        
        return t;
    }

    public boolean valid() {
    	C_Term sct = optimize(t);
    	if (sct.equals(typeSystem().TRUE()))
    		return true;
    	return false;
    }
    
    public void addIn(CvcSetConstraint_c sc) throws Failure {
        if (contains(t, sc.t)) {
            return;
        }
        
        C_Term sct = optimize(sc.t);
        if (sct.equals(typeSystem().TRUE()))
            return;
        this.t = new C_BinaryTerm_c("&&", this.t, sct, ts.Boolean());
    }

    public void addIn(SimpleConstraint sc) throws Failure {
        addIn((CvcSetConstraint_c) sc);
    }
}
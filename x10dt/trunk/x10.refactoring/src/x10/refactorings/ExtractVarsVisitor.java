package x10.refactorings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.LocalDecl;
import polyglot.ast.Local_c;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

import com.ibm.wala.cast.tree.CAstNode;

public class ExtractVarsVisitor extends NodeVisitor {

    public class VarUseType {
        // Maps for determining level of dereference and
        // number of parameter for a given Variable
        private HashMap<Variable, Integer> drefMap;
        private HashMap<Variable, Integer> paramMap;

        // Variable type flags - for mapping Polyglot Variable
        // to WALA PointerKey
        public static final int NONE = 1;
        public static final int LVAL = 2;
        public static final int RVAL = 4;
        public static final int LEFT = 8;
        public static final int RIGHT = 16;

        public static final int PUTFIELD = 32;
        public static final int GETFIELD = 64;

        public static final int DREF = 128;
        public static final int PARAM = 256;

        public static final int ARRAY = 512;
        public static final int X10ARRAY = 1024;
        public static final int X10ARRAY1 = 2048;
        public static final int POINT = 4096;

        public static final int INVOKE_TARGET = 8192;
        public static final int DECL = 16384;
        public static final int LOOP_VAR = 32768;

        /**
         * For a given refactoring, constructor will generate fresh dereference and parameter maps.
         * 
         */
        public VarUseType() {
            drefMap = new HashMap<Variable, Integer>();
            paramMap = new HashMap<Variable, Integer>();
        }

        /**
         * Standard Variable type generation method
         * 
         * @param vn
         *            Variable which is having its type generated
         * @param basetype
         *            Basic type (usually LVAL or RVAL)
         * @param fieldtype
         *            Type for Variables which are fields
         * @param arraytype
         *            Type for Variables which are Java arrays
         * @param x10arraytype
         *            Type for Variables which are X10 point-indexed arrays
         * @param x10array1type
         *            Type for Variables which are other X10 arrays
         * @return Variable type
         */
        private final int getType(Variable vn, int basetype, int fieldtype, int arraytype, int x10arraytype,
                int x10array1type) {
            return basetype | ((vn instanceof Field) ? fieldtype : 0) | ((vn instanceof ArrayAccess) ? arraytype : 0)
                    | (isX10ArrayAccess(vn) ? x10arraytype : 0);
        }

        private boolean isX10ArrayAccess(Variable vn) {
            return false; // (vn instanceof X10ArrayAccess) || (vn instanceof X10ArrayAccess1);
        }

        /**
         * Generates standard type for parameter Variable and also stores the parameter number in the parameter map
         * 
         * @param vn
         *            Variable which is having its type generated
         * @param paramNum
         *            Parameter number
         * @return Variable type
         */
        public final int getStandardParamType(Variable vn, int paramNum) {
            paramMap.put(vn, paramNum);
            return getStandardType(vn, PARAM);
        }

        /**
         * Generates standard type for dereferenced Variable and also stores the dereference number in the dereference
         * map
         * 
         * @param vn
         *            Variable which is having its type generated
         * @param basetype
         *            Basic type (usually LVAL or RVAL)
         * @param drefNum
         *            Dereference number
         * @return Variable type
         */
        public final int getStandardDrefType(Variable vn, int basetype, int drefNum) {
            drefMap.put(vn, drefNum);
            return getStandardType(vn, basetype | DREF);
        }

        /**
         * Generates standard type for a field store
         * 
         * @param vn
         *            Variable which is having its type generated
         * @param basetype
         *            Basic type (usually LVAL)
         * @return Variable type
         */
        public final int getStandardPutType(Variable vn, int basetype) {
            return getType(vn, basetype, PUTFIELD, ARRAY, X10ARRAY, X10ARRAY1);
        }

        /**
         * Generates standard typ for Variable
         * 
         * @param vn
         *            Variable which is having its type generated
         * @param basetype
         *            Basic type (usually LVAL or RVAL)
         * @return Variable type
         */
        public final int getStandardType(Variable vn, int basetype) {
            return getType(vn, basetype, GETFIELD, ARRAY, X10ARRAY, X10ARRAY1);
        }

        /**
         * Looks up parameter number of Variable
         * 
         * @param vn
         *            Key into parameter map
         * @return parameter number
         */
        public final int getParamNumber(Variable vn) {
            return paramMap.get(vn);
        }

        /**
         * Sets parameter number of Variable
         * 
         * @param vn
         *            Key into parameter map
         * @param paramNum
         *            New parameter number
         */
        public final void setParamNumber(Variable vn, int paramNum) {
            paramMap.put(vn, paramNum);
        }

        /**
         * Looks up dereference number of Variable
         * 
         * @param vn
         *            Key into dereference map
         * @return dereference number
         */
        public final int getDrefNumber(Variable vn) {
            return drefMap.get(vn);
        }

        /**
         * Sets dereference number of Variable
         * 
         * @param vn Key into dereference map
         * @param drefNum New dereference number
         */
        public final void setDrefNumber(Variable vn, int drefNum) {
            drefMap.put(vn, drefNum);
        }
    }

    Collection<Variable> evCol;
    HashMap<Variable, Integer> evTypeMap;
    Collection<CAstNode> caNodes;
    VarUseType vutype;

    private HashMap<RefactoringPosition, Variable> evMap;
    private final NodeFactory fNodeFactory;

    public ExtractVarsVisitor(NodeFactory nodeFactory) {
        this.fNodeFactory = nodeFactory;
        evCol = new ArrayList<Variable>();
        evTypeMap = new HashMap<Variable, Integer>();
        vutype = new VarUseType();
        caNodes = null;
        evMap = null;
    }

    /**
     * Populates evCol and evMap. Also, generates parameter information and basic derefence information.
     */
    @Override
    public NodeVisitor enter(Node par, Node n) {
        if (n instanceof For) {
            For nfor = (For) n;
        }
        if (n instanceof Formal) {
            Formal nformal = (Formal) n;
            Local_c nlocal = new Local_c(nformal.position(), nformal.name());
            nlocal.localInstance(nformal.localDef().asInstance());
            evCol.add(nlocal);
            evTypeMap.put(nlocal, VarUseType.LVAL | VarUseType.DECL
                    | ((par instanceof X10Loop) ? VarUseType.LOOP_VAR : 0));
        }
        if (n instanceof LocalDecl) {
            LocalDecl ndecl = (LocalDecl) n;
            if (ndecl.type().type().isReference()) {
                Position npos = n.position();
                Position p = new Position(npos.path(), npos.file(), npos.line(), npos.column(), npos.endLine(), npos
                        .column()
                        + ndecl.name().id().toString().length() - 1);
                Local_c nlocal = new Local_c(npos, ndecl.name());
                nlocal.localInstance(ndecl.localDef().asInstance());
                evCol.add(nlocal);
                evTypeMap.put(nlocal, VarUseType.LVAL | VarUseType.DECL);
            }
        }
        if (n instanceof Variable) {
            Variable vn = (Variable) n;
            if (vn.type().isReference()) {
                evCol.add(vn);
                if (par instanceof ArrayAccess) {
                    ArrayAccess apar = (ArrayAccess) par;
                    Integer parType = evTypeMap.get(apar);
                    parType = ((parType & VarUseType.PARAM) != 0) ? parType ^ VarUseType.PARAM | VarUseType.RVAL
                            : parType;
                    if ((parType & VarUseType.DREF) != 0)
                        vutype.setDrefNumber(vn, 1);
                    if (vn.equals(apar.array())) {
                        if (((parType & VarUseType.DREF) != 0) && ((parType & VarUseType.LVAL) != 0))
                            evTypeMap.put(vn, vutype.getStandardType(vn, parType ^ VarUseType.ARRAY ^ VarUseType.LVAL
                                    | VarUseType.RVAL));
                        else
                            evTypeMap.put(vn, vutype.getStandardType(vn, parType ^ VarUseType.ARRAY));
                    } else { // if (apar instanceof X10ArrayAccessAssign) {
                        evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.POINT));
                        throw new IllegalArgumentException("Need to port array access code to X10 1.7");
                    }
                } else if (true /*par instanceof X10ArrayAccess1*/) {
                    // TODO port array access code to X10 1.7
                    ArrayAccess apar = (ArrayAccess) par; // bogus, but compiles
//                    X10ArrayAccess1 apar = (X10ArrayAccess1) par;
                    Integer parType = evTypeMap.get(apar);
                    parType = ((parType & VarUseType.PARAM) != 0) ? parType ^ VarUseType.PARAM | VarUseType.RVAL
                            : parType;
                    if ((parType & VarUseType.DREF) != 0)
                        vutype.setDrefNumber(vn, 1);
                    if (vn.equals(apar.array()))
                        if (((parType & VarUseType.DREF) != 0) && ((parType & VarUseType.LVAL) != 0))
                            evTypeMap.put(vn, vutype.getStandardType(vn, parType ^ VarUseType.ARRAY ^ VarUseType.LVAL
                                    | VarUseType.RVAL));
                        else
                            evTypeMap.put(vn, vutype.getStandardType(vn, parType ^ VarUseType.ARRAY));
                    else
                        // if (apar instanceof X10ArrayAccessAssign){
                        evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.POINT));
                    // }
                } else if (par instanceof Assign) {
                    Assign assign = (Assign) par;
                    Expr lhs= assign.left(fNodeFactory);
                    if (vn.equals(lhs))
                        evTypeMap.put(vn, vutype.getStandardPutType(vn, VarUseType.LVAL));
                    else
                        evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.RVAL));
                }
                // else if (par instanceof Binary) {
                // Do not care about binary or unary expressions
                //					
                // if (vn.equals(((Binary) par).left()))
                // evTypeMap.put(vn, VarUseType.LEFT | ((vn instanceof Field)?VarUseType.GETFIELD:0));
                // else
                // evTypeMap.put(vn, VarUseType.RIGHT | ((vn instanceof Field)?VarUseType.GETFIELD:0)); }
                else if (par instanceof LocalDecl) {
                    evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.RVAL));
                } else if (par instanceof Field) {
                    Integer parType = evTypeMap.get(par);
                    if ((parType & VarUseType.PUTFIELD) != 0) {
                        if ((vn instanceof Field) || (vn instanceof ArrayAccess))
                            evTypeMap.put(vn, vutype.getStandardDrefType(vn, VarUseType.RVAL, 1));
                        else
                            evTypeMap.put(vn, vutype.getStandardDrefType(vn, VarUseType.LVAL, 1));
                    } else {
                        evTypeMap.put(vn, vutype.getStandardDrefType(vn, VarUseType.RVAL, 1));// (parType ^
                                                                                              // VarUseType.GETFIELD)));
                    }
                } else if ((par instanceof Call) || (par instanceof New)) {
                    Object[] parArgs;
                    if (par instanceof Call)
                        parArgs = ((Call) par).arguments().toArray();
                    else
                        parArgs = ((New) par).arguments().toArray();
                    for (int j = 0; j < parArgs.length && j < 3; j++) {
                        if (vn.equals(parArgs[j])) {
                            evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.PARAM));
                            vutype.setParamNumber(vn, j + 1);
                            return super.enter(n);
                        }
                    }
                    evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.INVOKE_TARGET));
                } else if (par instanceof ForLoop) {
                    if (vn.equals(((ForLoop) par).domain()))
                        evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.INVOKE_TARGET));
                } else
                    evTypeMap.put(vn, vutype.getStandardType(vn, VarUseType.NONE));
            }
        }
        return super.enter(n);
    }

    /**
     * Fixes derefence information for Variables by counting from the base dereferenced Variable up to the ultimate
     * Variable.
     */
    @Override
    public Node leave(Node par, Node old, Node n, NodeVisitor nv) {
        if ((n instanceof Variable) && ((Variable) n).type().isReference() && (par instanceof Variable)) {
            if (((evTypeMap.get((Variable) n) & VarUseType.DREF) != 0)
                    && ((evTypeMap.get((Variable) par) & VarUseType.DREF) != 0))
                vutype.setDrefNumber((Variable) par, vutype.getDrefNumber((Variable) n) + 1);
        }
        return leave(old, n, nv);
    }

    public Collection<Variable> getVars() {
        return evCol;
    }

    public Integer getVarType(Variable v) {
        return evTypeMap.get(v);
    }

    public HashMap<RefactoringPosition, Variable> getVarMap() {
        if (evMap == null) {
            HashMap<RefactoringPosition, Variable> hm = new HashMap<RefactoringPosition, Variable>(10);
            for (Variable v : evCol) {
                RefactoringPosition rp = new RefactoringPosition(v.position());
                rp.setCheckMode();
                hm.put(rp, v);
            }
            for (RefactoringPosition rp : hm.keySet())
                rp.setCheckMode();
            evMap = hm;
        }
        return evMap;
    }

}

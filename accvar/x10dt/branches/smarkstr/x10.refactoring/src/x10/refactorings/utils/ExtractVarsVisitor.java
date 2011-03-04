package x10.refactorings.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.X10ArrayAccess;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.ext.x10.ast.X10ArrayAccessAssign;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.ext.x10.extension.X10Ext;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.refactorings.RefactoringPosition;

import com.ibm.wala.cast.tree.CAstNode;

public class ExtractVarsVisitor extends NodeVisitor {

	public class VarUseType {
		// Maps for determining level of dereference and
		// number of parameter for a given Variable
		private HashMap<Variable, Integer> drefMap;
		private HashMap<Variable, Integer> paramMap;

		// Variable type flags - for mapping Polyglot Variable
		// to WALA PointerKey
		public static final int NONE = 1 << 0;
		public static final int LVAL = 1 << 1;
		public static final int RVAL = 1 << 2;
		public static final int LEFT = 1 << 3;
		public static final int RIGHT = 1 << 4;

		public static final int PUTFIELD = 1 << 5;
		public static final int GETFIELD = 1 << 6;

		public static final int DREF = 1 << 7;
		public static final int PARAM = 1 << 8;

		public static final int ARRAY = 1 << 9;
		public static final int X10ARRAY = 1 << 10;
		public static final int X10ARRAY1 = 1 << 11;
		public static final int POINT = 1 << 12;

		public static final int INVOKE_TARGET = 1 << 13;
		public static final int DECL = 1 << 14;
		public static final int LOOP_VAR = 1 << 15;

		/**
		 * For a given refactoring, constructor will generate fresh dereference
		 * and parameter maps.
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
		private final int getType(Variable vn, int basetype, int fieldtype,
				int arraytype, int x10arraytype, int x10array1type) {
			return basetype | ((vn instanceof Field) ? fieldtype : 0)
					| ((vn instanceof ArrayAccess) ? arraytype : 0)
					| ((vn instanceof X10ArrayAccess) ? x10arraytype : 0)
					| ((vn instanceof X10ArrayAccess1) ? x10array1type : 0);
		}

		/**
		 * Generates standard type for parameter Variable and also stores the
		 * parameter number in the parameter map
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
		 * Generates standard type for dereferenced Variable and also stores the
		 * dereference number in the dereference map
		 * 
		 * @param vn
		 *            Variable which is having its type generated
		 * @param basetype
		 *            Basic type (usually LVAL or RVAL)
		 * @param drefNum
		 *            Dereference number
		 * @return Variable type
		 */
		public final int getStandardDrefType(Variable vn, int basetype,
				int drefNum) {
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
		 * @param vn
		 *            Key into dereference map
		 * @param drefNum
		 *            New dereference number
		 */
		public final void setDrefNumber(Variable vn, int drefNum) {
			drefMap.put(vn, drefNum);
		}
	}

	NodeFactory nf;
	Collection<Variable> evCol;
	HashMap<Variable, Integer> evTypeMap;
	Collection<CAstNode> caNodes;
	VarUseType vutype;

	private HashMap<RefactoringPosition, Variable> evMap;

	public ExtractVarsVisitor(NodeFactory nf) {
		this.nf = nf;
		evCol = new ArrayList<Variable>();
		evTypeMap = new HashMap<Variable, Integer>();
		vutype = new VarUseType();
		caNodes = null;
		evMap = null;
	}

	/**
	 * Resets the state of the variable extracting visitor.
	 */
	public void reset() {
		evCol = new ArrayList<Variable>();
		evTypeMap = new HashMap<Variable, Integer>();
		vutype = new VarUseType();
		caNodes = null;
		evMap = null;
	}

	public VarUseType getVUType() {
		return vutype;
	}

	/**
	 * Populates evCol and evMap. Also, generates parameter information and
	 * basic derefence information.
	 */
	@Override
	public NodeVisitor enter(Node par, Node n) {
		if (n instanceof For) {
			For nfor = (For) n;
		}
		if (n instanceof Formal) {
			Formal nformal = (Formal) n;
			Local nlocal = nf.Local(nformal.position(), nformal.id());
			nlocal = nlocal.localInstance(nformal.localInstance());
			evCol.add(nlocal);
			evTypeMap.put(nlocal, VarUseType.LVAL | VarUseType.DECL
					| ((par instanceof X10Loop) ? VarUseType.LOOP_VAR : 0));
		}
		if (n instanceof LocalDecl) {
			LocalDecl ndecl = (LocalDecl) n;
			if (ndecl.type().type().isReference()) {
				Position npos = n.position();
				Position p = new Position(npos.path(), npos.file(),
						npos.line(), npos.column(), npos.endLine(), npos
								.column()
								+ ndecl.id().id().length() - 1);
				Local nlocal = nf.Local(npos, ndecl.id());
				nlocal = nlocal.localInstance(ndecl.localInstance());
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
					parType = ((parType & VarUseType.PARAM) != 0) ? parType
							^ VarUseType.PARAM | VarUseType.RVAL : parType;
					if ((parType & VarUseType.DREF) != 0)
						vutype.setDrefNumber(vn, 1);
					if (vn.equals(apar.array()))
						if (((parType & VarUseType.DREF) != 0)
								&& ((parType & VarUseType.LVAL) != 0))
							evTypeMap.put(vn,
									vutype
											.getStandardType(vn, parType
													^ VarUseType.ARRAY
													^ VarUseType.LVAL
													| VarUseType.RVAL));
						else
							evTypeMap.put(vn, vutype.getStandardType(vn,
									parType ^ VarUseType.ARRAY));
					else if (apar instanceof X10ArrayAccessAssign) {
						evTypeMap.put(vn, vutype.getStandardType(vn,
								VarUseType.POINT));
					}
				} else if (par instanceof X10ArrayAccess1) {
					X10ArrayAccess1 apar = (X10ArrayAccess1) par;
					Integer parType = evTypeMap.get(apar);
					parType = ((parType & VarUseType.PARAM) != 0) ? parType
							^ VarUseType.PARAM | VarUseType.RVAL : parType;
					if ((parType & VarUseType.DREF) != 0)
						vutype.setDrefNumber(vn, 1);
					if (vn.equals(apar.array()))
						if (((parType & VarUseType.DREF) != 0)
								&& ((parType & VarUseType.LVAL) != 0))
							evTypeMap.put(vn,
									vutype
											.getStandardType(vn, parType
													^ VarUseType.ARRAY
													^ VarUseType.LVAL
													| VarUseType.RVAL));
						else
							evTypeMap.put(vn, vutype.getStandardType(vn,
									parType ^ VarUseType.ARRAY));
					else if (apar instanceof X10ArrayAccessAssign) {
						evTypeMap.put(vn, vutype.getStandardType(vn,
								VarUseType.POINT));
					}
				} else if (par instanceof Assign) {
					if (vn.equals(((Assign) par).left()))
						evTypeMap.put(vn, vutype.getStandardPutType(vn,
								VarUseType.LVAL));
					else
						evTypeMap.put(vn, vutype.getStandardType(vn,
								VarUseType.RVAL));
				}
				// else if (par instanceof Binary) {
				// Do not care about binary or unary expressions
				//					
				// if (vn.equals(((Binary) par).left()))
				// evTypeMap.put(vn, VarUseType.LEFT | ((vn instanceof
				// Field)?VarUseType.GETFIELD:0));
				// else
				// evTypeMap.put(vn, VarUseType.RIGHT | ((vn instanceof
				// Field)?VarUseType.GETFIELD:0)); }
				else if (par instanceof LocalDecl) {
					evTypeMap.put(vn, vutype.getStandardType(vn,
							VarUseType.RVAL));
				} else if (par instanceof Field) {
					Integer parType = evTypeMap.get(par);
					if ((parType & VarUseType.PUTFIELD) != 0) {
						if ((vn instanceof Field)
								|| (vn instanceof ArrayAccess))
							evTypeMap.put(vn, vutype.getStandardDrefType(vn,
									VarUseType.RVAL, 1));
						else
							evTypeMap.put(vn, vutype.getStandardDrefType(vn,
									VarUseType.LVAL, 1));
					} else {
						evTypeMap.put(vn, vutype.getStandardDrefType(vn,
								VarUseType.RVAL, 1));// (parType ^
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
							evTypeMap.put(vn, vutype.getStandardType(vn,
									VarUseType.PARAM));
							vutype.setParamNumber(vn, j + 1);
							return super.enter(n);
						}
					}
					evTypeMap.put(vn, vutype.getStandardType(vn,
							VarUseType.INVOKE_TARGET));
				} else if (par instanceof ForLoop) {
					if (vn.equals(((ForLoop) par).domain()))
						evTypeMap.put(vn, vutype.getStandardType(vn,
								VarUseType.INVOKE_TARGET));
				} else
					evTypeMap.put(vn, vutype.getStandardType(vn,
							VarUseType.NONE));
			}
		}
		return super.enter(n);
	}

	/**
	 * Fixes dereference information for Variables by counting from the base
	 * dereferenced Variable up to the ultimate Variable.
	 */
	@Override
	public Node leave(Node par, Node old, Node n, NodeVisitor nv) {
		if ((n instanceof Variable) && ((Variable) n).type().isReference()
				&& (par instanceof Variable)) {
			if (((evTypeMap.get((Variable) n) & VarUseType.DREF) != 0)
					&& ((evTypeMap.get((Variable) par) & VarUseType.DREF) != 0))
				vutype.setDrefNumber((Variable) par, vutype
						.getDrefNumber((Variable) n) + 1);
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
			HashMap<RefactoringPosition, Variable> hm = new HashMap<RefactoringPosition, Variable>(
					10);
			for (Variable v : evCol) {
				RefactoringPosition rp = new RefactoringPosition(v.position());
				int nameLength = rp.endCol - rp.col + 1;
				if (!((X10Ext) v.ext()).generated()) {
					rp.setCheckMode();
					hm.put(rp, v);
				}
			}
			for (RefactoringPosition rp : hm.keySet())
				rp.setCheckMode();
			evMap = hm;
		}
		return evMap;
	}

}

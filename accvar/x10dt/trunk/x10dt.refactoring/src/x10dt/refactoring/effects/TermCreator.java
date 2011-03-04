/**
 * 
 */
package x10dt.refactoring.effects;

import java.util.HashMap;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CharLit;
import polyglot.ast.ClassLit;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FloatLit;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.StringLit;
import polyglot.ast.Unary;
import polyglot.types.FieldInstance;
import polyglot.types.Qualifier;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.NodeVisitor;
import x10.ast.SettableAssign;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class TermCreator {
        private static Map<Unary.Operator,String> sUnaryOpMap= new HashMap<Unary.Operator,String>();
        static {
            sUnaryOpMap.put(Unary.BIT_NOT, Unary.BIT_NOT.toString());
            sUnaryOpMap.put(Unary.NEG, Unary.NEG.toString());
            sUnaryOpMap.put(Unary.NOT, Unary.NOT.toString());
            sUnaryOpMap.put(Unary.POS, Unary.POS.toString());
            sUnaryOpMap.put(Unary.BIT_NOT, Unary.BIT_NOT.toString());
        }

        private static Map<Binary.Operator,String> sBinaryOpMap= new HashMap<Binary.Operator,String>();
        static {
            sBinaryOpMap.put(Binary.ADD, Binary.ADD.toString());
            sBinaryOpMap.put(Binary.BIT_AND, Binary.BIT_AND.toString());
            sBinaryOpMap.put(Binary.BIT_OR, Binary.BIT_OR.toString());
            sBinaryOpMap.put(Binary.BIT_XOR, Binary.BIT_XOR.toString());
            sBinaryOpMap.put(Binary.COND_AND, Binary.COND_AND.toString());
            sBinaryOpMap.put(Binary.COND_OR, Binary.COND_OR.toString());
            sBinaryOpMap.put(Binary.DIV, Binary.DIV.toString());
            sBinaryOpMap.put(Binary.EQ, Binary.EQ.toString());
            sBinaryOpMap.put(Binary.GE, Binary.GE.toString());
            sBinaryOpMap.put(Binary.GT, Binary.GT.toString());
            sBinaryOpMap.put(Binary.LE, Binary.LE.toString());
            sBinaryOpMap.put(Binary.LT, Binary.LT.toString());
            sBinaryOpMap.put(Binary.MOD, Binary.MOD.toString());
            sBinaryOpMap.put(Binary.MUL, Binary.MUL.toString());
            sBinaryOpMap.put(Binary.NE, Binary.NE.toString());
            sBinaryOpMap.put(Binary.SHL, Binary.SHL.toString());
            sBinaryOpMap.put(Binary.SHR, Binary.SHR.toString());
            sBinaryOpMap.put(Binary.SUB, Binary.SUB.toString());
            sBinaryOpMap.put(Binary.USHR, Binary.USHR.toString());
        }

        private String getNameFor(Binary.Operator op) {
            return sBinaryOpMap.get(op);
        }

        private String getNameFor(Unary.Operator op) {
            return sUnaryOpMap.get(op);
        }

        private class TermVisitor extends NodeVisitor {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (old instanceof BooleanLit) {
                    BooleanLit booleanLit = (BooleanLit) old;

                    fTermMap.put(old, XTerms.makeLit(booleanLit.value()));
                } else if (old instanceof FloatLit) {
                    FloatLit floatLit = (FloatLit) old;

                    fTermMap.put(old, XTerms.makeLit(floatLit.value()));
                } else if (old instanceof CharLit) {
                    CharLit charLit = (CharLit) old;

                    fTermMap.put(old, XTerms.makeLit(charLit.value()));
                } else if (old instanceof IntLit) {
                    IntLit intLit = (IntLit) old;

                    fTermMap.put(old, XTerms.makeLit(intLit.value()));
                } else if (old instanceof StringLit) {
                    StringLit stringLit = (StringLit) old;

                    fTermMap.put(old, XTerms.makeLit(stringLit.value()));
                } else if (old instanceof ClassLit) {
                    throw new UnsupportedOperationException("Can't handle class literals.");
                } else if (old instanceof CanonicalTypeNode) {
                    CanonicalTypeNode canonicalTypeNode = (CanonicalTypeNode) old;
                    Qualifier qualifier= canonicalTypeNode.qualifierRef().get();
                    String shortName= canonicalTypeNode.nameString();

                    fTermMap.put(old, XTerms.makeLit(qualifier.toString() + "." + shortName));
                } else if (old instanceof Field) {
                    Field field = (Field) old;
                    Receiver target= field.target();
                    Id name= field.name();

                    fTermMap.put(old, XTerms.makeFakeField((XVar) fTermMap.get(target), field));
                } else if (old instanceof Local) {
                    Local local = (Local) old;
                    Type localType= local.type();
                    TypeSystem ts= (TypeSystem) localType.typeSystem();
                    Type t = Types.baseType(local.type());
                    /* XArray no longer exists
                    if (t.isArray() || t.isClass() && ts.descendsFrom(t.toClass().def(), ts.Array().toClass().def())) {
                        fTermMap.put(old, XTerms.makeArray(new XVarDefWrapper(local)));
                    } else {
                        fTermMap.put(old, XTerms.makeLocal(new XVarDefWrapper(local)));
                    }
                    */
                    fTermMap.put(old, new XLocal(local));
                    
                } else if (old instanceof Binary) {
                    Binary binary = (Binary) old;
                    Binary.Operator op= binary.operator();
                    Expr lhs= binary.left();
                    Expr rhs= binary.right();
                    XTerm lhsTerm= fTermMap.get(lhs);
                    XTerm rhsTerm= fTermMap.get(rhs);

                    fTermMap.put(old, XTerms.makeAtom(getNameFor(op), lhsTerm, rhsTerm));
                } else if (old instanceof Unary) {
                    Unary unary = (Unary) old;
                    Unary.Operator op= unary.operator();
                    Expr opnd= unary.expr();
                    XTerm opndTerm= fTermMap.get(opnd);

                    fTermMap.put(old, XTerms.makeAtom(getNameFor(op), opndTerm));
                } else if (old instanceof Call) {
                    Call call = (Call) old;
                    
                    throw new UnsupportedOperationException("Don't know how to create an XTerm for a method call.");
                } else if (old instanceof SettableAssign) {
                    SettableAssign sa= (SettableAssign) old;
                    /* XArray no longer exists
                    Expr array= sa.array();
                    List<Expr> indices= sa.index();
                    XTerm arrayTerm= fTermMap.get(array);
                    XTerm indexTerm= fTermMap.get(indices.get(0));
                    
                    fTermMap.put(old, XTerms.makeArrayElement((XArray) arrayTerm, indexTerm));
                    */
                    throw new UnsupportedOperationException("Don't know how to create an XTerm for a settable assign.");
                } else if (old instanceof FieldAssign) {
                    FieldAssign fa= (FieldAssign) old;
                    FieldInstance fi= fa.fieldInstance();
                    Receiver target= fa.target();

                    fTermMap.put(old, XTerms.makeFakeField((XVar) fTermMap.get(target), fi.def()));
                } else if (old instanceof LocalAssign) {
                    LocalAssign la= (LocalAssign) old;
                    Local l= la.local();

                    fTermMap.put(old, new XLocal(l));
                } else if (old instanceof Special) {
                    Special special = (Special) old;
                    if (special.kind() == Special.SUPER) {
                        fTermMap.put(old, new XLocal("super"));
                    } else {
                        fTermMap.put(old, new XLocal("this"));
                    }
                } else if (old instanceof Id) {
                    // do nothing
//                    EffectsVisitor.fDiagStream.println("TermVisitor doing nothing for expr of type " + old.getClass().getCanonicalName());
                } else {
                    throw new UnsupportedOperationException("Unknown expression type");
                }
                return super.leave(parent, old, n, v);
            }
        }

        private final Expr fExpr;
        private final Map<Node,XTerm> fTermMap= new HashMap<Node,XTerm>();
        private final TermVisitor fVisitor= new TermVisitor();

        public TermCreator(Expr e) {
            fExpr= e;
            fExpr.visit(fVisitor);
        }

        public XTerm getTerm() {
            return fTermMap.get(fExpr);
        }
    }
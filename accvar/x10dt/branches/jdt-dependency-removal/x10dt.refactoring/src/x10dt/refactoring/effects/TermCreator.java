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
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

public class TermCreator {
        private static Map<Unary.Operator,XName> sUnaryOpMap= new HashMap<Unary.Operator,XName>();
        static {
            sUnaryOpMap.put(Unary.BIT_NOT, new XNameWrapper<Unary.Operator>(Unary.BIT_NOT));
            sUnaryOpMap.put(Unary.NEG, new XNameWrapper<Unary.Operator>(Unary.NEG));
            sUnaryOpMap.put(Unary.NOT, new XNameWrapper<Unary.Operator>(Unary.NOT));
            sUnaryOpMap.put(Unary.POS, new XNameWrapper<Unary.Operator>(Unary.POS));
            sUnaryOpMap.put(Unary.BIT_NOT, new XNameWrapper<Unary.Operator>(Unary.BIT_NOT));
        }

        private static Map<Binary.Operator,XName> sBinaryOpMap= new HashMap<Binary.Operator,XName>();
        static {
            sBinaryOpMap.put(Binary.ADD, new XNameWrapper<Binary.Operator>(Binary.ADD));
            sBinaryOpMap.put(Binary.BIT_AND, new XNameWrapper<Binary.Operator>(Binary.BIT_AND));
            sBinaryOpMap.put(Binary.BIT_OR, new XNameWrapper<Binary.Operator>(Binary.BIT_OR));
            sBinaryOpMap.put(Binary.BIT_XOR, new XNameWrapper<Binary.Operator>(Binary.BIT_XOR));
            sBinaryOpMap.put(Binary.COND_AND, new XNameWrapper<Binary.Operator>(Binary.COND_AND));
            sBinaryOpMap.put(Binary.COND_OR, new XNameWrapper<Binary.Operator>(Binary.COND_OR));
            sBinaryOpMap.put(Binary.DIV, new XNameWrapper<Binary.Operator>(Binary.DIV));
            sBinaryOpMap.put(Binary.EQ, new XNameWrapper<Binary.Operator>(Binary.EQ));
            sBinaryOpMap.put(Binary.GE, new XNameWrapper<Binary.Operator>(Binary.GE));
            sBinaryOpMap.put(Binary.GT, new XNameWrapper<Binary.Operator>(Binary.GT));
            sBinaryOpMap.put(Binary.LE, new XNameWrapper<Binary.Operator>(Binary.LE));
            sBinaryOpMap.put(Binary.LT, new XNameWrapper<Binary.Operator>(Binary.LT));
            sBinaryOpMap.put(Binary.MOD, new XNameWrapper<Binary.Operator>(Binary.MOD));
            sBinaryOpMap.put(Binary.MUL, new XNameWrapper<Binary.Operator>(Binary.MUL));
            sBinaryOpMap.put(Binary.NE, new XNameWrapper<Binary.Operator>(Binary.NE));
            sBinaryOpMap.put(Binary.SHL, new XNameWrapper<Binary.Operator>(Binary.SHL));
            sBinaryOpMap.put(Binary.SHR, new XNameWrapper<Binary.Operator>(Binary.SHR));
            sBinaryOpMap.put(Binary.SUB, new XNameWrapper<Binary.Operator>(Binary.SUB));
            sBinaryOpMap.put(Binary.USHR, new XNameWrapper<Binary.Operator>(Binary.USHR));
        }

        private XName getNameFor(Binary.Operator op) {
            return sBinaryOpMap.get(op);
        }

        private XName getNameFor(Unary.Operator op) {
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

                    fTermMap.put(old, XTerms.makeField((XVar) fTermMap.get(target), new XVarDefWrapper(field)));
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
                    fTermMap.put(old, XTerms.makeLocal(new XVarDefWrapper(local)));
                    
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

                    fTermMap.put(old, XTerms.makeField((XVar) fTermMap.get(target), new XVarDefWrapper(fi.def())));
                } else if (old instanceof LocalAssign) {
                    LocalAssign la= (LocalAssign) old;
                    Local l= la.local();

                    fTermMap.put(old, XTerms.makeLocal(new XVarDefWrapper(l)));
                } else if (old instanceof Special) {
                    Special special = (Special) old;
                    if (special.kind() == Special.SUPER) {
                        fTermMap.put(old, XTerms.makeLocal(XTerms.makeName("super")));
                    } else {
                        fTermMap.put(old, XTerms.makeLocal(XTerms.makeName("this")));
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
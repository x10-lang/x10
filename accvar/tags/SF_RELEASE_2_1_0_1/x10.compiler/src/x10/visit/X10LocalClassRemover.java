/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.SubtypeSet;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.LocalClassRemover;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeBuilder;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10NodeFactory;
import x10.types.ParameterType;
import x10.types.TypeParamSubst;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10Context_c;
import x10.types.X10MethodDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

public class X10LocalClassRemover extends LocalClassRemover {

    /**
     * The type to be extended when translating an anonymous class that
     * implements an interface.
     */
    protected TypeNode defaultSuperType(Position pos) {
        X10TypeSystem ts = (X10TypeSystem) this.ts;
        return nf.CanonicalTypeNode(pos, ts.Object());
    }

    protected class X10ConstructorCallRewriter extends ConstructorCallRewriter {
        private List<? extends Type> typeArgs;
        public X10ConstructorCallRewriter(List<FieldDef> fields, ClassDef ct, List<? extends Type> typeArgs) {
            super(fields, ct);
            this.typeArgs = typeArgs;
        }
            
        public Node leave(Node old, Node n, NodeVisitor v) {
            Node n_ = super.leave(old, n, v);
            
            if (n_ instanceof New) {
                New neu = (New) n_;
                X10ConstructorInstance ci = (X10ConstructorInstance) neu.constructorInstance();
                ConstructorDef nci = ci.def();
                X10ClassType container = (X10ClassType) Types.get(nci.container());
                
                if (container.def() == theLocalClass) {
                    X10ClassType type = (X10ClassType) X10TypeMixin.baseType(neu.objectType().type());
                    List<Type> ta = new ArrayList<Type>(type.typeArguments());
                    List<ParameterType> params = type.x10Def().typeParameters();
                    if (!params.isEmpty() && (ta.equals(params) || ta.isEmpty())) {
                        ta.clear();
                        ta.addAll(typeArgs);
                        assert (typeArgs.size() == params.size());
                    }
                    TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, ta, params);
                    X10ConstructorInstance xci = (X10ConstructorInstance) subst.reinstantiate(ci);
                    neu = neu.constructorInstance(xci);
                    neu = neu.objectType(nf.CanonicalTypeNode(neu.objectType().position(), subst.reinstantiate(type)));
                    neu = (New) neu.type(subst.reinstantiate(neu.type()));
                    // FIX:XTENLANG-949 (for mismatch between neu.argument and neu.ci.formalTypes)
                    if (neu.arguments().size() > ci.formalTypes().size()) {
                        List<Type> newFormalTypes = new ArrayList<Type>();
                        for (Expr arg : neu.arguments()) {
                            newFormalTypes.add(arg.type());
                        }
                        neu = neu.constructorInstance(ci.formalTypes(newFormalTypes));
                    }
                }
                
                return neu;
            }
            
            return n_;
        }
    }

    public X10LocalClassRemover(X10InnerClassRemover icrv) {
        super(icrv);
    }

    @Override
    protected X10ConstructorInstance computeConstructorInstance(ConstructorDef cd) {
        ClassDef container = ((X10ClassType) Types.get(cd.container())).def();
        return (X10ConstructorInstance) cd.asInstance().container(computeConstructedType(container));
    }

    @Override
    protected X10ClassType computeConstructedType(ClassDef cd) {
        X10ClassDef def = (X10ClassDef) cd;
        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        assert outer != null;
        X10ClassType t = ((X10ClassType)def.asType()).typeArguments(new ArrayList<Type>(outer.typeParameters()));
        return t;
    }

    /**
     * Rewrites the class L as follows:
     * <pre>
     * class X[A,B]{g} {
     *     def m[C,D](){h} {
     *         class L[E,F]{c} extends S[A,B,C,D,E,F] {
     *             body
     *         }
     *         val v = new L[P,Q]();
     *     }
     * }
     * </pre>
     * to
     * <pre>
     * class X[A,B]{g} {
     *     class L'[E,F,C',D']{c[C'/C,D'/D]&&h[C'/C,D'/D]} extends S[A,B,C',D',E,F] {
     *         body[C'/C,D'/D]
     *     }
     *     def m[C,D]() {
     *         val v = new L[P,Q,C,D]();
     *     }
     * }
     * </pre>
     */
    @Override
    protected ClassDecl rewriteLocalClass(ClassDecl n, List<FieldDef> newFields) {
        assert (n instanceof X10ClassDecl && n.classDef().isMember());
        X10ClassDecl cd = (X10ClassDecl) n;
        X10ClassDef def = cd.classDef();
        X10ClassDef outer = (X10ClassDef) Types.get(def.outer());
        X10CodeDef method = (X10CodeDef) context.currentCode();
        assert outer != null;
        assert method != null;

        List<TypeParamNode> params = new ArrayList<TypeParamNode>();
        List<ParameterType> typeParameters = new ArrayList<ParameterType>();
        List<ParameterType.Variance> variances = new ArrayList<ParameterType.Variance>();

        typeParameters.addAll(method.typeParameters());
        for (ParameterType pt : method.typeParameters()) {
            // methods cannot have variant type parameters
            variances.add(ParameterType.Variance.INVARIANT);
        }

        for (int i = 0; i < typeParameters.size(); i++) {
            ParameterType p = typeParameters.get(i);
            ParameterType.Variance v = variances.get(i);

            X10NodeFactory xnf = (X10NodeFactory) nf;
            TypeParamNode pn = xnf.TypeParamNode(n.position(), xnf.Id(n.position(), Name.makeFresh(p.name())), v);
            TypeBuilder tb = new TypeBuilder(job, ts, nf);
            try {
                tb = tb.pushClass(outer);
                tb = tb.pushCode(method);
                tb = tb.pushClass(def);
                pn = (TypeParamNode) pn.del().buildTypes(tb);
                def.addTypeParameter(pn.type(), v);
            }
            catch (SemanticException e) {
                throw new InternalCompilerError(e);
            }
            params.add(pn);
        }

        if (! params.isEmpty()) {
            cd = cd.typeParameters(params);
            TypeParamSubst subst = new TypeParamSubst((X10TypeSystem) ts, def.typeParameters(), typeParameters);
            cd = rewriteTypeParams(subst, cd);
        }

        n = cd.body((ClassBody) rewriteConstructorCalls(cd.body(), def, newFields));

        return icrv.addFieldsToClass(n, newFields, ts, nf, false);
    }

    private X10ClassDecl rewriteTypeParams(final TypeParamSubst subst, X10ClassDecl cd) {
        return (X10ClassDecl) cd.visit(new NodeTransformingVisitor(job, ts, nf, new TypeParamSubstTransformer(subst)).context(context));
    }

    @Override
    protected boolean isLocal(Context c, Name name) {
        X10Context_c xcon = (X10Context_c)c;
        CodeDef ci = xcon.definingCodeDef(name);
        if (ci == null) return false;
        while (c != null) {
            CodeDef curr = c.currentCode();
            if (curr == ci) return true;
            // Allow closures, asyncs
            if (curr instanceof MethodDef && ((MethodDef) curr).name().equals(Name.make("$dummyAsync$")))
                ;
            else {
                // FIX:XTENLANG-1159
                return xcon.isValInScopeInClass(name);
            }
            c = c.pop();
        }
        // FIX:XTENLANG-1159
        return xcon.isValInScopeInClass(name);
    }
    
    protected Node rewriteConstructorCalls(Node s, final ClassDef ct, final List<FieldDef> fields) {
        Node r = s.visit(new X10ConstructorCallRewriter(fields, ct, ((X10ClassDef) ct).typeParameters()));
        return r;
    }
}

/**
 * 
 */
package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.X10ClassDecl;
import polyglot.ext.x10.ast.X10New;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.ParameterType;
import polyglot.ext.x10.types.TypeParamSubst;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.ParameterType.Variance;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.FieldDef;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.InnerClassRemover;
import polyglot.visit.NodeVisitor;

public class X10InnerClassRemover extends InnerClassRemover {
    public X10InnerClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    protected ContextVisitor localClassRemover() {
        return new X10LocalClassRemover(job, ts, nf);
    }
    
    /*
    @Override
    protected Node leaveCall(Node old, Node n, NodeVisitor v)
    throws SemanticException {

        // Add the qualifier as an argument to constructor calls.
        if (n instanceof X10New) {
            X10New neu = (X10New) n;

            Expr q = neu.qualifier();
            
            if (q != null) {
                neu = (X10New) neu.qualifier(null);
                
                X10ConstructorInstance ci = (X10ConstructorInstance) neu.constructorInstance();
                // Fix the ci.
                {
                    List<Type> args = new ArrayList<Type>();
                    args.add(q.type());
                    args.addAll(ci.formalTypes());
                    ci = (X10ConstructorInstance) ci.formalTypes(args);
                    neu = (X10New) neu.constructorInstance(ci);
                }
                List<Expr> args = new ArrayList<Expr>();
                args.add(q);
                args.addAll(neu.arguments());
                neu = (X10New) neu.arguments(args);
            }

            return neu;
        }

        return super.leaveCall(n);
    }
    */
    

    // Create a field instance for a qualified this.
    protected FieldDef boxThis(ClassDef currClass, ClassDef outerClass) {
        FieldDef fi = outerFieldInstance.get(currClass);
        if (fi != null) return fi;
        
        Position pos = outerClass.position();
        
        X10ClassType currType = (X10ClassType) currClass.asType();
        TypeParamSubst subst = X10LocalClassRemover.inverseSubst(currType);
        Type outerType = outerClass.asType();
        if (subst != null)
            outerType = subst.reinstantiate(outerType);
        
        fi = ts.fieldDef(pos, Types.ref(currClass.asType()), Flags.FINAL.Private(), Types.ref(outerType), OUTER_FIELD_NAME);
        fi.setNotConstant();
        
        currClass.addField(fi);
        
        outerFieldInstance.put(currClass, fi);
        return fi;
    }

}
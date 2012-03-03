package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FieldDecl_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit_c;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AnnotationNode;
import x10.ast.X10FieldDecl;
import x10.extension.X10Ext_c;
import x10.types.X10FieldDef;
import x10.types.constants.ConstantValue;

/**
 * Visitor to optimize static fields with simple initializers 
 * into per-process initialized fields.  
 * Makes them more efficient to access and also avoids generating 
 * complex initialization/serialization code where it is not actually
 * semantically required.
 */
public class StaticFieldAnalyzer extends ContextVisitor {

    public StaticFieldAnalyzer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) {
        if (n instanceof FieldDecl_c) {
            FieldDecl_c fd = (FieldDecl_c) n;
            X10FieldDef f_def = (X10FieldDef)fd.fieldDef();
            if (fd.flags().flags().isStatic() && fd.flags().flags().isFinal() && 
                    f_def.annotationsMatching(ts.NativeType()).isEmpty()) {
                Expr e = fd.init();
                if (e != null) {
                    if (e.isConstant()) {
                        ts.addAnnotation(f_def, ts.PerProcess(), false);
                        Expr e2 = e.constantValue().toLit(nf, ts, e.type(), e.position());
                        return fd.init(e2);
                    } else if (e instanceof StringLit_c) {
                        ts.addAnnotation(f_def, ts.PerProcess(), false);
                        return e;
                    }
                }
            }
        }
        
        return n;
        
    }

}

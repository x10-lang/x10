/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.SourceFile;
import polyglot.ast.TopLevelDecl;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/** 
 * This method removes static nested classes from the AST and promotes them to top-level classes, adjusting the ClassDef. 
 * Does not adjust references to the class, but it might work anyway if ClassType doesn't cache values from the Def.
 * */
public class StaticNestedClassRemover extends ContextVisitor {

    public StaticNestedClassRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }
    
    List<ClassDecl> orphans = new ArrayList<ClassDecl>();
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof ClassBody) {
            ClassBody cb = (ClassBody) n;
            List<ClassMember> members = new ArrayList<ClassMember>();

            for (ClassMember cm : cb.members()) {
                if (cm instanceof ClassDecl) {
                    ClassDecl cd = (ClassDecl) cm;
                    
                    ClassDef def = cd.classDef();
                    assert def.kind() == ClassDef.MEMBER;
                    assert def.flags().isStatic();
                    
                    Name newName = mangleName(def);
                    
                    def.outer(null);
                    def.kind(ClassDef.TOP_LEVEL);
                    def.flags(def.flags().clearStatic().clearPrivate().clearProtected());
                    def.name(newName);
                    
                    cd = cd.name(nf.Id(cd.name().position(), newName));
                    cd = cd.flags(cd.flags().flags(def.flags()));
                    
                    orphans.add(cd);
                }
                else {
                    members.add(cm);
                }
            }
        
            return cb.members(members);
        }
        
        if (n instanceof SourceFile) {
            SourceFile sf = (SourceFile) n;
            List<TopLevelDecl> decls = new ArrayList<TopLevelDecl>();
            decls.addAll(sf.decls());
            decls.addAll(orphans);
            return sf.decls(decls);
        }
        
        return n;
    }

    public static Name mangleName(ClassDef def) {
        Name newName = def.name();
        
        for (ClassDef d = Types.get(def.outer()); d != null; d = Types.get(d.outer())) {
            newName = Name.make(d.name().toString() + "$" + newName.toString());
        }
        return newName;
    }
    
}

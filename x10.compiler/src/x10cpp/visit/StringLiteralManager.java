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

package x10cpp.visit;

import java.util.HashMap;
import java.util.Map.Entry;

import polyglot.ast.Node;
import polyglot.ast.StringLit_c;
import polyglot.ast.Typed;
import polyglot.types.Name;
import polyglot.util.InternalCompilerError;
import polyglot.util.StringUtil;
import polyglot.visit.NodeVisitor;
import x10.ast.X10ClassDecl_c;
import x10.types.X10ClassType;
import x10.util.ClassifiedStream;


/**
 * A class to manage generation of string literals.
 * Objectives:
 *   (1) All string literals should be created as C++ statics
 *       to minimize dynamic allocation costs.
 *   (2) If the same string literal is used multiple times within
 *       a compilation unit, only create it once.
 */
public class StringLiteralManager {
    HashMap<String,Name> map = new HashMap<String,Name>();
    String cname;
    String fqcname;
        
    public Name get(String lit) {
        return map.get(lit);
    }
    
    public String getCName(String lit) {
        Name n = map.get(lit);
        if (n == null) return null;
        return "::"+fqcname+"::"+Emitter.mangled_non_method_name(n.toString());
    }

    public void populate(X10ClassDecl_c n) {
        fqcname = Emitter.translate_mangled_FQN(Emitter.fullName(n.classDef().asType()).toString())+"_Strings";
        cname = Emitter.mangled_non_method_name(n.name().id().toString())+"_Strings";
        class FindStrings extends NodeVisitor {
            public Node override(Node n) {
                if (n instanceof StringLit_c) {
                    String str = ((StringLit_c) n).value();
                    if (!map.containsKey(str)) {
                        map.put(str, Name.makeFresh("sl"));
                    }
                }
                return null;
            }
        }
        n.visit(new FindStrings());
    }
    
    public boolean hasStrings() {
        return !map.isEmpty();
    }
    
    public void codeGen(ClassifiedStream h, ClassifiedStream d) {
        h.writeln("class "+cname+" {");
        h.writeln("  public:");
        for (Entry<String,Name> e : map.entrySet()) {
            String var = Emitter.mangled_non_method_name(e.getValue().toString());
            h.writeln("    static ::x10::lang::String "+var+";");
            d.write("::x10::lang::String "+fqcname+"::"+var+"(\"" );
            d.write(StringUtil.escape(e.getKey()));
            d.writeln("\");");
        }
        h.writeln("};");
        h.forceNewline();
        d.forceNewline();
    }
}

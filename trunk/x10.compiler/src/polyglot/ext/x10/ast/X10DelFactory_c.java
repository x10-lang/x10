/*
 * Created on Oct 7, 2004
 */
package polyglot.ext.x10.ast;

import polyglot.ast.JL;
import polyglot.ext.jl.ast.AbstractDelFactory_c;
import polyglot.ext.jl.ast.JL_c;
import polyglot.ext.x10.visit.X10PrettyPrinterVisitor;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;

/**
 * @author Christian Grothoff
 */
public class X10DelFactory_c
    extends AbstractDelFactory_c {

    /**
     * For each term, add the delegate that redirects prettyPrint to the
     * X10PrettyPrinterVisitor.
     */
    public JL delTermImpl() {
        return new JL_c() {
            public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
               // new X10PrettyPrinterVisitor(w,pp).visitAppropriate(jl());
            }            
        };        
    }
    
}

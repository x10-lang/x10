/*
 * Created by vj on Jan 23, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Formal;
import java.util.List;
import polyglot.ast.Stmt;

/**
 * @author vj Jan 23, 2005
 * 
 */
public interface X10Formal extends Formal {
   boolean hasExplodedVars();
   /**Return a list of statements containing the initializations for the exploded vars,
    * with s appended.
    * 
    * @param s -- the list of statements to be appended.
    * @return
    */
   List/*<Stmt>*/ explode( Stmt s );
   List/*<Stmt>*/ explode();
}

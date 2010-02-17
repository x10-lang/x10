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

package x10.ast;

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Stmt;
import polyglot.ast.AbstractBlock_c;
import polyglot.ast.Block_c;
import polyglot.types.Context;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class StmtSeq_c extends AbstractBlock_c implements StmtSeq {

	/**
	 * @param pos
	 * @param statements
	 */
	public StmtSeq_c(Position pos, List statements) {
		super(pos, statements);
		
	}
	// Do not push a block in. StmtSeq_c difers from AbstractBlock_c
	// only in that it does not create a new scope block.
	public Context enterScope(Context c) {
		return c;
	    }
	 public String toString() {
	        StringBuffer sb = new StringBuffer();
	       // sb.append("{");

	        int count = 0;

	        for (Iterator i = statements.iterator(); i.hasNext(); ) {
	            if (count++ > 20) {
	                sb.append(" ...");
	                break;
	            }

	            Stmt n = (Stmt) i.next();
	            sb.append(" ");
	            sb.append(n.toString());
	        }

	       // sb.append(" }");
	        return sb.toString();
	    }
}

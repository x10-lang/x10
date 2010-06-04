/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Sep 29, 2004
 */
package x10.ast;

import polyglot.ast.Expr;

/**
 * @author Christian Grothoff
 */
public interface RemoteActivityInvocation {
    
    /** Get the RemoteActivity's place. */
    Expr place();
    
    /** Set the RemoteActivity's place. */
    RemoteActivityInvocation place(Expr place);

}

/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package polyglot.ext.x10.types;

import polyglot.types.Type;


/**
 * @author vj
 *
 */
public interface X10Type extends Type, X10TypeObject {

    /**
     * An X10Type is said to be safe if all its methods are safe, i.e. sequential, local and nonblocking.
     * All primitive types (int, boolean, double, short, long, byte) are safe.
     * All Java array types are safe.
     * A future type is not safe. Its only operation, force, is blocking.
     * A nullable type is safe if its base type is safe.
     * A ParsedClass type (user-defined interface or class type) is safe if the user annotates it so.
     * The compiler will check that all its methods are safe.
     * @return
     */
    boolean safe();
}

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.mongo.yak;
import org.bson.BasicBSONObject;
import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import x10.util.*;
import x10.compiler.NoThisAccess;

  /**
   * A small variation on an <kbd>ArrayList</kbd>.
   * It mainly provides a multiple-element quasi-literal notation, which is useful for 
   * BSON objects.
   * <kbd>(new YakList()) &amp; a &amp; b &amp; c &amp; d </kbd> is a list 
   * with elements <kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, and <kbd>d</kbd>. 
   */
public class YakList extends java.util.ArrayList {
  public def this() {}

  /**
   * Add an element to <kbd>this</kbd> (mutating <kbd>this</kbd>), and return <kbd>this</kbd>.  
   * Operators which mutate their arguments are generally bad style.
   * The purpose of this one -- which may or may not excuse the bad style -- is to 
   * provide a form of list literal.  
   * <kbd>(new YakList()) &amp; a &amp; b &amp; c &amp; d </kbd> is a list 
   * with elements <kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, and <kbd>d</kbd>. 
   */
  public operator this & (a:Any) {
    this.add(LoadedYakMap.javify(a));
    return this;
  }
}

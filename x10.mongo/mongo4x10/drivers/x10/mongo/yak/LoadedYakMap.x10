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

package x10.mongo.yak;

  /**
   * An auxiliary type, designed to have the <kbd>y-<"a">-n</kbd> idiom work. <p>
   * This type provides the <kbd>>-</kbd> part of the idiom. 
   * It also has a few extra operations.
   * Internally, it has properties <kbd>yakMap:YakMap</kbd> and <kbd>newIndex:String</kbd>.
   * The interesting operations will modify <kbd>yakMap</kbd>.
   */
public struct LoadedYakMap (yakMap: YakMap, newIndex:String) {
  /**
   * A standard <kbd>hashCode()</kbd> operation.
   */
  public def hashCode():Int = yakMap.hashCode() ^ newIndex.hashCode();

  /**
   * A standard <kbd>toString()</kbd> operation.
   */
  public def toString() = "LoadedYakMap(" + yakMap + "-<" + newIndex + ")";
  /**
   * Standard equality test, implemented as <kbd>==</kbd>. 
   * @param a The value that <kbd>this</kbd> is compared to.
   */
  public def equals(a:Any) = this==a;

  /**
   * The standard <kbd>typeName()</kbd> method.
   */
  public def typeName():String = "x10.mongo.yak.LoadedYakMap";

  /**
   * Add, to <kbd>yakMap</kbd> at index <kbd>newIndex</kbd>, 
   * a pair {cmd:v}.  This is useful for a bunch of queries.
   * @param cmd 
   * @param v 
   */
  protected def operate(cmd:String, v:Any) { 
    yakMap.put(newIndex, new YakMap(cmd, javify(v))); 
    return yakMap;}


  /**
   * <kbd>m -&lt; k &gt; v </kbd>Adds to <kbd>yakMap</kbd> a query 
   * checking that the field whose name is the value of <kbd>k</kbd>
   * has value greater than <kbd>v</kbd>. 
   * This method expresses the second part of that idiom.
   * @param v The value to compare to.
   */
  public operator this > (v : Any) = operate("$gt", v);

  /**
   * <kbd>m -&lt; k &lt; v </kbd>Adds to <kbd>yakMap</kbd> a query 
   * checking that the field whose name is the value of <kbd>k</kbd>
   * has value less than <kbd>v</kbd>. 
   * This method expresses the second part of that idiom.
   * @param v The value to compare to.
   */
  public operator this < (v : Any) = operate("$lt", v);


  /**
   * <kbd>m -&lt; k &gt;= v </kbd>Adds to <kbd>yakMap</kbd> a query 
   * checking that the field whose name is the value of <kbd>k</kbd>
   * has value greater than or equal to <kbd>v</kbd>. 
   * This method expresses the second part of that idiom.
   * @param v The value to compare to.
   */
  public operator this >= (v : Any) = operate("$gte", v);

  /**
   * <kbd>m -&lt;= k &lt;= v </kbd>Adds to <kbd>yakMap</kbd> a query 
   * checking that the field whose name is the value of <kbd>k</kbd>
   * has value less than or equal to <kbd>v</kbd>. 
   * This method expresses the second part of that idiom.
   * @param v The value to compare to.
   */
  public operator this <= (v : Any) = operate("$lte", v);



  /**
   * Adds a new entry to <kbd>yakMap</kbd> at <kbd>newIndex</kbd>.
   * The basic idiom is, <kbd>ym -&lt; "a" &gt;- 1 </kbd> to add a new entry 
   * at index <kbd>"a"</kbd> of YakMap <kbd>ym</kbd> having value <kbd>1</kbd>. 
   */
  public operator this >- (v : Any) {
    val vv = javify(v);
    yakMap.put(newIndex, vv);
    return yakMap;
  }

  /**
   * Convert <kbd>a</kbd> to a <kbd>java.lang.Object</kbd>. 
   * (<kbd>java.lang.Object</kbd> works better than <kbd>x10.lang.Object</kbd> in 
   * Mongo maps.)
   * @param a 
   */
  public static def javify(a:Any) =
    a instanceof Int      ? new java.lang.Integer(a as Int)
  : a instanceof Boolean  ? new java.lang.Boolean(a as Boolean)
  : a instanceof Long  ? new java.lang.Long(a as Long)
  : a instanceof Double  ? new java.lang.Double(a as Double)
  : a instanceof Float    ? new java.lang.Float(a as Float)
  : a;
  
  /**
   * 
   */
  /**
   * Adds a new entry to <kbd>yakMap</kbd> at <kbd>newIndex</kbd>.
   * The basic idiom is, <kbd>ym -&lt; "a" &gt;- 1 </kbd> to add a new entry 
   * at index <kbd>"a"</kbd> of YakMap <kbd>ym</kbd> having value <kbd>1</kbd>. 
   * This specialized operator takes care of the case of adding an 
   * <kbd>Int</kbd>.
   */
  public operator this >- (v: Int) = this >- new java.lang.Integer(v);
  /**
   * Adds a new entry to <kbd>yakMap</kbd> at <kbd>newIndex</kbd>.
   * The basic idiom is, <kbd>ym -&lt; "a" &gt;- 1 </kbd> to add a new entry 
   * at index <kbd>"a"</kbd> of YakMap <kbd>ym</kbd> having value <kbd>1</kbd>. 
   * This specialized operator takes care of the case of adding a 
   * <kbd>Boolean</kbd>.
   */
  public operator this >- (v: Boolean) = this >- new java.lang.Boolean(v);
  /**
   * Adds a new entry to <kbd>yakMap</kbd> at <kbd>newIndex</kbd>.
   * The basic idiom is, <kbd>ym -&lt; "a" &gt;- 1 </kbd> to add a new entry 
   * at index <kbd>"a"</kbd> of YakMap <kbd>ym</kbd> having value <kbd>1</kbd>. 
   * This specialized operator takes care of the case of adding a 
   * <kbd>Float</kbd>.
   */
  public operator this >- (v: Float) = this >- new java.lang.Float(v);
}

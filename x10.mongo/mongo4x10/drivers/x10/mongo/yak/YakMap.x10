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
import org.bson.BasicBSONObject;
//import com.mongodb.DBObject;
import com.mongodb.BasicDBObject;
import x10.util.*;
import x10.compiler.NoThisAccess;

  /**
   * A Mongo-specific BSON object, presenting a good X10 interface.
   * In particular, it has operators and methods for adding and retrieving fields: 
   * Here's a way to make the object <kbd>{"a":1, "b":2, "c":3}</kbd>
   <pre>
      val y = YakUtil.it();
      val m : YakMap = 
        y -<"a">- 1
          -<"b">- 2
          -<"c">- 3
   </pre>
   <p>
   <kbd>YakMap</kbd>s have interfaces to construct queries and modifier objects.  For example, 
   the <kbd>YakMap</kbd> to build the query to see if <kbd>a</kbd> is 1 and <kbd>b</kbd> is even can be built
   as 
   <pre>
   (new YakMap()).eq("a",1).mod("b",2,0)
   </pre>
   Or, if you have the suggested binding <kbd>val y = YakUtil.it()</kbd>, as 
   <pre>
   y.eq("a",1).mod("b",2,0)
   </pre>
   <p>
   Similarly, a modifier to increment <kbd>x</kbd> by 1 can be built by
   <pre>
   y.inc("x", 1)
   </pre>
   And one to increment <kbd>x</kbd> by 1 and <kbd>y</kbd> by 2 by: 
   <pre>
   y.inc("x",1).inc("y",2)
   </pre>
   
   <p>
   In particular you don't need to remember that it's <kbd>{x: {"$lt":3}}</kbd> to see if 
   <kbd>x</kbd> is less than three, 
   but <kbd>{"$inc": {x:1}}</kbd> to increment <kbd>x</kbd> by one.
    
      
   */
public class YakMap extends org.bson.BasicBSONObject implements com.mongodb.DBObject {

  /**
   * A conventional <kbd>hashCode</kbd>.
   */
  public def hashCode():Int = super.hashCode();
  /**
   * A useful <kbd>toString()</kbd> implementation.
   */
  public def toString() = super.toString();
  /**
   * A conventional equality test.
   * @param a 
   */
  public def equals(a:Any) = super.equals(a);


  /**
   * The idiom <kbd>ym -<"a">- 1 -<"b">- 2</kbd> adds new entries at <kbd>"a"</kbd> and <kbd>"b"</kbd> to <kbd>this</kbd>.
   * The <kbd> -&lt;</kbd> operator itself does not modify <kbd>this</kbd>.  
   * <kbd>ym -&lt; k</kbd> constructs a new object, referring to both <kbd>ym</kbd> and <kbd>k</kbd>, whose 
   * purpose in existence is to accept a <kbd> &gt;- v</kbd> operation and add the mapping <kbd>k,v</kbd> to <kbd>ym</kbd>. 
   * Note that, the combination 
   * <kbd>ym -<"a">- 1</kbd> <i>does</i> modify <kbd>ym</kbd>. 
   */
  public operator this -< (s:String) = new LoadedYakMap(this, s);

  /**
   * Construct an empty <kbd>YakMap</kbd>.
   */
  public def this() { super(); }

  /**
   * Construct a <kbd>YakMap</kbd> containing a single binding.
   * @param k Key of the binding
   * @param a Value of hte binding.
   */
  public def this(k:String, a:Any) {super(k,a);}

  /**
   * Construct a new <kbd>YakMap</kbd> copying another <kbd>YakMap</kbd>.
   * @param ym The <kbd>YakMap</kbd> to copy.
   */
  public def this(ym: YakMap) { this(ym as java.util.AbstractMap); }

  /**
   * Construct a new <kbd>YakMap</kbd> copying another map.
   * @param ym 
   */
  public def this(ym: java.util.AbstractMap) {
    val it = new YakMap();
    it.putAll(ym);
  }
  
  /**
   * Construct a new <kbd>YakMap</kbd> whose elements are those of <kbd>b</kbd>.
   * @param b <kbd>BasicBSONObject</kbd> to copy.
   */
  public def this(b : BasicBSONObject) { super(b); }

  /**
   * Yet another copy constructor.
   * @param b <kbd>DBObject</kbd> to copy.
   */
  public def this(b : com.mongodb.DBObject) { super(b as BasicBSONObject); }
  

  /**
   * Convert a <kbd>BasicBSONObject</kbd> into a <kbd>YakMap</kbd>.
   * @param b The <kbd>BasicBSONObject</kbd> to convert.
   */
  public static def yakifyTopLevel(b: BasicBSONObject) : YakMap = b == null ? null : new YakMap(b);


  /**
   * Convert a <kbd>BasicBSONObject</kbd> into a <kbd>YakMap</kbd>.
   * @param b The <kbd>BasicBSONObject</kbd> to convert.
   */
  public static def yakifyTopLevel(b: com.mongodb.DBObject) : YakMap = b == null ? null : new YakMap(b);


  /**
   * Obtain an element of <kbd>this</kbd> which is itself a map.
   * If <kbd>ym</kbd> is <kbd>{"a": {"b": {"c":1}, "d":2}, "3":3}, </kbd>
   * <kbd>ym / "a" </kbd> is <kbd>{"b": {"c":1}, "d":2}</kbd>, 
   * and <kbd>ym / "a" / "b"</kbd> is <kbd>{"c":1}</kbd>.
   */
  public operator this / (key:String) : YakMap {
    val o = super.get(key);
    if (o instanceof YakMap) return o as YakMap;
    if (o instanceof BasicBSONObject) return yakifyTopLevel(o as BasicBSONObject);
    throw new YakException("'YakMap./ only works for members that are maps, not " + 
                               o.typeName() + " like " + o);
  }
  
  
  /**
   * Subscripting a <kbd>YakMap</kbd> retrieves its elements, as <kbd>Any</kbd>s.  
   * If you know what sort of value to expect, a more specific operation will 
   * retrieve it with the right type: <kbd>ym/"a"</kbd> for a subobject,
   * <kbd>ya.int("b")</kbd> for an integer, 
   * <kbd>ya.long("c")</kbd> for a long, 
   * <kbd>ya.bool("d")</kbd> for a boolean, 
   * <kbd>ya.float("d")</kbd> for a float, 
   * <kbd>ya.double("d")</kbd> for a double, 
   * <kbd>ya.str("e")</kbd> for a string.
   */
  public operator this (key:String) : Any {
    val o = super.get(key);
    if (o instanceof YakMap) return o;
    if (o instanceof BasicBSONObject) return yakifyTopLevel(o as BasicBSONObject);
    return o;
  }
  
  /**
   * Modify <kbd>this</kbd> by adding all the elements of <kbd>that</kbd> (except <kbd>_id</kbd>) and return the joined map.
   * In general in X10, operators should not modify their operands.  This violates that general design principle. 
   * This operation is intended for idioms like
   * <kbd>a + (y -<"b">-1) </kbd>
   */
  public operator this + (that: YakMap): YakMap {
    val hasId = this.containsKey("_id");
    val oldId = this.get("_id");
    this.putAll(that as java.util.Map);
    if (hasId) this.put("_id", oldId);
    return this;
  }

  /**
   * Return a new <kbd>YakMap</kbd> with the same elements as <kbd>this</kbd>.
   */
  public def dup():YakMap = new YakMap(this);
  
  /**
   * Get an <kbd>Int</kbd>-valued field of <kbd>this</kbd>.
   * @param s The name of the field to get.
   */
  public def int(s:String) = this.getInt(s);
  /**
   * Get a <kbd>Long</kbd>-valued field of <kbd>this</kbd>.
   * @param s The name of the field to get.
   */
  public def long(s:String) = this.getLong(s);
  /**
   * Get a <kbd>Boolean</kbd>-valued field of <kbd>this</kbd>.
   * @param s The name of the field to get.
   */

  /**
   * Get a <kbd>Float</kbd>-valued field of <kbd>this</kbd>.
   * @param s 
   */
  public def float(s:String) = this(s) as Float;

  /**
   * Get a <kbd>Double</kbd>-valued field of <kbd>this</kbd>.
   * @param s 
   */
  public def double(s:String) = this(s) as Double;

  public def bool(s:String) = this.getBoolean(s);
  /**
   * Get a <kbd>String</kbd>-valued field of <kbd>this</kbd>.
   * @param s The name of the field to get.
   */
  public def str(s:String) = this.getString(s);
  
  /**
   * Does this <kbd>YakMap</kbd> have an entry with a given name?
   * @param s The name fo the entry to check.
   */
  public def has(s:String) = this.containsKey(s);

  /**
   * A stub for compatibility with the Java classes.
   */
  public def markAsPartialObject():void {
    // 
  }
  
  /**
   * A stub for compatibility with the Java classes.
   */
  public def isPartialObject() {
    return false;
  }
  

  
  /**
   * Standard X10 <kbd>Any</kbd> method.
   */
  @NoThisAccess public def typeName():String = "YakMap";
  

  /**
   * Does a field accept multiple modifiers?
   * E.g., <kbd>{"$inc": {x:1, y:2}}</kbd>, when used as an update modifier, 
   * adds 1 to x and 2 to y.  This method is used internally to compute proper update modifiers.
   * Part of Yak's understanding of Mongo.
   * @param fieldName 
   */
  static def fieldAcceptsMultiplePairs(fieldName:String) = 
    "$inc".equals(fieldName)
    || "$set".equals(fieldName)
    || "$unset".equals(fieldName)
    || "$push".equals(fieldName)
    || "$pushAll".equals(fieldName)
    || "$addToSet".equals(fieldName)
    || "$each".equals(fieldName)
    || "$pop".equals(fieldName)
    || "$pull".equals(fieldName)
    || "$pullAll".equals(fieldName)
    || "$rename".equals(fieldName)
    || "$bit".equals(fieldName);



  /**
   * Adds to <kbd>this</kbd> a field <kbd>{aa: {bb:cc}}</kbd>, except in certain cases.
   * When <kbd>aa</kbd> is a mutation operator (like <kbd>"$inc"</kbd>) and there's already 
   * a field named <kbd>aa</kbd>, this combines the values of that field.
   * <i>e.g.</i> applying <kbd>fieldIs("$inc", "b", 2)</kbd> to <kbd>{$inc: {a:1}}</kbd>
   * results in <kbd>{$inc: {a:1, b:2}}</kbd>, that is, a modifier which will increment
   * both <kbd>a</kbd> and <kbd>b</kbd> by the right amount.
   * <p>
   * Generally it is better to use specific operations, like <kbd>inc("a",1)</kbd>, to 
   * produce modifiers and queries.   
   * @param aa 
   * @param bb 
   * @param cc 
   */
  public  def fieldIs(aa:String, bb:String, cc:Any) : YakMap {
    if (this.containsKey(aa)) {
      if (fieldAcceptsMultiplePairs(aa)) {
        val oldCc = this.get(aa);
        if (oldCc instanceof java.util.Map){
          val oldMap = oldCc as java.util.Map;
          if (oldMap.containsKey(bb)) {
            throw new YakException("Yak error: you're trying to add or overwrite a value to an update operation's field field " + aa 
                                       + " of a record " + this
                                       + "\n Current cc of field: " + oldCc
                                       + "\n Cc being updated to: " + cc 
                                       + "This is an update operation, and this operation will delete the old value's effects.");
          }
          (oldMap).put(bb, LoadedYakMap.javify(cc));
        }
        else {
          throw new YakException("Yak Error: Trying to add an extra name/value pair to the " + aa + " of a record, but the cc of that record is not a BSON object\n  cc = " + oldCc + "; trying to add {" + bb + ":" + cc + "}");
        }
      }
      else 
        throw new YakException("Yak Error: attempt to add a field operator named " + aa + " to a YakMap that already had one.\n  map=" + this + "\n  addition: {" + aa + ": {" + bb + ":" + cc + "}}");
    }
    else {
      this.put(aa, new YakMap(bb, LoadedYakMap.javify(cc)));
    }
    return this;
  }
  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value greater than <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def gt(fieldName: String, v: Any) = fieldIs(fieldName, "$gt", v);
  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value less than <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def lt(fieldName: String, v: Any) = fieldIs(fieldName, "$lt", v);
  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value greater than or equal to <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def gte(fieldName: String, v: Any) = fieldIs(fieldName, "$gte", v);
  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value less than or equal to <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def lte(fieldName: String, v: Any) = fieldIs(fieldName, "$lte", v);

  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value not equal to <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def ne(fieldName: String, v: Any) = fieldIs(fieldName, "$ne", v);


  /**
   * Add to <kbd>this</kbd> a query asking whether a list-valued field named <kbd>fieldName</kbd> has all its values in <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def all(fieldName: String, v: Any) = fieldIs(fieldName, "$all", v);

  /**
   * Add to <kbd>this</kbd> a query asking whether the subject has a field named <kbd>fieldName</kbd>.
   * <kbd>foo.exists("a", true)</kbd> returns true if there is a field named <kbd>"a"</kbd>.
   * <kbd>foo.exists("a", false)</kbd> returns true if there is <b>not</b> a field named <kbd>"a"</kbd>.
   * @param fieldName Name of field being compared
   * @param v         Value to return if a field named <kbd>fieldName</kbd> is there.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def exists(fieldName: String, v: Any) = fieldIs(fieldName, "$exists", v);

  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> exists.
   * Equivalent to <kbd>foo.exists(fieldName, true)</kbd>.
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def exists(fieldName: String) = exists(fieldName, true);

  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value equal to <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public  def eq(fieldName:String, v:Any) = this -< fieldName >- v;


  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value in the list <kbd>choices</kbd>
   * Sorry about the capitalization, <kbd>In</kbd> rather than <kbd>in</kbd>, but <kbd>in</kbd> is a keyword in X10.
   * @param fieldName Name of field being compared
   * @param choices   List of choices.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public def In(fieldName:String, choices:Any) = fieldIs(fieldName, "$in", choices);


  /**
   * Add to <kbd>this</kbd> a query asking whether an element of the list-valued field named <kbd>fieldName</kbd> has value matching <kbd>subquery</kbd>.
   * @param fieldName Name of field being compared
   * @param subquery  Query to look for.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public def elemMatch(fieldName:String, subquery:YakMap) = fieldIs(fieldName, "$elemMatch", subquery);

  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value not in <kbd>vs</kbd>
   * @param fieldName Name of field being compared
   * @param vv         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public def nin(fieldName:String, vs:Any) = fieldIs(fieldName, "$nin", vs);

  /**
   * Add to <kbd>this</kbd> a query asking whether a field named <kbd>fieldName</kbd> has value congruent to <kbd>remainder</kbd> modulo <kbd>divisor</kbd>.
   * For example, <kbd>foo.mod("a", 2, 0)</kbd> selects values with an even <kbd>"a"</kbd> field.
   * @param fieldName Name of field being compared
   * @param divisor The divisor
   * @param remainder The remainder for which the query answers true.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public def mod(fieldName:String, divisor: Long, remainder: Long) = fieldIs(fieldName, "$mod", YakUtil.list(divisor, remainder));


  
  /**
   * Add to <kbd>this</kbd> a query asking whether the field named <kbd>fieldName</kbd> is a list with precisely <kbd>exactNumberOfElements</kbd> elements.
   * @param fieldName 
   * @param exactNumberOfElements 
   */
  public def size(fieldName:String, exactNumberOfElements:Any) = fieldIs(fieldName, "$size", exactNumberOfElements);



  /**
   * Add a modifier which increments <kbd>fieldName</kbd> by <kbd>by</kbd>. 
   * @param fieldName Name of field to increment.
   * @param by Amount by which to increment.
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def inc(fieldName:String, by:Any) = fieldIs("$inc", fieldName, by);

  /**
   * Add a modifier which sets field <kbd>fieldName</kbd> to value <kbd>v</kbd>.
   * @param fieldName 
   * @param v 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def set(fieldName:String, v:Any) = fieldIs("$set", fieldName, v);

  /**
   * Add a modifier which deletes the field named <kbd>fieldName</kbd>
   * @param fieldName field name to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def unset(fieldName:String) = fieldIs("$unset", fieldName, 1);

  /**
   * Add a modifier which appends <kbd>by</kbd> to the list-valued field named <kbd>fieldName</kbd>.
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def push(fieldName:String, by:Any) = fieldIs("$push", fieldName, by);

  /**
   * Add a modifier which appends all the elements of list-valued <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd>
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def pushAll(fieldName:String, by:Any) = fieldIs("$pushAll", fieldName, by);

  /**
   * Add a modifier which adds value <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd> if it is not already there.
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def addToSet(fieldName:String, by:Any) = fieldIs("$addToSet", fieldName, by);

  /**
   *  Add a modifier which adds each of the values in list-value <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd> if it is not already there.
   * @param fieldName 
   * @param elements 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def addEachToSet(fieldName:String, elements:Any) = fieldIs("$addToSet", fieldName, YakUtil.it()-<"$each">-elements);

  /**
   * Add a modifier which removes the last element in list-valued field <kbd>fieldName</kbd>.
   * @param fieldName 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def pop(fieldName:String) = fieldIs("$pop", fieldName, 1);

  /**
   * Add a modifier which removes the <kbd>k</kbd>th element in list-valued field <kbd>fieldName</kbd>.
   * In particular, <kbd>pop("x", -1)</kbd> removes the last element of the field named <kbd>"x"</kbd>.
   * @param fieldName 
   * @param k 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def pop(fieldName:String, k:Any) = fieldIs("$pop", fieldName, k);

  /**
   * Add a modifier which removes all occurrences of <kbd>n</kbd> from list-valued field <kbd>fieldName</kbd>.
   * @param fieldName Field name to delete elements from
   * @param n Value to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def pull(fieldName:String, n:Any) = fieldIs("$pull", fieldName, n);

  /**
   * Add a modifier which removes all occurrences of elements of list-valued <kbd>vs</kbd> from list-valued field <kbd>fieldName</kbd>
   * @param fieldName Field name to have elements deleted
   * @param vs List of elements to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def pullAll(fieldName:String, vs:Any) = fieldIs("$pullAll", fieldName, vs);


  /**
   * Add a modifier which renames field <kbd>oldName</kbd> to have name <kbd>newName</kbd>.

   * @param oldName Old name for field
   * @param newName New, improved name for field
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public def rename(oldName:String, newName: String) = fieldIs("$rename", oldName, newName);

  

  // Returns an $or'ed YakMap, which might or might not be this.
  /**
   * <kbd>a | b</kbd> returns a <kbd>YakMap</kbd> which matches objects matched by either <kbd>a</kbd> or <kbd>b</kbd>.
   * This constructs or modifies an <kbd>$or</kbd> query.  
   * In particular, the object returned might be a modified version of <kbd>this</kbd> (if <kbd>this</kbd> already has an 
   * and is a new object if not.
   * 
   * @see http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-%24or
   */
  public operator this | (that:YakMap) {
    if (this.containsKey("$or")){
      val or = this.get("$or");
      val orlist = or as java.util.ArrayList;
      orlist.add(that);
      return this;
    }
    else {
      return YakUtil.it() -< "$or" >- (YakUtil.list(this, that));
    }
  }
  
  public def keys() : HashSet[String] {
    val S = new HashSet[String]();
    val T = (this as org.bson.BSONObject).keySet();
    val ti = T.iterator();
    while (ti.hasNext()) S.add(ti.next() as String);
    return S;
  }

}

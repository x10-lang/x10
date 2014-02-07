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

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.ServerAddress;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBList;
import com.mongodb.MapReduceOutput;
import x10.util.concurrent.Lock;
import x10.io.*;
import x10.util.*;

  /**
   * A class of utilities for working with BSON and Mongo objects in X10.
     This is a place-local singleton object, to support an idiom for object construction.
     Here is code to create 
     <kbd>{a: 1, b: {bx: 2, by: 3}, c: 4}</kbd>
     <pre>
     val y = YakUtil.it(); // get the singleton
     val it = y -<"a">- 1
                -<"b">- (y -<"bx">- 2 -<"by">- 3)
                -<"c">- 3;
     </pre>
     In code that constructs a lot of BSON or Mongo objects, consider putting this <kbd>y</kbd> as a static 
     variable.  
     <p>
     YakUtil has convenience methods for constructing queries and update modifiers.
     <kbd>y.gt("a",3).eq("b",4)</kbd> is a query object testing to see if field <kbd>a</kbd> is greater than 3, and field <kbd>b</kbd> is equal to 4.
     <kbd>y.inc("a",1).inc("b",2)</kbd> is an update modifier incrementing <kbd>a</kbd> by one and <kbd>b</kbd> by 2.  
     You can of course build these objects directly, using the Mongo representations, but the more abstract versions may be more convenient. 
     
   */
public class YakUtil implements x10.io.CustomSerialization {
  /**
   * A vanilla hash code.
   */
  public def hashCode():Int = 1;
  /**
   * An utterly uninspired <kbd>toString</kbd>.
   */
  public def toString() = "(YakUtil)";
  /**
   * A deadly dull equality test
   * @param a 
   */
  public def equals(a:Any) = this==a;


  /**
   * A debugging flag
   */
  static val CreateNewMongoEveryTime = false;


  // Singleton per place.
  /*package*/ def this() {}


  private static val them: PlaceLocalHandle[YakUtil]
  = PlaceLocalHandle.make[YakUtil](Dist.makeUnique(), () => new YakUtil());


  
  /**
   * Custom serialization.  Please do not use this.
   */
  public def serialize():SerialData {
    return new SerialData(null, null);
  }
  /**
   * Custom serialization.  Please do not use this.
   * @param SerialData 
   */
  public def this(SerialData) {}
  

  
  /**
   * This is the method you should use to get ahold of a <kbd>YakUtil</kbd> value.
   */
  public static def it(): YakUtil = them();
  
  /**
   * Start constructing a <kbd>YakMap</kbd>; <kbd>y-<"a">-1</kbd> is <kbd>{a:1}</kbd>.  See <kbd>YakMap</kbd> for details.
   */
  public operator this -< (s:String) = (new YakMap()) -< s;
  
  /**
   * Convert an object to Yak form.
   */
  public operator this(b: org.bson.BasicBSONObject) = YakMap.yakifyTopLevel(b);
  /**
   * Convert an object to Yak form.
   */
  public operator this(b: com.mongodb.DBObject) = YakMap.yakifyTopLevel(b);
  /**
   * Convert an object to Yak form.
   */
  public operator this(x: com.mongodb.DBCollection): YakCollection = YakCollection.make(x);
  /**
   * Convert an object to Yak form.
   */
  public operator this(x: com.mongodb.DBCursor): YakCursor = new YakCursor(x);
  
  /**
   * Convert an object to Yak form.
   */
  public operator this(x: YakMap):YakMap = x;
  /**
   * Convert an object to Yak form.
   */
  public operator this(x: YakCursor):YakCursor = x;
  /**
   * Convert an object to Yak form.
   */
  public operator this(x: YakCollection):YakCollection = x;
  
  public operator this(x: MapReduceOutput): YakMapReduceOutput = new YakMapReduceOutput(x);
  
  /**
   * Construct a new empty <kbd>YakMap</kbd>
   */
  public static def empty(): YakMap = new YakMap();


  /**
   * Construct a new empty <kbd>YakMap</kbd>.
   */
  public final operator this():YakMap = new YakMap();
  

  /**
   * Construct a new empty <kbd>YakList</kbd>.
   */
  public static def nil() : YakList = new YakList();


  /**
   * Construct a new empty <kbd>YakList</kbd>
   */
  public static def list() : YakList = new YakList();
  /**
   * Construct a new <kbd>YakList</kbd> with one element <kbd>a</kbd>.
   * @param a 
   */
  public static def list(a:Any) : YakList = new YakList() & a;
  /**
   * Construct a new <kbd>YakList</kbd> with elements <kbd>a</kbd> and <kbd>b</kbd>.
   * @param a 
   * @param b 
   */
  public static def list(a:Any, b:Any) : YakList = new YakList() & a & b;
  /**
   * Construct a new <kbd>YakList</kbd> with elements <kbd>a</kbd>, <kbd>b</kbd>, and <kbd>c</kbd>.
   * @param a 
   * @param b 
   * @param c 
   */
  public static def list(a:Any, b:Any, c:Any) : YakList = new YakList() & a & b & c;
  /**
   * Construct a new <kbd>YakList</kbd> with elements <kbd>a</kbd>, <kbd>b</kbd>, <kbd>c</kbd>, and <kbd>d</kbd>.
   * @param a 
   * @param b 
   * @param c 
   * @param d 
   */
  public static def list(a:Any, b:Any, c:Any, d:Any) : YakList = new YakList() & a & b & c & d;
  
  
   static mongoCell : PlaceLocalHandle[Cell[Mongo]] =
     PlaceLocalHandle.make[Cell[Mongo]](Dist.makeUnique(),  () => new Cell[Mongo](null) );

  private static mongoCellLock : PlaceLocalHandle[Lock] = 
    PlaceLocalHandle.make[Lock](Dist.makeUnique(), () => new Lock());
  
  // There should be only one Mongo object per place, usually, so make that easy.

  /**
   * Get the default <kbd>Mongo</kbd> object.
   * Caches, so that each place uses a single <kbd>Mongo</kbd> object for the same server address.
   */
  public static def mongo(): Mongo  {
    try {
      return mongo(new ServerAddress());
    }
    catch (e:java.net.UnknownHostException) {
      e.printStackTrace();
      throw new YakException(e);      
    }
  }

  static class Mongoness(mongoMap:Map[ServerAddress,Mongo], lock:Lock){
    def this(){
      property( new HashMap[ServerAddress,Mongo](), new Lock() );
    }
  }
  
  static val mongonessHandle : PlaceLocalHandle[Mongoness]
                    =     PlaceLocalHandle.make[Mongoness]
                          (Dist.makeUnique(), () => new Mongoness());

  /**
   * Create or retrieve a <kbd>Mongo</kbd> object for a given <kbd>ServerAddress</kbd>.  
   * Caches, so that each place uses a single <kbd>Mongo</kbd> object for the same server address.
   * @param sa The address of the server to use.
   */
  public static def mongo(sa : ServerAddress): Mongo {
    if (CreateNewMongoEveryTime){
      try {
        return new Mongo(sa);
      }
      catch (e:CheckedThrowable) {
        Runtime.println("Oh no!  " + e);
        throw(new YakException(e));
      }
    }
    // Look one up in the cache.
    val mongoness : Mongoness = mongonessHandle();
    val maphere = mongoness.mongoMap;
    val lock = mongoness.lock;
    if (maphere.containsKey(sa)){ // (A)
      return maphere.getOrThrow(sa);
    }
    // Not in the cache, so make one ... atomically.
    try {
      lock.lock();
      // Check again. Someone might have zoomed in and filled it in between (A) and here. (B)
      if (maphere.containsKey(sa)) {
        lock.unlock();
        return maphere.getOrThrow(sa);
      }
      // Well, nobody's poking at it *now*, not with the lock set at (B).  So let's do it.
      val mongo = new Mongo(sa);
      maphere.put(sa, mongo);
      return mongo;
    }
    catch (e:Exception) {
      Runtime.println("Oh, no! " + e);
      throw(new YakException(e));
    }
    finally{
      lock.unlock();
    }
  }

  
  /**
   * Get the database for <kbd>name</kbd> from the standard <kbd>Mongo</kbd> object
   * @param name 
   */
  public static def db(name:String):com.mongodb.DB {
    return mongo().getDB(name);
  }
  /**
   * Get a collection with a given database and collection name.
   * @param dbName Database name
   * @param collName Collection name.
   */
  public static def collection(dbName: String, collName: String):YakCollection 
   = them()(db(dbName).getCollection(collName));
  
  /**
   * Constructs a new <kbd>YakMap</kbd> of structure <kbd>{fieldName: {comparator: value}}</kbd>.
   * Mostly used for query and update modifier constructs, which are best done with methods like <kbd>gt</kbd> and <kbd>inc</kbd>. 
   * @param fieldName 
   * @param comparator 
   * @param value 
   */
  public static def fieldIs(fieldName: String, comparator:String, value: Any) = it() -< fieldName >- (it() -< comparator >- value);

  /**
   * Construct a one-element Mongo query object saying that field <kbd>fieldName</kbd> is greater than <kbd>v</kbd>
   * @param fieldName Name of the field to inspect
   * @param v         Value to compare against.
   * @see http://api.mongodb.org/wiki/current/Querying.html
   */
  public static def gt(fieldName: String, v: Any) = fieldIs(fieldName, "$gt", v);

  /**
   * Construct a one-element Mongo query object saying that field <kbd>fieldName</kbd> is less than <kbd>v</kbd>
   * @param fieldName Name of the field to inspect
   * @param v         Value to compare against.
   * @see http://api.mongodb.org/wiki/current/Querying.html
   */
  public static def lt(fieldName: String, v: Any) = fieldIs(fieldName, "$lt", v);
  /**
   * Construct a one-element Mongo query object saying that field <kbd>fieldName</kbd> is greater than or equal to <kbd>v</kbd>
   * @param fieldName Name of the field to inspect
   * @param v         Value to compare against.
   * @see http://api.mongodb.org/wiki/current/Querying.html
   */
  public static def gte(fieldName: String, v: Any) = fieldIs(fieldName, "$gte", v);
  /**
   * Construct a one-element Mongo query object saying that field <kbd>fieldName</kbd> is less than or equal to <kbd>v</kbd>
   * @param fieldName Name of the field to inspect
   * @param v         Value to compare against.
   * @see http://api.mongodb.org/wiki/current/Querying.html
   */
  public static def lte(fieldName: String, v: Any) = fieldIs(fieldName, "$lte", v);


  /**
   * Construct a one-element Mongo query object saying that field <kbd>fieldName</kbd> is not equal to <kbd>v</kbd>
   * @param fieldName Name of the field to inspect
   * @param v         Value to compare against.
   * @see http://api.mongodb.org/wiki/current/Querying.html
   */
  public static def ne(fieldName: String, v: Any) = fieldIs(fieldName, "$ne", v);

  /**
   * Construct a query asking whether a list-valued field named <kbd>fieldName</kbd> has all its values in <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def all(fieldName: String, v: Any) = fieldIs(fieldName, "$all", v);

  /**
   * Construct a query asking whether the subject has a field named <kbd>fieldName</kbd>.
   * <kbd>foo.exists("a", true)</kbd> returns true if there is a field named <kbd>"a"</kbd>.
   * <kbd>foo.exists("a", false)</kbd> returns true if there is <b>not</b> a field named <kbd>"a"</kbd>.
   * @param fieldName Name of field being compared
   * @param v         Value to return if a field named <kbd>fieldName</kbd> is there.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def exists(fieldName: String, v: Any) = fieldIs(fieldName, "$exists", v);

  /**
   * Construct a query asking whether a field named <kbd>fieldName</kbd> exists.
   * Equivalent to <kbd>foo.exists(fieldName, true)</kbd>.
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def exists(fieldName: String) = exists(fieldName, true);


  /**
   * Construct a query asking whether a field named <kbd>fieldName</kbd> has value equal to <kbd>v</kbd>
   * @param fieldName Name of field being compared
   * @param v         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def eq(fieldName:String, v:Any) = it() -< fieldName >- v;

  /**
   * Construct a query asking whether a field named <kbd>fieldName</kbd> has value in the list <kbd>choices</kbd>
   * Sorry about the capitalization, <kbd>In</kbd> rather than <kbd>in</kbd>, but <kbd>in</kbd> is a keyword in X10.
   * @param fieldName Name of field being compared
   * @param choices   List of choices.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def In(fieldName:String, choices:Any) = fieldIs(fieldName, "$in", choices);

  /**
   * Construct a query asking whether an element of the list-valued field named <kbd>fieldName</kbd> has value matching <kbd>subquery</kbd>.
   * @param fieldName Name of field being compared
   * @param subquery  Query to look for.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def elemMatch(fieldName:String, subquery:YakMap) = fieldIs(fieldName, "$elemMatch", subquery);

  /**
   * Construct a query asking whether a field named <kbd>fieldName</kbd> has value not in <kbd>vs</kbd>
   * @param fieldName Name of field being compared
   * @param vv         Value it is being compared to.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def nin(fieldName:String, vs:Any) = fieldIs(fieldName, "$nin", vs);


  /**
   * Construct a query asking whether a field named <kbd>fieldName</kbd> has value congruent to <kbd>remainder</kbd> modulo <kbd>divisor</kbd>.
   * For example, <kbd>foo.mod("a", 2, 0)</kbd> selects values with an even <kbd>"a"</kbd> field.
   * @param fieldName Name of field being compared
   * @param divisor The divisor
   * @param remainder The remainder for which the query answers true.
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def mod(fieldName:String, divisor: Long, remainder: Long) = fieldIs(fieldName, "$mod", list(divisor, remainder));


  /**
   * Construct a query asking that the field named <kbd>fieldName</kbd> has a specific number of elements.
   * @param fieldName Name of the field
   * @param exactNumberOfElements Desired number of elements
   * @see http://api.mongodb.org/wiki/current/Advanced%20Queries.html
   */
  public static def size(fieldName:String, exactNumberOfElements:Any) = fieldIs(fieldName, "$size", exactNumberOfElements);

  /**
   * Construct a modifier which increments <kbd>fieldName</kbd> by <kbd>by</kbd>. 
   * @param fieldName Name of field to increment.
   * @param by Amount by which to increment.
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def inc(fieldName:String, by:Any) = fieldIs("$inc", fieldName, by);
  /**
   * Construct a modifier which sets field <kbd>fieldName</kbd> to value <kbd>v</kbd>.
   * @param fieldName 
   * @param v 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def set(fieldName:String, by:Any) = fieldIs("$set", fieldName, by);

  /**
   * Construct a modifier which deletes the field named <kbd>fieldName</kbd>
   * @param fieldName field name to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def unset(fieldName:String, by:Any) = fieldIs("$unset", fieldName, by);

  /**
   * Construct a modifier which appends <kbd>by</kbd> to the list-valued field named <kbd>fieldName</kbd>.
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def push(fieldName:String, by:Any) = fieldIs("$push", fieldName, by);

  /**
   * Construct a modifier which appends all the elements of list-valued <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd>
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def pushAll(fieldName:String, by:Any) = fieldIs("$pushAll", fieldName, by);

  /**
   * Construct a modifier which adds value <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd> if it is not already there.
   * @param fieldName 
   * @param by 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def addToSet(fieldName:String, by:Any) = fieldIs("$addToSet", fieldName, by);

  /**
   *  Construct a modifier which adds each of the values in list-value <kbd>by</kbd> to list-valued field <kbd>fieldName</kbd> if it is not already there.
   * @param fieldName 
   * @param elements 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def addEachToSet(fieldName:String, elements:Any) = fieldIs("$addToSet", fieldName, it()-<"$each">-elements);

  /**
   * Construct a modifier which removes the last element in list-valued field <kbd>fieldName</kbd>.
   * @param fieldName 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def pop(fieldName:String) = fieldIs("$pop", fieldName, 1);

  /**
   * Construct a modifier which removes the <kbd>k</kbd>th element in list-valued field <kbd>fieldName</kbd>.
   * In particular, <kbd>pop("x", -1)</kbd> removes the last element of the field named <kbd>"x"</kbd>.
   * @param fieldName 
   * @param k 
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def pop(fieldName:String, n:Any) = fieldIs("$pop", fieldName, n);

  /**
   * Construct a modifier which removes all occurrences of <kbd>n</kbd> from list-valued field <kbd>fieldName</kbd>.
   * @param fieldName Field name to delete elements from
   * @param n Value to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def pull(fieldName:String, n:Any) = fieldIs("$pull", fieldName, n);

  /**
   * Construct a modifier which removes all occurrences of elements of list-valued <kbd>vs</kbd> from list-valued field <kbd>fieldName</kbd>
   * @param fieldName Field name to have elements deleted
   * @param vs List of elements to delete
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def pullAll(fieldName:String, vs:Any) = fieldIs("$pullAll", fieldName, vs);

  /**
   * Construct a modifier which renames field <kbd>oldName</kbd> to have name <kbd>newName</kbd>.
   * @param oldName Old name for field
   * @param newName New, improved name for field
   * @see http://www.mongodb.org/display/DOCS/Updating
   */
  public static def rename(oldName:String, newName: String) = fieldIs("$rename", oldName, newName);


/*
  public static def gt(fieldName: String, v: Any) = fieldIs(fieldName, "$gt", v);
  public static def gt(fieldName: String, v: Any) = fieldIs(fieldName, "$gt", v);
  public static def gt(fieldName: String, v: Any) = fieldIs(fieldName, "$gt", v);
*/
        
        
  /**
   * The WriteConcern to use when waiting for writes by default.  
   */
  public static val WaitForWrite = com.mongodb.WriteConcern.SAFE;
  

  /**
   * Convert an <kbd>x10.util.List</kbd> to a <kbd>java.util.List</kbd>.
   * @param M List to convert
   */
  public static def bsonize[T](M:List[T]) : java.util.List  {
    val L = new java.util.ArrayList();
    for(x in M) L.add(x);
    return L;
  }

  /**
   * Convert an X10 rail of <kbd>YakMap</kbd>s into a Java-style array of <kbd>DBBoject</kbd>s.
   * @param r Rail to convert
   */
  public static def convert(r: Rail[YakMap]) : x10.interop.Java.array[com.mongodb.DBObject] {
    val dr : Rail[com.mongodb.DBObject] = 
      new Rail[com.mongodb.DBObject](r.size, (i:Int) => r(i));
    return x10.interop.Java.convert[com.mongodb.DBObject](dr);
  }
  
  /**
   * Convert an X10 list into a Java list.
   * @param L X10 list to convert
   */
  public static def convert[T](L: List[T]) : java.util.List {
    val J = new java.util.ArrayList(L.size());
    for(x in L) J.add(x);
    return J;
  }

  /**
   * Convert a list of DBObjects, from the Java Mongo binding, into an X10-style List of YakMap
   * @param J Java list to convert.
   */
  public static def convertListOfDBObjects(J : java.util.List) : List[YakMap] {
    val Y = new ArrayList[YakMap](J.size());
    val Jiter = J.iterator();
    while(Jiter.hasNext()){
      val d : com.mongodb.DBObject = Jiter.next() as com.mongodb.DBObject;
      Y.add(YakMap.yakifyTopLevel(d));
    }
    return Y;
  }

  /**
   * Convert a Java-style list of Integers to an X10-style list of Ints.
   * @param J 
   */
  public static def convertIntList(J: java.util.List) : List[Int] {
    val X = new ArrayList[Int](J.size());
    val jiter = J.iterator();
    while (jiter.hasNext()) {
      val v = jiter.next();
      val iv : Int = v as Int;
      X.add(iv);
    }
    return X;
  }
  
  /**
   * Convert a Java-style list to an X10-style list.
   * @param J 
   */
  public static def convert[T](J: java.util.List) : List[T] {
    val X = new ArrayList[T](J.size());
    val Jiter = J.iterator();
    while(Jiter.hasNext()){
      val d : T = Jiter.next() as T;
      X.add(d);
    }
    return X;
  }
  
}

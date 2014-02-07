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
import com.mongodb.DBCollection;
import com.mongodb.DBEncoder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DB;
 
  /**
   * Yak implementation of cursors.  It is little more than a <kbd>Yak</kbd>-ified 
   * wrapper around the Java class <kbd>com.mongodb.DBCursor</kbd>, so that 
   * it can return <kbd>YakMap</kbd>s rather than the less X10-styled <kbd>DBObject</kbd>s. 
   * <p>
   * Like the underlying Java class <kbd>com.mongodb.DBCursor</kbd>, a cursor is either 
   * a reference to some database results (which is cheap), or an array of results (which can be
   * very large if there are a lot of results).  Calling <kbd>length</kbd> or <kbd>toArray</kbd> will 
   * convert the result set into an array.  Don't do this until you have narrowed the results to 
   * just the ones you want in the array.
   */
public class YakCursor implements Iterable[YakMap] {

  /**
   * An ordinary hash code method.
   */
  public def hashCode():Int = original.hashCode();
  /**
   * An ordinary equality test.
   * @param a 
   */
  public def equals(a:Any) = original.equals(a);


  /**
   * The wrapped object.
   */
  public val original : DBCursor;

  /**
   * Wrap a <kbd>com.mongodb.DBCursor</kbd> as a <kbd>YakCursor</kbd>.
   * @param orig 
   */
  public def this(orig: DBCursor) {
    this.original = orig;
  }


   static val y = YakUtil.it();
  /**
   * Returns an object containing some information about the execution of the query that created this cursor.
   * This creates a YakMap containing: 
   * <ol>
   * <li><kbd>"cursor":</kbd> cursor type</li>
   * <li><kbd>"millis":</kbd> how long it took the database to execute this query, in millisecond.</li>
   * <li><kbd>"n":</kbd> number of records that the database returned for this cursor.</li>
   * <li><kbd>"nScanned":</kbd> number of records that this query caused the database to examine</li>
   * </ol>
   */
    public  def explain(): YakMap = y( original.explain()); 

  /**
   * This does the same thing as <kbd>com.mongodb.DBCursor.snapshot()</kbd>.
   * As that method's javadoc says: 
   * <blockquote>Snapshot mode assures no duplicates are returned, or objects missed, which were present at both the start and end of the query's execution (if an object is new during the query, or deleted during the query, it may or may not be returned, even with snapshot mode). Note that short query responses (less than 1MB) are always effectively snapshotted. Currently, snapshot mode may not be used with sorting or explicit hints.</blockquote>
   */
    public  def snapshot(): YakCursor { return new YakCursor( original.snapshot()); }


  /**
   * Create a copy of a <kbd>YakCursor</kbd>. 
   * The copy is always a cursor, even if the original is backed by an array.
   * 
   */
    public  def copy(): YakCursor{ return new YakCursor(original.copy()); }
//    public  def iterator(): java.util.Iterator{ return original.iterator(); }


  /**
   * Sort the values that the cursor will yield, by sort criterion <kbd>sorting</kbd>.
   * This must be called before any values are obtained from the cursor.
   * This is called purely for side effects, and returns <kbd>this</kbd>.
   * @param sorting
   */
    public  def sort( sorting: com.mongodb.DBObject): YakCursor{
      original.sort(sorting);
      return this; }


  /**
   * Add a meta query operator to <kbd>this</kbd>.  
   * For example, <kbd>this.addSpecial("$maxScan", 100)</kbd> 
   * or <kbd>this.addSpecial("$returnKey", 1)</kbd>. 
   * See the Mongo spec for what the meta query operators are.
   * Some of them can be invoked in other ways; 
   * <i>e.g.</i>, <kbd>$orderBy</kbd> is equivalent to a call to <kbd>sort()</kbd>.
   * 
   * @param specialName Name of the meta query operator
   * @param value Its value
   * @see http://www.mongodb.org/display/DOCS/Advanced+Queries
   */
    public  def addSpecial( specialName: java.lang.String, value:  Any): YakCursor{  original.addSpecial(specialName, value); return this; }

  /**
   * Force the database to use a particular index for this query.
   * If used well, this can improve performance.  Be sure to call <kbd>hint()</kbd> before 
   * accessing any data.  
   * @param hint A BSON object whose fields are the keys in the index.
   * @see http://www.mongodb.org/display/DOCS/Optimization#Optimization-Hint
   */
    public  def hint( hint: com.mongodb.DBObject): YakCursor { original.hint(hint); return this;}

  /**
   * Force the database to use a particular index for this query.
   * If used well, this can improve performance.  Be sure to call <kbd>hint()</kbd> before 
   * accessing any data.  
   * @param indexName Name of the index to use.
   * @see http://www.mongodb.org/display/DOCS/Optimization#Optimization-Hint
   */
    public  def hint( indexName: java.lang.String): YakCursor{  original.hint(indexName); return this; }


  /**
   * Specify the maximum number of results to return.
   * @param limit The maximum number of results to return.
   * @see http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-%7B%7Blimit%28%29%7D%7D
   */
    public  def limit( limit: Int): YakCursor { original.limit(limit); return this; }

  /**
   * Set the maximum number of documents that this cursor will return from server to client in a batch; you generally will not care about this, since Mongo presents results to you as if they came in a single batch.
   * @param size 
   * @see http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-%7B%7BbatchSize%28%29%7D%7D
   */
    public  def batchSize( size: Int): YakCursor { original.batchSize(size); return this; }

  /**
   * Make the iteration start somewhere other than with the first element; this can be expensive.
   * Read the Mongo documentation about this if you are doing anything nontrivial with it.
   * @param nIgnored 
   * @see http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-%7B%7Bskip%28%29%7D%7D
   */
    public  def skip( nIgnored: Int): YakCursor {  original.skip(nIgnored); return this; }

  /**
   * Return the ID of the cursor, or 0 if there no active cursor.  
   * This just calls the Java method.
   */
    public  def getCursorId(): long{ return original.getCursorId(); }

  /**
   * Kills the current cursor on the server.
   */
    public  def close(): void{ original.close(); }

  /**
   * Deprecated; use <kbd>ReadPreference.SECONDARY</kbd>.  
   * Makes this query runnable on a secondary server.
   * Returns <kbd>this</kbd> for chaining.
   */
    public  def slaveOk(): YakCursor{  original.slaveOk(); return this; }

  /**
   * Add a query option to the underlying query.
   * See the static fields of <kbd>com.mongodb.Bytes</kbd> whose names start with 
   * <kbd>QUERYOPTION_</kbd> for the options.
   * @param option Option to add.
   */
    public  def addOption( option: int): YakCursor {  original.addOption(option); return this;}

  /**
   * Set the query options of the underlying query.
   * See the static fields of <kbd>com.mongodb.Bytes</kbd> whose names start with 
   * <kbd>QUERYOPTION_</kbd> for the options.
   * @param options Options to set
   */
    public  def setOptions( options: int): YakCursor { original.setOptions(options); return this; }

  /**
   * Clear the query options of the underlying query.
   * See the static fields of <kbd>com.mongodb.Bytes</kbd> whose names start with 
   * <kbd>QUERYOPTION_</kbd> for the options.
   */
    public  def resetOptions(): YakCursor{  original.resetOptions(); return this; }

  /**
   * Return the options of the underlying query.
   * See the static fields of <kbd>com.mongodb.Bytes</kbd> whose names start with 
   * <kbd>QUERYOPTION_</kbd> for the options.
   */
    public  def getOptions(): int{ return original.getOptions(); }


  /**
   * Return the number of times that the cursor has retrieved batches of results from the database.
   */
    public  def numGetMores(): int{ return  original.numGetMores(); }

  /**
   * Return the list of numbers of items received in each batch.
   */
    public  def getSizes(): x10.util.List[Int] { return YakUtil.convertIntList(original.getSizes()); }

  /**
   * Returns the number of objects which the cursor has returned.
   */
    public  def numSeen(): int{ return original.numSeen(); }

  /**
   * Returns true if there are one or more objects to return.
   */
    public  def hasNext(): boolean       { return original.hasNext(); }

  /**
   * Returns the next object from the iteration, if there is one, or throws an exception if not.
   */
    public  def next(): YakMap       { return y(original.next()); }

  /**
   * Re-returns the current object.
   */
    public  def curr(): com.mongodb.DBObject{ return original.curr(); }

  /**
   * Not implemented.
   * This method is required for the Java iteration framework, but it is inappropriate for cursors.
   */
    public  def remove(): void{  original.remove(); }

  /**
   * <b>Converts the cursor into an in-memory array</b> and returns the size of that array.
   * This can be very expensive if all you want is the length.
   */
    public  def length(): int       { return original.length(); }

  /**
   * Converts the cursor into an in-memory array.
   * For efficiency, this returns a <kbd>java.util.List</kbd>.  If you want a list of X10 objects, 
   * use <kbd>toX10List</kbd>.
   */
    public  def toArray(): java.util.List       { return original.toArray(); }

  /**
   * Converts the cursor to an in-memory list of <kbd>YakMap</kbd>s.
   * <b>Warning:</b> this is implemented inefficiently, first converting the cursor to a Java array and then 
   * converting that into an X10 list.  The former conversion is cached by the underlying Java object, 
   * so there is a point to having both.
   */
    public def toX10List() : x10.util.List[YakMap] = YakUtil.convertListOfDBObjects(original.toArray());

  /**
   * Convert the cursor to an in-memory array with a limit on how many elements to include.
   * @param max Maximum number of elements to include.
   */
    public  def toArray( max: int): java.util.List       { return original.toArray(max); }



  /**
   * Count the elements in the underlying query, ignoring limit and skip.
   * Evidently an alias for <kbd>size()</kbd>.
   */
    public  def count(): int       { return original.count(); }

  /**
   * Count the elements in the underlying query, ignoring limit and skip. 
   * Evidently an alias for <kbd>count()</kbd>.
   */
    public  def size(): int       { return original.size(); }


  /**
   * Return a BSON object whose field names are the names of the fields this query will return, or <kbd>null</kbd> if all keys are returned.
   */
    public  def getKeysWanted(): YakMap { return y(original.getKeysWanted()); }


  /**
   * Return the query that this cursor is iterating over.
   */
    public  def getQuery(): YakMap { return y(original.getQuery()); }
  /**
   * Return the collection that this cursor is iterating over.
   */
    public  def getCollection(): YakCollection { return y(original.getCollection()); }

  /**
   * Return the server address, if known.
   * This is certain to fail if no data has been retrieved yet.  It may also fail under other circumstances, 
   * depending on the underlying implementation.
   */
    public  def getServerAddress(): com.mongodb.ServerAddress{ return original.getServerAddress(); }

  /**
   * Set the read preferences.
   * @param prefs 
   */
    public  def setReadPreference( prefs: com.mongodb.ReadPreference): YakCursor{ original.setReadPreference(prefs); return this;}

  /**
   * Return the current read preferences.
   */
    public  def getReadPreference(): com.mongodb.ReadPreference{ return original.getReadPreference(); }

  /**
   * Set the decoder factory to use.
   * @param factory 
   */
    public  def setDecoderFactory( factory: com.mongodb.DBDecoderFactory): YakCursor{  original.setDecoderFactory(factory); return this;}
  /**
   * Return the decoder factory.
   */
    public  def getDecoderFactory(): com.mongodb.DBDecoderFactory{ return original.getDecoderFactory(); }
  /**
   * Standard string conversion.
   */
    public  def toString(): java.lang.String{ return original.toString(); }

    
  /**
   * X10-style iteration over the cursor, returning values as <kbd>YakMap</kbd>s.
   */
  public def iterator() : Iterator[YakMap] {
    val origerator = original.iterator();
    return new Iterator[YakMap]() {
      public def hasNext() = origerator.hasNext();
      public def next():YakMap = y(origerator.next() as DBObject);
    };
  }
  
  
  /**
   * X10-style iteration over the cursor, but returning the underlying values as seen by Java -- probably <kbd>DBObject</kbd>s rather than <kbd>YakMap</kbd>s.
   */
  public def anyIterator() : Iterator[Any] {
    val origerator = original.iterator();
    return new Iterator[Any]() {
      public def hasNext() = origerator.hasNext();
      public def next() = origerator.next();
    };
  }
}

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
import com.mongodb.DBCollection;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.DB;
import x10.io.*;

  /**
   * The Yak representation of a Mongo collection.
   * Internally, it is a wrapper around a <kbd>com.mongodb.DBCollection</kbd>.
   * It has approximately the same interface as one.
   * <p>
   * <p>
   * <a name="x10safety"/> <b>X10 Safety of Modification Operations:</b>
   * If you want X10 to be aware of when modification operations 
   * (<kbd>update</kbd>, <kbd>insert</kbd>, <kbd>save</kbd>, <kbd>remove</kbd>)
   * are finished, you will need to use methods which wait for the 
   * server to send back a response.  
   * <p>
   * Accordingly, <kbd>YakCollection</kbd>s have a default <kbd>WriteConcern</kbd> 
   * of <kbd>WriteConcern.SAFE</kbd>.    This <b>differs</b> from the Java Mongo 
   * class <kbd>com.mongodb.DBObject</kbd>, which uses a weaker default write concern. 
   * <p>
   * Using a <kbd>WriteConcern</kbd> argument of <kbd>WriteConcern.SAFE</kbd>
   * seems to do fairly well, but occasionally fails in stress tests. 
   * <b>Unlike</b> the Java binding for Mongo, the X10 binding takes 
   * <kbd>WriteConcern.SAFE</kbd> as the default.  This means that operations
   * which do not specify an explicit <kbd>WriteConcern</kbd> (usually) 
   * interact properly with X10's <kbd>finish</kbd> construct. 
   * <p>
   * If you need to get around this, you may either call <kbd>setWriteConcern()</kbd> on 
   * the whole collection, or use modification operations which specify a <kbd>WriteConcern</kbd>
   * argument.
   * <a name="queryoptions"/> <b>Query Options</b>
   * Like <kbd>com.mongodb.DBCollection</kbd>, a <kbd>YakCollection</kbd> has 
   * a set of default options which are used for queries.  These options 
   * can be manipulated by <kbd>addOption()</kbd>, <kbd>setOptions()</kbd>, 
   * <kbd>resetOptions()</kbd>, and <kbd>getOptions()</kbd>.  
   * See <kbd>com.mongodb.Bytes</kbd> for the available options.
   */
public class YakCollection   implements x10.io.CustomSerialization {

  /**
   * The <kbd>com.mongodb.DBCollection</kbd> object underlying this <kbd>YakCollection</kbd>, 
   * available as public data in case there's anything you need to do with it that you can't 
   * do on the <kbd>YakCollection</kbd>.
   */
  public val original : DBCollection;

  /**
   * A private constructor -- use the <kbd>make</kbd> method if you need to make one.
   * @param original 
   */
  private def this(original: DBCollection) {
    this.original = original;
    original.setWriteConcern(YakUtil.WaitForWrite);
  }

  /**
   * Factory method to create a <kbd>YakCollection</kbd> from an underlying
   * <kbd>DBCollection</kbd>.  Usually you won't need this method.  
   * @param coll The underlying collection.
   */
  public static def make(coll : DBCollection): YakCollection {
    return new YakCollection(coll);
  }

  // Custom serialization machinery

  /**
   * This class holds the information required to serialize a <kbd>YakCollection</kbd> sensibly.
   */
  static class SerializationClues(
    dbName: String,
    collName: String
  ){}
  
  /**
   * Serialization method, to satisfy the <kbd>x10.io.CustomSerialization</kbd> interface.  
   * Not recommended for any other purpose.
   */
  public def serialize():SerialData {
    return new SerialData(new SerializationClues(this.getDB().getName(), this.getName()), null);
  }

  /**
   * Serialization constructor.  This needs to be <kbd>public</kbd> for serialization purposes, but will rarely appear in your code.
   * @param sd Serialization data, which should be of type <kbd>SerializationClues</kbd>. 
   */
  public def this(sd:SerialData) {
    val sc = sd.data as SerializationClues;
    val dbName = sc.dbName;
    val collName = sc.collName;
    val db : com.mongodb.DB = YakUtil.db(dbName);
    original = db.getCollection(collName);
  }
  


  /**
   * This class's access to a <kbd>YakUtil</kbd>.  
   */
  static val y = YakUtil.it();
  // x10.interop.Java.array


  /**
   * Insert <kbd>newElements</kbd> into the collection, with write concern <kbd>concern</kbd>. 
   * @param newElements New elements to add
   * @param concern What kind of behavior and error reporting should this operation have?
   */
  public  def insert( newElements: Rail[YakMap], concern:  com.mongodb.WriteConcern): com.mongodb.WriteResult 
   = original.insert(YakUtil.convert(newElements), concern);

  /**
   * Insert several elements into the collection, specifying write concern and encoder
   * @param newElements New elements to add
   * @param concern What kind of behavior and error reporting should this operation have?
   * @param encoder Encoder to use when encoding the encodables.
   */
  public  def insert( newElements: Rail[YakMap], concern:  com.mongodb.WriteConcern, encoder:  com.mongodb.DBEncoder): com.mongodb.WriteResult = original.insert(YakUtil.convert(newElements), concern, encoder);

  /**
   * Insert one new element, specifying write concern
   * @param newObject New element to add. 
   * @param concern Write concern.
   */
  public  def insert( newObject: com.mongodb.DBObject, concern:  com.mongodb.WriteConcern): com.mongodb.WriteResult  = original.insert(newObject,concern);

  /**
   * Insert some new elements into the collection.
   * This method works pretty well with X10; see the class introduction
   * on <a href="#x10safety">X10 safety</a>.
   * @param newElements 
   */
  public  def insert( newElements: Rail[YakMap]): com.mongodb.WriteResult = original.insert(YakUtil.convert(newElements));


  /**
   * This method is an abstraction point for the Java class. 
   * @param concern 
   * @param newElements 
   */
  public  def insert( concern: com.mongodb.WriteConcern, newElements:  Rail[YakMap]): com.mongodb.WriteResult      = original.insert(concern, YakUtil.convert(newElements));

  //public  def insert( a11: java.util.List): com.mongodb.WriteResult       {}
  //public  def insert( a12: java.util.List, a13:  com.mongodb.WriteConcern): com.mongodb.WriteResult       {}


  /**
   * General update operation.
   * Find one or all (depending on <kbd>multi</kbd>) records matching <kbd>query</kbd>.  Apply the 
   * modifier <kbd>mod</kbd> to them.   If there are none and if <kbd>upsert</kbd> is true, 
   * insert a single record, obtained by applying <kbd>mod</kbd> to <kbd>query</kbd>.  
   * (This may do surprising things if <kbd>query</kbd> does more than equality tests on fields.)
   * Wait for results to be as reliably performed as specified by <kbd>concern</kbd>.  
   * @param query Search query for which record(s) to update.
   * @param mod Modifier to apply to those records
   * @param upsert Should this be an "upsert" operation? (See Mongo documents)
   * @param multi Should this apply to all records, or just one
   * @param concern What kind of behavior and error reporting should this operation have?
   */
  public  def update( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject, upsert:  boolean, multi:  boolean, concern:  com.mongodb.WriteConcern): com.mongodb.WriteResult  
  = original.update(query, mod, upsert, multi, concern);

  /**
   * General update operation.
   * Find one or all (depending on <kbd>multi</kbd>) records matching <kbd>query</kbd>.  Apply the 
   * modifier <kbd>mod</kbd> to them.   If there are none and if <kbd>upsert</kbd> is true, 
   * insert a single record, obtained by applying <kbd>mod</kbd> to <kbd>query</kbd>.  
   * (This may do surprising things if <kbd>query</kbd> does more than equality tests on fields.)
   * Wait for results to be as reliably performed as specified by <kbd>concern</kbd>. 
   * This uses an encoder. 
   * @param query Search query for which record(s) to update.
   * @param mod Modifier to apply to those records
   * @param upsert Should this be an "upsert" operation? (See Mongo documents)
   * @param multi Should this apply to all records, or just one
   * @param concern What kind of behavior and error reporting should this operation have?
   * @param encoder 
   */  public def update( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject, upsert:  boolean, a22:  boolean, concern:  com.mongodb.WriteConcern, encoder:  com.mongodb.DBEncoder): com.mongodb.WriteResult      
  = original.update(query, mod, upsert, a22, concern, encoder);

  /**
   * Update operation.
   * Find one or all (depending on <kbd>multi</kbd>) records matching <kbd>query</kbd>.  Apply the 
   * modifier <kbd>mod</kbd> to them.   If there are none and if <kbd>upsert</kbd> is true, 
   * insert a single record, obtained by applying <kbd>mod</kbd> to <kbd>query</kbd>.  
   * (This may do surprising things if <kbd>query</kbd> does more than equality tests on fields.)
   * Wait for results to be as reliably performed as specified by the default write concern, 
   * which is SAFE.  
   * This method works pretty well with X10; see the class introduction
   * on <a href="#x10safety">X10 safety</a>.
   * @param query Search query for which record(s) to update.
   * @param mod Modifier to apply to those records
   * @param upsert Should this be an "upsert" operation? (See Mongo documents)
   * @param multi Should this apply to all records, or just one
   */
  public  def update( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject, upsert:  boolean, multi:  boolean): com.mongodb.WriteResult     
     = original.update(query, mod, upsert, multi);


  /**
   * Update operation.
   * Find one  record matching <kbd>query</kbd>.  Apply the modifier <kbd>mod</kbd> to it.  
   * It neither upserts nor updates multiple records
   * Wait for results to be as reliably performed as specified by the default write concern, 
   * which is SAFE.  
   * This method works pretty well with X10; see the class introduction
   * on <a href="#x10safety">X10 safety</a>.
   * @param query Search query for which record(s) to update.
   * @param mod Modifier to apply to those records
   */
  public  def update( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject): com.mongodb.WriteResult  = original.update(query, mod, false, false);

 


 /**
   * Update operation.
   * Find all records matching <kbd>query</kbd>.  Apply the 
   * modifier <kbd>mod</kbd> to them.  
   * This is the usual <kbd>update</kbd>  operation with 
   * <kbd>multi=true</kbd> and <kbd>upsert=false</kbd>.
   * Wait for results to be as reliably performed as specified by the default write concern, 
   * which is SAFE.  
   * This method works pretty well with X10; see the class introduction
   * on <a href="#x10safety">X10 safety</a>.
   * @param query Search query for which record(s) to update.
   * @param mod Modifier to apply to those records
   */
    public  def updateMulti( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject): com.mongodb.WriteResult = original.update(query, mod, false, true);


    // protected abstract void doapply( a33: com.mongodb.DBObject) {}

  /**
   * Removes all elements satisfying <kbd>query</kbd>.
   * @param query 
   * @param concern - how much to wait for the result.
   */
    public  def remove( query: com.mongodb.DBObject, concern:  com.mongodb.WriteConcern): com.mongodb.WriteResult = original.remove(query, concern);

  /**
   * Removes all elements satisfying <kbd>query</kbd>.
   * @param query 
   * @param concern - how much to wait for the result.
   * @param encoder - encoder
   */
    public  def remove( query: com.mongodb.DBObject, concern:  com.mongodb.WriteConcern, encoder:  com.mongodb.DBEncoder): com.mongodb.WriteResult = original.remove(query, concern, encoder);

  /**
   * Removes all elements satisfying <kbd>query</kbd>.
   * Wait for results to be as reliably performed as specified by the default write concern, 
   * which is SAFE.  
   * This method works pretty well with X10; see the class introduction
   * on <a href="#x10safety">X10 safety</a>.
   * @param query 
   */
    public  def remove( query: com.mongodb.DBObject): com.mongodb.WriteResult = original.remove(query);

    //abstract java.util.Iterator __find( a40: com.mongodb.DBObject, a41:  com.mongodb.DBObject, a42:  int, a43:  int, a44:  int, a45:  int, a46:  com.mongodb.ReadPreference, a47:  com.mongodb.DBDecoder)       {}


  /**
   * Returns a cursor iterating over objects matching <kbd>query</kbd>.
   * <kbd>fields</kbd> is a BSON object whose fields will appear in the result; 
   * that is, to get only the <kbd>a</kbd> and <kbd>b</kbd> fields, 
   * use a <kbd>fields</kbd> parameter like <kbd>{a:1,b:1}</kbd>.
   * @param query Query that found objects will match.
   * @param fields Fields appearing in result.
   * @param numToSkip Number of records to skip before the first one returned.
   * @param batchSize Maximum number of records to return
   * @param options - Further options, which should be a sum of static fields of <kbd>com.mongodb.Bytes</kbd>, of the form <kbd>QUERYOPTION_*</kbd>.
   */
    public  final def find( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject, numToSkip:  int, batchSize:  int, options:  int): YakCursor = y(original.find(query,fields,numToSkip,batchSize,options));

  /**
   * Returns a cursor iterating over objects matching <kbd>query</kbd>.
   * <kbd>fields</kbd> is a BSON object whose fields will appear in the result; 
   * that is, to get only the <kbd>a</kbd> and <kbd>b</kbd> fields, 
   * use a <kbd>fields</kbd> parameter like <kbd>{a:1,b:1}</kbd>.
   * @param query Query that found objects will match.
   * @param fields Fields appearing in result.
   * @param numToSkip Number of records to skip before the first one returned.
   * @param batchSize Maximum number of records to return
   */
  public  final def find( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject, numToSkip:  int, batchSize:  int): YakCursor = y(original.find(query,fields,numToSkip,batchSize));

  /**
   * Returns a single object matching <kbd>query</kbd>, or <kbd>null</kbd> if no object matches.
   * @param query Query that found objects will match.
   */
  public  final def findOne( query: Any): YakMap = y(original.findOne(query));

  /**
   * Returns a single object matching <kbd>query</kbd>, or <kbd>null</kbd> if no object matches.
   * <kbd>fields</kbd> is a BSON object whose fields will appear in the result; 
   * that is, to get only the <kbd>a</kbd> and <kbd>b</kbd> fields, 
   * use a <kbd>fields</kbd> parameter like <kbd>{a:1,b:1}</kbd>.
   * @param query Query that found objects will match.
   * @param fields Fields appearing in result.
   */
    public  final def findOne( query: Any, fields:  com.mongodb.DBObject): YakMap = y(original.findOne(query,fields));


  /**
   * Find the first document in <kbd>sort</kbd> order matching <kbd>query</kbd>, and apply <kbd>mod</kbd> as an update modifier to it.
   * @param query Query to match
   * @param fields Fields to be returned, or null if all fields are to be returned.
   * @param sort Sort criterion to apply when picking which document is first, or null not to sort.
   * @param remove If true, the document found will be removed.
   * @param mod Update modifier to apply
   * @param returnNew If true, return the updated document; otherwise, return the original un-updated one.
   */
    public  def findAndModify( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject, sort:  com.mongodb.DBObject, remove:  boolean, mod:  com.mongodb.DBObject, returnNew:  boolean, upsert:  boolean): YakMap = y(original.findAndModify(query,fields,sort,remove,mod,returnNew,upsert));

  /**
   * Find the first document in <kbd>sort</kbd> order matching <kbd>query</kbd>, and apply <kbd>mod</kbd> as an update modifier to it.
   * @param query Query to match
   * @param sort Sort criterion to apply when picking which document is first.
   * @param mod Update modifier to apply
   */
  public  def findAndModify( query: com.mongodb.DBObject, sort:  com.mongodb.DBObject, mod:  com.mongodb.DBObject): YakMap = y(original.findAndModify(query,sort,mod));


  /**
   * Find a document matching <kbd>query</kbd>, and apply <kbd>mod</kbd> as an update modifier to it.
   * @param query Query to match
   * @param mod Update modifier to apply
   */
    public  def findAndModify( query: com.mongodb.DBObject, mod:  com.mongodb.DBObject): YakMap = y(original.findAndModify(query,mod));




  /**
   * Find a document matching <kbd>query</kbd>, remove it, and return it.
   * @param query The query to match.
   */
    public  def findAndRemove( query: com.mongodb.DBObject): YakMap = y(original.findAndRemove(query));


  /**
   * Forces creation of an index on <kbd>keys</kbd>, if one does not already exist.
   * @param keys A BSON object, whose keys will be the fields indexed upon.
   */
    public  final def createIndex( keys: com.mongodb.DBObject): void       {  original.createIndex(keys);}
  /**
   * Forces creation of an index on <kbd>keys</kbd>, if one does not already exist.
   * @param keys A BSON object, whose keys will be the fields indexed upon.
   * @param options See the Mongo documentation
   */
    public  def createIndex( keys: com.mongodb.DBObject, options:  com.mongodb.DBObject): void       {  original.createIndex(keys,options);}

  /**
   * Forces creation of an index on <kbd>keys</kbd>, if one does not already exist.
   * @param keys A BSON object, whose keys will be the fields indexed upon.
   * @param options See the Mongo documentation
   * @param encoder An encoder
   */
    public def createIndex( keys: com.mongodb.DBObject, options:  com.mongodb.DBObject, encoder:  com.mongodb.DBEncoder): void       {  original.createIndex(keys,options,encoder);}

  /**
   * Creates an index on the field named <kbd>fieldName</kbd> with default options, unless one already exists.
   * @param fieldName 
   */
    public  final def ensureIndex( fieldName: java.lang.String): void {original.ensureIndex(fieldName);}

  /**
   * Creates an index on the fields of <kbd>keys</kbd>, unless one already exists.
   * @param keys 
   */
    public  final def ensureIndex( keys: com.mongodb.DBObject): void       {  original.ensureIndex(keys);}

  /**
   * Creates an index on the fields of <kbd>keys</kbd>, unless one already exists, named <kbd>name</kbd>
   * @param keys 
   * @param name 
   */
    public  def ensureIndex( keys: com.mongodb.DBObject, name:  java.lang.String): void       {  original.ensureIndex(keys,name);}

  /**
   * Create an index named <kbd>name</kbd> on the fields of <kbd>keys</kbd> unless one already exists; it may be <kbd>unique</kbd>.
   * @param keys 
   * @param name 
   * @param unique - Should the index be unique?  (See the Mongo docs for what this means)
   */
    public  def ensureIndex( keys: com.mongodb.DBObject, name:  java.lang.String, unique:  boolean): void       {  original.ensureIndex(keys,name,unique);}

    public  final def ensureIndex( keys: com.mongodb.DBObject, a87:  com.mongodb.DBObject): void       {  original.ensureIndex(keys,a87);}


  /**
   * Clears all indices that have not yet been applied to this collection.
   */
    public  def resetIndexCache( ): void { original.resetIndexCache(); }
    //com.mongodb.DBObject defaultOptions( a89: com.mongodb.DBObject) = original.defaultOptions('');
    //public static def java.lang.String genIndexName( a90: com.mongodb.DBObject)  = original.java();

  /**
   * Set the hints for this collection, which may help optimizing queries.  See the Mongo documents for how to use this.
   * @param listOfHints - a 
   */
    public  def setHintFields( listOfHints: x10.util.List[YakMap]): void { original.setHintFields(YakUtil.convert(listOfHints));}
    
    
  /**
   * Return a cursor iterating over objects matching <kbd>query</kbd>, 
   * showing only the fields of <kbd>fields</kbd>.
   * @param query - query object
   * @param fields - fields to return.
   */
    public  final def find( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject): YakCursor = y(original.find(query,fields));

  /**
   * Return a cursor iterating over objects matching <kbd>query</kbd>, 
   * showing all fields
   * @param query - query object
   */
    public  final def find( query: com.mongodb.DBObject): YakCursor = y(original.find(query));

  /**
   * Return a cursor iterating over all the records of <kbd>this</kbd>. 
   */
    public  final def find(  ): YakCursor = y(original.find());

  /**
   * Return an element of this collection, or <kbd>null</kbd> if the collection is empty.
   */
    public  final def findOne(  ): YakMap  =y( original.findOne());

  /**
   * Return an element matching <kbd>query</kbd>, or <kbd>null</kbd> if no element does.
   * @param query The query to search for.
   */
    public  final def findOne( query: com.mongodb.DBObject): YakMap        = y(original.findOne(query));

  /**
   * Return the listed <kbd>fields</kbd> of an element of the collection matching <kbd>query</kbd>.
   * @param query the query to seek.
   * @param fields a BSON object, whose keys are the names of the fields to return
   */
    public  final def findOne( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject): YakMap = y(original.findOne(query,fields));

  /**
   * Return the listed <kbd>fields</kbd> of an element of the collection matching <kbd>query</kbd>, using <kbd>readPreference</kbd>.
   * @param query the query to seek.
   * @param fields a BSON object, whose keys are the names of the fields to return
   * @param readPreference your preferences for reading.
   */
    public  final def findOne( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject, readPreference:  com.mongodb.ReadPreference): YakMap = y(original.findOne(query,fields,readPreference));
 


  /**
   * Calls <kbd>com.mongodb.DBCollection.apply</kbd>, <i>q.v.</i>
   * @param a103 
   */
    public  final def apply( a103: com.mongodb.DBObject): Any = original.apply(a103);
  /**
   * Calls <kbd>com.mongodb.DBCollection.apply</kbd>, <i>q.v.</i>
   * @param a104
   * @param a105 
   */
    public  final def apply( a104: com.mongodb.DBObject, a105:  boolean): Any = original.apply(a104,a105);


  /**
   * Save a <kbd>newRecord</kbd> into this collection
   * @param newRecord The new record to save
   * @param concern How concerned you are that it actually be saved.
   */
    public  final def save( newRecord: com.mongodb.DBObject, concern:  com.mongodb.WriteConcern): com.mongodb.WriteResult        = original.save(newRecord,concern);

  /**
   * Save a <kbd>newRecord</kbd> into this collection
   * @param newRecord The new record to save
   */
    public  final def save( newRecord: com.mongodb.DBObject): com.mongodb.WriteResult = original.save(newRecord);


  /**
   * delete all indexes from this collection.  
   */
    public  def dropIndexes(  ): void       {  original.dropIndexes();}

  /**
   * Drop the index named <kbd>name</kbd> from this collection.
   * @param name the name of the index to drop
   */
    public  def dropIndexes( name: java.lang.String): void       {  original.dropIndexes(name);}

  /**
   * Delete this collection from the database.
   */
    public  def drop(  ): void       {original.drop();}

  /**
   * Return the number of documents in this collection.
   */
    public  def count( ): long       = original.count();


  /**
   * Return the number of elements in the collection matching <kbd>query</kbd>.
   * @param query 
   */
    public  def count( query: com.mongodb.DBObject): long       = original.count(query);


  /**
   * Returns the number of documents in this.  The relation to <kbd>count</kbd> is unclear.
   * This is a copy of a method from Java.
   */
    public  def getCount( ): long        = original.getCount();
  /**
   * Returns the number of documents matching <kbd>query</kbd>.  The relation to <kbd>count</kbd> is unclear.
   * This is a copy of a method from Java.
   * @param query The query to count.
   */
    public  def getCount( query: com.mongodb.DBObject): long        = original.getCount(query);
  /**
   * Returns the number of documents matching <kbd>query</kbd>.
   * This is a copy of a method from the Java implementation,
   * which calls <kbd>getCount(query,fields,0L,0L)</kbd>.
   * Despite <kbd>fields</kbd> being documented (as in Java) as "fields to return", 
   * <kbd>getCount()</kbd> returns a long, not any fields.
   * @param query query to match
   * @param fields fields to return.
   */
    public  def getCount( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject): long       = original.getCount(query,fields);


  /**
   * Returns the number of documents matching <kbd>query</kbd>.  
   * This is a copy of a method from the Java implementation. 
   * (In particular we have no explanation of <kbd>fields</kbd> beyond that given here, 
   * which is copied from the javadoc of the Java implementation.  This method does not return fields, 
   * so we are uncertain about the purpose of <kbd>fields</kbd>.)
   * @param query  query to select documents to count
   * @param fields fields to return
* @param limit limit the count to this value
   * @param skip number of entries to skip
   */
    public  def getCount( query: com.mongodb.DBObject, fields:  com.mongodb.DBObject, limit:  long, skip:  long): long       = original.getCount(query,fields,limit,skip);


  /**
   * Renames this collection to <kbd>newName</kbd>.   Returns the renamed collection.
   * @param newName The new name
   */
    public  def rename( newName: java.lang.String): YakCollection
                = this.rename(newName, false);

  /**
   * Renames this collection to <kbd>newName</kbd>, controlling what to do if that name is in use.
   * If a collection of that name already exists, the behavior depends on 
   * the flag <kbd>dropExistingCollectionWithThatName</kbd>.
   * If <kbd>dropExistingCollectionWithThatName</kbd> is true, the existing collection is deleted.
   * If it is false, an exception is thrown.
   * @param newName The new name
   * @param dropExistingCollectionWithThatName What to do in case of error.
   */
  public  def rename( newName: java.lang.String, dropExistingCollectionWithThatName:  boolean):YakCollection {
    var res : YakCollection = this;
    try {
      res = y(original.rename(newName,dropExistingCollectionWithThatName));
    }
    catch (e: CheckedThrowable) {
      throw new YakException(e.toString());
    }
    return res;
  }


  /**
   * A Mongo group operation, analogous to SQL's <kbd>GROUP BY</kbd> operation.
   * This function maintains an <i>aggregation counter object</i>, 
   * initialized to <kbd>initial</kbd>,
   * updated by the Javascript function <kbd>reduce</kbd> for each record in the group
   * and tidied up by the Javascript function <kbd>finalizer</kbd> afterwards.
   * See the Mongo documentation for details.
   * 
   * @param key A BSON object whose field names are the keys to group by. (In the Mongo examples, the associates values are always <kbd>true</kbd>. This may be significant.)
   * @param cond A query; only the values which satisfy <kbd>cond</kbd> are grouped.
   * @param initial The initial value of the aggregation counter object.
   * @param reducer A string holding a Javascript function <kbd>function(obj,prev){...}</kbd> taking the current record as <kbd>obj</kbd> and the previous aggregation counter as <kbd>prev</kbd>.
   * @param finalizer A string holding a Javascript function <kbd>function(finalCounter){...}</kbd> taking the final value of the aggregation counter.
   * @see http://www.mongodb.org/display/DOCS/Aggregation
   */
    public  def group( key: com.mongodb.DBObject, cond:  com.mongodb.DBObject, initial:  com.mongodb.DBObject, reducer:  java.lang.String, finalizer:  java.lang.String): YakMap        = y(original.group(key, cond, initial, reducer, finalizer));

  /**
   * A Mongo group operation, analogous to SQL's <kbd>GROUP BY</kbd> operation.
   * This function maintains an <i>aggregation counter object</i>, 
   * initialized to <kbd>initial</kbd>,
   * updated by the Javascript function <kbd>reduce</kbd> for each record in the group.
   * See the Mongo documentation for details.
   * 
   * @param key A BSON object whose field names are the keys to group by. (In the Mongo examples, the associates values are always <kbd>true</kbd>. This may be significant.)
   * @param cond A query; only the values which satisfy <kbd>cond</kbd> are grouped.
   * @param initial The initial value of the aggregation counter object.
   * @param reducer A string holding a Javascript function <kbd>function(obj,prev){...}</kbd> taking the current record as <kbd>obj</kbd> and the previous aggregation counter as <kbd>prev</kbd>.
   * @see http://www.mongodb.org/display/DOCS/Aggregation
   */
    public  def group( key: com.mongodb.DBObject, cond:  com.mongodb.DBObject, initial:  com.mongodb.DBObject, reducer:  java.lang.String): com.mongodb.DBObject        = y(original.group(key, cond, initial, reducer));

  /**
   * Grouping via a <kbd>GroupCommand</kbd> object.
   * @param groupCommand 
   * @see http://www.mongodb.org/display/DOCS/Aggregation
   */
    public  def group( groupCommand: com.mongodb.GroupCommand): YakMap = y(original.group(groupCommand));

  /**
   * Grouping by a key. 
   * @param key A BSON object whose field names are the keys to group by. (In the Mongo examples, the associates values are always <kbd>true</kbd>. This may be significant.)
   * @see http://www.mongodb.org/display/DOCS/Aggregation
   */
    public  def group( key: com.mongodb.DBObject): YakMap        = y(original.group(key));


  /**
   * Return the list of distinct values of the field named <kbd>key</kbd>, 
   * for records which match <kbd>query</kbd>.
   * @param key The name of the field to return
   * @param query The query describing the records of interest.
   */
    public  def distinct( key: java.lang.String, query:  com.mongodb.DBObject): java.util.List = original.distinct(key,query);

  /**
   * Return the list of distinct values of the field named <kbd>key</kbd>.
   * @param key The name of the field to return
   */
    public  def distinct( key: java.lang.String): java.util.List = original.distinct(key);

  /**
   * A fully-parameterized map/reduce function.
   * @param map A javascript function describing the mapping.
   * @param reduce A javascript function describing the reduction.
   * @param outputTarget Name of the collection that will hold the output, or <kbd>null</kbd> if it should be a temporary collection
   * @param outputType How should the output be captured?
   * @param query A description of which records are to be mapped and reduced.
   * @see http://www.mongodb.org/display/DOCS/MapReduce
   */
    public  def mapReduce( map: java.lang.String, reduce:  java.lang.String, outputTarget:  java.lang.String, outputType:  com.mongodb.MapReduceCommand.OutputType, query:  com.mongodb.DBObject): YakMapReduceOutput        = y(original.mapReduce(map,reduce,outputTarget,outputType,query));

  /**
   * A nearly-fully-parameterized map/reduce function.
   * The output is stored as the collection named <kbd>outputTarget</kbd>.
   * @param map A javascript function describing the mapping.
   * @param reduce A javascript function describing the reduction.
   * @param outputTarget Name of the collection that will hold the output, or <kbd>null</kbd> if it should be a temporary collection
   * @param query A description of which records are to be mapped and reduced.
   * @see http://www.mongodb.org/display/DOCS/MapReduce
   * TODO - make sure that YakUtil can handle MapReduceOutput.
   */
    public  def mapReduce( map: java.lang.String, reduce:  java.lang.String, outputTarget:  java.lang.String, query:  com.mongodb.DBObject): YakMapReduceOutput        = y(original.mapReduce(map,reduce,outputTarget,query));



  /**
   * Map/reduce using the <kbd>MapReduceCommand</kbd> interface.
   * @param mapReduceCommand
   * @see http://www.mongodb.org/display/DOCS/MapReduce
   */
    public  def mapReduce( mapReduceCommand: com.mongodb.MapReduceCommand): YakMapReduceOutput        = y(original.mapReduce(mapReduceCommand));
    
  /**
   * Map/reduce using a single BSON object.
   * @param command A BSON object holding all the parameters of the map/reduce operation
   * @see http://www.mongodb.org/display/DOCS/MapReduce
   */
    public  def mapReduce( command: com.mongodb.DBObject): YakMapReduceOutput        = y(original.mapReduce(command));


  /**
   * Return the index information for <kbd>this</kbd>, as a list of <kbd>YakMap</kbd>s. 
   * @see http://www.mongodb.org/display/DOCS/Indexes
   */
    public  def getIndexInfo( ): x10.util.List[YakMap] = YakUtil.convertListOfDBObjects(original.getIndexInfo());


  /**
   * Drop an index, given by the keys of <kbd>keys</kbd>
   * @param keys A BSON object whose keys are the keys of the index to drop
   */
    public  def dropIndex( keys: com.mongodb.DBObject): void       {  original.dropIndex(keys);}

  /**
   * Drop the index with the given name
   * @param indexName Name of the index to drop
   */
    public  def dropIndex( indexName: java.lang.String): void       {  original.dropIndex(indexName);}


  /**
   * Get statistics about this collection, as with the <kbd>collstats</kbd> command.
   */
    public  def getStats( ): com.mongodb.CommandResult = original.getStats();
  /**
   * Tell whether or not this collection is capped.
   * @see http://api.mongodb.org/wiki/current/Capped%20Collections.html
   */
    public  def isCapped( ): boolean = original.isCapped();


  /**
   * Return a collection whose name starts with <kbd>this</kbd>'s name.
   * For example, if this collection's name is <kbd>foo</kbd>, 
   * then <kbd>getCollection("bar")</kbd> will return a collection named <kbd>foo.bar</kbd>.
   * @param subname The suffix to add to the returned collection's name.
   */
    public  def getCollection( subname: java.lang.String):YakCollection /*com.mongodb.DBCollection*/ = y(original.getCollection(subname));


  /**
   * Return the name of this collection.
   * Unlike <kbd>getFullName()</kbd>, the name returned by <kbd>getName()</kbd> does not have 
   * the database's name as a prefix.
   */
    public  def getName(  ): java.lang.String = original.getName();
  /**
   * Return the name of this collection, starting with the database name.
   * Unlike <kbd>getName()</kbd>, the name returned by <kbd>getFullName()</kbd> has
   * the database's name as a prefix.
   */
    public  def getFullName( ): java.lang.String = original.getFullName();

  /**
   * Return the database containing <kbd>this</kbd>.
   */
    public  def getDB( ): com.mongodb.DB = original.getDB();


  /**
   * Hash code, as standard. 
   */
    public  def hashCode(  ): int = original.hashCode();
  /**
   * Equality test, as standard.
   * @param other 
   */
    public  def equals( other: Any): boolean = original.equals(other);
  /**
   * Utterly standard string conversion.
   */
    public  def toString(  ): java.lang.String = original.toString();

    //public  def setObjectClass( a168: java.lang.Class): void = original.setObjectClass(a168);
    //public  def getObjectClass(  ): java.lang.Class = original.getObjectClass();
    //public  def setInternalClass( a170: java.lang.String, a171:  java.lang.Class): void = original.setInternalClass('');
    //protected java.lang.Class getInternalClass( a172: java.lang.String);

  /**
   * Set the default write concern, which will be used for operations which don't mention another one.
   * @param concern New <kbd>WriteConcern</kbd>
   */
    public  def setWriteConcern( concern: com.mongodb.WriteConcern): void { original.setWriteConcern(concern); }
  /**
   * Retrieve the default write concern.
   */
    public  def getWriteConcern( ): com.mongodb.WriteConcern = original.getWriteConcern();
  /**
   * Set the default read preference, used for <kbd>findOnce</kbd>
   * @param pref Your favorite ReadPreference.
   */
    public  def setReadPreference( pref: com.mongodb.ReadPreference): void { original.setReadPreference(pref); }
  /**
   * Retrieve the default read preference.
   */
    public  def getReadPreference( ): com.mongodb.ReadPreference = original.getReadPreference();

    // Deprecated. public  def slaveOk( ): void { original.slaveOk(); }
  /**
   * Add an option to the default options for queries. 
   * See <kbd>com.mongodb.Bytes</kbd> for the available options.
   * @see #queryoptions
   * @param option 
   */
    public  def addOption( option: int): void  { original.addOption(option); }
  /**
   * Use option as the only default option(s) for queries.
   * See <kbd>com.mongodb.Bytes</kbd> for the available options.
   * @param option 
   * @see #queryoptions
   */
    public  def setOptions( option: int): void { original.setOptions(option); }
  /**
   * Clear the default options for queries. 
   * See <kbd>com.mongodb.Bytes</kbd> for the available options.
   * @see #queryoptions
   */
    public  def resetOptions( ): void { original.resetOptions(); }
  /**
   * Retrieve the default options for queries.
   * See <kbd>com.mongodb.Bytes</kbd> for the available options.
   * @see #queryoptions
   */
    public  def getOptions(  ): int  = original.getOptions();
    //public synchronized def void setDBDecoderFactory( a182: com.mongodb.DBDecoderFactory):  = original.void('');
    //public synchronized def  getDBDecoderFactory( a183: ): com.mongodb.DBDecoderFactory = original.com('');
    //public synchronized def void setDBEncoderFactory( a184: com.mongodb.DBEncoderFactory): void = original.void('');
    //public synchronized def getDBEncoderFactory( a185: ): com.mongodb.DBEncoderFactory   = original.getDBEncoderFactory('');


}

        

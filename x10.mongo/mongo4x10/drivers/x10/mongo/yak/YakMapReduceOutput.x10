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
import com.mongodb.DBObject;
import com.mongodb.DB;
import com.mongodb.CommandResult;
import com.mongodb.MapReduceOutput;
import x10.io.*;

public class YakMapReduceOutput  {
       
  public val original : MapReduceOutput;

  public def this(original: MapReduceOutput) {
    this.original = original;
  }
  
  public def toString()="YakMapReduceOutput()";
       
  private static val y = YakUtil.it();
  
  public def getCommand() : YakMap = y(original.getCommand());
  public def getOutputCollection() : YakCollection = y(original.getOutputCollection());
   
  
  public def getServerUsed() = original.getServerUsed();
  
  public def results() : Iterable[YakMap] {
    val origResults : java.lang.Iterable = original.results();
    val origIter = origResults.iterator();
    val resiter : Iterator[YakMap] = new Iterator[YakMap](){
      public def hasNext() = origIter.hasNext();
      public def next() : YakMap = y(origIter.next() as DBObject);
    };
     
    val res = new Iterable[YakMap]() {
      public def iterator() : Iterator[YakMap] = resiter;
    };
    
    return res;
  }

   
}

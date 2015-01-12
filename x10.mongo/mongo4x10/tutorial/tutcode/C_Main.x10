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
import x10.mongo.yak.YakUtil; 
import x10.mongo.yak.YakMap; 
import com.mongodb.ServerAddress;

public class C_Main {
  static val y = YakUtil.it(); 
  static val coll = y.collection("test", "x10tutorial.c");
  public static def main(argv:Array[String](1)) {
    discuss_write_concerns();
    discuss_original();
    reset_database("Example");
    for(rec in coll.find()) Runtime.println("reset_database> " + rec);
    other_dolphins();
    remove_chickadee();
    find_things();
    findOne_thing();
    add_wings();
    concurrency_and_modifiers();
  }
  
  
  static def discuss_write_concerns(){
    Runtime.println("Discuss Write Concerns");
  }
  
  static def discuss_original(){
    Runtime.println("Discuss this.original");
  }
  
  static def reset_database(message: String){
    coll.drop();
    coll.save(y -<"name">- "antelope"  -<"genus">- "Antilope"  -<"species">- "cervicapra"   -<"feet">- 4);
    coll.save(y -<"name">- "bison"     -<"genus">- "Bison"     -<"species">- "bison"        -<"feet">- 4);
    coll.save(y -<"name">- "chickadee" -<"genus">- "Poecile"   -<"species">- "atricapillus" -<"feet">- 2);
    coll.save(y -<"name">- "dolphin"   -<"genus">- "Delphinus" -<"species">- "delphis"      -<"feet">- 0); 
    if (message != null) Runtime.println("=== " + message + " ===");
  }

  static def other_dolphins(){
    reset_database("Use Another Kind of Dolphin");
    val dolphin = y.eq("name", "dolphin");
    val long_beaked = y.set("species", "capensis");
    coll.update(dolphin, long_beaked); // long-beaked
    for(rec in coll.find(dolphin)) Runtime.println("other_dolphins> " + rec);
  }
  
  static def remove_chickadee() {
    reset_database("Remove Chickadee");
    val bipeds = y.eq("feet", 2);
    Runtime.println("With birds: " + coll.count(bipeds));
    coll.remove(y -<"name">- "chickadee");
    Runtime.println("Without birds: " + coll.count(bipeds));
  }

  static def find_things() {
    reset_database("Finding Things");
    val cursor = coll.find( y.eq("feet", 4) );
    for(rec in cursor) Runtime.println("quadruped: " + rec);
  }
  
  static def findOne_thing(){
    reset_database("Finding One Thing");
    val rec = coll.findOne( y.lte("feet", 1) );
    Runtime.println("At most one foot: " + rec);
  }
  
  
  static def add_wings(){
    val chickadee = y.eq("name", "chickadee");
    val has_two_wings = y.set("wings", 2);
    coll.findAndModify(chickadee, has_two_wings);
    val rec = coll.findOne(chickadee);
    Runtime.println("Winged chickadee: " + rec);
  }

  static def concurrency_and_modifiers(){
    reset_database("Concurrency and Modifiers");
    // Let a thousand chickadees bloom!  In each of a dozen activities
    val N = 1000;
    val nActivities = 12;
    val desired_total = nActivities * N;
    val chickadee = y.eq("name", "chickadee");
    finish { coll.update(chickadee, y.set("number", 0)); }
    val hatch = y.inc("number", 1);
    // First, do it without concurrency at all: 
    finish { coll.update(chickadee, y.set("number", 0)); }
    {
      for(act in 1..nActivities)  {
        for (i in 1..N) coll.update(chickadee, hatch);
      }
      val nSeen = coll.findOne(chickadee).long("number");
      Runtime.println("With concurrency gone , nSeen = " + nSeen + ", but should be " + desired_total);
    }
    // First, do the concurrency wrong...
    {
      finish { coll.update(chickadee, y.set("number", 0)); }
      finish {
        for(act in 1..nActivities) async {
          for (i in 1..N) coll.update(chickadee, hatch);
        }
        var nSeen : Long = 0;
        var i : Int = 0;
        while (nSeen < desired_total) {
          nSeen = coll.findOne(chickadee).long("number");
          Runtime.println("With concurrency wrong (" + i + "), nSeen = " + nSeen + ", but should be " + desired_total);
          System.sleep(500);
          i += 1;
        }
      }//finish
    }
    // Now, do it right
    {
      reset_database(null);
      finish { coll.update(chickadee, y.set("number", 0)); }
      finish {
        for(act in 1..nActivities) async {
          for (i in 1..N) coll.update(chickadee, hatch);
        }
      }//finish
      val nSeen = coll.findOne(chickadee).long("number");
      Runtime.println("With concurrency right, nSeen = " + nSeen + ", but should be " + desired_total);
    }
  }

      
}

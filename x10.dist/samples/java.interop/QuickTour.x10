/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import com.mongodb.Mongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

import x10.interop.Java;


/**
 * An example to show how to use mongoDB from X10.
 *
 * The original is example/QuickTour.java
 * Ported to X10 2.3
 *
 * Compile as "x10c -cp mongo-2.9.1.jar QuickTour.x10"
 * Run as "x10 -cp .:mongo-2.9.1.jar QuickTour [mongohost]"
 */
public class QuickTour {

    public static def main(args:Rail[String]) throws CheckedException :void {

        // connect to the specified or local database server
        val m = args.size > 0 ? new Mongo(args(0)) : new Mongo();

        // get handle to "mydb"
        val db = m.getDB( "mydb" );

        // Authenticate - optional
        // val auth = db.authenticate("foo", "bar");


        // get a list of the collections in this database and print them out
        val colls = db.getCollectionNames();
        val collsIter = colls.iterator();
        while (collsIter.hasNext()) {
            val s = collsIter.next() as String;
            Console.OUT.println(s);
        }

        // get a collection object to work with
        val coll = db.getCollection("testCollection");

        // drop all the data in it
        coll.drop();


        // make a document and insert it
        val doc = new BasicDBObject();

        doc.put("name", "MongoDB");
        doc.put("type", "database");
        // N.B. Java classes don't understand x10.core.Int etc.
        //doc.put("count", 1);
        doc.put("count", Java.convert(1));

        val info = new BasicDBObject();

        // N.B. Java classes don't understand x10.core.Int etc.
        //info.put("x", 203);
        //info.put("y", 102);
        info.put("x", Java.convert(203));
        info.put("y", Java.convert(102));

        doc.put("info", info);

        // N.B. X10 doesn't support varargs
        //coll.insert(doc);
        coll.insert(Java.convert([doc]));

        // get it (since it's the only one in there since we dropped the rest earlier on)
        val myDoc = coll.findOne();
        Console.OUT.println(myDoc);

        // now, lets add lots of little documents to the collection so we can explore queries and cursors
        for (var i:Int=0; i < 100; i++) {
            // N.B. X10 doesn't support varargs
            // N.B. Java classes don't understand x10.core.Int etc.
            //coll.insert(new BasicDBObject().append("i", i));
            coll.insert(Java.convert([new BasicDBObject().append("i", Java.convert(i))]));
        }
        Console.OUT.println("total # of documents after inserting 100 small ones (should be 101) " + coll.getCount());

        //  lets get all the documents in the collection and print them out
        var cursor:DBCursor = coll.find();
        try {
            while (cursor.hasNext()) {
                Console.OUT.println(cursor.next());
            }
        } finally {
            cursor.close();
        }

        //  now use a query to get 1 document out
        var query:BasicDBObject = new BasicDBObject();
        // N.B. Java classes don't understand x10.core.Int etc.
        //query.put("i", 71);
        query.put("i", Java.convert(71));
        cursor = coll.find(query);

        try {
            while (cursor.hasNext()) {
                Console.OUT.println(cursor.next());
            }
        } finally {
            cursor.close();
        }

        //  now use a range query to get a larger subset
        query = new BasicDBObject();
        // N.B. Java classes don't understand x10.core.Int etc.
        //query.put("i", new BasicDBObject("$gt", 50));  // i.e. find all where i > 50
        query.put("i", new BasicDBObject("$gt", Java.convert(50)));  // i.e. find all where i > 50
        cursor = coll.find(query);

        try {
            while (cursor.hasNext()) {
                Console.OUT.println(cursor.next());
            }
        } finally {
            cursor.close();
        }

        // range query with multiple contstraings
        query = new BasicDBObject();
        // N.B. Java classes don't understand x10.core.Int etc.
        //query.put("i", new BasicDBObject("$gt", 20).append("$lte", 30));  // i.e.   20 < i <= 30
        query.put("i", new BasicDBObject("$gt", Java.convert(20)).append("$lte", Java.convert(30)));  // i.e.   20 < i <= 30
        cursor = coll.find(query);

        try {
            while (cursor.hasNext()) {
                Console.OUT.println(cursor.next());
            }
        } finally {
            cursor.close();
        }

        // create an index on the "i" field
        // N.B. Java classes don't understand x10.core.Int etc.
        //coll.createIndex(new BasicDBObject("i", 1));  // create index on "i", ascending
        coll.createIndex(new BasicDBObject("i", Java.convert(1)));  // create index on "i", ascending


        //  list the indexes on the collection
        val list = coll.getIndexInfo();
        val listIter = list.iterator();
        while (listIter.hasNext()) {
            val o = listIter.next() as DBObject;
            Console.OUT.println(o);
        }

        // See if the last operation had an error
        Console.OUT.println("Last error : " + db.getLastError());

        // see if any previous operation had an error
        Console.OUT.println("Previous error : " + db.getPreviousError());

        // force an error
        db.forceError();

        // See if the last operation had an error
        Console.OUT.println("Last error : " + db.getLastError());

        db.resetError();

        // release resources
        m.close();
    }
}

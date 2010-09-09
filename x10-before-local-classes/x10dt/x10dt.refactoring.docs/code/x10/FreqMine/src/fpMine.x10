/*
 *  fpMine.x10
 *  X10 Implementation of the Frequent Item Mining algorith
 *  @author Rajesh Bordawekar
 */

import x10.io.*;
import x10.io.Console;
import x10.util.GrowableRail;
import x10.util.HashMap;
import x10.util.ValHashMap;


class PlaceIdentifier{
 var identifier:Int;
 var bufferSize:Int;
 var tokens:Int;
    var iam:Int;
  
 var  gBuffer:GrowableRail[ValRail[Int]];

 public var map: HashMap[Int, Int] = new HashMap[Int, Int](); 
 var globalMap: ValHashMap[Int, Int];

 def this(val sz:int){
     identifier=0;
     gBuffer = new GrowableRail[ValRail[Int]](sz);
     bufferSize = 0;
     tokens = 0;
 } 

 public def insert(val record:ValRail[Int]){
    gBuffer.add(record);
    bufferSize++;
    tokens += record.length;
 }

 public def fetch(val pos:int): ValRail[Int]{
   return gBuffer.apply(pos);
 }

    public def setID(var id:int){
	iam = id;
    }

    public def getID(): Int{
	return iam;
    }

 public def view(val iam:Int){
    var i:Int = 0;

    for(i=0; i < bufferSize; i++){
      Console.OUT.println("Place: "+iam+" Record: "+i+": "+fetch(i));
   }

    Console.OUT.println("Place: "+iam+" #Transactions: "+ bufferSize+" #Items: "+ tokens);
 }

 public def getBufferSize(): Int{
   return bufferSize;
 }

 public def set(val x:int){
     identifier = x;
 }

 public def get(): Int{
   return identifier;
 }

 public global safe def getValMap(): ValHashMap[Int, Int]{

    val valMap:ValHashMap[Int,Int] = ValHashMap.make[Int, Int](map);

     /*
      var test:Int =0; 
   for(val entry in valMap.entries()){
    //    Console.OUT.println("The value for key: "+entry.getKey()+" is: "+entry.getValue());
        test += entry.getValue();
   }
   Console.OUT.println("getValMap(): The number of tokens: "+tokens+" entries in Hash Map: "+test); 
     */

   return valMap; 
 }
 
    public def setGlobalMap(valMap:ValHashMap[Int, Int]){
	globalMap = valMap;

	var total:Int=0, test:Int =0; 
   for(val entry in globalMap.entries()){
    //    Console.OUT.println("The value for key: "+entry.getKey()+" is: "+entry.getValue());
        test += entry.getValue();
	total++;
   }
   Console.OUT.println("getValMap(): The number of entries in Hash Map: "+total+ " with items: "+test); 
  
    }
  
 public def freq(){

   var i:Int=0, j:Int = 0;

   for(i=0; i < bufferSize; i++){
       val trans:ValRail[Int] = gBuffer(i);
       var size:Int = trans.length();
      
       // Hack to get the correct size  
       for(j=0; j < size; j++){
          var item:Int = trans(j);
          if (!map.containsKey(item)){
              map.put(item, 1);
          }
          else{
             var item_count:Int = map.get(item).value();
             map.put(item, item_count+1);
          }
       }
   }

   /*  var total:Int=0, test:Int =0; 
   for(val entry in map.entries()){
       Console.OUT.println("freq(): The value for key: "+entry.getKey()+" is: "+entry.getValue());
       test += entry.getValue();
       total++; 
   }
   Console.OUT.println("freq(): The number of tokens: "+tokens+" entries in Hash Map: "+total+ " number of occurances: "+ test); */

 }

}

public class fpMine{

    public static class HashMapReducer implements Reducible[ValHashMap[Int,Int]]{
	public global safe def zero() = {
	    val map: HashMap[Int, Int] = new HashMap[Int, Int]();
	    val vMap: ValHashMap[Int, Int] = ValHashMap.make[Int, Int](map);
	    
	    return vMap;
	}	

        public global safe def apply(a: ValHashMap[Int,Int], b:ValHashMap[Int,Int]) {
                val v = new HashMap[Int,Int]();
                for (entry in a.entries()) {
                        v.put(entry.getKey(), entry.getValue());
                }
                for (entry in b.entries()) {
                        val key = entry.getKey();
                        val v1 = v.get(key);
                        v.put(key, v1==null? entry.getValue() : entry.getValue() + v1());
                }
                return ValHashMap.make[Int, Int](v);
        }
    }


    public static def main(args:Rail[String]!):void{

    var fname:String = "input.dat";

    if (args.length == 1){
       fname = args(0);
    }

    Console.OUT.println("fpMine application is executing on "+ Place.MAX_PLACES+" places.");
    Console.OUT.println("fpMine application is reading from: "+ fname);

    // Create a closure to get unique id
    val placeId =()=> new PlaceIdentifier(8);
    val plh: PlaceLocalHandle[PlaceIdentifier] = PlaceLocalHandle.make[PlaceIdentifier] (Dist.makeUnique(), placeId);

    // Assign the appropriate IDs
    var i:Int=0;
    //var count:Int=0;

    for(i=0; i < Place.MAX_PLACES; i++){
	if (i != here.id){
	    val target = Point.make(i);
	    async(Dist.makeUnique().apply(target)){
		plh.apply().setID(here.id);
	    }
	}
    }

    var count:Int =0;

    try{
        val fr:ReaderIterator[String]! = new File(fname).lines() as ReaderIterator[String]!;
       
        finish for (val l:String in fr){
	  val l2 = l.trim();
          if (l2.equals("") || l2.equals("\n")) continue;
           val sl:ValRail[String] = l2.split(" ");
           val record:ValRail[Int] = ValRail.make[Int](sl.length, (i:Int)=>Int.parse(sl(i)));
        //   Console.OUT.println("Record size"+ sl.length);

           val offset = count % Place.MAX_PLACES;
       	       val currentPoint = Point.make(offset);
         
          async(Dist.makeUnique().apply(currentPoint)){
	      //	      Console.OUT.println("Hello from place "+here.id);
             plh.apply().insert(record);
          }
          count++;
       }


	
       ateach (p in Dist.makeUnique()){
	   plh.apply().freq();
       }

    }
    catch(e1:IOException){
      Console.ERR.println("Error in opening file: "+fname);
      e1.printStackTrace();
      at (Place.FIRST_PLACE) System.setExitCode(1);
      return;
    }

        val mergedHashMap = finish(new HashMapReducer()){
           ateach (p in Dist.makeUnique()){
            offer plh.apply().getValMap();
           }
       }; 


   
    ateach (p in Dist.makeUnique()){
	plh.apply().setGlobalMap(mergedHashMap);
    }


    var total:Int=0, occur: Int=0;
   for(entry in mergedHashMap.entries()){
      total += entry.getValue();
      occur++; 
      } 

   Console.OUT.println("Total number of unique items: "+occur+" with occurances: "+total);

    }

}

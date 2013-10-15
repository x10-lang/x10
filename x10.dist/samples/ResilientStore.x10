import x10.io.Console;
import x10.util.HashMap;
// import x10.util.*;

public class ResilientStore [T]{T isref} {
  data:T;
  backup:GlobalRef[T];

  static val MAX_DATA_SIZE = 100;
  public def this(init:T) {
    data = init;
    backup = at (here.next()) GlobalRef[T](init);
  } 
  
  def update (cl:(T)=>void) {
    cl(data);
    at (backup) { cl(backup()); }
  } 
  
  def applyPrimary[R] (cl:(T)=>R) {
    return cl(data);
  } 
  
  def applyBackup (cl:(T)=>void) {
    at (backup) { cl(backup()); };
  }

  def returnPrimary() {
    return data;
  } 
  
  def returnBackup() {
    return backup;
  } 
  
  public static def main(Rail[String]) {
    var hm : HashMap[String, Int];
    var data1 : Rail[int];
    var resStore:ResilientStore[Rail[int]];
    var resStore2:ResilientStore[HashMap[String, Int]];
    var arrayValue : int;
    var hashTableValue : int;

    Console.OUT.println("Program started"); 

    data1 = new Rail[int](MAX_DATA_SIZE, 12);
    resStore = new ResilientStore(data1);
    resStore.update((a:Rail[int])=>{a(1) = 200;});
    arrayValue = resStore.applyPrimary((a:Rail[int])=>a(0));
    Console.OUT.println("Value for primary element 0: " + arrayValue + ", place: " + here.id); 
    arrayValue = resStore.applyPrimary((a:Rail[int])=>a(1));
    Console.OUT.println("Value for primary element 1: " + arrayValue + ", place: " + here.id); 
    resStore.applyBackup((a:Rail[int])=>{Console.OUT.println("Value for backup array element 0: " + a(0) + ", place: " + here.id);});
    resStore.applyBackup((a:Rail[int])=>{Console.OUT.println("Value for backup array element 1: " + a(1) + ", place: " + here.id);});

    hm = new HashMap[String, Int] (); 
    hm.put("key1", 500);
    hashTableValue = hm.getOrElse("key1", 0);
    Console.OUT.println("Hash table value corresponding to key1: " + hashTableValue + ", place: " + here.id); 
    hashTableValue = hm.getOrElse("key2", 0);
    Console.OUT.println("Hash table value corresponding to key2: " + hashTableValue + ", place: " + here.id); 

    resStore2 = new ResilientStore(hm);
    resStore2.update((a:HashMap[String, Int])=>{a.put("key2", 600);});
    hashTableValue = resStore2.applyPrimary((a:HashMap[String, Int])=>a.getOrElse("key1", 0));
    Console.OUT.println("Value for primary key1: " + hashTableValue + ", place: " + here.id); 
    hashTableValue = resStore2.applyPrimary((a:HashMap[String, Int])=>a.getOrElse("key2", 0));
    Console.OUT.println("Value for primary key2: " + hashTableValue + ", place: " + here.id); 
    resStore2.applyBackup((a:HashMap[String, Int])=>{Console.OUT.println("Value for backup key 1: " + a.getOrElse("key1", 0) + ", place: " + here.id);});
    resStore2.applyBackup((a:HashMap[String, Int])=>{Console.OUT.println("Value for backup key 2: " + a.getOrElse("key2", 0) + ", place: " + here.id);});


  }
}
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/** Tests many syntactic features of dep types.
 *@author vj, 5/17/2006
 *
 */

import harness.x10Test;

public class DepType(i:int, j:int) extends x10Test { 
    
     //  property declaration for an inner class.
    class Test(k:int) extends DepType { 
        def this(kk:int):Test = {
            super(3,4);
            property(kk);
        }
    }
    class Test2 extends DepType {
        def this():Test2 = {
        super(3,5);
        }
    }
    
     //  thisClause on a class, and extension of a deptyped class
     class Test3  extends DepType { 
	 val k:int;
	 def this(v:int):Test3{self.j==k} { 
	     super(v,v);
	     k = v;
	 }
     }
     
    // A constructor may specify constraints on properties that are true of the returned object.
    public def this(i:int, j:int): DepType{self.i==i &&self.j==j} = {
        property(i,j);
    }
    
    //  method specifies a thisClause.
    public static def make(i: int{self==3}):DepType{self.i==3,self.j==3} = new DepType(i,i);
   
    // a local variable with a dep clause.
    public  def  run():boolean = {
	val d:DepType{i==3}  =  new DepType(3,6); 
	return true;
    }
	
    //  a method whose return type is a deptype
   public def run3():boolean{self==true} = { 
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public def run4( j:int):boolean = {
        System.out.println("i (=3?) = " + i);
        return true;
    }
    public static def main(args: Rail[String]):void = {
        new DepType(3,9).execute();
    }
   

		
}

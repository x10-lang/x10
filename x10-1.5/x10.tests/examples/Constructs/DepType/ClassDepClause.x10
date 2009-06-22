/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 
 /**  Check that a class can have a depclause, after the property list.
 *@author vj,10/30/2006
 *
 */

import harness.x10Test;

 public class ClassDepClause(i:int, j:int){i == j}  extends x10Test { 
  public static type ClassDepClause(i:int, j:int)=ClassDepClause{self.i==i,self.j==j};
  public def this(i: int, j: int){i==j}: ClassDepClause(i,j) = { property(i,j);}
  public def run(): boolean = { 
	  var x: ClassDepClause(2,2) =  new ClassDepClause(2,2);
      return true;
    }
    
   public static def main(var args: Rail[String]): void = {
        new ClassDepClause(1,1).execute();
    }
 }


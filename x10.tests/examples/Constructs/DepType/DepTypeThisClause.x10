/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
// This clauses are not currently implemented.

 /** Checks that an arg i from the param list of one method does not stray into 
 * body of another.
 *@author vj, 5/17/2006
 *
 */

import harness.x10Test;

public class DepTypeThisClause(i:int, j:int) extends x10Test { 
    def  this(ii:int, jj:int):DepTypeThisClause{i==ii,j==jj} = { property(ii,jj);}
    
    //  i is a param for this method and also a property
    def make(i:int(3)){this.i==3}:DepTypeThisClause = new DepTypeThisClause(i,i);

    //  a method whose return type is a deptype
   public def run(){this.i==3}:boolean(true) = { 
        System.out.println("i (=3?) = " + i); //property ref.
        return true;
    }
    
    public static  def main(Rail[String]) = {
        new DepTypeThisClause(3,9).execute();
    }
   
    }

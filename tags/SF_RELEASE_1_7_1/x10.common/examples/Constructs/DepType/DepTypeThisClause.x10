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

public class DepTypeThisClause(int i, int j) extends x10Test { 
    public DepTypeThisClause(:i==ii&&j=jj)(final int ii, final int jj) { property(ii,jj);}
    
    //  i is a param for this method and also a property
   this(:i==3) DepTypeThisClause  make(int(:self==3) i ) { 
       return new DepTypeThisClause(i,i);
       }
    //  a method whose return type is a deptype
    this(:i==3) public boolean(:self==true)  run() { 
        System.out.println("i (=3?) = " + i); //property ref.
        return true;
    }
    
    public static void main(String[] args) {
        new DepTypeThisClause(3,9).execute();
    }
   
    }

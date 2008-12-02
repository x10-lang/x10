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

 public class ClassDepClause(int i, int j : i == j )  extends x10Test { 
  public ClassDepClause(final int i, final int j) { property(i,j);}
  public boolean  run() { 
	  ClassDepClause(:i==2 && j==3) x = (ClassDepClause(:i==2 && j==3)) new ClassDepClause(2,3);
      return true;
    }
    
   public static void main(String[] args) {
        new ClassDepClause(1,1).execute();
    }
   
}
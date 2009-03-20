/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class NullableArray5 extends x10Test {        
       
    public boolean run() {
   
            nullable <double [.]> da=new double [[0:2,0:2]] (point [i,j]) {return i+j;};
            for (point p: da.region) System.out.println("da= "+da[p]); //this loop is fine
            /*the nested loop will generate wired compiler errors*/
            for (int i=0;i<3;i++){
                    for (int j=0;j<3;j++) System.out.print(" "+da[i,j]);
                    System.out.flush();System.out.print("\n");
            }
            return true;
    }
    public static void main(String[] a) {
    	new NullableArray5().execute();
    }
       
} 
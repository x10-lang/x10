/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;
import harness.x10Test;

/**
* Building arrays distributed accross places using the union-of-distribution approach.
* The performance of this kind of arrays is very poor at this moment.
* @author Tong
  11/29/2006
*/
public class BlockDistedArray2D extends x10Test {
	public final static int SIZE=5; 
	public final static int N_PLACES=place.MAX_PLACES; 
	public final static dist ALLPLACES=dist.factory.unique();
        public boolean run() {
        	dist(:rank==2) D =[0:SIZE-1,0:SIZE-1]->place.factory.place(0); //The dep type constraint should not be enforced here.
        	for(int i=1;i<N_PLACES;i++) D=D||([0:SIZE-1,i*SIZE:(i+1)*SIZE-1]->place.factory.place(i));
        	final int [.] intArray=new int [D] (point [i,j]) {return i+j;}; 
         	final double [.] dblArray=new double [D] (point [i,j]) {return (i+j)*0.1;};
        	finish ateach(point p[i]:ALLPLACES)
    			for (point [j,k]: intArray|here) 
    				dblArray[j,k]+=intArray[j,k]; 
    			
	    return true;
	}
	
	public static void main(String[] args) {
		new BlockDistedArray2D().execute();
	}

}


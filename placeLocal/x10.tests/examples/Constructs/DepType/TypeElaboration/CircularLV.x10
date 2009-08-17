/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 /**  
 Check that circular dependencies between classes (through local variables) are handled correctly
 during TypeElaboration.
 
 *@author vj
 *
 */

import harness.x10Test;

public class CircularLV(k: int ) extends x10Test {
    public def this(k: int): CircularLV = { this.k = k;}
    public def run(): boolean = { 
	val h: CLV1{i==j} = new CLV1(4,4) ;
	return true;
    }
   
    public static def main(var args: Rail[String]): void = {
	new CircularLV(4).execute();
    }
 }

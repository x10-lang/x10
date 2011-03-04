/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 *
 * @author vj
 */
public class EntailsPositive_MustFailCompile(i:int, j:int) extends x10Test {

	public def this(ii:int, jj:int):EntailsPositive_MustFailCompile = { property(ii,jj);}
	public def run():boolean = {
	    val x:EntailsPositive_MustFailCompile{self.i==1}  =  new EntailsPositive_MustFailCompile(1,2);
	    return true;
	}
	public static def main(Rail[String])= {
		new EntailsPositive_MustFailCompile(1,2).execute();
	}
}

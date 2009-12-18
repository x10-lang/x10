/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
public class DepTypeInMethodRet extends x10Test {

    public def m(t: Boolean(true))=t;
    public def run() =m(true);
    public static def main(Rail[String]) {
	new DepTypeInMethodRet().execute();
    }
}

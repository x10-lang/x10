/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


public class ConstDist extends x10Test {

    public def run(): boolean = {
    
        val R = [0..9, 0..9];
        val D = Dist.makeConstant(R, here);
        val a = Array.make[double](Dist.makeConstant(R, here));
        val b = Array.make[double](Dist.makeConstant(R, here));
        
        x10.io.Console.OUT.println("results are " + a + " " + b);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ConstDist().execute();
    }
}

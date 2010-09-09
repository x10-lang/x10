/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The partition method of region is called, which should never
 * be. Whether the run time error occurs or not depends on the number
 * of places used (for instance, 3)and the value of k.
 *
 * @author Tong @date 10/31/06
 */

public class RegionDifference extends x10Test {

    // need fix for XTENLANG-51, or change test

    public def run(): boolean = {
        val size: int = 10;
            val k: int = 5;
            val factor: int = 8;
            val r: Region{rank==2} = [k..size-1, k..k];
            //x10.io.Console.OUT.println(([k:k+factor-1,k:k]-r).toString());
            val d: Dist = Dist.makeCyclic([k..k+factor-1, k..k]-r, 0);
            
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new RegionDifference().execute();
    }
}

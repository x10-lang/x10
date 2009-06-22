****Error: examples/Constructs/Cast/PrimitiveDependentTypeCast/CastPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile.x10:20:35:20:35:0:-1:9: ";" inserted to complete scope
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * Issue: Value to cast does not meet constraint requirement of target type.
 * @author vcave
 **/
public class CastPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile extends x10Test {

	public def run(): boolean = {
		
		try { 
			var i: int{self == 1} = 1
			var j: int{self == 0} = i as int{self == 0};
		}catch(e: Throwable) {
			return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new CastPrimitiveConstrainedToPrimitiveConstrained_MustFailCompile().execute();
	}

}

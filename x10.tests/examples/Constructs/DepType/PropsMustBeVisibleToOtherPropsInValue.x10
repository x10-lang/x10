/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The test checks that property syntax is accepted for structs
 *
 * @author vj
 */
public class PropsMustBeVisibleToOtherPropsInValue extends x10Test {

    static struct Value2(i:int, j:int{self==i})  {
        public def this(k:int):Value2{self.i==k} = {
            property(k,k);
        }
	public def typeName() = "Value2";
    }
    public def run():boolean = {
        Value2(4);
        return true;
    }
    public static def main(var args: Rail[String]): void = {
        new PropsMustBeVisibleToOtherPropsInValue().execute();
    }
}

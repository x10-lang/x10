/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import x10.lang.Object;

public class List(n: int{self >=0}) {
  
    protected var value: Object;
    protected var tail: List;
  
    public def this(var o: Object, var t: List): List = {
        property(t.n+1 as int{self>=0});
        tail = t;
        value = o;
    }
    public def this(): List = {
        property(n as int{self>=0});
    }
     public def a(l: List): List = {
        return (n==0) ? l : new List(value, tail.append(l));
    }
   
    public def append(var l: List): List = {
        return n==0? (this as List{n==0}).a(l) : (this as List{n>0}).a(l);
    }
    
    public def nth(var k: int{self >= 1 && self <= n}): Object = {
        return k==1 ? value : tail.nth(k-1);
    }
  
    public def gen(val k: int{self >= 0}): List = {
        return k==0 ? new List() : new List(k, gen(k-1));
    }
}

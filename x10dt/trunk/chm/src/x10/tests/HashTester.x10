/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* A Test of ConcurrentX10HashMap functionality
 * 
 * HashTester tests the functionality of ConcurrentX10HashMap by
 * dealing with a relatively large set of value objects.
 * These objects are inserted into the distributed hash map and 
 * various tests are then conducted, with the output being sent to 
 * System.out. The distributions of the two ConcurrentX10HashMaps in
 * this program are distinct.
 * 
 * @author Shane Markstrum
 * @version 08/02/06
 */
 package x10.tests;
 
import x10.lang.Object;
import java.util.*;
import x10.concurrent.util.*;

public class HashTester extends x10.lang.Object {
	
	public static void main(String[] args){
		ConcurrentX10HashMap test = new ConcurrentX10HashMap();
		
		boolean emptyTest = test.isEmpty();
		System.out.println("Emptyness test "+(emptyTest?"succeeded.":"failed."));
		
		boolean sizeTest = test.size() == 0;
		System.out.println("Size test "+(sizeTest?"succeeded.":"failed."));
		
		final TestString value[.] s = new TestString value[dist.factory.arbitrary([1:2000])] (point[j]) { return new TestString(j); };
		final TestInteger value[.] i = new TestInteger value[dist.factory.arbitrary([1:2000])] (point[j]) { return new TestInteger(j); };
		
		System.out.print("Contains "+s[1].val);
		nullable<Object> res = 
			test.putIfAbsent(s[1],i[1]).force();
		System.out.print(" test ");
		boolean containsTest = test.containsValue(i[1]);
		System.out.println((containsTest?"succeeded.":"failed."));
		
		System.out.println("Replacing "+s[1].val+"? "+(test.replace(s[1], i[1], i[2]).force()?"Yes":"No"));
		containsTest = test.containsValue(i[1]);
		System.out.println("Contains "+s[1].val+" test 2 "+(!containsTest?"succeeded.":"failed."));
		TestInteger i2 = (TestInteger) test.get(s[1]).force();
		System.out.println("Key "+s[1].val+" maps to "+i2.val);
		
		test.clear();
		System.out.println("Empty again? "+(test.isEmpty()?"succeeded.":"failed."));

		ConcurrentX10HashMap test2 = new ConcurrentX10HashMap(dist.factory.block([1:16]));
		for (point[j] : s.distribution|[3:2000]) { nullable<Object> foo = test2.put(s[j],i[j]).force(); }
		System.out.println("New hash map size is "+test2.size());
		System.out.println("New hash map contains "+s[5].val+"? "+(test2.containsKey(s[5])?"Yes":"No"));
		
		System.out.println("Adding all of new hash map to old hash map.");
		test.putAll(test2);
		System.out.println("Old hash map size is "+test.size());
		
		
		return;
	}
}
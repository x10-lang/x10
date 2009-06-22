/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/* A Test of ConcurrentX10HashMap functionality
 * 
 * HashTester2 tests the functionality of ConcurrentX10HashMap by
 * dealing with a relatively large set of distributed objects.
 * These objects are inserted into the hash map and various tests
 * are then conducted with the output being sent to System.out.
 * There are no two arrays guaranteed to have the same distribution
 * in this program.
 * 
 * @author Shane Markstrum
 * @version 08/02/06
 */
 package x10.tests;
 
import x10.lang.Object;
import java.util.*;
import x10.concurrent.util.*;

public class HashTester2 extends x10.lang.Object {
	
	public static void main(String[] args){
		ConcurrentX10HashMap test = new ConcurrentX10HashMap();
		
		boolean emptyTest = test.isEmpty();
		System.out.println("Emptyness test "+(emptyTest?"succeeded.":"failed."));
		
		boolean sizeTest = test.size() == 0;
		System.out.println("Size test "+(sizeTest?"succeeded.":"failed."));
		
		final TestString[.] s = new TestString[dist.factory.arbitrary([1:2000])] (point[j]) { return new TestString(j); };
		final TestInteger[.] i = new TestInteger[dist.factory.arbitrary([1:2000])] (point[j]) { return new TestInteger(j); };
		
		System.out.print("Contains "+future(s.distribution[1]){s[1]}.force().val);
		nullable<Object> res = 
			test.putIfAbsent(future(s.distribution[1]){s[1]}.force(),future(i.distribution[1]){i[1]}.force()).force();
		System.out.print(" test ");
		boolean containsTest = test.containsValue(future(i.distribution[1]){i[1]}.force());
		System.out.println((containsTest?"succeeded.":"failed."));
		
		System.out.println("Replacing "+future(s.distribution[1]){s[1]}.force().val+"? "+(test.replace(future(s.distribution[1]){s[1]}.force(), future(i.distribution[1]){i[1]}.force(), future(i.distribution[2]){i[2]}.force()).force()?"Yes":"No"));
		containsTest = test.containsValue(future(i.distribution[1]){i[1]}.force());
		System.out.println("Contains "+future(s.distribution[1]){s[1]}.force().val+" test 2 "+(!containsTest?"succeeded.":"failed."));
		final TestInteger i2 = (TestInteger) test.get(future(s.distribution[1]){s[1]}.force()).force();
		System.out.println("Key "+future(s.distribution[1]){s[1]}.force().val+" maps to "+future(i2){i2}.force().val);
		
		test.clear();
		System.out.println("Empty again? "+(test.isEmpty()?"succeeded.":"failed."));

		final ConcurrentX10HashMap test2 = new ConcurrentX10HashMap(dist.factory.block([1:16]));
		for (point[j] : s.distribution|[3:2000]) { nullable<Object> foo = test2.put(future(s.distribution[j]){s[j]}.force(),future(i.distribution[j]){i[j]}.force()).force(); }
		System.out.println("New hash map size is "+test2.size());
		System.out.println("New hash map contains "+future(s.distribution[5]){s[5]}.force().val+"? "+(test2.containsKey(future(s.distribution[5]){s[5]}.force())?"Yes":"No"));
		
		System.out.println("Adding all of new hash map to old hash map.");
		test.putAll(test2);
		System.out.println("Old hash map size is "+test.size());
		
		
		return;
	}
}

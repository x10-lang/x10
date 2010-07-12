package x10.finish.util;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import x10.finish.table.CallTableAsyncVal;
import x10.finish.table.CallTableAtVal;
import x10.finish.table.CallTableScopeKey;
import x10.finish.table.CallTableKey;
import x10.finish.table.CallTableMethodKey;
import x10.finish.table.CallTableVal;
import x10.finish.table.CallTableVal.Arity;

public class CallTableUtil {

    public static void getStat(
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	System.out.println("#number of callees:" + getCalleeNum(calltable));
    }

    public static int getCalleeNum(
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	int cnt = 0;
	Set<CallTableKey> kset = calltable.keySet();
	Iterator<CallTableKey> kit = kset.iterator();
	while (kit.hasNext()) {
	    CallTableKey k = kit.next();
	    LinkedList<CallTableVal> v = calltable.get(k);
	    cnt = cnt + v.size();
	}
	return cnt;
    }

    /*
     * For test purpose, to dump all contents in the call table in a readable
     * format
     */
    public static void dumpCallTable(
	    HashMap<CallTableKey, LinkedList<CallTableVal>> ct) {
	Iterator<CallTableKey> ikey = ct.keySet().iterator();
	while (ikey.hasNext()) {
	    CallTableKey key = ikey.next();
	    LinkedList<CallTableVal> vals = ct.get(key);
	    System.out.println(key.toString() + ": ");
	    for (int i = 0; i < vals.size(); i++) {
		CallTableVal v = vals.get(i);
		System.out.println("\t" + v.toString());
	    }
	    System.out.print("\n");
	}
    }

    /**
     * 
     * @param k
     *            - entry to the current method in the calltable
     * @param cfg
     *            - current method's control flow graph
     */
    private static void updateArity(CallTableKey k,
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	LinkedList<CallTableVal> callees = calltable.get(k);
	LinkedList<CallTableVal> new_callees = new LinkedList<CallTableVal>();
	boolean rec_call = false;
	String key_sig = k.scope;

	// get block numbers of all recursive calls
	for (int i = 0; i < callees.size(); i++) {
	    CallTableVal v = callees.get(i);
	    // CallTableAtVal always has the same scope as its enclosing
	    // methods,
	    // but it is just skipped because it never causes a recursion
	    if (v instanceof CallTableAsyncVal) {
		String val_sig = v.scope;
		if (val_sig.endsWith(key_sig)) {
		    rec_call = true;
		}
	    }
	}
	if (rec_call) {
	    // check each method invocation if it is in a loop caused by
	    // recursion
	    for (int i = 0; i < callees.size(); i++) {
		CallTableVal v = callees.get(i);
		v.setArity(CallTableVal.Arity.Unbounded);
		new_callees.add(v);
	    }
	    // replace the old callees list with the new one
	    calltable.remove(k);
	    calltable.put(k, new_callees);
	}

    }

    @SuppressWarnings("unchecked")
    public static void updateAllArity(
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	HashMap<CallTableKey, LinkedList<CallTableVal>> new_table = (HashMap<CallTableKey, LinkedList<CallTableVal>>) OutputUtil
		.copy(calltable);
	Iterator<CallTableKey> it = new_table.keySet().iterator();
	while (it.hasNext()) {
	    updateArity(it.next(), calltable);
	}
    }

    /**
     * the "calltable" records each method's history of method invocation.
     * Because method invocation is not expanded in the control flow graph we
     * use and a program might have recursive calls, we need to "saturate" this
     * table to get a complete list of method invocation.
     */
    @SuppressWarnings("unchecked")
    public static HashMap<CallTableKey, LinkedList<CallTableVal>> expandCallTable(
	    HashMap<CallTableKey, LinkedList<CallTableVal>> calltable) {
	boolean changed = true;
	Set<CallTableKey> keyset;
	Iterator<CallTableKey> keyit;
	HashMap<CallTableKey, LinkedList<CallTableVal>> new_table;

	while (changed) {
	    // System.err.println(".........");
	    // this.dumpCallTable();
	    keyset = calltable.keySet();
	    keyit = keyset.iterator();
	    new_table = new HashMap<CallTableKey, LinkedList<CallTableVal>>();
	    changed = false;
	    // do it for each row of the table
	    while (keyit.hasNext()) {

		CallTableKey key = keyit.next();
		// System.err.println("key:"+key.toString());
		LinkedList<CallTableVal> vals = calltable.get(key);
		LinkedList<CallTableVal> new_vals = (LinkedList<CallTableVal>) OutputUtil
			.copy(vals);

		// check each object in vals
		for (int i = 0; i < vals.size(); i++) {
		    CallTableVal callee = vals.get(i);
		    CallTableVal.Arity tmparity = callee.getArity();
		    Iterator<CallTableVal> tmpiter = null;
		    /* when the "at" or "method" this callee represents 
		     * is also a "key" in this table, get this CallTableKey object
		     */
		    CallTableKey tmpkey = getKey(callee);
		    LinkedList<CallTableVal> tmplist = calltable.get(tmpkey);
		    if (tmplist != null) {
			tmpiter = tmplist.iterator();
			/* add each CallTableVal in this newly obtained 
			 * CallTableKey to the original key's list
			 */
			while (tmpiter.hasNext()) {
			    CallTableVal tmpcallee = tmpiter.next();
			    /* we need a copy of tmpcallee here, because when 
			     * we add this callee to a new caller's list, and
			     * if we change this callee's arity, we don't 
			     * want the original callee which is in another 
			     * caller's list is changed too
			     */
			    CallTableVal copiedcallee = 
				(CallTableVal) OutputUtil.copy(tmpcallee);
			    
			    if (!new_vals.contains(copiedcallee)) {
				copiedcallee.setArity(tmparity);
				copiedcallee.blk = callee.blk;
				new_vals.add(copiedcallee);
				changed = true;
			    } else {
				int index = new_vals.indexOf(copiedcallee);
				/* tv has the same signature, but possibly
				 * different arities as tmpcallee
				 */
				CallTableVal tv = new_vals.get(index);
				if (tmpcallee.a.compareTo(tv.a) > 0) {
				    tv.setArity(tmpcallee.a);
				    new_vals.remove(index);
				    new_vals.add(tv);
				    changed = true;
				}
			    }
			}
		    }
		}// end of for vals
		new_table.put(key, new_vals);
	    }
	    calltable = new_table;
	    // System.out.println(">>>>>>>>>>>>>>>>>>>>");
	    // dumpCallTable();
	}// end of while(!terminate)

	return calltable;
    }

    private static CallTableKey getKey(CallTableVal callee) {
	CallTableKey tmpkey;
	if (callee instanceof CallTableAtVal) {
	    tmpkey = new CallTableScopeKey(callee.scope, callee.name,
		    ((CallTableAtVal) callee).line,
		    ((CallTableAtVal) callee).column,
		    ((CallTableAtVal) callee).blk, false);
	} else {
	    tmpkey = new CallTableMethodKey(callee.scope, callee.name,
		    callee.line, callee.column);
	}
	return tmpkey;
    }
}

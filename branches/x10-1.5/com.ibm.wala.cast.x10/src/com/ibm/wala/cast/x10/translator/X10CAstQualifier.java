/*
 * Created on Oct 7, 2005
 */
package com.ibm.wala.cast.x10.translator;

import com.ibm.wala.cast.tree.CAstQualifier;

public class X10CAstQualifier extends CAstQualifier {

    protected X10CAstQualifier(String name) {
	super(name);
    }

    public static final CAstQualifier ATOMIC= new CAstQualifier("atomic");
    public static final CAstQualifier VALUE= new CAstQualifier("value");
    public static final CAstQualifier NULLABLE= new CAstQualifier("nullable");
    public static final CAstQualifier REFERENCE= new CAstQualifier("reference");

    static {
	CAstQualifier.sQualifiers.add(ATOMIC);
	CAstQualifier.sQualifiers.add(VALUE);
	CAstQualifier.sQualifiers.add(NULLABLE);
	CAstQualifier.sQualifiers.add(REFERENCE);
    }
}

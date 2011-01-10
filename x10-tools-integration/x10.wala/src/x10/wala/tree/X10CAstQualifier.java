/*
 * Created on Oct 7, 2005
 */
package x10.wala.tree;

import com.ibm.wala.cast.tree.CAstQualifier;

public class X10CAstQualifier extends CAstQualifier {

    protected X10CAstQualifier(String name) {
        super(name);
    }

    public static final CAstQualifier ATOMIC = new CAstQualifier("atomic");

    static {
        CAstQualifier.sQualifiers.add(ATOMIC);
    }
}

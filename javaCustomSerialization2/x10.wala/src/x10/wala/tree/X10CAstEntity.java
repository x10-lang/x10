/*
 * Created on Oct 19, 2005
 */
package x10.wala.tree;

import com.ibm.wala.cast.tree.CAstEntity;

public interface X10CAstEntity extends CAstEntity {
    /**
     * Kind constant for a CAstEntity representing the body of an X10 async or closure.
     */
    public static final int ASYNC_BODY = SUB_LANGUAGE_BASE;
    public static final int CLOSURE_BODY = SUB_LANGUAGE_BASE + 1;
}

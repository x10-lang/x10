/*
 * Created on Oct 6, 2005
 */
package x10.wala.tree;

import com.ibm.wala.cast.tree.CAstNode;

public interface X10CastNode extends CAstNode {
    /**
     * Kind constant for a CAstNode representing the invocation of an X10 'async' body.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR of type 'Place' indicating where the computation is to occur
     * <li>CAstNode.Constant containing the CAstEntity representing the body
     * </ol>
     */
    public static final int ASYNC = SUB_LANGUAGE_BASE;

    /**
     * Kind constant for a CAstNode representing the beginning of an X10 'atomic' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.BLOCK representing the body of the 'atomic'
     * </ol>
     */
    public static final int ATOMIC_ENTER = SUB_LANGUAGE_BASE + 2;

    /**
     * Kind constant for a CAstNode representing the end of an X10 'atomic' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.BLOCK representing the body of the 'atomic'
     * </ol>
     */
    public static final int ATOMIC_EXIT = SUB_LANGUAGE_BASE + 3;

    /**
     * Kind constant for a CAstNode representing the start of an X10 'finish' statement.<br>
     * Children: none<br>
     * This is typically enclosed within the block that is the first child of an UNWIND node.
     */
    public static final int FINISH_ENTER = SUB_LANGUAGE_BASE + 6;

    /**
     * Kind constant for a CAstNode representing the end of an X10 'finish' statement.<br>
     * Children: none
     * This is typically the last child of an UNWIND node.
     */
    public static final int FINISH_EXIT = SUB_LANGUAGE_BASE + 7;

    /**
     * Kind constant for a CAstNode representing an X10 'here' expression.<br>
     * Children: &lt;none&gt;
     */
    public static final int HERE = SUB_LANGUAGE_BASE + 9;

    /**
     * Kind constant for a CAstNode representing an X10 'next' statement.<br>
     * Children: &lt;none&gt;
     */
    public static final int NEXT = SUB_LANGUAGE_BASE + 10;

    /**
     * Kind constant for a CAstNode representing an X10 region iterator initialization.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region being iterated over)
     * </ol>
     */
    public static final int ITER_INIT = SUB_LANGUAGE_BASE + 17;

    /**
     * Kind constant for a CAstNode representing a test for whether an X10 region iteration
     * has a next point.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region iterator)
     * </ol>
     */
    public static final int ITER_HASNEXT = SUB_LANGUAGE_BASE + 18;

    /**
     * Kind constant for a CAstNode representing the accessor for the next element of an
     * X10 region iteration.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region iterator)
     * </ol>
     */
    public static final int ITER_NEXT = SUB_LANGUAGE_BASE + 19;

    /**
     * Kind constant for a CAstNode representing an array reference where the index is given as an
     * instance of x10.lang.point. This has exactly the same structure as an ARRAY_REF.<br>
     * Children:
     * <ol>
     *   <li>CAstNode.EXPR representing the array
     *   <li>CAstValue representing the type of the result
     *   <li>CAstNode.EXPR representing the point index
     * </ol>
     */
    public static final int ARRAY_REF_BY_POINT = SUB_LANGUAGE_BASE + 22;

    /**
     * Kind constant for a CAstNode representing a tuple expression.<br>
     * Children:
     * <ol>
     *   <li>CAstNode.CONSTANT representing the type of the result
     *   <li>CAstNode.EXPR representing each child expression
     *   <li>...
     * </ol>
     */
    public static final int TUPLE = SUB_LANGUAGE_BASE + 23;

    /**
     * Kind constant for a CAstNode representing the beginning of an "at (P) {S}" statement.
     * Children:
     * <ol>
     *   <li>CAstNode.BLOCK_STMT</li>
     * </ol>
     */
    public static final int AT_STMT_ENTER = SUB_LANGUAGE_BASE + 24;

    /**
     * Kind constant for a CAstNode representing the end of an "at (P) {S}" statement.
     * Children:
     * <ol>
     *   <li>CAstNode.BLOCK_STMT</li>
     * </ol>
     */
    public static final int AT_STMT_EXIT = SUB_LANGUAGE_BASE + 25;
}

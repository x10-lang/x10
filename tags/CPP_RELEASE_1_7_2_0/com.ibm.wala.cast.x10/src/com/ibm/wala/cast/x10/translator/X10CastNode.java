/*
 * Created on Oct 6, 2005
 */
package com.ibm.wala.cast.x10.translator;

import com.ibm.wala.cast.tree.CAstNode;

public interface X10CastNode extends CAstNode {
    /**
     * Kind constant for a CAstNode representing the invocation of an X10 'async'
     * body or 'future' body. In the former case, the body happens to be a statement,
     * while in the latter, the body is always an expression.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR of type 'Place' indicating where the computation is to occur
     * <li>CAstNode.Constant containing the CAstEntity representing the body
     * </ol>
     * N.B. To obtain the returned type for a 'future', ask the CAstEntity for its type,
     * which should be a CAstType.Function, and examine its return type.
     */
    public static final int ASYNC_INVOKE = SUB_LANGUAGE_BASE;

    /**
     * Kind constant for a CAstNode representing an X10 'ateach' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.CONSTANT whose value is a <em>BINDING STRUCTURE</em> for the induction variables
     * <li>CAstNode.EXPR of type 'Distribution' representing the domain of the induction
     * <li>CAstNode.BLOCK representing the body
     * </ol>
     */
    public static final int ATEACH = SUB_LANGUAGE_BASE + 1;

    /**
     * Kind constant for a CAstNode representing the beginning of an X10 'atomic' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.BLOCK representing the body of the 'atomic'
     * </ol>
     */
    public static final int ATOMIC_ENTER= SUB_LANGUAGE_BASE + 2;

    /**
     * Kind constant for a CAstNode representing the end of an X10 'atomic' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.BLOCK representing the body of the 'atomic'
     * </ol>
     */
    public static final int ATOMIC_EXIT= SUB_LANGUAGE_BASE + 3;

    /**
     * Kind constant for a CAstNode representing an X10 'await' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR of type boolean representing the awaited condition
     * </ol>
     */
    public static final int AWAIT = SUB_LANGUAGE_BASE + 4;

    /**
     * Kind constant for a CAstNode representing an X10 'clocked' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.BLOCK representing the body of the 
     * <li>1 or more CAstNode.EXPR's of type 'clock'...
     * </ol>
     */
    public static final int CLOCKED = SUB_LANGUAGE_BASE + 5;

    /**
     * Kind constant for a CAstNode representing the start of an X10 'finish' statement.<br>
     * Children: none<br>
     * This is typically enclosed within the block that is the first child of an UNWIND node.
     */
    public static final int FINISH_ENTER= SUB_LANGUAGE_BASE + 6;

    /**
     * Kind constant for a CAstNode representing the end of an X10 'finish' statement.<br>
     * Children: none
     * This is typically the last child of an UNWIND node.
     */
    public static final int FINISH_EXIT= SUB_LANGUAGE_BASE + 7;

    /**
     * Kind constant for a CAstNode representing an X10 'foreach' statement.<br>
     * Children:
     * <ol>
     * <li>CAstNode.CONSTANT whose value is a <em>BINDING STRUCTURE</em> for the induction variables
     * <li>CAstNode.EXPR of type 'Region' representing the domain of the induction
     * <li>CAstNode.BLOCK representing the body
     * </ol>
     */
    public static final int FOREACH = SUB_LANGUAGE_BASE + 8;

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
     * Kind constant for a CAstNode representing an X10 'place-cast' expression.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the value to be cast
     * <li>CAstNode.CONSTANT whose value is a TypeReference for the cast-to type (<em>REALLY?</em>)
     * </ol>
     */
    public static final int PLACE_CAST = SUB_LANGUAGE_BASE + 11;

    /**
     * Kind constant for a CAstNode representing an X10 'point' expression.<br>
     * Children: sequence of CAstNode.EXPR, each of type int.
     */
    public static final int POINT = SUB_LANGUAGE_BASE + 12;

    /**
     * Kind constant for a CAstNode representing an X10 'region' expression.<br>
     * Children: sequence of CAstNode.EXPR, each of type 'Range'
     */
    public static final int REGION = SUB_LANGUAGE_BASE + 13;

    /**
     * Kind constant for a CAstNode representing an X10 'range' expression.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR of type int, representing the lower bound
     * <li>CAstNode.EXPR of type int, representing the upper bound
     * <li>CAstNode.EXPR of type int, representing the stride
     * </ol>
     */
    public static final int RANGE = SUB_LANGUAGE_BASE + 14;

    /**
     * Kind constant for a CAstNode representing an X10 'when' statement.<br>
     * Children: an even number of pairs of the following type
     * <ol>
     * <li>CAstNode.EXPR of type boolean, representing the condition
     * <li>Some kind of CAstNode representing the body associated with that condition
     * (e.g. CAstNode.BLOCK or CAstNode.STMT)
     * </ol>
     */
//  12/6/2005 - currently unused; see X10toCAstTranslator.visit(When).
//  public static final int WHEN = SUB_LANGUAGE_BASE + 15;

    /**
     * Kind constant for a CAstNode representing an X10 'force' expression.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the future being forced)
     * </ol>
     */
    public static final int FORCE= SUB_LANGUAGE_BASE + 16;

    /**
     * Kind constant for a CAstNode representing an X10 region iterator initialization.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region being iterated over)
     * </ol>
     */
    public static final int REGION_ITER_INIT= SUB_LANGUAGE_BASE + 17;

    /**
     * Kind constant for a CAstNode representing a test for whether an X10 region iteration
     * has a next point.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region iterator)
     * </ol>
     */
    public static final int REGION_ITER_HASNEXT= SUB_LANGUAGE_BASE + 18;

    /**
     * Kind constant for a CAstNode representing the accessor for the next element of an
     * X10 region iteration.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver (the region iterator)
     * </ol>
     */
    public static final int REGION_ITER_NEXT= SUB_LANGUAGE_BASE + 19;

    /**
     * Kind constant for a CAstNode representing the accessor for the distribution of an
     * X10 array-typed expression.<br>
     * Children:
     * <ol>
     * <li>CAstNode.EXPR representing the receiver array
     * </ol>
     */
    public static final int ARRAY_DISTRIBUTION= SUB_LANGUAGE_BASE + 20;

    /**
     * Kind constant for a CAstNode representing the accessor for the place at which a given point
     * resides in a given distribution.<br>
     * Children:
     * <ol>
     *   <li>CAstNode.EXPR representing the array
     *   <li>CAstNode.EXPR representing the point
     * </ol>
     */
    public static final int PLACE_OF_POINT= SUB_LANGUAGE_BASE + 21;

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
    public static final int ARRAY_REF_BY_POINT= SUB_LANGUAGE_BASE + 22;
}

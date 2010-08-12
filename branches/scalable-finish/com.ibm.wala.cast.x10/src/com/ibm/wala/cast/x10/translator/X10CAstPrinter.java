/*
 * Created on Oct 21, 2005
 */
package com.ibm.wala.cast.x10.translator;

import java.io.Writer;

import com.ibm.wala.cast.tree.CAstEntity;
import com.ibm.wala.cast.tree.CAstNode;
import com.ibm.wala.cast.util.CAstPrinter;

public class X10CAstPrinter extends CAstPrinter {
    static {
	CAstPrinter.setPrinter(new X10CAstPrinter());
    }

    public String getKindAsString(int kind) {
	switch(kind) {
	    case X10CastNode.ARRAY_REF_BY_POINT: return "ARRAY_REF_BY_POINT";
	    case X10CastNode.ASYNC_INVOKE: return "ASYNC_INVOKE";
	    case X10CastNode.ATEACH: return "ATEACH";
	    case X10CastNode.ATOMIC_ENTER: return "ATOMIC_ENTER";
	    case X10CastNode.ATOMIC_EXIT: return "ATOMIC_EXIT";
	    case X10CastNode.AWAIT: return "AWAIT";
	    case X10CastNode.CLOCKED: return "CLOCKED";
	    case X10CastNode.FINISH_ENTER: return "FINISH_ENTER";
	    case X10CastNode.FINISH_EXIT: return "FINISH_EXIT";
	    case X10CastNode.FOREACH: return "FOREACH";
	    case X10CastNode.FORCE: return "FORCE";
	    case X10CastNode.HERE: return "HERE";
	    case X10CastNode.NEXT: return "NEXT";
	    case X10CastNode.PLACE_CAST: return "PLACE_CAST";
	    case X10CastNode.POINT: return "POINT";
	    case X10CastNode.RANGE: return "RANGE";
	    case X10CastNode.REGION: return "REGION";
	    case X10CastNode.REGION_ITER_INIT: return "REGION_INIT";
	    case X10CastNode.REGION_ITER_HASNEXT: return "REGION_HASNEXT";
	    case X10CastNode.REGION_ITER_NEXT: return "REGION_NEXT";
	    case X10CastNode.PLACE_OF_POINT: return "PLACE_OF_POINT";
	    case X10CastNode.ARRAY_DISTRIBUTION: return "ARRAY_DISTRIBUTION";
	    case X10CastNode.TUPLE_EXPR: return "TUPLE";
	    case X10CastNode.AT_STMT_ENTER: return "AT_STMT_ENTER";
        case X10CastNode.AT_STMT_EXIT: return "AT_STMT_EXIT";
	}
        return super.getKindAsString(kind);
    }

    public String getEntityKindAsString(int kind) {
	switch(kind) {
	    case X10CAstEntity.ASYNC_BODY: return "async_body";
	    case X10CAstEntity.CLOSURE_BODY: return "closure_body";
	    default: return super.getEntityKindAsString(kind);
	}
    }

    public static String print(CAstNode n) {
	return CAstPrinter.print(n);
    }

    public static void printTo(CAstEntity e, Writer w) {
	CAstPrinter.printTo(e, w);
    }
}

/*
 * Created on Oct 21, 2005
 */
package x10.wala.util;

import java.io.Writer;

import x10.wala.tree.X10CAstEntity;
import x10.wala.tree.X10CastNode;

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
	    case X10CastNode.ASYNC: return "ASYNC";
	    case X10CastNode.ATOMIC_ENTER: return "ATOMIC_ENTER";
	    case X10CastNode.ATOMIC_EXIT: return "ATOMIC_EXIT";
	    case X10CastNode.FINISH_ENTER: return "FINISH_ENTER";
	    case X10CastNode.FINISH_EXIT: return "FINISH_EXIT";
	    case X10CastNode.HERE: return "HERE";
	    case X10CastNode.NEXT: return "NEXT";
	    case X10CastNode.ITER_INIT: return "ITER_INIT";
	    case X10CastNode.ITER_HASNEXT: return "ITER_HASNEXT";
	    case X10CastNode.ITER_NEXT: return "ITER_NEXT";
	    case X10CastNode.TUPLE: return "TUPLE";
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

/*
 * Created on Oct 21, 2005
 */
package com.ibm.domo.ast.x10.translator;

import java.io.Writer;

import com.ibm.capa.ast.CAstEntity;
import com.ibm.capa.ast.CAstNode;
import com.ibm.capa.ast.impl.CAstPrinter;

public class X10CAstPrinter extends CAstPrinter {
    static {
	CAstPrinter.setPrinter(new X10CAstPrinter());
    }

    public String getKindAsString(int kind) {
	switch(kind) {
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
	    case X10CastNode.WHEN: return "WHEN";
	}
        return super.getKindAsString(kind);
    }

    public String getEntityKindAsString(int kind) {
	switch(kind) {
	    case X10CAstEntity.ASYNC_BODY: return "async_body";
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

package org.eclipse.imp.x10dt.ui.parser;

import lpg.runtime.Monitor;
import lpg.runtime.PrsStream;

import org.eclipse.imp.parser.IParser;

import x10.parser.X10Parser;

public class ParserDelegate implements IParser {
    X10Parser myParser;

    public PrsStream getParseStream() {
    	// SMS 25 Jun 200y:
    	//This doesn't work; can still get NPE due to
    	// interference of concurrent threads	
//      assert(myParser != null);
//		return myParser.getParseStream();
    	// Replacement text:
   		try {
   			assert(myParser != null);
			return myParser.getParseStream();
		} catch (NullPointerException e) {
			System.err.println("ParserDelegate.getParseStream(..):  NullPointerException; discarding, returning null");
			throw e;
		}
    }

    public int getEOFTokenKind() {
	return myParser.getEOFTokenKind();
    }

    public Object parser(Monitor monitor, int error_repair_count) {
	return myParser.parser(monitor);
    }

    String[] orderedTerminalSymbols() {
	return myParser.orderedTerminalSymbols();
    }

    ParserDelegate(X10Parser myParser) {
        this.myParser = myParser;
    }
}
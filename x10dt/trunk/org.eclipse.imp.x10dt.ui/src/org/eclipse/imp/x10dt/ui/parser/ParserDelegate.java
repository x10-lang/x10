package x10.uide.parser;

import java.io.File;
import java.io.IOException;

import org.eclipse.uide.parser.IParser;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.util.StdErrorQueue;
import x10.parser.X10Parser;

import com.ibm.lpg.LexStream;
import com.ibm.lpg.Monitor;
import com.ibm.lpg.PrsStream;

public class ParserDelegate implements IParser
{
	X10Parser myParser;

	public PrsStream getParseStream() { return myParser.getParseStream(); }
    public int getEOFTokenKind() { return myParser.getEOFTokenKind(); }
    public Object parser(int error_repair_count, Monitor monitor) {
        return myParser.parser(monitor);
    }

    String[] orderedTerminalSymbols() { return myParser.orderedTerminalSymbols(); }
    
    ParserDelegate(LexStream lexStream) {
    	ExtensionInfo extInfo= new ExtensionInfo();
    	StdErrorQueue eq = new StdErrorQueue(System.err, 100000, "stderr");// TODO replace me
		Compiler compiler= new Compiler(extInfo, eq);
		extInfo.initCompiler(compiler);
		try {
	    	FileSource fileSource= new FileSource(new File("c:/"));

	    	myParser = new X10Parser(lexStream, extInfo.typeSystem(), extInfo.nodeFactory(), fileSource, extInfo.compiler().errorQueue());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
   	}
}
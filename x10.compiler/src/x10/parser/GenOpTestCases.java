/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.parser;

import java.util.ArrayList;
import java.io.File;

public class GenOpTestCases {        
    public static void testCoercions() {
        for (int i=0; i<6; i++)
            for (int j=0; j<6; j++)
                if (i!=j)
                    System.err.println("w"+i+" = w"+j+";");
    }

    public static void main(String[] args) {           
        if (false) {
            testCoercions();
            return;
        }
        // Part 1: Output an X10 class
        // Part 2: runs AddErrMarkers on it
        if (args.length==0) {
            System.err.println("You need to run GenOpTestCases like the x10 compiler, with at least one argument: OUTPUT_FILE\nFor example: java GenOpTestCases Output.x10\n");
            System.exit(-1);
        }
        new GenOpTestCases(args).run();
    }
    private final String[] args;
    private final File output;
    GenOpTestCases(String[] args) {
        this.args = args;
        this.output = new File(args[0]);
    }
    void run() {
        writeTest();
        // part 2
        AddErrMarkers.main(args);
    }
    void writeTest() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("class TestInstanceOperators {");
        int c = 0;
        ArrayList<String> types = new ArrayList<String>();
        ArrayList<String> terms = new ArrayList<String>();
        for (String t : typesAndInit) {
            String[] typeAndInit = t.split("=");
            assert typeAndInit.length==2 : t;
            types.add(typeAndInit[0]);
            String varName = "w"+c;
            lines.add("\tvar "+varName+":"+t+";");
            terms.add(varName);
            c++;
        }
        lines.add("\n");

        for (String numericLit : numericLiterals) {
            terms.add(numericLit);
            terms.add("(-"+numericLit+")");
        }
        for (String otherLit : otherLiterals) {
            terms.add(otherLit);
        }

        int i=1;
        (i)++;
        ArrayList<String> exprs = new ArrayList<String>();
            for (String op : unaryPostfixOps)
                for (String arg : terms)
                    exprs.add(arg+op);
            for (String op : unaryPrefixOps)
                for (String arg : terms)
                    exprs.add(op+arg);
            for (String op : binaryOps)
                for (String arg1 : terms)
                    for (String arg2 : terms)
                        exprs.add(arg1+op+arg2);

        for (String res : types)
            for (String expr : exprs)
                lines.add("\tdef m"+(c++)+"():void { var l:"+res+" = "+expr+"; }");        

        lines.add("}");
        AutoGenSentences.writeFile(output,lines);
    }

    // todo: do a java subset and a C++ subset (so we can see what is different between them and X10)
    //,String=null,Char='a',Point=null,Region=null
    // ,Any=null,Object=null,Place=here,Array[Int]=null,Array[Double]=null
    //Float=0,Double=0,Byte=0,Int=0,Long=0,Short=0,UByte=0,UInt=0,ULong=0,UShort=0
    //Double=0,Long=0,String=null,Char='a',Point=null,Region=null
    String[] typesAndInit = "Float=0,Double=0,Byte=0,Int=0,Long=0,Short=0,UByte=0,UInt=0,ULong=0,UShort=0,Point=null".split(","); // todo later add: Arithmetic[.], Bitwise[.], Ordered[.]

    //,2147483648,4294967295,4294967296,9007199254740992L,9007199254740993L
    String[] numericLiterals = "0,1.1f,2.2,127,128,255,256,32767,32768,65535,65536,16777216,16777217,2147483647".split(","); // see numericConversionValid, also a "-" version for each literal
    
    String[] otherLiterals = new String[0];//"'\\0','1',\"\",\"0\",[],[0],[1.1],here".split(",");
    String[] binaryOps = new String[0]; //<,==,<=,>=,!=,||,&&,+,-,*,/,%,|,&,^,<<,>>,>>>
    String[] unaryPrefixOps = "-".split(",");//"~,-,++,--,+,!".split(",");
    String[] unaryPostfixOps  = "++".split(",");//"++,--".split(",");
}

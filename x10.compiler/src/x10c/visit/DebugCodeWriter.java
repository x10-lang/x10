/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10c.visit;

import java.io.IOException;
import java.io.PrintStream;

import polyglot.frontend.Job;
import polyglot.util.CodeWriter;

public class DebugCodeWriter extends CodeWriter {

    private final CodeWriter w;
    private final String name;
    private PrintStream html;
    
    public DebugCodeWriter(CodeWriter w, Job job) {
        String htmlname = job.source().name().replaceFirst(".x10", ".html");
        if (htmlname.equals(job.source().name())) // double check to prevent overwriting the source
            htmlname = htmlname + ".html"; 
        name = htmlname;
        try {
            html = new PrintStream(name);
        } catch (IOException e) {
            html = System.err;
        }
        html.println("<html><head>");
        //html.println("<meta http-equiv='Refresh' content='20'/>");
        html.println("<style type='text/css'>\n" +
                "  a:hover {\n" +
                "    background-color: #ccccff;\n" +
                "  }\n" +
                "  a.a { position: relative; }\n" +
                "  a.a span {\n" +
                "    border: dotted 2px #cccccc;\n" +
                "    background-color: #00ffff;\n" +
                "    display: none;\n" +
                "    z-index: 1;\n" +
                "    position: absolute; top: 1em; left: 0px;\n" +
                "    font-size: smaller;\n" +
                "  }\n" +
                "</style>");
        html.println("<script type='text/javascript'>\n" + 
        		"\n" + 
        		"function toggle() {\n" + 
        		"  h = this.getElementsByTagName('span')[0];\n" + 
        		"  if (h.style.display == 'block') {\n" + 
        		"    h.style.display = 'none';\n" + 
        		"  } else {\n" + 
        		"    h.style.display = 'block';\n" + 
        		"  }\n" + 
        		"}\n" + 
        		"\n" + 
        		"function setup() {\n" + 
        		"  elements = document.getElementsByTagName('a')\n" + 
        		"  for (var i = 0; i < elements.length; i++) {\n" + 
        		"    elt = elements[i];\n" + 
        		"    if (elt.className != 'a') continue;\n" + 
        		"    elt.onclick = toggle;\n" + 
        		"  }\n" + 
        		"}\n" + 
        		"\n" + 
        		"</script>\n" + 
        		"");
        html.println("</head><body onload='setup();'>");
        html.println("<pre>");
        this.w = w;
    }

    public void allowBreak(int n, int level, String alt, int altlen) {
        w.allowBreak(n, level, alt, altlen);
        html.print(alt);
    }

    public void allowBreak(int n, String alt) {
        w.allowBreak(n, alt);
    }

    public void allowBreak(int n) {
        w.allowBreak(n);
    }

    public void begin(int n) {
        w.begin(n);
    }

    public void close() throws IOException {
        try {
            w.close();
        } finally {
            html.println("</pre></body></html>");
            if (html != System.err)
                html.close();
        }
    }

    public void end() {
        w.end();
    }

    public boolean equals(Object arg0) {
        return w.equals(arg0);
    }

    public boolean flush() throws IOException {
        return w.flush();
    }

    public boolean flush(boolean format) throws IOException {
        return w.flush(format);
    }

    public int hashCode() {
        return w.hashCode();
    }

    public void newline() {
        w.newline();
        html.println();
    }

    public void newline(int n, int level) {
        w.newline(n, level);
        html.println();
    }

    public void newline(int n) {
        w.newline(n);
        html.println();
    }

    public String toString() {
        return w.toString();
    }

    public void unifiedBreak(int n, int level, String alt, int altlen) {
        w.unifiedBreak(n, level, alt, altlen);
    }

    public void unifiedBreak(int n) {
        w.unifiedBreak(n);
    }

    public void write(String s, int length) {
        w.write(s, length);
        html.print(html_escape(s));
    }

    public void write(String s) {
        //if (name.equals("polyglot.util.OptimalCodeWriter")) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < Math.min(60, stackTrace.length); i++) {
            sb.append("<br/>");
            sb.append(stackTrace[i].toString());
        }
        String stack = sb.toString();
        
        // hide location as a stack tooltip
        if (s.startsWith("/*location:")) {
            stack = s.substring("/*location:".length(), s.indexOf("*/"));
            s = s.substring(s.indexOf("*/") + 2);
        }
        
        html.print("<a class='a'>" + html_escape(s) + 
                   "<span>" + stack + "</span></a>");
        w.write(s);
    }
    
    public static String html_escape(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
            case '<': sb.append("&lt;"); break;
            case '>': sb.append("&gt;"); break;
            case '&': sb.append("&amp;"); break;
            default: sb.append(c);
            }
        }
        return sb.toString();
    }
}

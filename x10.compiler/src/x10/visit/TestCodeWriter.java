package x10.visit;

import java.io.IOException;

import polyglot.util.CodeWriter;

public class TestCodeWriter extends CodeWriter {

    private final CodeWriter w;
    private final String name;
    
    public TestCodeWriter(CodeWriter w) {
        name = w.getClass().getName();
        this.w = w;
    }

    public void allowBreak(int n, int level, String alt, int altlen) {
        w.allowBreak(n, level, alt, altlen);
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
        w.close();
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
    }

    public void newline(int n, int level) {
        w.newline(n, level);
    }

    public void newline(int n) {
        w.newline(n);
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
//        System.out.println(s);
        w.write(s, length);
    }

    public void write(String s) {
        if (name.equals("polyglot.util.OptimalCodeWriter")) {
            StackTraceElement[] stackTrace = new Exception().getStackTrace();
            System.out.print(stackTrace[4]);
            System.out.print(" " + stackTrace[3]);
            System.out.print(" " + stackTrace[2]);
            System.out.println(" " + stackTrace[1]);
            System.out.println("write(s):" + s);
        }
        w.write(s);
    }
}

package x10.io;

import x10.compiler.Native;

public value Console {
        @Native("java", "java.lang.System.out")
        @Native("c++", "x10::io::FileWriter__FileOutputStream::STANDARD_OUT")
        private native static def realOut(): FileWriter.FileOutputStream;

        @Native("java", "new java.io.FileOutputStream(java.io.FileDescriptor.err)")
        @Native("c++", "x10::io::FileWriter__FileOutputStream::STANDARD_ERR")
        private native static def realErr(): FileWriter.FileOutputStream;

        @Native("java", "java.lang.System.in")
        @Native("c++", "x10::io::FileReader__FileInputStream::STANDARD_IN")
        private native static def realIn(): FileReader.FileInputStream;
    
        public const OUT: Printer = new Printer(new OutputStreamWriter(realOut()));
        public const ERR: Printer = new Printer(new OutputStreamWriter(realErr()));
        public const IN:  Reader  = new InputStreamReader(realIn());
        
   /*
        public static def write(b: Byte): Void throws IOException = OUT.write(b);
        public static def println(): Void throws IOException = OUT.println();
        public static def print(o: Object): Void throws IOException = OUT.print(o);
        public static def print(o: String): Void throws IOException = OUT.print(o);
        public static def println(o: Object): Void throws IOException = OUT.print(o);
        public static def println(o: String): Void throws IOException = OUT.print(o);
    
        public static def printf(fmt: String, args: Rail[Object]): Void throws IOException = OUT.printf(fmt, args);
        public static def printf(fmt: String, args: ValRail[Object]): Void throws IOException = OUT.printf(fmt, args);

        public static def ewrite(b: Byte): Void throws IOException = ERR.write(b);
        public static def eprintln(): Void throws IOException = ERR.println();
        public static def eprint(o: Object): Void throws IOException = ERR.print(o);
        public static def eprint(o: String): Void throws IOException = ERR.print(o);
        public static def eprintln(o: Object): Void throws IOException = ERR.print(o);
        public static def eprintln(o: String): Void throws IOException = ERR.print(o);
    
        public static def eprintf(fmt: String, args: Rail[Object]): Void throws IOException = ERR.printf(fmt, args);
        public static def eprintf(fmt: String, args: ValRail[Object]): Void throws IOException = ERR.printf(fmt, args);
        
        public static def read(): Byte throws IOException = IN.read();
        public static def readln(): Byte throws IOException = IN.readLine();
        public static def readByte(): Byte throws IOException = IN.readByte();
        public static def readChar(): Char throws IOException = IN.readChar();
   */
}

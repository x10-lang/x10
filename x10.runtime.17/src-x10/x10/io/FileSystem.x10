/**
 * Usage:
 *
 * try {
 *   val in = new File(inputFileName);
 *   val out = new File(outputFileName);
 *   val p = out.printer();
 *   for (line in in.lines()) {
 *      line = line.chop();
 *      p.println(line);
 *   }
 * }
 * catch (IOException e) { }
 */    
package x10.io;

public abstract class FileSystem {
    const SEPARATOR_CHAR: Char = System.getProperty("x10.io.fileSeparator")(0);
    const SEPARATOR: String = System.getProperty("x10.io.fileSeparator");
    const PATH_SEPARATOR_CHAR: Char = System.getProperty("x10.io.pathSeparator")(0);
    const PATH_SEPARATOR: String = System.getProperty("x10.io.pathSeparator");

    incomplete def delete(File);
    incomplete def deleteOnExit(File);
    incomplete def rename(from: File, to: File);
    incomplete def mkdir(File);
    incomplete def mkdirs(File);
    incomplete def exists(File);
    incomplete def size(File);

    incomplete def listFiles(File): ValRail[File];
    incomplete def listFiles(File, (File) => Boolean): ValRail[File];
}

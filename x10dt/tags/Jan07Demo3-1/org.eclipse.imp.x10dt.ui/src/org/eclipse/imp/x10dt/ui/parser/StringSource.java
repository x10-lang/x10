package x10.uide.parser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class SafariFileSource extends polyglot.frontend.FileSource
{
    private String contents;
    private String filePath;
    private SafariReader reader;

    public SafariFileSource(String contents, File file, String filePath) throws IOException {
        super(file);
        this.contents = contents;
        this.filePath = filePath;
    }

    public boolean equals(Object o) {
        if (o instanceof SafariFileSource) {
            SafariFileSource s = (SafariFileSource) o;
            return filePath.equals(s.filePath);
        }

        return false;
    }

    public int hashCode() {
        return filePath.hashCode();
    }

    /** Open the source file. */
    public Reader open() throws IOException {
        if (reader == null) {
            reader = new SafariReader(contents.toCharArray());
        }

        return reader;
    }

    /** Close the source file. */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    public String toString() {
        return file.getPath();
    }
}
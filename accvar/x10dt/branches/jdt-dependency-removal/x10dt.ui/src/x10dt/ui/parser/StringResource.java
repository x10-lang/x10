package x10dt.ui.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import polyglot.frontend.Resource;

public class StringResource implements Resource {
    private final String fileName;
    private final File file;
    private final String contents;

    public StringResource(String contents, File file, String fileName) {
        this.contents = contents;
        this.file = file;
        this.fileName = fileName;
    }

    public File file() {
        return this.file;
    }

    public InputStream getInputStream() throws IOException {
        return new StringBufferInputStream(contents);
    }

    public String name() {
        return this.fileName;
    }

    @Override
    public String toString() {
        return name();
    }
}

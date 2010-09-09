/**
 * 
 */
package polyglot.frontend;

import java.io.*;

public interface Resource {
    File file();
    String name();
    InputStream getInputStream() throws IOException;
}
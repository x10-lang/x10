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

package x10.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public abstract class FileUtils {
    
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        if (targetFile.equals(sourceFile)) return;
        FileInputStream sourceInputStream = null;
        FileOutputStream targetOutputStream = null;
        try {
            sourceInputStream = new FileInputStream(sourceFile);
            FileChannel sourceChannel = sourceInputStream.getChannel();
            targetOutputStream = new FileOutputStream(targetFile);
            FileChannel targetChannel = targetOutputStream.getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
        } finally {
            if (sourceInputStream != null) sourceInputStream.close();
            if (targetOutputStream != null) targetOutputStream.close();
        }
    }
    
    public static void copyFile(InputStream sourceInputStream, long sourceSize, File targetFile) throws IOException {
        byte[] buf = new byte[1024];
        FileOutputStream targetOutputStream = null;
        try {
            targetOutputStream = new FileOutputStream(targetFile);
            while (sourceSize > 0) {
                int readBytes = sourceInputStream.read(buf);
                targetOutputStream.write(buf, 0, readBytes);
                sourceSize -= readBytes;
            }
        } finally {
            if (sourceInputStream != null) sourceInputStream.close();
            if (targetOutputStream != null) targetOutputStream.close();
        }
    }
    
}

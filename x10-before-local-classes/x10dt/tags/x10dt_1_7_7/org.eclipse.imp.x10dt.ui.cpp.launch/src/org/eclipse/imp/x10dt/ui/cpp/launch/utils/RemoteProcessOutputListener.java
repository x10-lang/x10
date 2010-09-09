/**
 * 
 */
package org.eclipse.imp.x10dt.ui.cpp.launch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.ptp.remote.core.IRemoteProcess;

public final class RemoteProcessOutputListener extends Thread {
  private final IRemoteProcess process;
  private final OutputStream outputStream;
  private final OutputStream errorStream;

  public RemoteProcessOutputListener(IRemoteProcess process,
                                     OutputStream outputStream,
                                     OutputStream errorStream)
  {
    this(process, outputStream, errorStream, "Process output listener"); //$NON-NLS-1$
  }

  public RemoteProcessOutputListener(IRemoteProcess process,
                                     OutputStream outputStream,
                                     OutputStream errorStream,
                                     String name)
  {
    super(name);
    this.process = process;
    this.outputStream = outputStream;
    this.errorStream = errorStream;
  }
  
  public void run() {
    final InputStream err = process.getErrorStream();
    final InputStream out = process.getInputStream();
    byte[] buf = new byte[100];
    int elen = 0;
    int olen = 0;
    try {
      while (elen >= 0 || olen >= 0) {
        elen = err.read(buf);
        if (elen >= 0)
          errorStream.write(buf, 0, elen);
        olen = out.read(buf);
        if (olen >= 0)
          outputStream.write(buf, 0, olen);
        Thread.sleep(10);
      }
      errorStream.flush();
      outputStream.flush();
    } catch (IOException e) {
    } catch (InterruptedException e) {
    }
  }
}
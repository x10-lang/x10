/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.builder.target_op;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;

import x10dt.ui.launch.core.utils.IProcessOuputListener;

/**
 * Helper class for providing operations around file and process management on a target machine (either local or remote).
 * 
 * <p>See {@link TargetOpHelperFactory} for getting implementation(s) of this interface.
 * 
 * @author egeay
 */
public interface ITargetOpHelper {
  
  /**
   * Returns the environment variable value for the name provided.
   * 
   * @param envVarName The environment variable name to consider.
   * @return A <b>null</b> value if the variable is not defined, otherwise a non-null, but possibly empty, string value.
   */
  public String getEnvVarValue(final String envVarName);
    
  /**
   * Returns the appropriate file store for a given path according to the connection type.
   * 
   * @param resourcePath The path to the resource to consider.
   * @return A non-null {@link IFileStore} instance.
   */
  public IFileStore getStore(final String resourcePath);
  
  /**
   * Converts a given resource path to an appropriate path for the target machine. For instance in the case of Cygwin system,
   * this means transforming the beginning of the path with the cygdrive path prefix.
   * 
   * @param resourcePath The path of the resource to transform.
   * @return A non-null non-empty (if the parameter is non-empty) string.
   */
  public String getTargetSystemPath(final String resourcePath);
  
  /**
   * Runs a given command in a new process on the target machine with the help of the parameters provided.
   * 
   * @param command The command to execute.
   * @param directory The directory where the command will be executed.
   * @param envVariables The environment variables to consider.
   * @param outputListener The listener that will get notified of the characters sent to the standard and error output 
   * streams.
   * @return The exit value of the process. By convention, 0 indicates normal termination.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the current thread is {@linkplain Thread#interrupt() interrupted} by another
   * thread while it is waiting, then the wait is ended and an {@link InterruptedException} is thrown.
   */
  public int run(final List<String> command, final String directory, final Map<String, String> envVariables,
                 final IProcessOuputListener outputListener) throws IOException, InterruptedException;
  
  /**
   * Runs a given command in a new process on the target machine with the help of the directory provided.
   * 
   * <p>This method will use the default set of environment variables for the process as opposed to 
   * {@link #run(List, String, Map, IProcessOuputListener)}.
   * 
   * @param command The command to execute.
   * @param directory The directory where the command will be executed.
   * @param outputListener The listener that will get notified of the characters sent to the standard and error output 
   * streams.
   * @return The exit value of the process. By convention, 0 indicates normal termination.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the current thread is {@linkplain Thread#interrupt() interrupted} by another
   * thread while it is waiting, then the wait is ended and an {@link InterruptedException} is thrown.
   */
  public int run(final List<String> command, final String directory,
                 final IProcessOuputListener outputListener) throws IOException, InterruptedException;
  
  /**
   * Runs a given command in a new process on the target machine with the help of the environment variables provided.
   * 
   * <p>This method will use the current directory to run for the process as opposed to 
   * {@link #run(List, String, Map, IProcessOuputListener)}.
   * 
   * @param command The command to execute.
   * @param envVariables The environment variables to consider.
   * @param outputListener The listener that will get notified of the characters sent to the standard and error output 
   * streams.
   * @return The exit value of the process. By convention, 0 indicates normal termination.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the current thread is {@linkplain Thread#interrupt() interrupted} by another
   * thread while it is waiting, then the wait is ended and an {@link InterruptedException} is thrown.
   */
  public int run(final List<String> command, final Map<String, String> envVariables,
                 final IProcessOuputListener outputListener) throws IOException, InterruptedException;
  
  /**
   * Runs a given command in a new process on the target machine.
   * 
   * <p>This method will use the current directory and the default set of environment variables to run as opposed to 
   * {@link #run(List, String, Map, IProcessOuputListener)}.
   * 
   * @param command The command to execute.
   * @param outputListener The listener that will get notified of the characters sent to the standard and error output 
   * streams.
   * @return The exit value of the process. By convention, 0 indicates normal termination.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the current thread is {@linkplain Thread#interrupt() interrupted} by another
   * thread while it is waiting, then the wait is ended and an {@link InterruptedException} is thrown.
   */
  public int run(final List<String> command, 
                 final IProcessOuputListener outputListener) throws IOException, InterruptedException;
  
  /**
   * Converts a URI into a path consistent with the target system.
   * 
   * @param uri The URI to convert.
   * @return A non-null string.
   */
  public String toPath(final URI uri);
  
}

/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;


/**
 * Defines a thread that will wait for a customizable amount of time before taking some general action.
 * 
 * @author egeay
 */
public final class TimerThread extends Thread {
  
  /**
   * Creates the timer thread instance with the action to run and the time to consider.
   * 
   * @param runnableAction The action to run after the time has expired.
   * @param time The time threshold after which the thread should stop and run the general action.
   */
  public TimerThread(final Runnable runnableAction, final int time) {
    super("Keyboard Timer Thread"); //$NON-NLS-1$
    this.fRunnableAction = runnableAction;
    this.fTime = time;
  }
  
  // --- Overridden methods
  
  public void run() {
    while (true) {
      try {
        Thread.sleep(this.fTime);
      } catch (InterruptedException except) {
      }
      if (! this.fShouldSleepAgain && ! this.fIsReseted) {
        this.fRunnableAction.run();
        break;
      }
      this.fShouldSleepAgain = false;
    }
  }
  
  // --- Public services
  
  /**
   * Sets a flag that defines if the timer should be reseted or not while waiting.
   * 
   * @param resetFlag The flag value.
   */
  public void setShouldBeReseted(final boolean resetFlag) {
    this.fIsReseted = resetFlag;
    if (resetFlag) {
      this.fShouldSleepAgain = true;
    }
  }
  
  // --- Fields
  
  private final Runnable fRunnableAction;
  
  private final int fTime;
  
  private boolean fIsReseted;
  
  private boolean fShouldSleepAgain;
  
}

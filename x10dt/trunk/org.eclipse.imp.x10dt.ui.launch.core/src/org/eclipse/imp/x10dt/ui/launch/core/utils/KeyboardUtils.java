/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;

/**
 * Utility methods around keyboard events and/or actions.
 * 
 * @author egeay
 */
public final class KeyboardUtils {
  
  /**
   * Adds a key listener to the control provided to run a given action after 2 seconds of inactivity.
   * 
   * @param control The control for which one wants to attach a key listener for delayed action.
   * @param runnable The runnable that contains the action to execute.
   */
  public static void addDelayedActionOnControl(final Control control, final Runnable runnable) {
    addDelayedActionOnControl(control, runnable, 2000);
  }
  
  /**
   * Adds a key listener to the control provided to run a given action after a given time of inactivity.
   * 
   * @param control The control for which one wants to attach a key listener for delayed action.
   * @param runnable The runnable that contains the action to execute.
   * @param time The time of inactivity after which the runnable can be executed.
   */
  public static void addDelayedActionOnControl(final Control control, final Runnable runnable, final int time) {
    control.addKeyListener(new KeyListener() {
      
      public void keyReleased(final KeyEvent event) {
        if ((this.fTimerThread == null) || (! this.fTimerThread.isAlive())) {
          this.fTimerThread = new TimerThread(runnable, time);
          this.fTimerThread.start();
        } else {
          this.fTimerThread.setShouldBeReseted(false);
        }
      }
      
      public void keyPressed(final KeyEvent event) {
        if ((this.fTimerThread != null) && this.fTimerThread.isAlive()) {
          this.fTimerThread.setShouldBeReseted(true);
        }
      }
      
      // --- Fields
      
      private TimerThread fTimerThread;
      
    });

  }
  
  // --- Private classes
  
  private static final class TimerThread extends Thread {
    
    TimerThread(final Runnable runnableAction, final int time) {
      super("Keyboard Timer Thread"); //$NON-NLS-1$
      this.fRunnableAction = runnableAction;
      this.fTime = time;
    }
    
    // --- Overridden methods
    
    public void run() {
      for (;;) {
        try {
          Thread.sleep(this.fTime);
        } catch (InterruptedException except) {
          continue;
        }
        if (! this.fIsReseted) {
          this.fRunnableAction.run();
          break;
        }
      }
    }
    
    // --- Internal services
    
    void setShouldBeReseted(final boolean resetFlag) {
      this.fIsReseted = resetFlag;
    }
    
    // --- Fields
    
    private final Runnable fRunnableAction;
    
    private final int fTime;
    
    private boolean fIsReseted;
    
  }
  
  // --- Private code
  
  private KeyboardUtils() {}

}

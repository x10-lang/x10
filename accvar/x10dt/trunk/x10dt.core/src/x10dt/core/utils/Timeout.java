package x10dt.core.utils;

/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation. All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * Timeout class that has constants for all timeout values that are multiplied by a multiplier. The multiplier can be changed
 * by changing the java system property "com.ibm.etools.swtbot.multiplier", so slower machines or slower connections can change
 * all timeout values by changing the system property.
 *******************************************************************************/
public class Timeout {

  public static String MULTIPLIER_KEY = "com.ibm.etools.swtbot.multiplier";

  public static long MULTIPLIER = toLong(System.getProperty(MULTIPLIER_KEY, "1"));

  public static long FIVE_SECONDS = 5000 * MULTIPLIER;

  public static long TEN_SECONDS = 10000 * MULTIPLIER;

  public static long FIFTEEN_SECONDS = 15000 * MULTIPLIER;

  public static long TWENTY_SECONDS = 20000 * MULTIPLIER;

  public static long TWENTY_FIVE_SECONDS = 25000 * MULTIPLIER;

  public static long THIRTY_SECONDS = 30000 * MULTIPLIER;

  public static long THIRTY_FIVE_SECONDS = 35000 * MULTIPLIER;

  public static long FOURTY_SECONDS = 40000 * MULTIPLIER;

  public static long FOURTY_FIVE_SECONDS = 45000 * MULTIPLIER;

  public static long FIFTY_SECONDS = 50000 * MULTIPLIER;

  public static long FIFTY_FIVE_SECONDS = 50000 * MULTIPLIER;

  public static long SIXTY_SECONDS = 60000 * MULTIPLIER;

  private static long toLong(String timeoutValue) {
    try {
      Long timeout = Long.valueOf(timeoutValue);
      return timeout.longValue();
    } catch (Exception e) {
      return 1;
    }
  }
}

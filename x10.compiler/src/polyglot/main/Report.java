/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.main;

/** Class used for reporting debug messages. */
public class Report {
  
  public static Reporter reporter;
  
  /* TODO: eliminate static Report.
   * There are just a few legacy uses of static Report.
   * We handle them individually here until uses go away.
   */
  public static boolean trace;
  public static int workstealing;
  public static boolean verbose;

  public static void initialize(Reporter theReporter) {
      reporter = theReporter;
      synchronized (reporter) {
          trace |= reporter.should_report("trace",1);
          if (reporter.should_report("workstealing",1)) {
              int level = reporter.level("workstealing");
              workstealing = (level > workstealing) ? level : workstealing;
          }
          verbose |= reporter.should_report("verbose",1);
      }
  }

}

/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2006 IBM Corporation
 * 
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

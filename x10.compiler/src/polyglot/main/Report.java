/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * Copyright (c) 2006 IBM Corporation
 * 
 */

package polyglot.main;

import java.util.Arrays;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;

/** Class used for reporting debug messages. */
public class Report {
  
  private static Reporter reporter;

  /** Report topics understood by the base compiler. */
  public final static String cfg = "cfg";
  public final static String context = "context";
  public final static String dataflow = "dataflow";
  public final static String errors = "errors";
  public final static String frontend = "frontend";
  public final static String imports = "imports";
  public final static String loader = "loader";
  public final static String resolver = "resolver";
  public final static String serialize = "serialize";
  public final static String time = "time";
  /** threshold specifies a percentage. Only report time that takes
   *  more than the percent of total time specified by this threshold.
   */
  public final static String threshold = "threshold";
  public final static String frequency = "frequency";
  public final static String types = "types";
  public final static String visit = "visit";
  public final static String verbose = "verbose";

  public static void initialize(Reporter theReporter) {
        // We're attempting to make Report not be static.
        // Until this is completed, we're continuing to use a single
        // Reporter to reproduce the behaviour of having a static Report.
        if (reporter == null)
            reporter = theReporter;
        else {
            synchronized (reporter) {
                for (Map.Entry<String, Integer> e : theReporter.reportTopics.entrySet()) {
                    reporter.addTopic(e.getKey(), e.getValue());
                }
            }
        }
  }
  
  /**
   * Return whether a message on <code>topic</code> of obscurity
   * <code>level</code> should be reported, based on use of the
   * -report command-line switches given by the user.
   */
  public static boolean should_report(String topic, int level) {
    return reporter.should_report(topic,level);
  }

  /**
   * Return whether a message on <code>topics</code> of obscurity
   * <code>level</code> should be reported, based on use of the
   * -report command-line switches given by the user.
   */
    public static boolean should_report(Collection<String> topics, int level) {
        return reporter.should_report(topics,level);
    }


  /** This is the standard way to report debugging information in the
   *  compiler.  It reports a message of the specified level (which
   *  controls the presentation of the message. To test whether such
   *  message should be reported, use "should_report".
   *
   *  NOTE: This is a change of spec from earlier versions of Report.
   *  NOTE: If position information is available, call report(int, String, Position)
   *  instead, to ensure the error is associated with the right file/location.
   */
  public static void report(int level, String message) {
      reporter.report(level, message, null);
  }


}

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
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.util.SimpleErrorQueue;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;

/** Class used for reporting debug messages. */
public class Reporter {
    /**
     * A collection of string names of topics which can be used with the -report
     * command-line switch
     */
    public final Collection<String> topics = CollectionFactory.newHashSet();

    /**
     * A collection of string names of topics which we should always check if we
     * should report.
     */
    protected Stack<String> stackedTopics;

    /**
     * The topics that the user has selected to report, mapped to the level they
     * want to report them to.
     */
    // This is teporarily public so that Report can get at it.
    public final Map<String, Integer> reportTopics = CollectionFactory.newHashMap(); // Map[String,
    // protected final Map<String, Integer> reportTopics = CollectionFactory.newHashMap(); // Map[String,
                                                                                        // Integer]

    /** Error queue to which to write messages. */
    protected ErrorQueue eq;

    /**
     * Indicates if there is no reporting at all. The normal case is that we do
     * not report anything, so for efficiency reasons, since
     * <code>should_report</code> is called so often, we'll use this flag to
     * bypass a lot of the checking. When the options are processed, this flag
     * should be changed.
     */
    protected boolean noReporting = true;

    /** Report topics understood by the base compiler. */
    public final static String cfg = "cfg";
    public final static String context = "context";
    public final static String dataflow = "dataflow";
    public final static String errors = "errors";
    public final static String frontend = "frontend";
    public final static String imports = "imports";
    public final static String innerremover = "innerremover";
    public final static String InstanceInvariantChecker = "InstanceInvariantChecker";
    public final static String PositionInvariantChecker = "PositionInvariantChecker";
    public final static String loader = "loader";
    public final static String nativeclass = "nativeclass";
    public final static String resolver = "resolver";
    public final static String serialize = "serialize";
    public final static String specificity = "specificity";
    public final static String sysresolver = "sysresolver";
    public final static String time = "time";
    /**
     * threshold specifies a percentage. Only report time that takes more than
     * the percent of total time specified by this threshold.
     */
    public final static String threshold = "threshold";
    public final static String frequency = "frequency";
    public final static String types = "types";
    public final static String visit = "visit";
    public final static String verbose = "verbose";

    // This topic is the level of detail that should be in messages.
    public final static String debug = "debug";

    public Reporter() {
        topics.add(cfg);
        topics.add(context);
        topics.add(dataflow);
        topics.add(errors);
        topics.add(frontend);
        topics.add(imports);
        topics.add(loader);
        topics.add(resolver);
        topics.add(serialize);
        topics.add(specificity);
        topics.add(time);
        topics.add(threshold);
        topics.add(frequency);
        topics.add(types);
        topics.add(visit);
        topics.add(verbose);
        topics.add(debug);
    }

    /**
     * Return whether a message on <code>topic</code> of obscurity
     * <code>level</code> should be reported, based on use of the -report
     * command-line switches given by the user.
     */
    public boolean should_report(String topic, int level) {
        if (noReporting) return false;
        return should_report(Collections.singletonList(topic), level);
    }

    /**
     * Return whether a message on <code>topics</code> of obscurity
     * <code>level</code> should be reported, based on use of the -report
     * command-line switches given by the user.
     */
    public boolean should_report(Collection<String> topics, int level) {
        if (noReporting) return false;
        if (stackedTopics != null) {
            for (String topic : stackedTopics) {
                if (level(topic) >= level) return true;
            }
        }
        if (topics != null) {
            for (String topic : topics) {
                if (level(topic) >= level) return true;
            }
        }
        return false;
    }

    /**
     * Start reporting messages on <code>topic</code>.
     */
    public void start_reporting(String topic) {
        if (noReporting) return;
        if (should_report(topic, 1)) {
            if (stackedTopics == null) stackedTopics = new Stack<String>();
            stackedTopics.push(topic);
        }
    }

    /**
     * Stop reporting messages on <code>topic</code>.
     */
    public void stop_reporting(String topic) {
        if (stackedTopics == null) return;
        stackedTopics.remove(topic);
    }

    /** Add a topic to report */
    public void addTopic(String topic, int level) {
        Integer i = (Integer) reportTopics.get(topic);
        if (i == null || i.intValue() < level) {
            reportTopics.put(topic, Integer.valueOf(level));
        }
        noReporting = false;
    }

    /** Remove a topic to report */
    public void removeTopic(String topic) {
        reportTopics.remove(topic);
        if (reportTopics.isEmpty()) {
            noReporting = true;
        }
    }

    /** Get the error queue, possibly creating it if not set. */
    public ErrorQueue getQueue() {
        if (eq == null) {
            eq = new SimpleErrorQueue();
        }
        return eq;
    }

    /** Set the error queue. */
    public void setQueue(ErrorQueue eq) {
        this.eq = eq;
    }

    public int level(String name) {
        Object i = reportTopics.get(name);
        if (i == null)
            return 0;
        else
            return ((Integer) i).intValue();
    }

    /**
     * This is the standard way to report debugging information in the compiler.
     * It reports a message of the specified level (which controls the
     * presentation of the message. To test whether such message should be
     * reported, use "should_report".
     * 
     * NOTE: This is a change of spec from earlier versions of Report. NOTE: If
     * position information is available, call report(int, String, Position)
     * instead, to ensure the error is associated with the right file/location.
     */
    public void report(int level, String message) {
        report(level, message, null);
    }

    /**
     * This is the standard way to report debugging information in the compiler.
     * It reports a message of the specified level (which controls the
     * presentation of the message. To test whether such message should be
     * reported, use "should_report".
     * 
     * NOTE: This is a change of spec from earlier versions of Report. NOTE:
     * This version takes an explicit Position, so that position info gets
     * properly associated with the ErrorInfo that gets created by enqueue().
     */
    public void report(int level, String message, Position pos) {
        StringBuffer buf = new StringBuffer(message.length() + level);
        for (int j = 1; j < level; j++) {
            buf.append(" ");
        }
        buf.append(message);
        getQueue().enqueue(ErrorInfo.DEBUG, buf.toString(), pos);
    }
}

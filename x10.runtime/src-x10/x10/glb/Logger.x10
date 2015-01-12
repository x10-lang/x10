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

package x10.glb;
/**
 * <p>Class that collects lifeline statistics of GLB
 * </p>
 */
public final class Logger {
    
    /* workload sent/recieved stat*/
    public var nodesCount:Long = 0;
    public var nodesGiven:Long = 0;
    public var lifelineNodesReceived:Long = 0;
    
    /* (random)stealing requests stat*/
    public var stealsAttempted:Long = 0;
    public var stealsPerpetrated:Long = 0;
    public var stealsReceived:Long = 0;
    public var stealsSuffered:Long = 0;
    public var nodesReceived:Long = 0;

    /* (lifeline)stealing requests stat*/
    public var lifelineStealsAttempted:Long = 0;
    public var lifelineStealsPerpetrated:Long = 0;
    public var lifelineStealsReceived:Long = 0;
    public var lifelineStealsSuffered:Long = 0;
   

    /* timing stat */
    public var lastStartStopLiveTimeStamp:Long = -1;
    public var timeAlive:Long = 0;
    public var timeDead:Long = 0;
    public var startTime:Long = 0;
    public val timeReference:Long;
    
    
    
    /**
     * Constructor
     * @param b true, called when prior-calculation; false, called when post-calculation
     */
    public def this(b:Boolean) {
        if (b) x10.util.Team.WORLD.barrier();
        timeReference = System.nanoTime();
    }
    

    /**
     * Timer is started before processing, which includes calculation, distribution and requesting/rejects tasks
     */
    public def startLive() {
        val time = System.nanoTime();
        if (startTime == 0) startTime = time;
        if (lastStartStopLiveTimeStamp >= 0) {
            timeDead += time - lastStartStopLiveTimeStamp;
        }
        lastStartStopLiveTimeStamp = time;
    }

    /**
     * Timer is stopped when running out of tasks and failing to steal any task
     */
    public def stopLive() {
        val time = System.nanoTime();
        timeAlive += time - lastStartStopLiveTimeStamp;
        lastStartStopLiveTimeStamp = time;
    }

    /**
     * Aggregate stats for all places
     * @param logs log from every place
     */
    public def collect(logs:Rail[Logger]) {
        for (l in logs) add(l);
    }

    /**
     * Print out the actual workload re-distribution by showing the steals that were carried out.
     */
    public def stats() {
        Console.OUT.println(nodesGiven + " Task items stolen = " + nodesReceived + " (direct) + " +
            lifelineNodesReceived + " (lifeline)."); 
        Console.OUT.println(stealsPerpetrated + " successful direct steals."); 
        Console.OUT.println(lifelineStealsPerpetrated + " successful lifeline steals.");
    }

    /**
     * Gets part of the string.
     * @param str original string
     * @param start starting index of the string
     * @param end ending index of the string
     * @return string from start to end
     */
    static def sub(str:String, start:Int, end:Int) = str.substring(start, Math.min(end, str.length()));

    /**
     * Sum up stat with another logger
     * @param other another logger to sum up with
     */
    public def add(other:Logger) {
        nodesCount += other.nodesCount;
        nodesGiven += other.nodesGiven;
        nodesReceived += other.nodesReceived;
        stealsPerpetrated += other.stealsPerpetrated;
        lifelineNodesReceived += other.lifelineNodesReceived;
        lifelineStealsPerpetrated += other.lifelineStealsPerpetrated;
    }

    /**
     * Print out more detailed lifeline stats when verbose flag turned on
     * @param verbose verbose flag true when {@link GLBParameters} show glb flag is on.
     */
    public def get(verbose:Boolean) {
        if (verbose) {
            Console.OUT.println("" + Runtime.hereLong() + " -> " +
                sub("" + (timeAlive/1E9), 0n, 6n) + " : " +
                sub("" + (timeDead/1E9), 0n, 6n) + " : " + 
                sub("" + ((timeAlive + timeDead)/1E9), 0n, 6n) + " : " + 
                sub("" + (100.0*timeAlive/(timeAlive+timeDead)), 0n, 6n) + "%" + " :: " +
                sub("" + ((startTime-timeReference)/1E9), 0n, 6n) + " : " +
                sub("" + ((lastStartStopLiveTimeStamp-timeReference)/1E9), 0n, 6n)  + " :: " +
                nodesCount + " :: " +
                nodesGiven + " : " +
                nodesReceived + " : " +
                lifelineNodesReceived + " :: " +
                stealsReceived + " : " +
                lifelineStealsReceived + " :: " +
                stealsSuffered + " : " +
                lifelineStealsSuffered + " :: " +
                stealsAttempted + " : " +
                (stealsAttempted - stealsPerpetrated) + " :: " +
                lifelineStealsAttempted + " : " +
                (lifelineStealsAttempted - lifelineStealsPerpetrated));
        }
        return this;
    }
}

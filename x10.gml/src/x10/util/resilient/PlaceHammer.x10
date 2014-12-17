/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Sara Salem Hamouda 2014.
 */
package x10.util.resilient;

import x10.util.ArrayList;
import x10.io.FileReader;
import x10.io.File;
import x10.io.EOFException;

/**
 * The hammer class kills places at certain times during the computation
 * to test application resilience. An input file is required to configure the
 * timing and the identities of places to kill.
 * Config file format:
 * First Line Format:  [mode=time|mode=iteration]
 * The first line specifies the hammer mode:
 *    mode=iteration: The iter mode is for iterative applications, a place
 *                    should be killed at a certain iteration number.
 *    mode=time     : In the time mode, a place should be killed after sleeping
 *                    for a certain time (milliseconds) 
 * 
 * Remaining Lines Format (for Iteration Mode):
 * [Iteration_Number:Place_Id:Iteration_Kill_Type]
 *   Iteration_Kill_Type specifies when during the iteration the place should
 *   be killed.  There are 3 possible values [S|C|R]:
 *       S: kill during the iteration step execution
 *       C: kill during taking a checkpoint
 *       R: kill during restore
 *
 * Remaining Lines Format (for Time Mode):
 * [Time_In_Milliseconds:Place_Id]
 *    The line specifies the time in milliseconds the hammer should sleep
 *    before killing the specified place
 */
public class PlaceHammer (configFile:String) {

    class HammerRecord (timeOrIter:Long, placeId:Long, iterKillType:String) { }

    private var mode:Long = -1; // 1- iterations, 2- time
    private var records:ArrayList[HammerRecord];

    public static def make(configFile:String): PlaceHammer {
        val hammer = new PlaceHammer(configFile);
        hammer.init();
        return hammer;
    }
    
    /***********Timer Hammer***************/
    private var stop:Boolean = false;
    public def startTimerHammer() {
        if (mode != 2)
            throw new UnsupportedOperationException("Wrong Hammer Mode Configurations ...");
        
        async {
           var i:Long = 0;
            var rec:HammerRecord = null;
            while (i < records.size() && !stop) {
                rec = records.get(i);
            
                Console.OUT.println("[Hammer Log] Going to sleep for ["+rec.timeOrIter+"ms], then kill Place ("+rec.placeId+") ...");
                System.sleep(rec.timeOrIter);
            
                if (stop)
                    break;

                for (p in Place.places()) {
                    if (p.id == rec.placeId) {
                        try {
                            at(p) {
                                Console.OUT.println("[Hammer Log] Killing ["+here+"] ...");
                                System.killHere();
                            }
                        } catch (e: Exception) {
                        
                        }
                    }
                }
                i++;
            }
            
            stop = true;
            Console.OUT.println("[Hammer Log] Timer Hammer Stopped ...");
        }
    }

    public def stopTimerHammer() {
        if (mode != 2)
            throw new UnsupportedOperationException("Wrong Hammer Mode Configurations ...");
        stop = true;
    }
    
    /***********Iterative Hammer***************/
    private var index:Long = 0;
    
    public def checkKillStep(iter:Long) {
        checkKill(iter, "S");
    }
    
    public def checkKillCheckpoint(iter:Long) {
        checkKill(iter, "C");
    }
    
    public def checkKillRestore(iter:Long) {
        checkKill(iter, "R");
    }
    
    private def checkKill(iter:Long, iterKillType:String) {
        if (mode != 1)
            throw new UnsupportedOperationException("Wrong Hammer Mode Configurations ...");
        
        if (index < records.size()) {
            var rec:HammerRecord = records(index);
            //skip the records that belong to previous iterations but have not been matched
            while (rec.timeOrIter < iter) {
                index++;
                if (index < records.size()) {
                    rec = records(index);
                } else {
                    rec = null;
                    break;
                }
            }
            
            if (rec != null && rec.timeOrIter == iter && rec.iterKillType.equals(iterKillType)) {
                index ++;
                
                for (p in Place.places()) {
                    if (p.id == rec.placeId) {
                        at(p) {
                            Console.OUT.println("[Hammer Log] Killing ["+here+"] ...");
                            System.killHere();
                        }
                    }
                }
            }            
        }      
    }
    
    public def isIterativeHammer():Boolean = mode == 1;
    public def isTimerHammer():Boolean = mode == 2;
    
    public def init() {
        val file:FileReader = new FileReader(new File(configFile));
        var line:String = file.readLine();
        
        if (line != null) {
            if (line.equals("mode=iteration"))
               mode = 1;
            else if (line.equals("mode=time"))
               mode = 2;
        }
        if (mode != -1) {
            try{
                records = new ArrayList[HammerRecord]();
                line = file.readLine();
                while(line != null && line.trim().length() > 0) {
                    if (!line.startsWith("#")) {
                        val parts = line.split(":");
                        var record:HammerRecord = null;
                        if (mode == 1) {
                            record = new HammerRecord(Long.parseLong(parts(0)),Long.parseLong(parts(1)), parts(2));
                        } else {
                            record = new HammerRecord(Long.parseLong(parts(0)),Long.parseLong(parts(1)), "N/A");
                        }
                        records.add(record);
                    }
                    line = file.readLine();
                }
            } catch (eof:EOFException) {
                // end of file
            }
        }
        file.close();
    }
}

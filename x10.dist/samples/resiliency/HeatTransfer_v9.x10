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

import x10.array.*;

import x10.util.*;

public class HeatTransfer_v9 {

    public final static class Config {
        var quiet : Boolean;
        var verbose : Boolean;
        var result : Boolean;

        var killTest : Long;
        var globalWidth : Long;
        var globalHeight : Long;
        var globalDim : DenseIterationSpace_2;
        var iterations : Long;

        public def toString() {
            return "Array Dimension: ("+(globalDim.max0+1)+" x "+(globalDim.max1+1)+"), iterations: "+iterations+", number of places: "+Place.numPlaces();
        }
    }

    public final static class PlaceState {
        val cfg : Config;
        var frontArray : Rail[Double];
        var backArray : Rail[Double];

        var localWidth : Long;
        var localHeight : Long;
        var localXOffset : Long;
        var localYOffset : Long;

        // mapped to 'here' if don't exist...
        val leftPlaces = new HashSet[Place]();
        val rightPlaces = new HashSet[Place]();
        val abovePlaces = new HashSet[Place]();
        val belowPlaces = new HashSet[Place]();

        def this(cfg:Config) {
            this.cfg = cfg;
        }

        public def front(x:Long, y:Long) = frontArray((y+1)*(localWidth+2)+x+1);
        public def back(x:Long, y:Long) = backArray((y+1)*(localWidth+2)+x+1);
        public def front(x:Long, y:Long, v:Double) { frontArray((y+1)*(localWidth+2)+x+1) = v; }
        public def back(x:Long, y:Long, v:Double) { backArray((y+1)*(localWidth+2)+x+1) = v; }

        private static def whoMapsTo(mapping:Rail[Long], v:Long) : Place {
            for (i in mapping.range()) {
                if (mapping(i) == v) return Place(i);
            }
            throw new Exception("Shouldn't get here");
        }

        public def assignPartition(mapping:Rail[Long]) {
        
            val localDim = BlockingUtils.partitionBlockBlock(cfg.globalDim, mapping.size, mapping(here.id));


            this.localWidth = localDim.max0 - localDim.min0 + 1;
            this.localHeight = localDim.max1 - localDim.min1 + 1;
            this.localXOffset = localDim.min0;
            this.localYOffset = localDim.min1;

            Console.OUT.println(here+" gets ("+localXOffset+","+localYOffset+") size ("+localWidth+","+localHeight+")");
            
            this.frontArray = new Rail[Double]((this.localWidth + 2) * (this.localHeight + 2)); 
            this.backArray = new Rail[Double]((this.localWidth + 2) * (this.localHeight + 2)); 
            if (this.localYOffset == 0) {
                for (x in -1..localWidth) {
                    back(x, -1, 1.0);
                    front(x, -1, 1.0);
                }
            }

            leftPlaces.clear();
            rightPlaces.clear();
            abovePlaces.clear();
            belowPlaces.clear();

            if (localXOffset>0) for (y in 0..(localHeight-1)) {
                leftPlaces.add(whoMapsTo(mapping, BlockingUtils.mapIndexToBlockBlockPartition(cfg.globalDim, mapping.size, localXOffset-1, localYOffset+y)));
            }
            if (localXOffset+localWidth<cfg.globalWidth) for (y in 0..(localHeight-1)) {
                rightPlaces.add(whoMapsTo(mapping, BlockingUtils.mapIndexToBlockBlockPartition(cfg.globalDim, mapping.size, localXOffset+localWidth, localYOffset+y)));
            }
            if (localYOffset>0) for (x in 0..(localWidth-1)) {
                abovePlaces.add(whoMapsTo(mapping, BlockingUtils.mapIndexToBlockBlockPartition(cfg.globalDim, mapping.size, localXOffset+x, localYOffset-1)));
            }
            if (localYOffset+localHeight<cfg.globalHeight) for (x in 0..(localWidth-1)) {
                belowPlaces.add(whoMapsTo(mapping, BlockingUtils.mapIndexToBlockBlockPartition(cfg.globalDim, mapping.size, localXOffset+x, localYOffset+localHeight)));
            }

        }

        public def swapBuffers() {
            val tmp = frontArray;   
            frontArray = backArray;
            backArray = tmp;
        }

        public def compute() {
            for (y in 0..(localHeight-1)) {
                for (x in 0..(localWidth-1)) {
                    val v = 0.25 * (back(x-1,y) + back(x+1,y) + back(x,y+1) + back(x,y-1));
                    front(x,y,v);
                }
            }
        }

        public def receiveLeft(home:Place, offset:Long, data:Rail[Double]) {
            //Console.OUT.println(here+" receiveLeft "+data.size+" offset "+offset+" from "+home);
            for (i in 0..(localHeight-1)) {
                val index = i+localYOffset - offset;
                if (index >=0 && index < data.size) front(-1, i, data(index));
            }
        }
        public def receiveRight(home:Place, offset:Long, data:Rail[Double]) {
            //Console.OUT.println(here+" receiveRight "+data.size+" offset "+offset+" from "+home);
            for (i in 0..(localHeight-1)) {
                val index = i+localYOffset - offset;
                //if (here.id==0) Console.OUT.println(index+" = "+data(index));
                if (index >=0 && index < data.size) front(localWidth, i, data(index));
            }
        }
        public def receiveAbove(home:Place, offset:Long, data:Rail[Double]) {
            //Console.OUT.println(here+" receiveAbove "+data.size+" offset "+offset+" from "+home);
            for (i in 0..(localWidth-1)) {
                val index = i+localXOffset - offset;
                if (index >=0 && index < data.size) front(i, -1, data(index));
            }
        }
        public def receiveBelow(home:Place, offset:Long, data:Rail[Double]) {
            //Console.OUT.println(here+" receiveBelow "+data.size+" offset "+offset+" from "+home);
            for (i in 0..(localWidth-1)) {
                val index = i+localXOffset - offset;
                if (index >=0 && index < data.size) front(i, localHeight, data(index));
            }
        }
    
        public def transmit(plh:PlaceLocalHandle[PlaceState]) {
            val x_offset = localXOffset;
            val y_offset = localYOffset;
            val left_column = new Rail[Double](localHeight, (i:Long)=>front(0,i));
            val right_column = new Rail[Double](localHeight, (i:Long)=>front(localWidth-1,i));
            val above_row = new Rail[Double](localWidth, (i:Long)=>front(i,0));
            val below_row = new Rail[Double](localWidth, (i:Long)=>front(i,localHeight-1));
            val home = here;
            for (p in leftPlaces) at (p) async {
                plh().receiveRight(home, y_offset, left_column);
            }
            for (p in rightPlaces) at (p) async {
                plh().receiveLeft(home, y_offset, right_column);
            }
            for (p in abovePlaces) at (p) async {
                plh().receiveBelow(home, x_offset, above_row);
            }
            for (p in belowPlaces) at (p) async {
                plh().receiveAbove(home, x_offset, below_row);
            }
        }
    
        public def printHeatArray() {
          for (y in -1..(localHeight)) {
            for (x in -1..(localWidth)) {
              Console.OUT.printf("%1.4f ", front(x,y));
            }
            Console.OUT.println();
          }
        }

    }


    public static def main(args:Rail[String]) {here==Place.FIRST_PLACE} {

        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration"),
            Option("r","verbose","print out final result"),
            Option("h","help","this information")
        ], [
            Option("i","iterations","number of iterations"),
            Option("k","kill","kill place 1 at this iteration"),
            Option("W","width","width of grid"),
            Option("H","height","height of grid")
        ]);
        if (opts.filteredArgs().size!=0) {
            Console.ERR.println("Unexpected arguments: "+opts.filteredArgs());
            Console.ERR.println("Use -h or --help.");
            System.setExitCode(1n);
            return;
        }
        if (opts("-h")) {
            Console.OUT.println(opts.usage(""));
            return;
        }

        val cfg = new Config();
        cfg.quiet = opts("-q");
        cfg.verbose = opts("-v");
        cfg.result = opts("-r");
        cfg.killTest = opts("-k",-1);
        cfg.globalWidth = opts("-W",1000);
        cfg.globalHeight = opts("-H",1000);
        cfg.globalDim = new DenseIterationSpace_2(0, 0, cfg.globalWidth-1, cfg.globalHeight-1);
        cfg.iterations = opts("-i",10);

        Console.OUT.println(cfg);

        var before : Long;
        var after : Long;

        val plh = PlaceLocalHandle.make[PlaceState](Place.places(), ()=>new PlaceState(cfg));

        val active_places = new Rail[Long](Place.numPlaces(), (i:Long)=>i);

        finish for (active_id in active_places.range()) {
            if (active_places(active_id)==-1) continue;
            at (Place(active_places(active_id))) async plh().assignPartition(active_places);
        }


        var iterationCounter : Long = 0;
        
        before = System.nanoTime();
        for (iter in 1..cfg.iterations) {
            try {
                finish for (active_id in active_places.range()) {
                    if (active_places(active_id)==-1) continue;
                    at (Place(active_places(active_id))) async {
                        val state = plh();
                        state.swapBuffers();
                    }
                }
                finish for (active_id in active_places.range()) {
                    if (active_places(active_id)==-1) continue;
                    at (Place(active_places(active_id))) async {
                        val state = plh();
                        state.compute();
                        state.transmit(plh);
                    }
                }
            } catch (es:MultipleExceptions) {
                val deadPlaceExceptions = es.getExceptionsOfType[DeadPlaceException]();
                for (dpe in deadPlaceExceptions) {
                    Console.OUT.println(dpe);
                    // TODO recovery
                }
                val filtered = es.filterExceptionsOfType[DeadPlaceException]();
                if (filtered != null) throw filtered;
            }
            if (cfg.verbose) outputAcrossAllPlaces(plh, iter);
            iterationCounter++;
        }
        after = System.nanoTime();

        Console.OUT.println("Computation finished.  "+iterationCounter+" iterations.\n");
        Console.OUT.println("Time in seconds: " + (after-before)/1E9);

        
        if (cfg.result) {
            Console.OUT.println("Below is the final heat array:");
            outputAcrossAllPlaces(plh, iterationCounter);
        }
    }

    private static def outputAcrossAllPlaces(plh:PlaceLocalHandle[PlaceState], iter:Long) {
        Console.OUT.println("Total Iterations: "+iter);
        for (p in Place.places()) {
            Console.OUT.println("Heat Array at "+p);
            at (p) plh().printHeatArray();
            Console.OUT.println();
        }
    }
}


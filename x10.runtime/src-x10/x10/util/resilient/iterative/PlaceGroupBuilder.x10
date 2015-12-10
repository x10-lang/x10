/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2014.
 */

package x10.util.resilient.iterative;

import x10.util.ArrayList;

/**
 * A utility class to test the use of arbitrary place groups in the GML library
 */
public class PlaceGroupBuilder {

    static val mode = getEnvLong("X10_PLACE_GROUP_RESTORE_MODE", RESTORE_MODE_SHRINK);
    private static val VERBOSE = (System.getenv("DEBUG_RESILIENT_EXECUTOR") != null 
                          && System.getenv("DEBUG_RESILIENT_EXECUTOR").equals("1"));
    
    static val RESTORE_MODE_SHRINK = 0;
    static val RESTORE_MODE_REPLACE_REDUNDANT = 1;
    
    static def getEnvLong(name:String, defValue:Long) {
        val env = System.getenv(name);
        val v = (env!=null) ? Long.parseLong(env) : defValue;
        return v;
    }

    public static def createRestorePlaceGroup(oldPlaceGroup:PlaceGroup):RestorePlaceGroup {
        if (VERBOSE){
            var str:String = "";
            for (p in oldPlaceGroup){
                str += p.id + ",";
            }
            Console.OUT.println(">>> createRestorePlaceGroup: oldPlaceGroup is ["+str+"] ...");
        }
        //RESTORE_MODE_SHRINK//
        if (mode == RESTORE_MODE_SHRINK) {
            if (VERBOSE) Console.OUT.println("Shrinking oldPlaceGroup ...");
            return new RestorePlaceGroup(oldPlaceGroup.filterDeadPlaces(), new ArrayList[Place]());            
        }
        
        
        
        //RESTORE_MODE_REPLACE_REDUNDANT//
        var maxUsedPlaceId:Long = -1;
        for (p in oldPlaceGroup){
            if (p.id > maxUsedPlaceId){
                maxUsedPlaceId = p.id;
            }
        }
        
        if (VERBOSE) Console.OUT.println("Max used place id is: ["+maxUsedPlaceId+"] ...");
        
        if (maxUsedPlaceId == Place.numPlaces()-1) {
            Console.OUT.println("[PlaceGroupBuilder Log] WARNING: No spare places available, forcing SHRINK mode ...");
            return new RestorePlaceGroup(oldPlaceGroup.filterDeadPlaces(), new ArrayList[Place]());
        }
        
        var spareIndex:Long = maxUsedPlaceId+1;
        val group = new x10.util.ArrayList[Place]();
        val addedSparePlaces = new x10.util.ArrayList[Place]();
        var deadCount:Long = 0;
        var allocated:Long = 0;
        for (p in oldPlaceGroup){
            if (p.isDead()){
                deadCount++;
                if (spareIndex < Place.numPlaces()){
                    if (VERBOSE) Console.OUT.println("adding place at spareIndex["+spareIndex+"] because p["+p.id+"] is dead ");
                    val spareP = Place(spareIndex++); 
                    group.add(spareP);
                    addedSparePlaces.add(spareP);
                    allocated++;
                }
            }
            else{
                if (VERBOSE) Console.OUT.println("adding place p["+p.id+"]");
                group.add(p);
            }
        }
        Console.OUT.println("[PlaceGroupBuilder Log] "+ deadCount +" Dead Place(s) Replaced by "+allocated+" Spare Places"); 
        return new RestorePlaceGroup(new SparsePlaceGroup(group.toRail()), addedSparePlaces);        
    }

    public static def execludeSparePlaces(sparePlaces:Long):PlaceGroup {
        val livePlaces = new x10.util.ArrayList[Place]();
        val allPlaces = Place.places();
        val inPlacesCount = allPlaces.size() - sparePlaces;
        for (var i:Long = 0; i < inPlacesCount; i++){
            livePlaces.add(allPlaces(i));
        }
        var placeGroup:SparsePlaceGroup = new SparsePlaceGroup(livePlaces.toRail());
        return placeGroup;
    }
    
    public static def execludePlace(placeId:Long):PlaceGroup {
        val livePlaces = new x10.util.ArrayList[Place]();
        val allPlaces = Place.places();
        for (var i:Long = 0; i < Place.numPlaces(); i++){
            if (i != placeId)
                livePlaces.add(Place(i));
        }
        var placeGroup:SparsePlaceGroup = new SparsePlaceGroup(livePlaces.toRail());
        return placeGroup;
    }
}

class RestorePlaceGroup {
    val newGroup:PlaceGroup;
    val newAddedPlaces:ArrayList[Place];
    public def this(g:PlaceGroup, n:ArrayList[Place]){
        this.newGroup = g;
        this.newAddedPlaces = n;
    }
}
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

package x10.matrix.util;

/**
 * A utility class to test the use of arbitrary place groups in the GML library
 */
public class PlaceGroupBuilder {

    static val mode = getEnvLong("X10_PLACE_GROUP_RESTORE_MODE", RESTORE_MODE_SHRINK);
    static val elasticMaxWaitTime = getEnvLong("X10_ELASTIC_MAX_WAIT_SECONDS", 1);
    
    static val RESTORE_MODE_SHRINK = 0;
    static val RESTORE_MODE_REPLACE_REDUNDANT = 1;
    static val RESTORE_MODE_REPLACE_ELASTIC = 2;
    
    static def getEnvLong(name:String, defValue:Long) {
        val env = System.getenv(name);
        val v = (env!=null) ? Long.parseLong(env) : defValue;
        return v;
    }

    public static def createRestorePlaceGroup(oldPlaceGroup:PlaceGroup):PlaceGroup {
        if (mode == RESTORE_MODE_SHRINK) {
            return oldPlaceGroup.filterDeadPlaces();            
        }
        
        val newPlaces = new x10.util.ArrayList[Place]();
        
        var deadCount:Long = 0;
        for (p in oldPlaceGroup){
            if (p.isDead())
                deadCount++;
            else
                newPlaces.add(p);
        }
        
        if (mode == RESTORE_MODE_REPLACE_ELASTIC) {
            try{
                System.addPlacesAndWait(deadCount, elasticMaxWaitTime*1000);
            }
            catch (ex:Exception){
                Console.OUT.println("[PlaceGroupBuilder Log] Creating New Places Failed ...");
                ex.printStackTrace();
            }
        }
        
        //RESTORE_MODE_REPLACE_ELASTIC and RESTORE_MODE_REPLACE_REDUNDANT//
        val allPlaces = Place.places();
        val allPlacesCount = allPlaces.size();
        val filteredOldPG = oldPlaceGroup.filterDeadPlaces();
        val sparePlaces = allPlacesCount - filteredOldPG.size();
        if (sparePlaces > 0) {
            val maxUsedPlaceId = filteredOldPG(filteredOldPG.size()-1).id;
            var allocated:Long = 0;
            var i:Long = 0;
            
            while (i < allPlacesCount && allPlaces(i).id <= maxUsedPlaceId){
                i++;
            }
            
            while (allocated < deadCount && i < allPlacesCount){
                if (!allPlaces(i).isDead()){                    
                    newPlaces.add(allPlaces(i));
                    allocated++;
                }
                i++;
            }
            Console.OUT.println("[PlaceGroupBuilder Log] "+ deadCount +" Dead Place(s) Replaced by "+allocated+" Spare Places");      
            return new SparsePlaceGroup(newPlaces.toRail());
        }
        else {
            Console.OUT.println("[PlaceGroupBuilder Log] WARNING: No spare places available, forcing SHRINK mode ...");
            return oldPlaceGroup.filterDeadPlaces();
        }
    }

    public static def makeTestPlaceGroup(sparePlaces:Long):PlaceGroup {
        val livePlaces = new x10.util.ArrayList[Place]();
        val allPlaces = Place.places();
        val inPlacesCount = allPlaces.size() - sparePlaces;
        for (var i:Long = 0; i < inPlacesCount; i++){
            livePlaces.add(allPlaces(i));
        }
        var placeGroup:SparsePlaceGroup = new SparsePlaceGroup(livePlaces.toRail());
        return placeGroup;
    }
}
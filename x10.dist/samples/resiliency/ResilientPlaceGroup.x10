/*
 * THIS CLASS IS NOT USD ANY MORE, USE x10.lang.SparsePlaceGroup
 */

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
import x10.util.*;

/**
 * ResilientPlaceGroup which can contain arbitrary places
 * @author kawatiya
 */
public class ResilientPlaceGroup extends PlaceGroup {
    private places = new ArrayList[Place]();
    private def add(pl:Place) { // should only be called from constructor
        if (!places.contains(pl)) places.add(pl);
    }
    // private def remove(pl:Place) = places.remove(pl); // not used, PG should be immutable
    def this(numPlaces:Long)       { for (id in 0..(numPlaces-1)) add(Place(id)); }
    def this(pg:PlaceGroup)        { for (pl in pg)  add(pl); }
    def this(pls:Rail[Place])      { for (pl in pls) add(pl); }
    def this(pls:ArrayList[Place]) { for (pl in pls) add(pl); }
    def this(ids:Rail[Long])       { for (id in ids) add(Place(id)); }
    def this(ids:ArrayList[Long])  { for (id in ids) add(Place(id)); }
    
    public operator this(i:long):Place = places.get(i);
    public def numPlaces() = places.size();
    public def contains(id:long) = places.contains(Place(id));
    public def indexOf(id:long) = places.indexOf(Place(id));
    public def iterator():Iterator[Place]{self!=null} = places.iterator();
    
    public def equals(thatObj:Any):Boolean {
        if (!(thatObj instanceof PlaceGroup)) return false;
        val thatPg = thatObj as PlaceGroup;
        if (numPlaces() != thatPg.numPlaces()) return false;
        for (pl in thatPg) if (!this.contains(pl.id)) return false;
        return true;
    }
    public def hashCode() = numPlaces().hashCode(); // should be compatible with other PlaceGroups
    // public def broadcastFlat(cl:()=>void) // use default implementation in PlaceGroup.x10
}

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *  (C) Copyright Australian National University 2010.
 */

package x10.lang;

import x10.compiler.Pragma;
import x10.io.Serializer;
import x10.io.Deserializer;
import x10.util.ArrayList;

/**
 * <p> A PlaceGroup represents an ordered set of Places.
 * PlaceGroups are represented by a specialized set of classes (instead of using
 * arbitrary collection types) because it is necessary for performance/scalability
 * to have optimized representations of specific special cases.  The API is also 
 * designed to efficiently support the operations needed by Team and DistArray.
 *
 * @see Place
 * @see x10.util.Team
 */
public abstract class PlaceGroup implements Iterable[Place] {

  /**
   * The size of the PlaceGroup is equal to the value returned by numPlaces()
   */
  public final property def size():Long = numPlaces();

  /**
   * @return the number of Places in the PlaceGroup
   */
  public abstract def numPlaces():Long;

  /**
   * @return true if the PlaceGroup contains place, false otherwise
   */
  public def contains(place:Place):Boolean = contains(place.id);

  /**
   * @return true if the PlaceGroup contains the place with
   *  the given id, false otherwise
   */
  public abstract def contains(id:Long):Boolean;

  /**
   * <p>If the argument place is contained in the PlaceGroup
   * return a long between 0 and numPlaces()-1 that is the
   * ordinal number of the Place in the PlaceGroup. 
   * If the argument place is not contained in the PlaceGroup,
   * then return -1.</p>
   *
   * <p>If the PlaceGroup pg contains place, then the invariant
   * <code>pg(indexOf(place)).equals(place) == true</code> holds.</p>
   * 
   * @return the index of place
   */
  public def indexOf(place:Place):Long = indexOf(place.id);

  /**
   * <p>If the Place with id equal to id is contained in the PlaceGroup
   * return a long between 0 and numPlaces()-1 that is the
   * ordinal number of said Place in the PlaceGroup. 
   * If the argument place is not contained in the PlaceGroup,
   * then return -1.</p>
   *
   * <p>If the PlaceGroup pg contains the argument Place, 
   * then the invariant
   * <code>pg(indexOf(id)).equals(Place(id)) == true</code> holds.</p>
   * 
   * @return the index of the Place encoded by id
   */
  public abstract def indexOf(id:Long):Long;

  /**
   * Return the Place with ordinal number <code>i</code> in the place group
   *
   * @param i the ordinal number of the desired place
   * @return the ith place in the place group
   */
  public abstract operator this(i:Long):Place;

  /**
   * Return the next Place in iteration order from
   * the argument Place, with a wrap around to the 
   * first Place in iteration order if the argument 
   * Place is the last Place in iteration order.
   */
  public def next(p:Place):Place {
      val idx = indexOf(p);
      if (idx == -1) return Place.INVALID_PLACE;
      val nIdx = (idx + 1 == numPlaces()) ? 0 : idx+1;
      return this(nIdx);
  }

  /**
   * Return the previous Place in iteration order from
   * the argument Place, with a wrap around to the 
   * last Place in iteration order if the argument 
   * Place is the first Place in iteration order.
   */
  public def prev(p:Place):Place {
      val idx = indexOf(p);
      if (idx == -1) return Place.INVALID_PLACE;
      val pIdx = (idx == 0) ? numPlaces()-1 : idx-1;
      return this(pIdx);    
  }

  /**
   * Two place groups are equal iff they contain the same places
   */
  public def equals(thatObj:Any):Boolean {
    if (this == thatObj) return true;
    if (!(thatObj instanceof PlaceGroup)) return false;
    val that = thatObj as PlaceGroup;
    if (numPlaces() != that.numPlaces()) return false;
    for (var i:Long=0; i<numPlaces(); i++) {
      if (!this(i).equals(that(i))) return false;
    }
    return true;
  }

  /**
   * Execute the closure cl at every place in the PlaceGroup.
   * Note: cl must not have any exposed at/async constructs
   *    (any async/at must be nested inside of a finish).
   */
  public def broadcastFlat(cl:()=>void) {
    val ser = new Serializer();
    ser.writeAny(cl);
    ser.addDeserializeCount(this.size()-1);
    val message = ser.toRail();
    @Pragma(Pragma.FINISH_SPMD) finish for (p in this) {
      at (p) async {
          val dser = new x10.io.Deserializer(message);
          val cls = dser.readAny() as ()=>void;
          cls();
      };
    }
  }

  /**
   * Execute the closure cl at every live place in the PlaceGroup.
   * Note: cl must not have any exposed at/async constructs
   *    (any async/at must be nested inside of a finish).
   */
  public def broadcastFlat(cl:()=>void, ignoreIfDead:(Place)=>Boolean) {
    val ser = new Serializer();
    ser.writeAny(cl);
    ser.addDeserializeCount(this.size()-1);
    val message = ser.toRail();
    var numSkipped:Long = 0;
    @Pragma(Pragma.FINISH_SPMD) finish for (p in this) {
      if (!p.isDead() || !ignoreIfDead(p)) {
          at (p) async {
              val dser = new x10.io.Deserializer(message);
              val cls = dser.readAny() as ()=>void;
              cls();
          };
      } else {
          numSkipped++;
      }
    }
    if (numSkipped > 0) {
        for (1..numSkipped) {
            // TODO: Resilient X10.
            // We skipped some places, so need to adjust the
            // ref counts. The simplest way to do this is to simply
            // deserialize the object graph here and discard it.
            // There should be a more efficient way to accomplish this by directly 
            // adjusting the weights (perhaps count the number of skipped places and
            // then adjust weights by (#drop/size) at the end of the finish?)
            val dser = new x10.io.Deserializer(message);
            val cls = dser.readAny() as ()=>void;
        }    
    }
  }

    /** 
     * Return a new PlaceGroup which contains all places from this group
     * that are not dead places.
     */
    public def filterDeadPlaces():PlaceGroup {
        val livePlaces = new ArrayList[Place]();

        for (pl in this) {
            if (!pl.isDead()) livePlaces.add(pl);
        }

        return new SparsePlaceGroup(livePlaces.toRail());
    }

  public static class SimplePlaceGroup extends PlaceGroup {
    private val numPlaces:Long;
    def this(numPlaces:Long) { this.numPlaces = numPlaces; }
    public operator this(i:Long):Place = Place(i);
    public def numPlaces() = numPlaces;
    public def contains(id:Long) = id >= 0 && id < numPlaces;
    public def indexOf(id:Long) = contains(id) ? id : -1;
    public def iterator():Iterator[Place]{self!=null} = new Iterator[Place](){
      private var i:Long = 0;
      public def hasNext() = i < numPlaces;
      public def next() = Place(i++);
    };
    public def equals(thatObj:Any):Boolean {
        if (thatObj instanceof SimplePlaceGroup) {
            return numPlaces() == (thatObj as SimplePlaceGroup).numPlaces();
        } else {
            return super.equals(thatObj);
        }
    }
    public def hashCode() = numPlaces.hashCode();
    public def broadcastFlat(cl:()=>void) {
        if (numPlaces() >= 1024) {
            val ser = new Serializer();
            ser.writeAny(cl);
            ser.addDeserializeCount(this.size()-1);
            val message = ser.toRail();
            @Pragma(Pragma.FINISH_SPMD) finish for(var i:Long=numPlaces()-1; i>=0; i-=32) {
                at (Place(i)) async {
                    val max = here.id;
                    val min = Math.max(max-31, 0);
                    @Pragma(Pragma.FINISH_SPMD) finish for (var j:Long=min; j<=max; ++j) {
                        at (Place(j)) async {
                            val dser = new x10.io.Deserializer(message);
                            val cls = dser.readAny() as ()=>void;
                            cls();
                       }
                    }
                }
            }
        } else {
            super.broadcastFlat(cl);
        }
    }
  }

  public static make(numPlaces:Long) = new SimplePlaceGroup(numPlaces);
}

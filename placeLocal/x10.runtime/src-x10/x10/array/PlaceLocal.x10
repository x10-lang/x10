// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array; // for now

/**
 * A simple implementation of place-local storage. This implementation
 * uses a sparsely populated ValRail (one entry per Place.MAX_PLACES)
 * to store the local data, and therefore it does not scale.
 *
 * A better implementation would use a HashMap keyed on the place
 * instead of a rail to store the local data, but this must be a value
 * (since it would store data for every place), so that would require
 * a ValHashMap. This would still have the problem of storing local
 * values everywhere, which is unlikely to be helpful.
 * 
 * An even better implementation would use a per-place static HashMap
 * keyed on a unique id for this PlaceLocal object to store only
 * place-local data for that place (on the assumption that it is
 * usually needed only in that place), but the language currently does
 * not support static place-local.
 *
 * Ultimately in any case this will have a native implementation that
 * supports very large numbers of places efficiently. Such an
 * implementation may also provide, and use here, a special-case
 * representation for Iterable[Place] to efficiently support very
 * large numbers of places.
 */

final value class PlaceLocal[T] implements ()=>T {


    // would like Iterable[Place] but XTENLANG-164 prevents that

    static def make[T](places: ValRail/*Iterable*/[Place], init: ()=>T)
        = new PlaceLocal[T](places, init);
        
    public def apply():T = local(here.id);

    //
    //
    //

    private val local: ValRail[T];

    private def this(places: ValRail/*Iterable*/[Place], init: ()=>T) {

        // construct a sparsely populated rail
        val rail = Rail.makeVar[T](Place.MAX_PLACES);
        for (p:Place in places) {
            rail(p.id) = at (p) init();
        }

        // make an immutable value copy
        this.local = Rail.makeVal[T](rail.length, (i:Int) => rail(i));
    }

}

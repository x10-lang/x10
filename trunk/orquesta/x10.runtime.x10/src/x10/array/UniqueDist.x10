package x10.array;

import java.util.Map;

import x10.lang.*;


public class UniqueDist extends BaseDist {

    UniqueDist() {

        super(Region.makeRectangular(0,place.MAX_PLACES-1), true, false, null);

        // places
        places = new place[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            places[i] = place.factory.place(i);

        // regions
        regions = new Region[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++) {
            regions[i] = Region.makeRectangular(i, i);
        }

        // region map
        initRegionMap();
    }
}

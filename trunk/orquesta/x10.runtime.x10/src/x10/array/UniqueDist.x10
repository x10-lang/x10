package x10.array;

import x10.lang.*;

import java.util.Map;

public class UniqueDist extends BaseDist {

    UniqueDist() {

        // places
        places = new place[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++)
            places[i] = place.factory.place(i);

        // regions
        regions = new Region[place.MAX_PLACES];
        for (int i=0; i<place.MAX_PLACES; i++) {
            regions[i] = Region.makeRectangular(i, i);
        }

        // overall region
        region = Region.makeRectangular(0, place.MAX_PLACES-1);

        // region map
        initRegionMap();
    }
}

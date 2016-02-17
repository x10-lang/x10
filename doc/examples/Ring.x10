class Ring {
    val places: PlaceGroup;

    public def this (places: PlaceGroup) {
        this.places = places;
    }

    public operator at(body: ()=>void) {
        at(places.next(here)) { body(); }
    }

    public static def main(Rail[String]) {
        val r = new Ring(Place.places());
        r.at() {
	    Console.OUT.println("Hello from "+here+"!");
	    r.at() {
		Console.OUT.println("Hello from "+here+"!");
	    }
        }
    }
}

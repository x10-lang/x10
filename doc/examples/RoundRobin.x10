class RoundRobin {
    val places: GlobalRef[PlaceGroup];
    private val current =
	new GlobalRef[Cell[Place]](new Cell[Place](here));

    public def this (places: PlaceGroup) {
        this.places = new GlobalRef[PlaceGroup](places);
    }

    public operator at(body: ()=>void) {
        val p = current.evalAtHome((there:Cell[Place]) => {
                                     val next: Place;
                                     atomic {
				       next = places().next(current()());
                                       current()() = next;
                                     }
				     next });
        at(p) { body(); }
    }

    public static def main(Rail[String]) {
        val rr = new RoundRobin(Place.places());
        rr.at() {
	    Console.OUT.println("Hello from "+here+"!");
        }
    }
}

class Owned(Owned owner) {
    Owned(:owner==o)(final Owned o) { property(o); }
    boolean owns(Owned o) {
        return this == world || this == o.owner || this.owns(o.owner);
    }
    static final Owned(null) world = new Owned(null);
}

class List(Owned(:owns(owner)) valOwner) extends Owned {
    Owned(:owner==valOwner) head;
    List(:owner==this & valOwner==this.valOwner) tail;
    List(:owner==o & valOwner==v)(Owned o, Owned v: o.owns(v)) {
        super(o);
        property(v);
    }
    List(:owner==this & valOwner==this.valOwner) tail() {
        return tail;
    }
}

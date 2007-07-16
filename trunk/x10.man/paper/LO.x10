class Owned(Owned owner) { }

class List(Owned valOwner
         : owner contains valOwner)
    extends Owned {
  Owned(valOwner) head;
  List(this, valOwner) tail;

  List(:owner=o, valOwner=v, o contains v)
      (Owned o, Owned v: o contains v) {
    super(o);
    property(v);
  }

  List(this, valOwner) expose() {
    return tail;
  }

  ...
}

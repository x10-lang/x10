class Owned(Owned owner) { }

class List(Owned valOwner
         : owner owns valOwner)
    extends Owned {
  Owned(valOwner) head;
  List(this, valOwner) tail;

  List(:owner=o, valOwner=v, o owns v)
      (Owned o, Owned v: o owns v) {
    super(o);
    property(v);
  }

  List(this, valOwner) expose() {
    return tail;
  }

  ...
}

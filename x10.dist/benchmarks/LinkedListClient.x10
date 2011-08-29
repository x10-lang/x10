
interface ListIterator {
	public def hasNext() : Boolean;
	public def next() : Object;
	public def hasPrev() : Boolean;
	public def prev() : Object;
	public def set(o:Object) : void;
}

abstract class AbsList {
	public atomic var size:Int = 0;               //the size is an atomic field
	public def size() : Int {return size;}
	public abstract def iterator() : ListIterator;
	public abstract def add(o:Object) : void;
	public abstract def addAll(unitfor c:AbsList) : Boolean;   //add c as a part of this unit
	public abstract def get(i:Int) : Object;
}

class Entry {
	atomic var elem:Object;      //an atomic field
	var next:(atomicplus Entry); //merge the atomic info
	var prev:(atomicplus Entry); //merge the atomic info
	public def this(e:Object, n:(atomicplus Entry), p:(atomicplus Entry)) { //merge the atomic info
		elem = e;
		next = n;
		prev = p;
	}
}

class LinkedList extends AbsList {
	private var header:(atomicplus Entry);   //merge the atomics
	
	public def this() {
		header = new (atomicplus Entry)(null, null, null);  //merge the atomics
		header.next = header;
		header.prev = header;
	}
	
	public atomic def add(o:Object):void {
		//merge the atomics
		var newEntry:(atomicplus Entry) = new (atomicplus Entry)(o, header, header.prev);
		newEntry.prev.next = newEntry;
		newEntry.next.prev = newEntry;
		size++;
	}
	
    public /*it should have one*/ def get(index:Int):Object {
    	if(index < 0 || index >= size()) {
    		throw new AssertionError();
    	}
    	var e:(atomicplus Entry) = header; //merge the atomics
    	for(var i:Int = 0; i <= index; i++) {
    		e = e.next;
    	}
        return e.elem;
    }
    public atomic def iterator():ListIterator {
        return new (atomicplus ListItr)(this, this.header, 0) as ListIterator;  //need to remove
    }
    public atomic def addAll(unitfor c:AbsList) : Boolean {  //expand the unit
        var modified : Boolean = false;
        var e : ListIterator = c.iterator();
        while (e.hasNext()) {
        	add(e.next());
        	modified = true;
        }
        return modified;
    }
    
    //implements equals / hashCode
    public atomic def hashCode():Int {
    	var hashCode:Int = 1;
    	var i : ListIterator = iterator();
    	while(i.hasNext()) {
    		var obj:Object = i.next();
    		hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
    	}
    	return hashCode;
    }
    
    public atomic def equals(/*unitfor*/ o:Object) : Boolean {
    	if(o == this) {
    		return true;
    	}
    	if(o instanceof LinkedList) {
    		return false;
    	}
    	var e1 : ListIterator = iterator();
    	var e2 : ListIterator = (o as LinkedList).iterator();
    	while(e1.hasNext() && e2.hasNext()) {
    		var o1 : Object = e1.next();
    		var o2 : Object = e2.next();
    		if(!(o1 == null ? o2 == null : o1.equals(o2))) {
    			return false;
    		}
    	}
    	
    	return !(e1.hasNext() || e2.hasNext());
    }
}

class ListItr implements ListIterator {
	
	var lastReturned : (atomicplus Entry);  //merge the atomics
	var next : (atomicplus Entry);    //merge the atomics
	atomic var nextIndex : Int;       //an atomic field
	var list : (atomicplus LinkedList); //merge the atomics
	var header : (atomicplus Entry);    //merge the atomics
	
	public def this(l:(atomicplus LinkedList), h:(atomicplus Entry), index:Int) {
		list = l;
		header = h;
		lastReturned = header;
		if(index < 0 || index > list.size()) {
			throw new AssertionError();
		}
		next = header.next;
		for(nextIndex = 0; nextIndex < index; nextIndex++) {
			next = next.next;
		}
	}
	
	public atomic def hasNext() : Boolean {
		return nextIndex != list.size();
	}
	
	public atomic def next() : Object {
		if(nextIndex == list.size()) {
			throw new AssertionError();
		}
		lastReturned = next;
		next = next.next;
		nextIndex++;
		return lastReturned.elem;
	}

	public atomic def hasPrev() : Boolean {
		return nextIndex != 0;
	}
	
	public atomic def prev() : Object {
		if(nextIndex == 0) {
			throw new AssertionError();
		}
		lastReturned = next = next.prev;
		nextIndex --;
		return lastReturned.elem;
    }

    public atomic def set(var o : Object):void {
    	if(lastReturned == header) {
    		throw new AssertionError();
    	}
    	lastReturned.elem = o;
    }
}

public class LinkedListClient {
	public static def main(args : Array[String](1)) {
		var x : AbsList = new LinkedList();
		var y : AbsList = new LinkedList();
		y.add("a");
		y.add("a");
		var z : AbsList = new LinkedList();
		z.add("b");
		z.add("b");
		
		//initializes 2 activities
		finish {
			async {
				//atomic x.addAll(y);, that is the old fashion
				x.addAll(y);
			}
			async {
				//atomic x.addAll(z);, that is the old fashion
				x.addAll(z);
			}
		}
		
		//set the content
		var it:ListIterator = x.iterator();
		while(it.hasNext()) {
			var o: Object = it.next();
			if(o.equals("b")) {
				it.set("c");
			}
		}
		//see the content
		while(it.hasPrev()) {
			Console.OUT.println(it.prev());
		}
	}
}
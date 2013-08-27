package x10.lang;

public class X10TM {
	val places = new Array[Place](10);
	var num_places:int = 0;
	
	def addPlace(p:Place) {
		for (var i:int=0; i < num_places; i++) {
			if (places(i) == p) {
				return;
			}
		}
		
		places(num_places) = p;
		num_places++;
	}
	
	def finish_commits() {
		for (var i:int=0; i < num_places; i++) {
			at (places(i)) {
				Runtime.initTMThread();
			}
		}
	}
	
	def fail_commits() {
		for (var i:int=0; i < num_places; i++) {
			at (places(i)) {
				Runtime.initTMThread();
			}
		}
	}
}
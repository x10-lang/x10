public class HeavySyncExample {
	
	public static def atomic_method(hse:HeavySyncExample) {
		atomic(hse) {
			var str:String = "";
			val R=0..10000;
			for(i in R) {
				str = str + i + " ";
			}
		}
	}
	
	public static def main(args: Array[String](1)) {
		var c:Int = 20;
		val arrays:Array[HeavySyncExample]  = new Array[HeavySyncExample](c);
		val R = 0..(c-1);
		for(i in R){
			arrays(i) = new HeavySyncExample();
		}
		finish for(i in R) async {
			atomic_method(arrays(i));
		}
	}
	
}
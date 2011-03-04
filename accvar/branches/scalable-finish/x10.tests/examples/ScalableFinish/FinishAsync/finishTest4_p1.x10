import x10.compiler.*;
public class finishTest4_p1 {
     public def f1():void {
    	 // method contains async
    	 async{}
     }
     public def f2():void {
    	 // method contains at
    	 at(here){}
     } 
     public def run() {
 
    	 //TODO: test code
    	 @FinishAsync(0,0,true,1) finish{
    	 at(here){
    		 async{}
    	 }
    	 async{
    		 at(here){}
    	 }
        }
        async{
                @FinishAsync(0,0,true,1) finish{
                        at(here){}
                }
                at(here){
                        @FinishAsync(0,0,true,1) finish{}
                        async{}
                }
        }
        at(here){
                @FinishAsync(0,0,true,1) finish{
                        async{}
                }
                async{
                        @FinishAsync(0,0,true,1) finish{
                                async{}
                        }
                }
        }

        at(here){
                async{}
                at(here){
                        async{}
                }
                f1();
        }
        async{
                async{
                        f2();
                }
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest4_p1().run();
	}
}


         

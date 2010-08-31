import x10.compiler.*;
public class finishTest6_p1 {

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

        var i:int = 1;
        for(i=1;i<10;i++){
                @FinishAsync(0,0,true,1) finish{
                        async{}
                }
                @FinishAsync(0,0,true,1) finish{
                        at(here){}
                }
                async{}
                at(here){}
        }
        for(i=1;i<10;i++){
                for(i=1;i<10;i++){
                        @FinishAsync(0,0,true,1) finish{
                                async{}
                        }
                        @FinishAsync(0,0,true,1) finish{
                                at(here){}
                        }
                        async{}
                        at(here){}
                }
                f2();
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest6_p1().run();
	}
}


         

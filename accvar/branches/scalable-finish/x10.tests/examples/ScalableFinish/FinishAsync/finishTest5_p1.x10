import x10.compiler.*;
public class finishTest5_p1 {

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
        @FinishAsync(0,0,true,1) finish{
                if(i>1){
                        async{}
                }
                else{
                        at(here){}
                }
                f1();
        }

        if(i<1){
                @FinishAsync(0,0,true,1) finish{
                        at(here){}
                        async{}
                }

                async{}
                at(here){}
        }
        
        @FinishAsync(0,0,true,1) finish{
                async{
                        var i:int =1;
                        if(i==0){
                                async{}
                        }
                        if(i==1){
                                at(here){}
                        }
                }
        }
        @FinishAsync(0,0,true,1) finish{
                at(here){
        
                        var i:int =1;
                        if(i==2){
                                at(here){
                        
                                       async{}
                                }
                                async{}
                        }
                        at(here){}

                }
        }
	}

	public static def main(args: Rail[String]) {
		new finishTest5_p1().run();
	}
}


         

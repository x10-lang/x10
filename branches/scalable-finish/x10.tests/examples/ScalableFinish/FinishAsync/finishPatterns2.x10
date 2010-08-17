import x10.compiler.*;
public class finishPatterns2 {
     public def f1():void {
    	 async{}
     }

     public def foo(body:()=>void){
             async{body();}
     }
     //the second 
     public def run() {
        {
                 val body:()=>void = ()=>{};
                 //foo(body);
                 async{body();}
        }
        {         val body:()=>void = ()=>{};
                  //foo(body);
                 async{body();}
        }


     }

     public def f6(p:int){
             async(Place.place(p)){}
     }


     public static def main(args: Rail[String]) {
    	 new finishPatterns2().run();
     }


 }




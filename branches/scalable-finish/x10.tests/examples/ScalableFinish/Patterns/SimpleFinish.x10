import x10.compiler.FinishAsync;
public class SimpleFinish {
    public static def main(args: Rail[String]!) throws Exception{

            
            //val p = here;
            /*
            @FinishAsync(1,1,false,0)
            finish{
            
                    async(p.next().next()){ 
                            Console.OUT.println(33);
                    }
                    async(p.next()){
                            async{
                                Console.OUT.println(55);
                            }
                            Console.OUT.println(11);
                    }
                    async(p.next()){ 
                            async{
                                Console.OUT.println(66);
                                async{
                                        Console.OUT.println(77);
                                }
                            }
                            Console.OUT.println(22);
                    } 
            }*/
            /*
            @FinishAsync(1,1,false,0)
            finish{
                    async(p.next()){}
            }
            */

           /* 
            @FinishAsync(1,1,false,3)
            finish {
                    for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            async(Place.places(p)){
                                    Console.OUT.println(here);
                             }
                     }
            
            }
            */

            /*
            @FinishAsync(1,1,false,3)
            finish {
                    for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            async(Place.places(p)){
                                    for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                        val i = pp;
                                        async{
                                                Console.OUT.println(here+":"+i);
                                        }
                                    }
                             }
                     }
            
            }
            */


            @FinishAsync(1,1,false,3)
            finish {
                    async{
                            Console.OUT.println("SPECIAL:"+here);
                    }
                    for(var p:int = 0; p<Place.MAX_PLACES; p++){
                            async(Place.places(p)){
                                    for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                        val i = pp;
                                        async{
                                                Console.OUT.println(here+":"+i);
                                        }
                                    }
                             }
                     }
                    async(Place.places(Place.MAX_PLACES/2)){
                            Console.OUT.println("SPECIAL:"+here);
                    }

                    for(var p:int = Place.MAX_PLACES-1; p>=0;p--){
                            async(Place.places(p)){
                                    for(var pp:int = 0; pp<Place.MAX_PLACES; pp++){
                                        val i = pp;
                                        async{
                                                Console.OUT.println(here+":"+i);
                                        }
                                    }
                             }
                    }
                    async(Place.places(Place.MAX_PLACES-1)){
                            Console.OUT.println("SPECIAL:"+here);
                    }
                    for(var p3:int = Place.MAX_PLACES-1; p3>=0;p3--){
                            async(Place.places(p3)){
                                        Console.OUT.println(here);
                             }
                    }


            }
            Console.OUT.println("OVER!");
     }
    /** x10doc comment for myMethod */
    public def myMethod(): boolean = {
       return true;
    }
}

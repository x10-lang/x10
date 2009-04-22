public class ArrayAccess1 {                                                                         
    public static def main(args: Rail[String]) = {                                                  
        val aa: ArrayAccess1 = new ArrayAccess1(Array.make[int]([0..5]));                           
        aa.init();                                                                                  
        aa.sum();                                                                                   
        aa.dump();                                                                                  
    }                                                                                               

    private val array: Array[int];                                                                  

    public def this(a: Array[int]) {                                                                
        array = a;                                                                                  
    }                                                                                               

    private def init() = {                                                                          
        for(val p(i): Point(array.rank) in array.region) {                                          
            array(p) = i;                                                                           
        }                                                                                           
    }                                                                                               

    private def sum(): int = {                                                                      
        var result: int = 0;                                                                        
        for(val p: Point(array.rank) in array.region) {                                             
            result += array(p);                                                                     
        }                                                                                           
        return result;                                                                              
    }                                                                                               

    private def dump() = {                                                                          
        for(val p: Point(array.rank) in array.region) {                                             
            Console.OUT.println(array(p).toString());                                               
        }                                                                                           
    }                                                                                               
}                                                                                                   

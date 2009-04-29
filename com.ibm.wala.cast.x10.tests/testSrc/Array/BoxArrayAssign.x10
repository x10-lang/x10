/**
 * 
 * 
 * @author shane
 * @author vj
 */
public class BoxArrayAssign {

    public def run(): boolean = {
        val table = Array.make[Box[Value]](1..5->here, (Point)=>(null as Box[Value]));
        foreach (val p: Point(1) in table) table(p) = null;
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new BoxArrayAssign().run();
    }

}

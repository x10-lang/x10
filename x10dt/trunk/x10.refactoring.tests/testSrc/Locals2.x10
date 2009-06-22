public class Effects2 {
    public def test() = {
        var z: int;
        {
            /*<<<*/
            val x= 10;
            val y= 15;
            z = x * y;
            /*>>>*/
        }
    }
}

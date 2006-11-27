package lpg.lpgjavaruntime;
import java.util.ArrayList;

public class UnimplementedTerminalsException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    ArrayList symbols;

    public UnimplementedTerminalsException(ArrayList symbols)
    {
        this.symbols = symbols;
    }

    public ArrayList getSymbols() { return symbols; }
}

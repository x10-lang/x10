package lpg.lpgjavaruntime;

public class BadParseSymFileException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String str;
    
    public BadParseSymFileException()
    {
        str = "BadParseSymFileException";
    }
    public BadParseSymFileException(String str)
    {
        this.str = str;
    }
    public String toString() { return str; }
}

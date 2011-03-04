package lpg.lpgjavaruntime;

public class DifferTokens extends Differ
{
    //
    // Class that encodes the meaningful content of a line.
    //
    public class Token implements ILine
    {
        private int kind,
                    start_line,
                    start_column,
                    end_line,
                    end_column;
        private String name;

        private int hash_code;

        public Token(PrsStream stream, int token)
        {
            this.kind = stream.getKind(token);
            this.name = stream.getName(token);
            this.start_line = stream.getLine(token);
            this.start_column = stream.getColumn(token);
            this.end_line = stream.getEndLine(token);
            this.end_column = stream.getEndColumn(token);
            hash_code = this.name.hashCode();
        }

        public final int size() { return name.length(); }

        public final int hashCode() { return hash_code; }

        public final boolean equals(Object anObject)
        {
            if (this == anObject)
                return true;

            if (anObject instanceof Token)
            {
                Token another_token = (Token) anObject;
                return (this.kind == another_token.kind && this.name.equals(another_token.name));
            }

            return false;
        }

        public final int getStartLine() { return start_line; }
        public final int getStartColumn() { return start_column; }
        public final int getEndLine() { return end_line; }
        public final int getEndColumn() { return end_column; }
        public final String toString() { return this.name; }
    }

    protected DifferTokens() {}
    
    public DifferTokens(PrsStream newStream, PrsStream oldStream)
    {
        super(newStream, oldStream);
    }

    public ILine[] getBuffer(PrsStream stream)
    {
        Token buffer[] = new Token[stream.getStreamLength()];
        for (int i = 1; i < stream.getStreamLength(); i++)
            buffer[i] = new Token(stream, i);
        return buffer;
    }

    //
    //
    //
    void printLines(PrsStream prs_stream, int first_token, int last_token)
    {
        int start_line = prs_stream.getLine(first_token),
            end_line = prs_stream.getLine(last_token);
        LexStream lex_stream = prs_stream.getLexStream();
        char buffer[] = lex_stream.getInputChars();

        //
        // THIS IS TEMPORARY code just to show proof of concept.
        // TODO: Ultimately, we want to change the color of the affected
        // section in an IDE such as Eclipse...
        //
        int start = lex_stream.getLineOffset(start_line - 1) + 1,
            end = lex_stream.getLineOffset(start_line);
        if (start_line == end_line)
        {
            String num = "" + start_line;
            for (int i = 0; i < (6 - num.length()); i++)
                System.out.print(' ');
            System.out.println(start_line + " " + new String(buffer, start, end - start));

            System.out.print("       "); // 7 spaces: 6 for number and 1 blank.
            for (int i = 0; i < prs_stream.getColumn(first_token) - 1; i++)
                System.out.print(' ');
            int length = prs_stream.getEndColumn(last_token) - prs_stream.getColumn(first_token);
            if (length < 2)
                System.out.println('^');
            else
            {
                System.out.print('<');
                for (int i = 1; i < length; i++)
                    System.out.print('-');
                System.out.println('>');
            }
        }
        else
        {
            System.out.print("       "); // 7 spaces: 6 for number and 1 blank.
            for (int i = 0; i < prs_stream.getColumn(first_token) - 1; i++)
                System.out.print(' ');
            int last_column = lex_stream.getColumn(lex_stream.getPrevious(end));
            System.out.print('<');
            for (int i = prs_stream.getColumn(first_token) + 1; i < last_column; i++)
                System.out.print('-');
            System.out.println();

            for (int line_no = start_line; line_no <= end_line; line_no++)
            {
                start = lex_stream.getLineOffset(line_no - 1) + 1;
                end = lex_stream.getLineOffset(line_no);
                String num = "" + line_no;
                for (int i = 0; i < (6 - num.length()); i++)
                    System.out.print(' ');
                System.out.println(line_no + " " + new String(buffer, start, end - start));

            }

            System.out.print("       "); // 7 spaces: 6 for number and 1 blank.
            for (int i = 0; i < prs_stream.getEndColumn(last_token) - 1; i++)
                System.out.print('-');
            System.out.println('>');
        }
    }

    //
    //
    //
    void outputInsert(Change element)
    {
        insertCount += (element.getNewe() - element.getNews() + 1);
        System.out.println("Insert after " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(element.getOlde()) +
                           ":" +
                           oldStream.getEndColumn(element.getOlde()) +
                           " " +
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(element.getNews()) +
                           ":" +
                           newStream.getColumn(element.getNews()) +
                           ".." +
                           newStream.getLine(element.getNewe()) +
                           ":" +
                           newStream.getEndColumn(element.getNewe()) +
                           ":");
        printLines(newStream, element.getNews(), element.getNewe());
    }

    //
    //
    //
    void outputDelete(Change element)
    {
        deleteCount += (element.getOlde() - element.getOlds() + 1);
        System.out.println("Delete after " + 
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(element.getNewe()) +
                           ":" +
                           newStream.getEndColumn(element.getNewe()) +
                           " " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(element.getOlds()) +
                           ":" +
                           oldStream.getColumn(element.getOlds()) +
                           ".." +
                           oldStream.getLine(element.getOlde()) +
                           ":" +
                           oldStream.getEndColumn(element.getOlde()) +
                           ":");
        printLines(oldStream, element.getOlds(), element.getOlde());
    }

    //
    //
    //
    void outputReplace(Change element)
    {
        replaceDeleteCount += (element.getOlde() - element.getOlds() + 1);
        replaceInsertCount += (element.getNewe() - element.getNews() + 1);
        System.out.println("Replace " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(element.getOlds()) +
                           ":" +
                           oldStream.getColumn(element.getOlds()) +
                           ".." +
                           oldStream.getLine(element.getOlde()) +
                           ":" +
                           oldStream.getEndColumn(element.getOlde()) +
                           ":");
        printLines(oldStream, element.getOlds(), element.getOlde());

        System.out.println("With " +
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(element.getNews()) +
                           ":" +
                           newStream.getColumn(element.getNews()) +
                           ".." +
                           newStream.getLine(element.getNewe()) +
                           ":" +
                           newStream.getEndColumn(element.getNewe()) +
                           ":");
        printLines(newStream, element.getNews(), element.getNewe());
    }

    //
    //
    //
    void outputMove(Change element)
    {
         moveCount += (element.getNewe() - element.getNews() + 1);
         System.out.println("Move " +
                            oldStream.getFileName() +
                            "," +
                            oldStream.getLine(element.getOlds()) +
                            ":" +
                            oldStream.getColumn(element.getOlds()) +
                            ".." +
                            oldStream.getLine(element.getOlde()) +
                            ":" +
                            oldStream.getEndColumn(element.getOlde()) +
                            " to " +
                            newStream.getFileName() +
                            "," +
                            newStream.getLine(element.getNews()) +
                            ":" +
                            newStream.getColumn(element.getNews()) +
                            ".." +
                            newStream.getLine(element.getNewe()) +
                            ":" +
                            newStream.getEndColumn(element.getNewe()) +
                            ":");
        printLines(newStream, element.getNews(), element.getNewe());
    }

    //
    //
    //
    void outputMoveDelete(Change element)
    {
        int bound = element.getNewe() - element.getNews();
        moveCount += (bound + 1);
        System.out.println("Move " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(element.getOlds()) +
                           ":" +
                           oldStream.getColumn(element.getOlds()) +
                           ".." +
                           oldStream.getLine(element.getOlds() + bound) +
                           ":" +
                           oldStream.getEndColumn(element.getOlds() + bound) +
                           " to " +
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(element.getNews()) +
                           ":" +
                           newStream.getColumn(element.getNews()) +
                           ".." +
                           newStream.getLine(element.getNewe()) +
                           ":" +
                           newStream.getEndColumn(element.getNewe()) +
                           ":");
        printLines(newStream, element.getNews(), element.getNewe());

        int oldi = element.getOlds() + bound + 1;
        deleteCount += (element.getOlde() - oldi + 1);
        System.out.println("... And delete " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(oldi) +
                           ":" +
                           oldStream.getColumn(oldi) +
                           ".." +
                           oldStream.getLine(element.getOlde()) +
                           ":" +
                           oldStream.getEndColumn(element.getOlde()));
        printLines(oldStream, oldi, element.getOlde());
    }

    //
    //
    //
    void outputMoveInsert(Change element)
    {
        int bound = element.getOlde() - element.getOlds();
        moveCount += (element.getOlde() - element.getOlds() + 1);
        System.out.println("Move " +
                           oldStream.getFileName() +
                           "," +
                           oldStream.getLine(element.getOlds()) +
                           ":" +
                           oldStream.getColumn(element.getOlds()) +
                           ".." +
                           oldStream.getLine(element.getOlde()) +
                           ":" +
                           oldStream.getEndColumn(element.getOlde()) +
                           " to " +
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(element.getNews()) +
                           ":" +
                           newStream.getColumn(element.getNews()) +
                           ".." +
                           newStream.getLine(element.getNews() + bound) +
                           ":" +
                           newStream.getEndColumn(element.getNews() + bound) +
                           ":");
        printLines(newStream, element.getNews(), element.getNews() + bound);

        int newi = element.getNews() + bound + 1;
        insertCount += (element.getNewe() - newi + 1);
        System.out.println("... And insert " +
                           newStream.getFileName() +
                           "," +
                           newStream.getLine(newi) +
                           ":" +
                           newStream.getColumn(newi) +
                           ".." +
                           newStream.getLine(element.getNewe()) +
                           ":" +
                           newStream.getEndColumn(element.getNewe()) +
                           ":");
        printLines(newStream, newi, element.getNewe());
    }
}

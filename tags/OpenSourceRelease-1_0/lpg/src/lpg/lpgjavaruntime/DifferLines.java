package lpg.lpgjavaruntime;

public class DifferLines extends Differ
{
    //
    // Class that encodes the meaningful content of a line.
    //
    public class Line implements ILine
    {
        private int kind[];
        private String name[];

        private int hash_code,
                    start_line,
                    start_column,
                    end_line,
                    end_column,
                    buffer_length;

        public Line(PrsStream stream, int start_token, int gate_token)
        {
            int size = (gate_token > start_token ? gate_token - start_token : 0),
                end_token = gate_token - 1;

            this.start_line = stream.getLine(start_token);
            this.start_column = stream.getColumn(start_token);
            if (size > 0)
            {
                this.end_line = stream.getEndLine(end_token);
                this.end_column = stream.getEndColumn(end_token);
            }
            else
            {
                this.end_line = stream.getLine(start_token);
                this.end_column = stream.getColumn(start_token);
            }
                    
            this.kind = new int[size];
            this.name = new String[size];

            hash_code = size;
            for (int i = 0; i < size; i++)
            {
                this.kind[i] = stream.getKind(start_token + i);
                this.name[i] = stream.getName(start_token + i);
                buffer_length += (this.name[i].length() + 1);
                hash_code += this.kind[i];
            }

            hash_code += buffer_length;
        }

        public final int size() { return kind.length; }

        public final int hashCode() { return hash_code; }

        public final boolean equals(Object anObject)
        {
            if (this == anObject)
                return true;

            if (anObject instanceof Line)
            {
                Line another_line = (Line) anObject;

                if (this.kind.length == another_line.kind.length)
                {
                    int i;
                    for (i = 0; i < kind.length; i++)
                    {
                        if (! (this.kind[i] == another_line.kind[i] && this.name[i].equals(another_line.name[i])))
                            break;
                    }
                    return i == this.kind.length;
                }
            }

            return false;
        }

        public final int getStartLine() { return start_line; }
        public final int getStartColumn() { return start_column; }
        public final int getEndLine() { return end_line; }
        public final int getEndColumn() { return end_column; }

        public final String toString()
        {
            StringBuffer buffer = new StringBuffer(buffer_length);
            if (name.length > 0)
            {
                buffer.append(name[0]);
                for (int i = 1; i < name.length; i++)
                {
                    buffer.append(" ");
                    buffer.append(name[i]);
                }
            }

            return buffer.toString();
        }
    }

    public DifferLines(PrsStream newStream, PrsStream oldStream)
    {
        super(newStream, oldStream);
    }

    protected DifferLines() {}

    public ILine[] getBuffer(PrsStream stream)
    {
        LexStream lex_stream = stream.getLexStream();
        Line buffer[] = new Line[lex_stream.getLineCount() + 1];

        int token = 1;
        buffer[0] = new Line(stream, 0, 0);
        for (int line_no = 1; line_no < buffer.length; line_no++)
        {
            int first_token = token;
            while (token < stream.getSize() && stream.getLine(token) == line_no)
                token++;
            buffer[line_no] = new Line(stream, first_token, token);
        }

        return buffer;
    }

    //
    //
    //
    void printLines(PrsStream prs_stream, int first_line, int last_line)
    {
        LexStream lex_stream = prs_stream.getLexStream();
        char buffer[] = lex_stream.getInputChars();

        for (int line_no = first_line; line_no <= last_line; line_no++)
        {
            int start = lex_stream.getLineOffset(line_no - 1) + 1,
                end = lex_stream.getLineOffset(line_no);
            String num = line_no + " ";
            for (int i = 0; i < (7 - num.length()); i++)
                System.out.print(' ');
            String line = num + new String(buffer, start, end - start);
            System.out.println(line);
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
                           oldBuffer[element.getOlde()].getStartLine() +
                           " " +
                           newStream.getFileName() +
                           "," +
                           newBuffer[element.getNews()].getStartLine() +
                           (newBuffer[element.getNewe()].getEndLine() > newBuffer[element.getNews()].getStartLine()
                                                                         ? (".." + newBuffer[element.getNewe()].getEndLine())
                                                                         : "") +
                           ":");
        printLines(newStream, newBuffer[element.getNews()].getStartLine(), newBuffer[element.getNewe()].getEndLine());
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
                           newBuffer[element.getNewe()].getStartLine() +
                           " " +
                           oldStream.getFileName() +
                           "," +
                           oldBuffer[element.getOlds()].getStartLine() +
                           (oldBuffer[element.getOlde()].getEndLine() > oldBuffer[element.getOlds()].getStartLine()
                                                                      ? (".." + oldBuffer[element.getOlde()].getEndLine())
                                                                      : "") +
                           ":");
        printLines(oldStream, oldBuffer[element.getOlds()].getStartLine(), oldBuffer[element.getOlde()].getEndLine());
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
                           oldBuffer[element.getOlds()].getStartLine() +
                           (oldBuffer[element.getOlde()].getEndLine() > oldBuffer[element.getOlds()].getStartLine()
                                                                      ? (".." + oldBuffer[element.getOlde()].getEndLine())
                                                                      : "") +
                           ":");
        printLines(oldStream, oldBuffer[element.getOlds()].getStartLine(), oldBuffer[element.getOlde()].getEndLine());

        System.out.println("With " +
                           newStream.getFileName() +
                           "," +
                           newBuffer[element.getNews()].getStartLine() +
                           (newBuffer[element.getNewe()].getEndLine() > newBuffer[element.getNews()].getStartLine()
                                                                      ? (".." + newBuffer[element.getNewe()].getEndLine())
                                                                      : "") +
                           ":");
        printLines(newStream, newBuffer[element.getNews()].getStartLine(), newBuffer[element.getNewe()].getEndLine());
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
                            oldBuffer[element.getOlds()].getStartLine() +
                            (oldBuffer[element.getOlde()].getEndLine() > oldBuffer[element.getOlds()].getStartLine()
                                                                       ? (".." + oldBuffer[element.getOlde()].getEndLine())
                                                                       : "") +
                            " to " +
                            newStream.getFileName() +
                            "," +
                            newBuffer[element.getNews()].getStartLine() +
                            (newBuffer[element.getNewe()].getEndLine() > newBuffer[element.getNews()].getStartLine()
                                                                       ? (".." + newBuffer[element.getNewe()].getEndLine())
                                                                       : "") +
                            ":");
        printLines(newStream, newBuffer[element.getNews()].getStartLine(), newBuffer[element.getNewe()].getEndLine());
    }

    //
    //
    //
    void outputMoveDelete(Change element)
    {
        moveCount += (element.getNewe() - element.getNews() + 1);
        int bound = newBuffer[element.getNewe()].getEndLine() - newBuffer[element.getNews()].getStartLine();
        System.out.println("Move " +
                           oldStream.getFileName() +
                           "," +
                           oldBuffer[element.getOlds()].getStartLine() +
                           (bound > 0 ? (".." + (oldBuffer[element.getOlds()].getStartLine() + bound)) : "") +
                           " to " +
                           newStream.getFileName() +
                           "," +
                           newBuffer[element.getNews()].getStartLine() +
                           (newBuffer[element.getNewe()].getEndLine() > newBuffer[element.getNews()].getStartLine()
                                                                      ? (".." + newBuffer[element.getNewe()].getEndLine())
                                                                      : "") +
                           ":");
        printLines(newStream, newBuffer[element.getNews()].getStartLine(), newBuffer[element.getNewe()].getEndLine());

        int oldi = oldBuffer[element.getOlds()].getStartLine() + bound + 1;
        deleteCount += (element.getOlde() - element.getOlds() - element.getNewe() + element.getNews());
        System.out.println("... And delete " +
                           oldStream.getFileName() +
                           "," +
                           oldi +
                           (oldBuffer[element.getOlde()].getEndLine() > oldi
                                                                      ? (".." + oldBuffer[element.getOlde()].getEndLine())
                                                                      : ""));
        printLines(oldStream, oldi, newBuffer[element.getOlde()].getEndLine());
    }

    //
    //
    //
    void outputMoveInsert(Change element)
    {
        moveCount += (element.getOlde() - element.getOlds() + 1);
        int bound = oldBuffer[element.getOlde()].getStartLine() - oldBuffer[element.getOlds()].getEndLine();
        System.out.println("Move " +
                           oldStream.getFileName() +
                           "," +
                           oldBuffer[element.getOlds()].getStartLine() +
                           (oldBuffer[element.getOlde()].getEndLine() > oldBuffer[element.getOlds()].getStartLine()
                                                                      ? (".." + oldBuffer[element.getOlde()].getEndLine())
                                                                      : "") +
                           " to " +
                           newStream.getFileName() +
                           "," +
                           newBuffer[element.getNews()].getStartLine() +
                           (bound > 0 ? (".." + newBuffer[element.getNews()].getStartLine() + bound) : "") +
                           ":");
        printLines(newStream, newBuffer[element.getNews()].getStartLine(), newBuffer[element.getNews()].getStartLine() + bound);

        int newi = newBuffer[element.getNews()].getStartLine() + bound + 1;
        insertCount += (element.getNewe() - element.getNews() - element.getOlde() + element.getOlds());
        System.out.println("... And insert " +
                           newStream.getFileName() +
                           "," +
                           newi +
                           (newBuffer[element.getNewe()].getEndLine() > newi
                                                                      ? (".." + newBuffer[element.getNewe()].getEndLine())
                                                                      : "") +
                           ":");
        printLines(newStream, newi, newBuffer[element.getNewe()].getEndLine());
    }
}

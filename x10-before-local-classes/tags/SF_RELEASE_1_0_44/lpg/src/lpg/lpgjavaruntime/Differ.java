package lpg.lpgjavaruntime;

import java.util.HashMap;

public abstract class Differ
{
    //
    // Class that encodes the meaningful content of a line.
    //
    public interface ILine
    {
        public abstract int hashCode();
        public abstract boolean equals(Object anObject);
        public abstract int size();
        public abstract int getStartLine();
        public abstract int getStartColumn();
        public abstract int getEndLine();
        public abstract int getEndColumn();
    }

    //
    // Class to store a difference between two sections in the files.
    //
    class Change
    {
        private int number,
                    olds,
                    olde,
                    news,
                    newe,
                    code,
    
                    temp,
                    temp2;
    
        private Change next;
    
        public Change(int code, int olds, int olde, int news, int newe, int number)
        {
            this.code = code;
            this.olds = olds;
            this.olde = olde;
            this.news = news;
            this.newe = newe;
            this.number = number;
        }
    
        public final int getCode() { return code; }
        public final int getOlds() { return olds; }
        public final int getOlde() { return olde; }
        public final int getNews() { return news; }
        public final int getNewe() { return newe; }
        public final int getNumber() { return number; }
    
        public final int getTemp() { return temp; }
        public final int getTemp2() { return temp2; }
    
        public final Change getNext() { return next; }
    
        public final void setCode(int code) { this.code = code; }
        public final void setOlds(int olds) { this.olds = olds; }
        public final void setOlde(int olde) { this.olde = olde; }
        public final void setNews(int news) { this.news = news; }
        public final void setNewe(int newe) { this.newe = newe; }
        public final void setNumber(int number) { this.number = number; }
    
        public final void setTemp(int temp) { this.temp = temp; }
        public final void setTemp2(int temp2) { this.temp2 = temp2; }
    
        public final void setNext(Change next) { this.next = next; }
    }

    //
    // The codes for the different kinds of changes.
    //
    final static int I_CODE   = 0;
    final static int D_CODE   = 1;
    final static int R_CODE   = 2;
    final static int M_CODE   = 3;
    final static int MM_CODE  = 4;
    final static int IMI_CODE = 5;
    final static int MI_CODE  = 6;
    final static int IM_CODE  = 7;
    final static int DMD_CODE = 8;
    final static int MD_CODE  = 9;
    final static int DM_CODE  = 10;

    PrsStream newStream,
              oldStream;

    int insertCount = 0,
        deleteCount = 0,
        replaceDeleteCount = 0,
        replaceInsertCount = 0,
        moveCount = 0;

    Change deleteRoot  = null,
           insertRoot  = null,
           replaceRoot = null,
           changeRoot  = null;

    int changeCount = 0,
        extraCount = 0;

    int newStart,
        oldStart,
        newEnd,
        oldEnd;

    ILine newBuffer[],
          oldBuffer[];

    HashMap newMap = new HashMap();
    int newLink[];

    public abstract ILine[] getBuffer(PrsStream stream);

    protected Differ() {}
    
    public Differ(PrsStream newStream, PrsStream oldStream)
    {
        this.newStream = newStream;
        this.oldStream = oldStream;

        newBuffer = getBuffer(newStream);
        oldBuffer = getBuffer(oldStream);

        int i,
            k;
        for (i = 1; i < newBuffer.length && i < oldBuffer.length; i++)
        {
            if (! (newBuffer[i].equals(oldBuffer[i])))
                break;
        }

        if (i == newBuffer.length && i == oldBuffer.length)
            return;

        newStart = i;
        oldStart = i;

        for (i = newBuffer.length - 1, k = oldBuffer.length - 1; 
             i > newStart && k > oldStart; 
             i--, k--)
        {
            if (! (newBuffer[i].equals(oldBuffer[k])))
                 break;
        }

        newEnd = i;
        oldEnd = k;

        newLink = new int[newBuffer.length];
        int tail[] = new int[newBuffer.length];
        for (i = 1; i < newBuffer.length; i++)
        {
            ILine line = newBuffer[i];
            Integer root = (Integer) newMap.get(line);
            if (root == null)
            {
                root = new Integer(i);
                newMap.put(line, root);
            }
            else newLink[tail[root.intValue()]] = i;

            tail[root.intValue()] = i;
        }
    } 

    final int min(int x, int y) { return x < y ? x : y; }

    public final int getChangeCount() { return changeCount; }

    public final int getInsertCount() { return insertCount; }
    public final int getDeleteCount() { return deleteCount; }
    public final int getReplaceDeleteCount() { return replaceDeleteCount; }
    public final int getReplaceInsertCount() { return replaceInsertCount; }
    public final int getMoveCount() { return moveCount; }

    public void compare()
    {
        if (newLink != null)
        {
            compare(oldStart, oldEnd, newStart, newEnd);
            findMoves();
            mergeChanges();
        }

        return;
    }
 
    //
    // This procedure outputs the differences found between the two files*/
    //
    public void outputChanges()
    {
        for (Change element = changeRoot; element != null; element = element.getNext())
        {
            if (element.getCode() == I_CODE)
                 outputInsert(element);
            else if (element.getCode() == D_CODE)
                 outputDelete(element);
            else if (element.getCode() == R_CODE)
                 outputReplace(element);
            else if (element.getCode() == M_CODE)
                 outputMove(element);
            else if (element.getCode() == MD_CODE)
                 outputMoveDelete(element);
            else if (element.getCode() == MI_CODE)
                 outputMoveInsert(element);
            else System.out.println("Don't know what to do with code " + element.getCode());
        }

        return;
    }

    //
    // In this procedure,the section of text from a stream identified
    // by the indices start..end is removed from its hash map.
    //
    void detach(ILine buffer[], HashMap map, int link[], int start, int end)
    {
        for (int k = start; k <= end; k++)
        {
            ILine line = buffer[k];
            Integer root = (Integer) map.get(line);
            if (root == null)
            {
                int previous = 0;
                for (int i = root.intValue(); i != k && i != 0; previous = i, i = link[i])
                    ;
                if (previous == 0)
                {
                    int successor = link[root.intValue()];
                    if (successor == 0)
                         map.remove(line);
                    else map.put(line, new Integer(successor));
                }
                else link[previous] = link[k];
            }
        }

        return;
    }
 
    //
    // This function tests whether two sections of text are identical
    //
    boolean compareSections(int olds, int news, int bound)
    {
        for (int i = 0; i <= bound; i++)
        {
            if (! oldBuffer[olds + i].equals(newBuffer[news + i]))
                return false;
        }

        return true;
    }

    //
    // This procedure takes 4 integer argumets that indicate a section
    // of the old file that was replaced by a section of the new file.
    // We first analyze the given change to see if in fact it is not a
    // disguised Move.  If it is a cross-Move we update the old Replace
    // that makes it so, and add a new Move.  Otherwise, we add a new
    // Replace change.
    //
    void addReplace(int olds, int olde, int news, int newe)
    {
        changeCount++;
        detach(newBuffer, newMap, newLink, news, newe);

        for (Change element = replaceRoot, tail = null; element != null; tail = element, element = element.getNext())
        {
            int bound1 = (element.getOlde() - element.getOlds()),
                bound2 = (element.getNewe() - element.getNews());
            if (bound1 == (newe - news)  &&  bound2 == (olde - olds))
            {
                if (compareSections(element.getOlds(), news, bound1))
                {
                    if (compareSections(olds, element.getNews(), bound2))
                    {
                        Change change_element = new Change(M_CODE, element.getOlds(), element.getOlde(), news, newe, changeCount);
                        change_element.setNext(changeRoot);
                        changeRoot = change_element;

                        element.setCode(M_CODE);
                        element.setOlds(olds);
                        element.setOlde(olde);
                        if (element == replaceRoot)
                             replaceRoot = element.getNext();
                        else tail.setNext(element.getNext());

                        element.setNext(changeRoot);
                        changeRoot = element;

                        return;
                    }
                }
            }
        }

        Change element = new Change(R_CODE, olds, olde, news, newe, changeCount);
        element.setNext(replaceRoot);
        replaceRoot = element;

        return;
    }

    //
    // In this procedure, we search for identical sections of text that
    // were deleted from one place and inserted into another, and
    // combine these changes into Move changes.
    //
    void findMoves()
    {
        Change i_element,
               d_element,
               previous_d,
               previous_i;

        //
        // For each deletion, we search the insertion list for an identical
        // or similar section that was inserted in another location.  If so,
        // these changes are turned into Move changes.
        // The function similarSections takes care of the comparison, and
        // if it finds that the two sections are similar, it then updates
        // the Insert node and turns it into a Move or Move_Move.
        //
        for (i_element = insertRoot, previous_i = null; i_element != null && deleteRoot != null; previous_i = i_element, i_element = i_element.getNext())
        {
            int bound = i_element.getNewe() - i_element.getNews();

            for (d_element = deleteRoot, previous_d = null; d_element != null; previous_d = d_element, d_element = d_element.getNext())
            {
                if (bound == d_element.getOlde() - d_element.getOlds())
                {
                     if (similarSections(d_element, i_element))
                     {
                         if (d_element == deleteRoot)
                              deleteRoot = d_element.getNext();
                         else previous_d.setNext(d_element.getNext());
                         break;
                     }
                 }
            }

            if (i_element.getCode() != I_CODE)
            {
                if (i_element == insertRoot)
                     insertRoot = i_element.getNext();
                else previous_i.setNext(i_element.getNext());

                i_element.setNext(changeRoot);
                changeRoot = i_element;
            }
        }
 
        //
        // We now look for Delete sections that overlap Insert sections.
        // The Delete section must contain at least 3 lines to be considered.
        // The function DELETE_OVERLAP is invoked to do the check.  If a
        // successful overlap is found, the Delete node is changed into the
        // the appropriate DM, MD, or DMD change.
        //
        for (d_element = deleteRoot, previous_d = null; d_element != null && insertRoot != null; previous_d = d_element, d_element = d_element.getNext())
        {
            int bound = d_element.getOlde() - d_element.getOlds();
            if (bound >= 2) // At least 3 lines?
            {
                for (i_element = insertRoot; i_element != null; previous_i = i_element, i_element = i_element.getNext())
                {
                    if (bound < i_element.getNewe() - i_element.getNews())
                    {
                        if (deleteOverlap(d_element, i_element))
                        {
                            if (i_element == insertRoot)
                                 insertRoot = i_element.getNext();
                            else previous_i.setNext(i_element.getNext());
                            break;
                        }
                    }
                }

                if (d_element.getCode() != D_CODE)
                {
                    if (d_element == deleteRoot)
                         deleteRoot = d_element.getNext();
                    else previous_d.setNext(d_element.getNext());

                    d_element.setNext(changeRoot);
                    changeRoot = d_element;
                }
            }
        }
 
        //
        // We now look for Insert sections that overlap Delete sections.
        // The Insert section must contain at least 3 lines to be considered.
        // The function INSERT_OVERLAP is invoked to do the check.  If a
        // successful overlap is found, the Insert node is changed into the
        // the appropriate IM, MI, or IMI change.
        //
        for (i_element = insertRoot, previous_i = null; i_element != null && deleteRoot != null; previous_i = i_element, i_element = i_element.getNext())
        {
            int bound = i_element.getNewe() - i_element.getNews();
            if (bound >= 2) // At least 3 lines?
            {
                for (d_element = deleteRoot, previous_d = null; d_element != null; previous_d = d_element, d_element = d_element.getNext())
                {
                    if (bound < d_element.getOlde() - d_element.getOlds())
                    {
                        if (insertOverlap(d_element, i_element))
                        {
                            if (d_element == deleteRoot)
                                  deleteRoot = d_element.getNext();
                             else previous_d.setNext(d_element.getNext());
                             break;
                        }
                    }
                }
            }

            if (i_element.getCode() != I_CODE)
            {
                if (i_element == insertRoot)
                     insertRoot = i_element.getNext();
                else previous_i.setNext(i_element.getNext());

                i_element.setNext(changeRoot);
                changeRoot = i_element;
            }
        }

        return;
    }
 
    //
    // The procedure COMPARE takes four arguments OLDS and OLDE which    
    // mark the start and end indices of old lines in the buffer to be
    // compared to new lines in the buffer indicated by NEWS and NEWE
    // which respectively mark the new start and end indices.
    //
    void compare(int olds, int olde, int news, int newe)
    {
        //
        // At this stage, We know that we are dealing with two sections
        // which differ by at least their first line. If the end of both
        // sections was reached, we simply return.
        //
        if (olds > olde  &&  news > newe) // end of both sections?
            return;
 
        if (olds > olde) // End of old section?
        {
            changeCount++;
            Change element = new Change(I_CODE, olds, olde, news, newe, changeCount);
            element.setNext(insertRoot);
            insertRoot = element;
            detach(newBuffer, newMap, newLink, news, newe);
        }
        else if (news > newe) // End of new section?
        {
            changeCount++;
            Change element = new Change(D_CODE, olds, olde, news, newe, changeCount);
            element.setNext(deleteRoot);
            deleteRoot = element;
        }
        else // Find largest subsection that matches
        {
            int largest = 0, // keeps track of size of matched subsection
                lolds = 0,
                lnews = 0;

            //
            // Start OLDI at OLDS and move forward while OLDI has
            // a chance to beat the previous largest subsection.
            //
            for (int oldi = olds; oldi + largest <= olde; oldi++)
            {
                Integer image = (Integer) newMap.get(oldBuffer[oldi]); // list of OLDI lines
                if (image != null)
                {
                    for (int newi = image.intValue(); newi != 0 && oldi + largest <= olde; newi = newLink[newi])
                    {
                        if (newi >= news && newi + largest <= newe)
                        {
                            if (oldBuffer[oldi + largest].equals(newBuffer[newi + largest]))
                            {
                                // Any chance to do better than before?
                                int bound = min(newe - newi, olde - oldi),
                                    i;
                                for (i = 1; i <= bound; i++)
                                {
                                    if (! oldBuffer[oldi + i].equals(newBuffer[newi + i]))
                                        break;
                                }
                                if (i > largest)
                                {
                                    largest = i ;
                                    lolds = oldi ;
                                    lnews = newi ;
                                }
                            }
                        }
                    }
                }
            }

            if (largest > 0)
            {
                //
                // A matched subsection was found. It starts at index
                // LOLDS for the old file, and LNEWS for the new file,
                // and contains LARGEST records.
                //
                detach(newBuffer, newMap, newLink, lnews, lnews + largest - 1);
                compare(olds, lolds - 1, news, lnews - 1);
                compare(lolds + largest, olde, lnews + largest, newe);
            }
            else addReplace(olds, olde, news, newe);
        }

        return;
    }
 
    //
    // This function receives two pointers as parameters: D and I.
    // D identifies a Delete change, and I an Insert change. The changes
    // are known to contain the same number of lines.  The objective is
    // to check wheter the changes are identical or that they are each
    // separated into two segments that have been interchanged,  but
    // identical.
    //
    boolean similarSections(Change d_element, Change i_element)
    {
        HashMap map = new HashMap();
        int link[] = new int[newBuffer.length],
            tail[] = new int[newBuffer.length];

        //
        // We first check whether the two sections are identical, then we
        // check whether they do not contain null lines only...
        //
        int bound = d_element.getOlde() - d_element.getOlds();
        if (compareSections(d_element.getOlds(), i_element.getNews(), bound))
        {
            for (int i = d_element.getOlds();  i <= d_element.getOlde(); i++)
            {
                if (! (oldBuffer[i].size() == 0)) // Not all the lines are nulls
                {
                    i_element.setCode(M_CODE);
                    i_element.setOlds(d_element.getOlds());
                    i_element.setOlde(d_element.getOlde());
                    extraCount++;

                    return true;
                }
            }

            return false; // All the lines are null
        }

        //
        // We first insert all the lines from the new file into the hash
        // table for quick look up.  We then attempt to locate a subsection
        // in the bottom part of the new sections that matches the upper
        // part of the old section.  If we are successful, we then check
        // whether the bottom portion of the old section matches the upper
        // part of the new section.
        //
        for (int i = i_element.getNews(); i <= i_element.getNewe(); i++)
        {
            ILine line = newBuffer[i];
            Integer root = (Integer) map.get(line);
            if (root == null)
            {
                root = new Integer(i);
                map.put(line, root);
            }
            else link[tail[root.intValue()]] = i;

            tail[root.intValue()] = i;
        }

        int largest = -1,
            newi = 0;
        bound = 0;
        Integer image = (Integer) map.get(oldBuffer[d_element.getOlds()]);
        if (image != null)
        {
            for (int j = image.intValue(); j != 0; j = link[j])
            {
                bound = i_element.getNewe() - j;
                if (compareSections(d_element.getOlds(), j, bound))
                {
                    if (bound > largest)
                    {
                        largest = bound;
                        newi = j;
                    }
                }
            }
        }
        
        detach(newBuffer, map, link, i_element.getNews(), i_element.getNewe());

        if (largest >= 0) // We found a matched segment.
        {
            i_element.setTemp(bound); // Save old offset
            int oldi = d_element.getOlds() + bound + 1;
            bound = d_element.getOlde() - oldi;
            if (compareSections(oldi, i_element.getNews(), bound)) // Does the other segment compare
            {
                //
                // We mark the change as MM to indicate that it really
                // represents two Moves.  The OLDE field marks the end
                // point of the old section that matches the last line
                // in the new section. The TEMP field marks the beginning
                // of the second old segment and the TEMP2 field marks
                // the beginning of the second new segment.
                // In the procedure MERGE_CHANGES, the changes marked MM
                // are broken down into two separate Moves.
                //
                i_element.setCode(MM_CODE);
                i_element.setOlds(d_element.getOlds());
                i_element.setOlde(d_element.getOlde());
                i_element.setTemp(oldi);
                i_element.setTemp2(newi);

                return true;
            }
        }

        return false;
    }
    
    //
    // In this function, we check whether an Insert section overlaps a
    // Delete section.
    //
    boolean insertOverlap(Change d_element, Change i_element)
    {
        HashMap map = new HashMap();
        int link[] = new int[oldBuffer.length],
            tail[] = new int[oldBuffer.length];

        //
        // We first insert all the lines from the Delete section into the
        // table for quick look up.  We then look to see if the Insert
        // section overlaps the Delete section.  If so, we update the Insert
        // change to reflect the new changes.
        //
        for (int i = d_element.getOlds(); i <= d_element.getOlde(); i++)
        {
            ILine line = oldBuffer[i];
            Integer root = (Integer) map.get(line);
            if (root == null)
            {
                root = new Integer(i);
                map.put(line, root);
            }
            else link[tail[root.intValue()]] = i;

            tail[root.intValue()] = i;
        }
        int oldi = 0,
            bound = i_element.getNewe() - i_element.getNews();
        Integer image = (Integer) map.get(oldBuffer[i_element.getNews()]);
        if (image != null)
        {
            for (oldi = image.intValue(); oldi != 0; oldi = link[oldi])
            {
                if (d_element.getOlde() - oldi >= bound)
                {
                    if (compareSections(oldi, i_element.getNews(), bound))
                        break;
                }
            }
        }
        
        detach(oldBuffer, map, link, d_element.getOlds(), d_element.getOlde());
    
        if (oldi != 0) //  We found a matched segment.
        {
            if (oldi == d_element.getOlds())
            {
                extraCount++;
                i_element.setCode(MD_CODE);
            }
            else if (d_element.getOlde() == oldi + bound)
                 i_element.setCode(DM_CODE);
            else i_element.setCode(DMD_CODE);
            i_element.setTemp(oldi);
            i_element.setTemp2(d_element.getNewe());
            i_element.setOlds(d_element.getOlds());
            i_element.setOlde(d_element.getOlde());

            return true;
        }

        return false;
    }
    
    //
    // In this function, we check whether a Delete section overlaps an
    // Insert section.
    //
    boolean deleteOverlap(Change d_element, Change i_element)
    {
        HashMap map = new HashMap();
        int link[] = new int[newBuffer.length],
            tail[] = new int[newBuffer.length];

        //
        // We first insert all the lines from the Insert section into the
        // table for quick look up.  We then look to see if the Delete
        // section overlaps the Insert section.  If so, we update the Insert
        // change to reflect the new changes.
        //
        for (int i = i_element.getNews(); i <= i_element.getNewe(); i++)
        {
            ILine line = newBuffer[i];
            Integer root = (Integer) map.get(line);
            if (root == null)
            {
                root = new Integer(i);
                map.put(line, root);
            }
            else link[tail[root.intValue()]] = i;

            tail[root.intValue()] = i;
        }

        int bound = d_element.getOlde() - d_element.getOlds(),
            newi = 0;

        Integer image = (Integer) map.get(oldBuffer[d_element.getOlds()]);
        if (image != null)
        {
            for (newi = image.intValue(); newi != 0; newi = link[newi])
            {
                if (i_element.getNewe() - newi >= bound)
                {
                    if (compareSections(d_element.getOlds(), newi, bound))
                        break;
                }
            }
        }
        
        detach(newBuffer, map, link, i_element.getNews(), i_element.getNewe());
    
        if (newi != 0) //  We found a matched segment.
        {
            if (newi == i_element.getNews())
            {
                 extraCount++;
                 d_element.setCode(MI_CODE);
            }
            else if (i_element.getNewe() == newi + bound)
                 d_element.setCode(IM_CODE);
            else d_element.setCode(IMI_CODE);
            d_element.setTemp(newi);
            d_element.setTemp2(i_element.getOlde());
            d_element.setNews(i_element.getNews());
            d_element.setNewe(i_element.getNewe());

            return true;
        }

        return false;
    }

    //
    // This procedure merges the separate lists of changes which are
    // contained in linear linked lists identified by the pointers:
    // CHANGE_ROOT, INSERT_ROOT, DELETE_ROOT, and REPLACE_ROOT into a
    // separate list pointed to by CHANGE_ROOT.
    //
    void mergeChanges()
    {
        Change change[] = new Change[changeCount + 1];
        boolean slot_used[] = new boolean[changeCount + 1];

        //
        // In this loop, We make each element I of the array CHANGE point to
        // change number I.
        //
        for (Change element = insertRoot; element != null; element = element.getNext())
        {
            change[element.getNumber()] = element;
            slot_used[element.getNumber()] = true;
        }
        for (Change element = deleteRoot; element != null; element = element.getNext())
        {
            change[element.getNumber()] = element;
            slot_used[element.getNumber()] = true;
        }
        for (Change element = replaceRoot; element != null; element = element.getNext())
        {
            change[element.getNumber()] = element;
            slot_used[element.getNumber()] = true;
        }
        for (Change element = changeRoot; element != null; element = element.getNext())
        {
            change[element.getNumber()] = element;
            slot_used[element.getNumber()] = true;
        }

        //
        // We loop backwards on the array of changes and insert them into a
        // new linear linked list in a stack fashion so as to preserve the
        // correct ordering.
        //
        changeRoot = null;
        for (int i = changeCount; i >= 1; i--)
        {
            if (slot_used[i])
            {
                //
                // Changes marked MM represent two contiguous Moves. The
                // old lines are marked by the fields OLDS, and OLDE.
                // The new lines are marked by the fields NEWS, and NEWE.
                // The temporary field TEMP marks the first line in the
                // the second segment of the old lines; and TEMP2 marks
                // the first line in the second segment of the new lines.
                // We know that the first segment of the old line is
                // identical to the second segment of the new lines.
                //
                if (change[i].getCode() == MM_CODE)
                {
                    Change element = new Change(M_CODE, 
                                                change[i].getOlds(),
                                                change[i].getTemp() - 1,
                                                change[i].getTemp2(),
                                                change[i].getNewe(),
                                                change[i].getNumber());
                    element.setNext(changeRoot);
                    changeRoot = element;

                    change[i].setCode(M_CODE);
                    change[i].setOlds(change[i].getTemp());
                    change[i].setNewe(change[i].getTemp2() - 1);
                }
                //
                // TEMP marks the beginning of the Move segment, and
                // TEMP2 marks the new line after which the deletion
                // took place.
                //
                else if (change[i].getCode() == DM_CODE)
                {
                    Change element = new Change(M_CODE,
                                                change[i].getTemp(),
                                                change[i].getOlde(),
                                                change[i].getNews(),
                                                change[i].getNewe(),
                                                change[i].getNumber());
                    element.setNext(changeRoot);
                    changeRoot = element;

                    change[i].setCode(D_CODE);
                    change[i].setOlde(change[i].getTemp() - 1);
                    change[i].setNewe(change[i].getTemp2());
                }
                //
                // Same as DM except that the Move is followed by
                // another Delete.
                //
                else if (change[i].getCode() == DMD_CODE)
                {
                    Change element = new Change(MD_CODE,
                                                change[i].getTemp(),
                                                change[i].getOlde(),
                                                change[i].getNews(),
                                                change[i].getNewe(),
                                                change[i].getNumber());
                    element.setNext(changeRoot);
                    changeRoot = element;

                    change[i].setCode(D_CODE);
                    change[i].setOlde(change[i].getTemp() - 1);
                    change[i].setNewe(change[i].getTemp2());
                }
                //
                // TEMP marks the beginning of the Move segment, and
                // TEMP2 marks the old line after which the initial
                // Insertion was made.
                //
                else if (change[i].getCode() == IM_CODE)
                {
                    Change element = new Change(M_CODE,
                                                change[i].getOlds(),
                                                change[i].getOlde(),
                                                change[i].getTemp(),
                                                change[i].getNewe(),
                                                change[i].getNumber());
                    element.setNext(changeRoot);
                    changeRoot = element;

                    change[i].setCode(I_CODE);
                    change[i].setNewe(change[i].getTemp() - 1);
                    change[i].setOlde(change[i].getTemp2());
                }
                //
                // Same as IM except that the Move is followed by
                // another Insert.
                //
                else if (change[i].getCode() == IMI_CODE)
                {
                    Change element = new Change(MI_CODE,
                                                change[i].getOlds(),
                                                change[i].getOlde(),
                                                change[i].getTemp(),
                                                change[i].getNewe(),
                                                change[i].getNumber());
                    element.setNext(changeRoot);
                    changeRoot = element;

                    change[i].setCode(I_CODE);
                    change[i].setNewe(change[i].getTemp() - 1);
                    change[i].setOlde(change[i].getTemp2());
                }

                change[i].setNext(changeRoot);
                changeRoot = change[i];
            }
        }

        changeCount -= extraCount;

        return;
    }

    abstract void outputInsert(Change element);
    abstract void outputDelete(Change element);
    abstract void outputReplace(Change element);
    abstract void outputMove(Change element);
    abstract void outputMoveDelete(Change element);
    abstract void outputMoveInsert(Change element);
}

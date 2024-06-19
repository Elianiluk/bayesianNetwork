import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class bayesBall {

    // function for the bayes ball algorithm
    public boolean bayesBall(Variable start,Variable end, ArrayList<Variable> evidence) {
        ArrayList<Variable> visited=new ArrayList<>();

        // casual chain
//        if(end.parents.contains(start))
//            return false;

        // common cause
        for(Variable v:start.parents)
            for(Variable v2:end.parents)
                if(v==v2 && !evidence.contains(v))
                    return false;

        //common effect
        for(Variable v:start.childs)
            for(Variable v2:end.childs)
                if(v==v2 && evidence.contains(v))
                    return false;

        return areIndependent(start,end,visited,false,evidence);
    }
    private static boolean areIndependent(Variable start, Variable end, ArrayList<Variable> visited, boolean comingFromChild, ArrayList<Variable> evidence) {
        visited.add(start);

        // if we can reach end from start it means the variables are dependent
        if(start==end)
            return false;

        // if the variable we are on now is evidence, and we are coming from a child it means we are stuck and the variables are independent
        if(evidence.contains(start) && comingFromChild)
            return true;

        // if we are now on evidence node we can only search its parent
        else if(evidence.contains(start)){
            for(int i=0;i<start.parents.size();i++) {
                    if (!areIndependent(start.parents.get(i), end, visited, true, evidence))
                        return false;
            }
        }

        // if we are not on evidence and we are coming from a child we can go to any connected node, parent or child
        else if(!evidence.contains(start) && comingFromChild){
            for(int i=0;i<start.childs.size();i++)
                if(!visited.contains(start.childs.get(i)))
                    if(!areIndependent(start.childs.get(i),end,visited,false,evidence))
                        return false;

            for(int i=0;i<start.parents.size();i++)
                    if(!areIndependent(start.parents.get(i),end,visited,true,evidence))
                        return false;
        }

        // if we are not on evidence and not coming from child we can only go to children
        else if(!evidence.contains(start) && !comingFromChild){
            for(int i=0;i<start.childs.size();i++)
                if(!visited.contains(start.childs.get(i))) {
                    if (!areIndependent(start.childs.get(i), end, visited, false, evidence))
                        return false;
                }
        }

        // if we get past all the possible nodes and we didn't get to the "end" node, it means it cant be reached
        // so the variables are independent
        return true;
    }
}
import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class bayesBall {
    public boolean bayesBall(ArrayList<Variable> v1,Variable start,Variable end, ArrayList<Variable> evidence) {
        ArrayList<Variable> visited=new ArrayList<>();
        return areIndependent(start,end,visited,false,evidence);
    }
    private static boolean areIndependent(Variable start, Variable end, ArrayList<Variable> visited, boolean comingFromChild, ArrayList<Variable> evidence) {
        visited.add(start);

        if(start==end)
            return false;

        if(evidence.contains(start) && comingFromChild)
            return true;

        if(start.childs.size()==0 && !comingFromChild)
            return true;

        if(start.parents.size()==0 && comingFromChild)
            return true;

        else if(evidence.contains(start)){
            for(int i=0;i<start.parents.size();i++)
                if(!visited.contains(start.parents.get(i)))
                    if(!areIndependent(start.parents.get(i),end,visited,true,evidence))
                        return false;
        }

        else if(!evidence.contains(start) && comingFromChild){
            for(int i=0;i<start.childs.size();i++)
                if(!visited.contains(start.childs.get(i)))
                    if(!areIndependent(start.childs.get(i),end,visited,false,evidence))
                        return false;

            for(int i=0;i<start.parents.size();i++)
                if(!visited.contains(start.parents.get(i)))
                    if(!areIndependent(start.parents.get(i),end,visited,true,evidence))
                        return false;
        }

        else if(!evidence.contains(start) && !comingFromChild){
            for(int i=0;i<start.childs.size();i++)
                if(!visited.contains(start.childs.get(i)))
                    if(!areIndependent(start.childs.get(i),end,visited,false,evidence))
                        return false;
        }

        return true;
    }
}

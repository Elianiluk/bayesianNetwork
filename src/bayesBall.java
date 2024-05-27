import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class bayesBall {
    public boolean bayesBall(ArrayList<Variable> v1,Variable start,Variable end, ArrayList<Variable> evidence) {
        ArrayList<Variable> visited=new ArrayList<>();
        if(areIndependent(start,end,visited,false,evidence))
            return true;
        return false;
    }

    private static boolean areIndependent(Variable start, Variable end, ArrayList<Variable> visited, boolean comingFromChild, ArrayList<Variable> evidence) {
        if(start==end)
            return false;
        visited.add(start);

        if(evidence.contains(start) && comingFromChild)
            return true;

        if(evidence.contains(start)){
            for(int i=0;i<start.parents.size()-1;i++)
                if(!visited.contains(start.parents.get(i)) && !areIndependent(start.parents.get(i),end,visited,true,evidence))
                    return false;
        }

        if(!evidence.contains(start) && comingFromChild){
            for(int i=0;i<start.childs.size()-1;i++)
                if(!visited.contains(start.childs.get(i)) && !areIndependent(start.childs.get(i),end,visited,false,evidence))
                    return false;

            for(int i=0;i<start.parents.size()-1;i++)
                if(!visited.contains(start.parents.get(i)) && !areIndependent(start.parents.get(i),end,visited,true,evidence))
                    return false;
        }

        if(!evidence.contains(start) && !comingFromChild){
            for(int i=0;i<start.childs.size()-1;i++)
                if(!visited.contains(start.childs.get(i)) && !areIndependent(start.childs.get(i),end,visited,false,evidence))
                    return false;
        }

        return true;

    }
}

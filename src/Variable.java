import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Variable {
    String name;
    List<String> outcomes = new ArrayList<>();
    List<Variable> parents = new ArrayList<>();
    List<Variable> childs = new ArrayList<>();

    Variable(String name) {
        this.name = name;
    }

    void addOutcome(String outcome) {
        outcomes.add(outcome);
    }

    void addParent(Variable p){
        parents.add(p);
    }

    void addChile(Variable c){
        childs.add(c);
    }
    //elian
    String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        String child="";
        for (Variable v:childs)
            child+=v.name;
        String parent="";
        for (Variable v1:parents)
            parent+=v1.name;
        return "Variable{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes + ",childs=["+child+"]"+",parents=["+parent+"]"+
//                ", childs="+childs.toString()+
//                ", parents="+parents.toString()+
                '}';
    }
}
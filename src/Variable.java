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

    String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
                '}';
    }
}
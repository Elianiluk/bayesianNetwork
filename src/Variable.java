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
    String [][] table;
    int numberOfOutcomes;

    Variable(String name) {
        this.name = name;
        this.numberOfOutcomes = 0;
    }

    Variable(Variable other){
        this.name= other.name;
        this.table=other.table;
        this.parents=other.parents;
        this.childs=other.childs;
        this.outcomes=other.outcomes;
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

    void setTable(String[][] table) {
        this.table=table;
    }

    @Override
    public String toString() {
        String child="";
        for (Variable v:childs)
            child+=v.name;
        String parent="";
        for (Variable v1:parents)
            parent+=v1.name;
        StringBuilder tableString = new StringBuilder();
        for (String[] row : this.table) {
            for (String value : row) {
                tableString.append(value).append(" ");
            }
            tableString.append("\n");
        }

        return "Variable{" +
                "name='" + name + '\'' +
                ", outcomes=" + outcomes +
                ", childs=[" + child.toString().trim() + "]" +
                ", parents=[" + parent.toString().trim() + "]" +
                ", table=\n" + tableString.toString().trim() +
                ", \nnumber of outcomes=" + numberOfOutcomes +
                '}';
    }
}
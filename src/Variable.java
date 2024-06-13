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
//    ArrayList<Double> table= new ArrayList<>();
    String [][] table;
    int numberOfOutcomes;

    Variable(String name) {
        this.name = name;
//        this.table="";
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
//        String[] values = table.split(" ");
//        for (String value : values) {
//            this.table.add(Double.parseDouble(value));
//        }
//            String[] values = table.split(" ");
//            int numRows = values.length / outcomes.size();
//            this.table = new double[numRows][outcomes.size()];
//
//            int index = 0;
//            for (int i = 0; i < numRows; i++) {
//                for (int j = 0; j < outcomes.size(); j++) {
//                    this.table[i][j] = Double.parseDouble(values[index++]);
//                }
//            }

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
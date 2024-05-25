import java.util.ArrayList;
import java.util.List;

class Definition {
    String forVar;
    List<String> givens = new ArrayList<>();
    String table;

    Definition(String forVar) {
        this.forVar = forVar;
    }

    void addGiven(String given,Variable v1,Variable v2) {
        givens.add(given);
        v1.parents.add(v2);
        v2.childs.add(v1);
//        v2.parents.add(v1);
//        v1.childs.add(v2);
    }

    void setTable(String table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "forVar='" + forVar + '\'' +
                ", givens=" + givens +
                ", table='" + table + '\'' +
                '}';
    }
}
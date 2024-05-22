import java.util.ArrayList;
import java.util.List;

class Definition {
    String forVar;
    List<Variable> givens = new ArrayList<>();
    String table;

    Definition(String forVar) {
        this.forVar = forVar;
    }

    void addGiven(Variable given) {
        givens.add(given);

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
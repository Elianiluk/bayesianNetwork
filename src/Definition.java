import java.util.ArrayList;
import java.util.List;

class Definition {
    String forVar;
    List<String> givens = new ArrayList<>();
    ArrayList<Double> probabilities = new ArrayList<>();

    Definition(String forVar) {
        this.forVar = forVar;
    }

    void addGiven(String given,Variable v1,Variable v2) {
        givens.add(given);
        v1.parents.add(v2);
        v2.childs.add(v1);
    }

    void setTable(String table) {
        String[] probs = table.trim().split("\\s+");
        for (String prob : probs) {
            probabilities.add(Double.parseDouble(prob));
        }
    }

    @Override
    public String toString() {
        return "Definition{" +
                "forVar='" + forVar + '\'' +
                ", givens=" + givens +
                ", table='" + probabilities.toString() + '\'' +
                '}';
    }
}
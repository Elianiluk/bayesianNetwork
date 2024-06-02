import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class variableElimination {
    public void variableElimination(Variable start, ArrayList<Variable> variables, ArrayList<Variable> order,ArrayList<Variable> evidence) {
            int numAdds=0, numMultipy=0,probability=0;
    }
}

class Result {
    double probability;
    int addOperations;
    int multiplyOperations;

    public Result(double probability, int addOperations, int multiplyOperations) {
        this.probability = probability;
        this.addOperations = addOperations;
        this.multiplyOperations = multiplyOperations;
    }

    @Override
    public String toString() {
        return "Probability: " + probability + ", Additions: " + addOperations + ", Multiplications: " + multiplyOperations;
    }
}

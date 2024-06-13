import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class variableElimination {

    public void variableElimination(Variable start, ArrayList<Variable> variables, ArrayList<Variable> order, ArrayList<Variable> evidence, ArrayList<String> outcome, FileWriter myWriter) throws IOException {
        int numAdds = 0, numMultiply = 0;
        double probability = 0;
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<Variable> toAdd = new ArrayList<>();
        toAddStart(toAdd, start);

        for (Variable v : evidence) {
            toAddStart(toAdd, v);
        }

        for (Variable v : toAdd) {
            System.out.println("name: " + v.name);
        }

        for (Variable v : toAdd) {
            factors.add(new Factor(v));
        }

        System.out.println("before delete");
        for (Factor factor : factors) {
            factor.printFactor();
        }


        for (int i = 0; i < evidence.size(); i++) {
            Variable evi = evidence.get(i);
            String outcomeValue = outcome.get(i);
            for (Factor factor : factors) {
                if (factor.getVariables().contains(evi)) {
                    factor.evidenceDelete(evi, outcomeValue);
                }
            }
        }

        System.out.println("after delete");
        for (Factor factor : factors) {
            factor.printFactor();
        }


        for (Variable ord : order) {
            if (!toAdd.contains(ord)) {
                continue;
            }
            ArrayList<Factor> newFactors = new ArrayList<>();
            for (Factor factor : factors) {
                if (factor.getVariables().contains(ord)) {
                    newFactors.add(factor);
                }
            }
            sortFactors(newFactors);
            factors.removeAll(newFactors);



            Factor newFactor = newFactors.get(0);
            newFactors.remove(0);
            for (Factor factor : newFactors) {
                numMultiply += newFactor.multiply(factor);
            }
            numAdds += newFactor.marginalize(ord);
            factors.add(newFactor);
        }

        Factor newFactor = factors.get(0);
        newFactor.normalize();
        probability = Double.parseDouble(newFactor.getTable()[1][newFactor.getTable()[0].length - 1]);
        myWriter.write(probability + "," + numAdds + "," + numMultiply + "\n");
        System.out.println("finish");
    }

    private static void toAddStart(ArrayList<Variable> toAdd, Variable start) {
        if (toAdd.contains(start)) {
            return;
        }
        toAdd.add(start);
        for (Variable parent : start.parents) {
            toAddStart(toAdd, parent);
        }
    }

    private static void sortFactors(ArrayList<Factor> factors) {
        ArrayList<Factor> newFactors = new ArrayList<>();
        for (Factor factor : factors) {

        }
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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class variableElimination {

    public void variableElimination(Variable start, ArrayList<Variable> variables, ArrayList<Variable> order, ArrayList<Variable> evidence, ArrayList<String> outcome, FileWriter myWriter,ArrayList<String> queryOutcome) throws IOException {
        int numAdds = 0, numMultiply = 0;
        double probability = 0;
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<Variable> toAdd = new ArrayList<>(variables);
        toAddStart(toAdd, start);
        bayesBall ball = new bayesBall();

//        for (Variable v : evidence) {
//            toAddStart(toAdd, v);
//        }
//        for (Variable v : order) {
//            toAddStart(toAdd, v);
//        }

        removeIndependentVariables(toAdd,start,ball,evidence);

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

//        for (Variable v : order) {
//            factors.add(new Factor(v));
//        }

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
            newFactors.remove(newFactor);
            for (Factor factor : newFactors) {
                numMultiply += newFactor.multiply(factor);
            }
            numAdds += newFactor.sumUp(ord);
            factors.add(newFactor);
        }

        Factor newFactor=factors.get(0);
        factors.remove(newFactor);
        for(Factor fr: factors)
            numMultiply += newFactor.multiply(fr);
//        Factor newFactor = factors.get(0);
//        numAdds+= newFactor.marginalize();
        numAdds+=newFactor.normalize();
        newFactor.printFactor();

        int index=start.outcomes.indexOf(queryOutcome.get(0));
        probability = Double.parseDouble(newFactor.getTable()[index+1][newFactor.getTable()[0].length - 1]);
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

    private static void removeIndependentVariables(ArrayList<Variable> toRemove, Variable start, bayesBall ball,ArrayList<Variable> evidence) {
        toRemove.removeIf(v -> ball.bayesBall(v, start, evidence));
    }

    private static void sortFactors(ArrayList<Factor> factors) {
        Collections.sort(factors, new Comparator<Factor>() {
            @Override
            public int compare(Factor f1, Factor f2) {
                return Integer.compare(f1.getVariables().size(), f2.getVariables().size());
            }
        });
    }
}


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

        removeIndependentVariables(toAdd,start,ball,evidence);

        for (Variable v : toAdd) {
            System.out.println("name: " + v.name);
        }

        for (Variable v : toAdd) {
            factors.add(new Factor(v));
        }

        boolean check=checkForBuiltIn(factors,evidence,start,outcome,queryOutcome,myWriter);
        if (check)
            return;

        for (Factor v : factors) {
            Collections.reverse(v.getVariables());
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

        for (Factor factor : factors) {
            if(factor.getVariables().size()==1 && evidence.contains(factor.getVariables().get(0))){
                factors.remove(factor);
            }
        }

        System.out.println("after delete");
        for (Factor factor : factors) {
            factor.printFactor();
        }

        int index=1;
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
            System.out.println(index+":");
//            index++;
            newFactor.printFactor();
            newFactors.remove(newFactor);
            for (Factor factor : newFactors) {
                System.out.println("I multipy:");
                newFactor.printFactor();
                System.out.println("with:");
                factor.printFactor();
                numMultiply += newFactor.multiply(factor);
                index++;
                System.out.println("and i get this:");
                newFactor.printFactor();
            }
            numAdds += newFactor.sumUp(ord);
            factors.add(newFactor);
            newFactors.clear();
        }

        Factor newFactor=factors.get(0);
        index++;
        System.out.println(index+":");
        newFactor.printFactor();
        factors.remove(newFactor);
        if(!factors.isEmpty()){
            for(Factor fr: factors) {
                numMultiply += newFactor.multiply(fr);
                index++;
                System.out.println(index+":");
                fr.printFactor();
            }
        }
//        Factor newFactor = factors.get(0);
//        numAdds+= newFactor.marginalize();
        numAdds+=newFactor.normalize();
        newFactor.printFactor();

        int index1=start.outcomes.indexOf(queryOutcome.get(0));
        probability = Double.parseDouble(newFactor.getTable()[index1+1][newFactor.getTable()[0].length - 1]);
        String roundedNumber = String.format("%.5f", probability);
        myWriter.write(roundedNumber + "," + numAdds + "," + numMultiply + "\n");
        System.out.println("finish");
    }

    private boolean checkForBuiltIn(ArrayList<Factor> factors, ArrayList<Variable> evidence, Variable start, ArrayList<String> outcome, ArrayList<String> queryOutcome, FileWriter myWriter) throws IOException {
        double probability = 0;
        boolean flag = true;

        for (Factor factor : factors) {
            if (factor.getVariables().contains(start)) {
                // Check if all evidence variables are contained in this factor
                for (Variable v : evidence) {
                    if (!factor.getVariables().contains(v)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    String[][] table = factor.getTable();

                    // Iterate through each row in the table
                    for (int i = 1; i < table.length; i++) {
                        String[] row = table[i];
                        boolean match = true;

                        // Check if the row matches the evidence and query outcomes
                        for (int j = 0; j < factor.getVariables().size(); j++) {
                            Variable var = factor.getVariables().get(j);

                            // Check against evidence
                            if (evidence.contains(var)) {
                                int evidenceIndex = evidence.indexOf(var);
                                if (!row[j].equals(outcome.get(evidenceIndex))) {
                                    match = false;
                                    break;
                                }
                            }

                            // Check against query outcome
                            if (var.equals(start)) {
                                int queryIndex = factor.getVariables().indexOf(start);
                                if (!row[queryIndex].equals(queryOutcome.get(0))) {
                                    match = false;
                                    break;
                                }
                            }
                        }

                        // If row matches both evidence and query outcomes, add its probability
                        if (match) {
                            probability += Double.parseDouble(row[row.length - 1]);
                        }
                    }

                    // Write the probability to the file
                    if (probability > 0) {
                        myWriter.write(probability+",0,0");
                        myWriter.write("\n");
                        return true; // If found, return true
                    }
                }
            }
        }
        return false;
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
        factors.sort(new Comparator<Factor>() {
            @Override
            public int compare(Factor f1, Factor f2) {
                return Integer.compare(f1.getTableSize(), f2.getTableSize());
            }
        });
    }
}


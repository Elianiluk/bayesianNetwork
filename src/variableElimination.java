import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class variableElimination {

    // function to calculate the
    public void variableElimination(Variable start, ArrayList<Variable> order, ArrayList<Variable> evidence, ArrayList<String> outcome, FileWriter myWriter,ArrayList<String> queryOutcome) throws IOException {
        int numAdds = 0, numMultiply = 0;
        double probability = 0;
        ArrayList<Factor> factors = new ArrayList<>(); // array list for the factors
        ArrayList<Variable> relevantVariables = new ArrayList<>(); // list for the relevant variables for the query

        // add the ancestors of the query or evidence variables as they are important for the solution of the query
        addRelevantVariables(relevantVariables, start);
        for (Variable v : evidence)
            addRelevantVariables(relevantVariables,v);
        bayesBall ball = new bayesBall();

        // remove independent variables with the query variable
        removeIndependentVariables(relevantVariables,start,ball,evidence);

        // create the factors for the relevant variables
        for (Variable v : relevantVariables) {
            factors.add(new Factor(v));
        }

        for (Factor v : factors) {
            Collections.reverse(v.getVariables());
        }

        // check if the query is already in the one of the tables, if so return its value and end the program
        boolean check=checkForBuiltIn(factors,evidence,start,outcome,queryOutcome,myWriter);
        if (check)
            return;

        // placing the outcomes of the evidence in the factors as we begin the variable elimination algorithm
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

        // loop over the order variables and eliminate one at a time
        for (Variable ord : order) {
            // if the order variable isn't related to the query we can skip on him as there isn't factor that contains him
            if (!relevantVariables.contains(ord)) {
                continue;
            }

            // get only the factors that relevant for the order variables which we want to eliminate
            ArrayList<Factor> newFactors = new ArrayList<>();
            for (Factor factor : factors) {
                if (factor.getVariables().contains(ord)) {
                    newFactors.add(factor);
                }
            }
            sortFactors(newFactors); // sort the factors
            factors.removeAll(newFactors); // remove the factors as we dont need them anymore

            // multiplying the factors related to the order variable
            Factor newFactor = newFactors.get(0);
            newFactor.printFactor();
            newFactors.remove(newFactor);
            for (Factor factor : newFactors) {
                System.out.println("I multipy:");
                newFactor.printFactor();
                System.out.println("with:");
                factor.printFactor();
                numMultiply += newFactor.multiply(factor);
                System.out.println("and i get this:");
                newFactor.printFactor();
            }

            //marginalize the new factor to eliminate the order variable
            numAdds += newFactor.sumUp(ord);
            factors.add(newFactor); // add the new factor
            newFactors.clear();
        }

        Factor newFactor=factors.get(0);
        newFactor.printFactor();
        factors.remove(newFactor);

        // if there is more than one factor remain, we need to multiply them
        if(!factors.isEmpty()){
            for(Factor fr: factors) {
                numMultiply += newFactor.multiply(fr);
                fr.printFactor();
            }
        }

        // normalize the new factor
        numAdds+=newFactor.normalize();
        newFactor.printFactor();

        // extract the results from the final table and store it in the output file
        int index1=start.outcomes.indexOf(queryOutcome.get(0));
        probability = Double.parseDouble(newFactor.getTable()[index1+1][newFactor.getTable()[0].length - 1]);
        String roundedNumber = String.format("%.5f", probability);
        myWriter.write(roundedNumber + "," + numAdds + "," + numMultiply);
        myWriter.write("\n");
        System.out.println(roundedNumber + "," + numAdds + "," + numMultiply);
    }

    // function to check if the query is already in one of the tables, if so return its value right away
    private boolean checkForBuiltIn(ArrayList<Factor> factors, ArrayList<Variable> evidence, Variable queryVariable, ArrayList<String> outcome, ArrayList<String> queryOutcome, FileWriter myWriter) throws IOException {
        double probability = 0;
        boolean flag = true;

        for (Factor factor : factors) {
            if (factor.getVariables().contains(queryVariable)) {
                // Check if all evidence variables are contained in this factor
                for (Variable v : evidence) {
                    if (!factor.getVariables().contains(v)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    boolean fl=true;
                    for (Variable v : factor.getVariables()) {
                        if(v!=queryVariable && !evidence.contains(v)){
                            fl=false;
                            break;
                        }
                    }
                    if (!fl) {
                        break;
                    }
                    String[][] table = factor.getTable();

                    // Iterate through each row in the table
                    for (int i = 1; i < table.length; i++) {
                        String[] row = table[i];
                        boolean match = true;

                        ArrayList<Variable> thisVariables=new ArrayList<>();
                        for(int s=0;s<table[0].length-1;s++){
                            String str=table[0][s];
                            for(Variable var: factor.getVariables()){
                                if(str.equals(var.name))
                                    thisVariables.add(var);
                            }
                        }

                        // Check if the row matches the evidence and query outcomes
                        for (int j = 0; j < thisVariables.size(); j++) {
                            Variable var = thisVariables.get(j);

                            // Check against evidence
                            if (evidence.contains(var)) {
                                int evidenceIndex = evidence.indexOf(var);
                                if (!row[j].equals(outcome.get(evidenceIndex))) {
                                    match = false;
                                    break;
                                }
                            }

                            // Check against query outcome
                            if (var.equals(queryVariable)) {
                                int queryIndex =thisVariables.indexOf(queryVariable);
                                if (!row[queryIndex].equals(queryOutcome.get(0))) {
                                    match = false;
                                    break;
                                }
                            }
                        }

                        // If row matches both evidence and query outcomes, add its probability
                        if (match) {
                            for(Variable v : factor.getVariables())
                                System.out.println(v.name);
                            probability += Double.parseDouble(row[row.length - 1]);
                        }
                    }

                    // Write the probability to the file
                    if (probability >= 0) {
                        String roundedNumber = String.format("%.5f", probability);
                        myWriter.write(roundedNumber+",0,0");
                        myWriter.write("\n");
                        factor.printFactor();
                        return true; // If found, return true
                    }
                }
            }
        }
        return false;
    }

    // function to add relevant variables
    private static void addRelevantVariables(ArrayList<Variable> relevantVariables, Variable queryVariable) {
        if (relevantVariables.contains(queryVariable)) {
            return;
        }
        relevantVariables.add(queryVariable);
        for (Variable parent : queryVariable.parents) {
            addRelevantVariables(relevantVariables, parent);
        }
    }

    // function to remove independent variables from the query variable
    private static void removeIndependentVariables(ArrayList<Variable> toRemove, Variable queryVariable, bayesBall ball,ArrayList<Variable> evidence) {
        toRemove.removeIf(v -> ball.bayesBall(v, queryVariable, evidence));
    }

    // function to sort factors by their table size
    private static void sortFactors(ArrayList<Factor> factors) {
        factors.sort(new Comparator<Factor>() {
            @Override
            public int compare(Factor f1, Factor f2) {
                return Integer.compare(f1.getTableSize(), f2.getTableSize());
            }
        });
    }
}


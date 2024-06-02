import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factor {
    private List<Variable> variables;
    private double[] values;

    public Factor(List<Variable> variables, double[] values) {
        this.variables = new ArrayList<>(variables);
        this.values = values.clone();
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public double[] getValues() {
        return values;
    }

    // Multiply two factors
    public Factor multiply(Factor other) {
        List<Variable> newVariables = new ArrayList<>(variables);
        for (Variable var : other.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }

        int newSize = 1;
        for (Variable var : newVariables) {
            newSize *= var.outcomes.size();
        }

        double[] newValues = new double[newSize];

        for (int i = 0; i < values.length; i++) {
            Map<Variable, Integer> assignment = indexToAssignment(i, variables);
            for (int j = 0; j < other.values.length; j++) {
                Map<Variable, Integer> otherAssignment = indexToAssignment(j, other.variables);
                if (isConsistent(assignment, otherAssignment)) {
                    Map<Variable, Integer> mergedAssignment = new HashMap<>(assignment);
                    mergedAssignment.putAll(otherAssignment);
                    int newIndex = assignmentToIndex(mergedAssignment, newVariables);
                    newValues[newIndex] += values[i] * other.values[j];
                }
            }
        }

        return new Factor(newVariables, newValues);
    }

    // Marginalize (sum out) a variable
    public Factor marginalize(Variable var) {
        if (!variables.contains(var)) {
            return this; // No need to marginalize if the variable is not in the factor
        }

        List<Variable> newVariables = new ArrayList<>(variables);
        newVariables.remove(var);

        int newSize = 1;
        for (Variable v : newVariables) {
            newSize *= v.outcomes.size();
        }

        double[] newValues = new double[newSize];

        for (int i = 0; i < values.length; i++) {
            Map<Variable, Integer> assignment = indexToAssignment(i, variables);
            assignment.remove(var);
            int newIndex = assignmentToIndex(assignment, newVariables);
            newValues[newIndex] += values[i];
        }

        return new Factor(newVariables, newValues);
    }

    // Incorporate evidence
    public void incorporateEvidence(Variable var, int value) {
        if (!variables.contains(var)) {
            return; // No need to incorporate evidence if the variable is not in the factor
        }

        int varIndex = variables.indexOf(var);
        int stepSize = values.length / var.outcomes.size();
        for (int i = 0; i < values.length; i++) {
            int assignmentValue = (i / stepSize) % var.outcomes.size();
            if (assignmentValue != value) {
                values[i] = 0;
            }
        }
    }

    // Normalize the factor
    public double normalize() {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] /= sum;
        }
        return sum;
    }

    // Get the probability of a specific assignment
    public double getProbability(Variable var, int value) {
        if (!variables.contains(var)) {
            return 0.0;
        }

        int varIndex = variables.indexOf(var);
        int stepSize = values.length / var.outcomes.size();
        double probability = 0;
        for (int i = 0; i < values.length; i++) {
            int assignmentValue = (i / stepSize) % var.outcomes.size();
            if (assignmentValue == value) {
                probability += values[i];
            }
        }
        return probability;
    }

    // Helper methods

    // Convert an index to a variable assignment
    private Map<Variable, Integer> indexToAssignment(int index, List<Variable> variables) {
        Map<Variable, Integer> assignment = new HashMap<>();
        for (Variable var : variables) {
            int domainSize = var.outcomes.size();
            assignment.put(var, index % domainSize);
            index /= domainSize;
        }
        return assignment;
    }

    // Convert a variable assignment to an index
    private int assignmentToIndex(Map<Variable, Integer> assignment, List<Variable> variables) {
        int index = 0;
        int multiplier = 1;
        for (Variable var : variables) {
            index += assignment.get(var) * multiplier;
            multiplier *= var.outcomes.size();
        }
        return index;
    }

    // Check if two assignments are consistent
    private boolean isConsistent(Map<Variable, Integer> assignment1, Map<Variable, Integer> assignment2) {
        for (Map.Entry<Variable, Integer> entry : assignment1.entrySet()) {
            if (assignment2.containsKey(entry.getKey()) && !assignment2.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}

import java.util.*;

public class Factor {
    private List<Variable> variables;
    private String[][] table;

    public Factor(Variable variable) {
        this.variables = new ArrayList<>();
        this.variables.add(variable);
        this.variables.addAll(variable.parents);

        int numRows = variable.table.length;
        int numCols = variable.table[0].length;
        this.table = new String[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                this.table[i][j] = variable.table[i][j];
            }
        }
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public String[][] getTable() {
        return table;
    }

    public int multiply(Factor other) {
        int numMultiplies = 0;

        List<Variable> newVariables = new ArrayList<>(this.variables);
        for (Variable var : other.getVariables()) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }

        int newTableSize = 1;
        for (Variable var : newVariables) {
            newTableSize *= var.outcomes.size();
        }
        double[] newTable = new double[newTableSize];

        Map<Variable, Integer> varIndices = new HashMap<>();
        for (int i = 0; i < newVariables.size(); i++) {
            varIndices.put(newVariables.get(i), i);
        }

        for (int i = 0; i < newTable.length; i++) {
            int[] assignment = new int[newVariables.size()];
            int index = i;
            for (int j = newVariables.size() - 1; j >= 0; j--) {
                assignment[j] = index % newVariables.get(j).outcomes.size();
                index /= newVariables.get(j).outcomes.size();
            }

            int index1 = 0, index2 = 0;
            for (int j = 0; j < this.variables.size(); j++) {
                index1 = index1 * this.variables.get(j).outcomes.size() + assignment[varIndices.get(this.variables.get(j))];
            }
            for (int j = 0; j < other.getVariables().size(); j++) {
                index2 = index2 * other.getVariables().get(j).outcomes.size() + assignment[varIndices.get(other.getVariables().get(j))];
            }

            double value1 = 0, value2 = 0;
            try {
                value1 = Double.parseDouble(this.table[index1][this.table[0].length - 1]);
            } catch (NumberFormatException e) {
                value1 = 1; // Default to 1 if not a number
            }
            try {
                value2 = Double.parseDouble(other.table[index2][other.table[0].length - 1]);
            } catch (NumberFormatException e) {
                value2 = 1; // Default to 1 if not a number
            }

            newTable[i] = value1 * value2;
            numMultiplies++;
        }

        this.variables = newVariables;
        this.table = new String[newTable.length][this.table[0].length];
        for (int i = 0; i < newTable.length; i++) {
            this.table[i][this.table[0].length - 1] = String.format("%.5f", newTable[i]);
        }
        return numMultiplies;
    }

    public int marginalize(Variable var) {
        int numAdds = 0;
        List<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.remove(var);
        int newTableSize = 1;
        for (Variable v : newVariables) {
            newTableSize *= v.outcomes.size();
        }
        double[] newTable = new double[newTableSize];

        Map<Variable, Integer> varIndices = new HashMap<>();
        for (int i = 0; i < this.variables.size(); i++) {
            varIndices.put(this.variables.get(i), i);
        }

        for (int i = 0; i < this.table.length; i++) {
            int[] assignment = new int[this.variables.size()];
            int index = i;
            for (int j = this.variables.size() - 1; j >= 0; j--) {
                assignment[j] = index % this.variables.get(j).outcomes.size();
                index /= this.variables.get(j).outcomes.size();
            }

            int newIndex = 0;
            for (int j = 0; j < newVariables.size(); j++) {
                newIndex = newIndex * newVariables.get(j).outcomes.size() + assignment[varIndices.get(newVariables.get(j))];
            }
            try {
                newTable[newIndex] += Double.parseDouble(this.table[i][this.table[0].length - 1]);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
            numAdds++;
        }

        this.variables = newVariables;
        this.table = new String[newTable.length][this.table[0].length];
        for (int i = 0; i < newTable.length; i++) {
            this.table[i][this.table[0].length - 1] = String.format("%.5f", newTable[i]);
        }
        return numAdds;
    }

    public void incorporateEvidence(Variable var, int value) {
        if (!variables.contains(var)) {
            return;
        }

        int varIndex = variables.indexOf(var);
        int stepSize = table.length / var.outcomes.size();
        for (int i = 0; i < table.length; i++) {
            int assignmentValue = (i / stepSize) % var.outcomes.size();
            if (assignmentValue != value) {
                table[i][this.table[0].length - 1] = "0";
            }
        }
    }

    public void normalize() {
        double sum = 0;
        for (String[] row : table) {
            try {
                sum += Double.parseDouble(row[this.table[0].length - 1]);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        for (int i = 0; i < table.length; i++) {
            try {
                table[i][this.table[0].length - 1] = String.format("%.5f", Double.parseDouble(table[i][this.table[0].length - 1]) / sum);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
    }

    public double getProbability(Variable var, int value) {
        if (!variables.contains(var)) {
            return 0.0;
        }

        int varIndex = variables.indexOf(var);
        int stepSize = table.length / var.outcomes.size();
        double probability = 0;
        for (int i = 0; i < table.length; i++) {
            int assignmentValue = (i / stepSize) % var.outcomes.size();
            if (assignmentValue == value) {
                try {
                    probability += Double.parseDouble(table[i][this.table[0].length - 1]);
                } catch (NumberFormatException e) {
                    // Skip invalid values
                }
            }
        }
        return probability;
    }

    private Map<Variable, Integer> indexToAssignment(int index, List<Variable> variables) {
        Map<Variable, Integer> assignment = new HashMap<>();
        for (Variable var : variables) {
            int domainSize = var.outcomes.size();
            assignment.put(var, index % domainSize);
            index /= domainSize;
        }
        return assignment;
    }

    private int assignmentToIndex(Map<Variable, Integer> assignment, List<Variable> variables) {
        int index = 0;
        int multiplier = 1;
        for (Variable var : variables) {
            index += assignment.get(var) * multiplier;
            multiplier *= var.outcomes.size();
        }
        return index;
    }

    private boolean isConsistent(Map<Variable, Integer> assignment1, Map<Variable, Integer> assignment2) {
        for (Map.Entry<Variable, Integer> entry : assignment1.entrySet()) {
            if (assignment2.containsKey(entry.getKey()) && !assignment2.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public void evidenceDelete(Variable evi, String value) {
        if (!variables.contains(evi)) {
            return;
        }
        if(!evi.outcomes.contains(value))
            return;
        ArrayList<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.remove(evi);
        this.variables = newVariables;
        String [][] newTable = new String[(this.table.length/ evi.numberOfOutcomes)+1][this.table[0].length-1];
        int lol=0;
        for (int j = 0; j < newVariables.size(); j++) {
//            if(this.variables.get(j).name.equals(evi.name))
//                continue;
//            newTable[0][lol] = this.variables.get(j).name;
//            lol++;
              newTable[0][j] = newVariables.get(j).name;
        }

        newTable[0][newTable[0].length-1]="pro";
        int indexOf=0;
        for(int l=0;l<this.table[0].length;l++){
            if(this.table[0][l].equals(evi.name)) {
                indexOf = l;
                break;
            }
        }

        int index=1;
        for(int i=1;i<this.table.length;i++) {
            String [] line=this.table[i];
            if(line[indexOf].equals(value)){
                String [] newLine = new String[line.length-1];
                int s=0;
                for(int j=0;j<line.length;j++){
                    if(j==indexOf)
                        continue;
                    newLine[s]=line[j];
                    s++;
                }
                newTable[index]=newLine;
                index++;
            }
        }
        this.table = newTable;
    }

    public void printFactor(){
        if(this.table == null) {
            System.err.println("Error: Cannot print a null table.");
            return;
        }
        // Print the header
        for (String[] row : this.table) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}

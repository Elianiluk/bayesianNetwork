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

    public int getTableSize(){
        return table.length-1;
    }

    public String[][] getTable() {
        return table;
    }

    public int multiply(Factor other) {
        if(other.getVariables().size()>this.getVariables().size()){
            return this.otherMultiply(other);
        }

        int numMultiplies = 0;

        List<Variable> newVariables = new ArrayList<>();
        // Combine variables from both factors
        for(int i=0;i<this.table[0].length-1;i++){
            String str=this.table[0][i];
            for(Variable var: this.variables){
                if(str.equals(var.name))
                    newVariables.add(var);
            }
        }
        for (Variable var : other.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
            }
        }

        ArrayList<Variable> otherVariables=new ArrayList<>();
        for(int i=0;i<other.table[0].length-1;i++){
            String str=other.table[0][i];
            for(Variable var: other.variables){
                if(str.equals(var.name))
                    otherVariables.add(var);
            }
        }

        ArrayList<Variable> thisVariables=new ArrayList<>();
        for(int i=0;i<this.table[0].length-1;i++){
            String str=this.table[0][i];
            for(Variable var: this.variables){
                if(str.equals(var.name))
                    thisVariables.add(var);
            }
        }

        // Calculate the size of the new table based on combined variables
        int newTableSize = 1;
        for (Variable var : newVariables) {
            newTableSize *= var.numberOfOutcomes;
        }

        // Initialize a new table to hold multiplied values
        String[][] newTable = new String[newTableSize + 1][newVariables.size() + 1];

        // Populate the header row of the new table with variable names and "pro" for probability
        for (int j = 0; j < newVariables.size(); j++) {
            newTable[0][j] = newVariables.get(j).name;
        }
        newTable[0][newTable[0].length - 1] = "pro";

        // Identify common variables between this factor and the other factor
        List<Variable> commonVariables = new ArrayList<>();
        for (Variable var : newVariables) {
            if (this.variables.contains(var) && other.variables.contains(var)) {
                commonVariables.add(var);
            }
        }

        // Iterate through each row in this factor's table
        for (int i = 1; i < this.table.length; i++) {
            String[] thisLine = this.table[i];
            double prob1 = Double.parseDouble(thisLine[thisLine.length - 1]);

            // Iterate through each row in the other factor's table
            for (int j = 1; j < other.table.length; j++) {
                String[] otherLine = other.table[j];
                double prob2 = Double.parseDouble(otherLine[otherLine.length - 1]);

                // Check if the current rows can be multiplied (consistent assignments for common variables)
                boolean consistent = true;
                for (Variable var : commonVariables) {
                    int thisIndex = thisVariables.indexOf(var);
                    int otherIndex = otherVariables.indexOf(var);
                    if (!thisLine[thisIndex].equals(otherLine[otherIndex])) {
                        consistent = false;
                        break;
                    }
                }

                if (consistent) {
                    // Create a new row in the new table for the multiplied result
                    String[] newRow = new String[newTable[0].length];

                    // Fill the new row according to the new variable order
                    for (int k = 0; k < newVariables.size(); k++) {
                        Variable newVar = newVariables.get(k);
                        if (this.variables.contains(newVar)) {
                            newRow[k] = thisLine[thisVariables.indexOf(newVar)];
                        } else if (other.variables.contains(newVar)) {
                            newRow[k] = otherLine[otherVariables.indexOf(newVar)];
                        }
                    }

                    // Multiply the probabilities
                    newRow[newTable[0].length - 1] = String.format(String.valueOf(prob1 * prob2));

                    // Add the new row to the new table
                    newTable[numMultiplies + 1] = newRow;
                    numMultiplies++;
                }
            }
        }

        // Update this factor's variables and table with the new variables and table
        this.variables = newVariables;
        this.table = newTable;

        return numMultiplies;
    }

    public int otherMultiply(Factor other) {
        int numMultiplies = 0;

        List<Variable> newVariables = new ArrayList<>();
        // Combine variables from both factors
        for(int i=0;i<other.table[0].length-1;i++) {
            String str = other.table[0][i];
            for (Variable var : other.variables) {
                if (str.equals(var.name)) {
                    newVariables.add(var);
                    break;
                }
            }
        }
        for (Variable var : this.variables) {
            if (!newVariables.contains(var)) {
                newVariables.add(var);
                break;
            }
        }

        ArrayList<Variable> otherVariables=new ArrayList<>();
        for(int i=0;i<other.table[0].length-1;i++){
            String str=other.table[0][i];
            for(Variable var: other.variables){
                if(str.equals(var.name))
                    otherVariables.add(var);
            }
        }

        ArrayList<Variable> thisVariables=new ArrayList<>();
        for(int i=0;i<this.table[0].length-1;i++){
            String str=this.table[0][i];
            for(Variable var: this.variables){
                if(str.equals(var.name))
                    thisVariables.add(var);
            }
        }

        // Calculate the size of the new table based on combined variables
        int newTableSize = 1;
        for (Variable var : newVariables) {
            newTableSize *= var.numberOfOutcomes;
        }

        // Initialize a new table to hold multiplied values
        String[][] newTable = new String[newTableSize + 1][newVariables.size() + 1];

        // Populate the header row of the new table with variable names and "pro" for probability
        for (int j = 0; j < newVariables.size(); j++) {
            newTable[0][j] = newVariables.get(j).name;
        }
        newTable[0][newTable[0].length - 1] = "pro";

        // Identify common variables between this factor and the other factor
        List<Variable> commonVariables = new ArrayList<>();
        for (Variable var : newVariables) {
            if (this.variables.contains(var) && other.variables.contains(var)) {
                commonVariables.add(var);
            }
        }

        // Iterate through each row in this factor's table
        for (int i = 1; i < other.table.length; i++) {
            String[] thisLine = other.table[i];
            double prob1 = Double.parseDouble(thisLine[thisLine.length - 1]);

            // Iterate through each row in the other factor's table
            for (int j = 1; j < this.table.length; j++) {
                String[] otherLine = this.table[j];
                double prob2 = Double.parseDouble(otherLine[otherLine.length - 1]);

                // Check if the current rows can be multiplied (consistent assignments for common variables)
                boolean consistent = true;
                for (Variable var : commonVariables) {
                    int thisIndex = otherVariables.indexOf(var);
                    int otherIndex = thisVariables.indexOf(var);
                    if (!thisLine[thisIndex].equals(otherLine[otherIndex])) {
                        consistent = false;
                        break;
                    }
                }

                if (consistent) {
                    // Create a new row in the new table for the multiplied result
                    String[] newRow = new String[newTable[0].length];

                    // Fill the new row according to the new variable order
                    for (int k = 0; k < newVariables.size(); k++) {
                        Variable newVar = newVariables.get(k);
                        if (other.variables.contains(newVar)) {
                            newRow[k] = thisLine[otherVariables.indexOf(newVar)];
                        } else if (this.variables.contains(newVar)) {
                            newRow[k] = otherLine[thisVariables.indexOf(newVar)];
                        }
                    }

                    // Multiply the probabilities
                    newRow[newTable[0].length - 1] = String.format(String.valueOf(prob1 * prob2));

                    // Add the new row to the new table
                    newTable[numMultiplies + 1] = newRow;
                    numMultiplies++;
                }
            }
        }

        // Update this factor's variables and table with the new variables and table
        this.variables = newVariables;
        this.table = newTable;

        return numMultiplies;
    }

    public int sumUp(Variable var) {
        // Create a list of variables excluding the given variable
        ArrayList<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.remove(var);

        // Calculate the size of the new table after summing up
        int newTableSize = 1;
        for (Variable v : newVariables) {
            newTableSize *= v.numberOfOutcomes;
        }

        // Initialize a new table for the summed-up factor
        String[][] newTable = new String[newTableSize + 1][newVariables.size() + 1];

        // Populate the header row of the new table with variable names and "pro" for probability
        for (int j = 0; j < newVariables.size(); j++) {
            newTable[0][j] = newVariables.get(j).name;
        }
        newTable[0][newTable[0].length - 1] = "pro";

        // Map to store sums of probabilities for each assignment of newVariables
        Map<String, Double> sumMap = new LinkedHashMap<>();

        // Iterate through each row in the current factor's table
        for (int i = 1; i < this.table.length; i++) {
            String[] row = this.table[i];

            // Build key for the assignment of variables excluding the given variable
            StringBuilder sb = new StringBuilder();
            for (Variable newVar : newVariables) {
                int originalIndex = this.variables.indexOf(newVar);
                sb.append(row[originalIndex]);
                sb.append(",");  // Use comma as a delimiter for better separation
            }
            String assignmentKey = sb.toString();

            // Update the sum for this assignment key
            double currentProbability;
            try {
                currentProbability = Double.parseDouble(row[row.length - 1]);
            } catch (NumberFormatException e) {
                // Handle parsing errors gracefully
                continue;  // Skip this row if parsing fails
            }

            double sum = sumMap.getOrDefault(assignmentKey, 0.0);
            sum += currentProbability;
            sumMap.put(assignmentKey, sum);
        }

        // Populate the new table with summed probabilities
        int newIndex = 1;
        for (Map.Entry<String, Double> entry : sumMap.entrySet()) {
            String assignmentKey = entry.getKey();
            double summedProbability = entry.getValue();

            String[] newRow = new String[newTable[0].length];
            String[] assignmentValues = assignmentKey.split(",");

            for (int j = 0; j < newVariables.size(); j++) {
                newRow[j] = assignmentValues[j];
            }
            newRow[newTable[0].length - 1] = String.format(String.valueOf(summedProbability));
            newTable[newIndex++] = newRow;
        }

        // Update this factor's variables and table with the new variables and table
        this.variables = newVariables;
        this.table = newTable;

        // Return the number of additions performed
        return this.table.length - 1;
    }

    public int normalize() {
        double sum = 0;
        int numOfAdds = 0;
        int index=0;
        for (String[] row : table) {
            if(index==0){
                index++;
                continue;
            }
            try {
                sum += Double.parseDouble(row[this.table[0].length - 1]);
                numOfAdds++;
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        for (int i = 1; i < table.length; i++) {
            try {
                table[i][this.table[0].length - 1] = String.format(String.valueOf(Double.parseDouble(table[i][this.table[0].length - 1]) / sum));
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        return numOfAdds-1;
    }

    public void evidenceDelete(Variable evi, String value) {
        if (!variables.contains(evi)) {
            return;
        }
        if(!evi.outcomes.contains(value))
            return;

        ArrayList<Variable> newVariables = new ArrayList<>(this.variables);
        newVariables.remove(evi);
        String [][] newTable = new String[(this.table.length/ evi.numberOfOutcomes)+1][this.table[0].length-1];
        for (int j = 0; j < newVariables.size(); j++) {
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
//        Collections.reverse(newVariables);
        this.variables = newVariables;
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
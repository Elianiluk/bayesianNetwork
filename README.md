# Bayesian Networ

## Files Overview

- **Ex1.java**: Main class for processing Bayesian network queries from `input.txt` and xml files and writing results to `output.txt`.
  
- **Variables.java**: Defines the Variable class for Bayesian network nodes, managing outcomes and probability tables.
  
- **bayesBall.java**: Implements Bayesian Ball algorithm to test variable independence in Bayesian networks.
  
- **Factor.java**: Manages factors in probabilistic graphical models, supporting operations like multiplication and normalization.
  
- **variableElimination.java**: Implements variable elimination algorithm for Bayesian network inference.

- **Definition.java**: Defines structure for capturing variable definitions and conditional probabilities in Bayesian networks.
  
- **input.txt**: Input file containing queries and evidence configurations.
  
- **output.txt**: Output file where results of Bayesian network queries are written.
  
- **alarm_net.xml**: Example XML representation of a Bayesian network structure and probabilities.

## Usage

**Setup and Execution:**

1. Ensure Java is installed on your system.
2. Compile the code:
   ```bash
   javac Ex1.java Variables.java bayesBall.java Factor.java variableElimination.java Definition.java
   ```
3. Run the program:
   ```bash
   java Ex1
   ```

**Input Format:**

Modify `input.txt` to include queries:
- For Variable Elimination: `P(variables | evidence)`
- For Bayesian Ball: `B-variable1-variable2 | evidence`

**Output:**

Results are written to `output.txt`, with each line corresponding to `yes` or `no` for each query.

## Example Queries

**Variable Elimination:**

```plaintext
P(B=t | A=f)
```
Computes probability of B=true given evidence A=false.

**Bayesian Ball:**

```plaintext
B-E | J=t,K=f
```
Checks independence between B and E given evidence J=true and K=false.

## Dependencies

Standard Java libraries (`java.io.*`, `java.util.*`, `javax.xml.parsers.*`, `org.w3c.dom.*`) are used for file operations, data structures, and XML parsing.

## Notes

- Ensure `input.txt` and `output.txt` follow specified query formats.
- Bayesian network structure and probabilities are read from XML format (`alarm_net.xml`).

## Contributors

- Developed by Elian Iluk.
- Contact: elian10119@gmail.com.

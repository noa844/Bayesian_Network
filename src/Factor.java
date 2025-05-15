//noa.honigstein@gmail.com

import java.util.*;

public class Factor implements Comparable {
    private List<Variable> _variables;
    private Map<Map<String, String>, Double> _factor;

    public Factor() {
    }

    public Factor(Collection<Variable> variables) {
        this._variables = new ArrayList<>(variables);
        this._factor = new LinkedHashMap<>();
    }

    public Factor(Collection<Variable> variables, Map<Map<String, String>, Double> table) {
        this._variables = new ArrayList<>(variables);
        this._factor = new LinkedHashMap<>(table);
    }


    public void setFactor(Map<Map<String, String>, Double> table) {
        _factor.clear();
        _factor.putAll(table);
    }

    public void addRow(Map<String, String> assignment, double prob) {
        _factor.put((new LinkedHashMap<>(assignment)), prob);
    }


    public List<Variable> getVariables() {
        return _variables;
    }

    public Map<Map<String, String>, Double> getfactor() {
        return _factor;
    }

    public double getProb(Map<String, String> assignment) {
        Double prob = _factor.get(assignment);
        if (prob == null) {
            System.out.println("prob value doesn't exist for this key");
            return 1.0;
        } else {
            return prob;
        }
    }


    public int getSize() {

        return _factor.size();
    }

    @Override
    public int compareTo(Object o) {
        Factor f2 = (Factor) o;
        int f1Size = this.getSize();
        int f2Size = f2.getSize();

        if (f1Size > f2Size) {
            return 1;
        } else if (f1Size < f2Size) {
            return -1;

        } else { // In case of tie, compare ASCII sum of variable names
            int f1Ascii = this.getVarsAscii(_variables);
            int f2Ascii = f2.getVarsAscii(f2.getVariables());
            return Integer.compare(f1Ascii, f2Ascii);
        }
    }

    private int getVarsAscii(List<Variable> vars) {
        int sum = 0;
        for (Variable var : vars) {
            for (char c : var.getName().toCharArray()) {
                sum += c;
            }
        }
        return sum;
    }
    // Variable elimination: remove 'var' from this factor by summing over it
    public Factor eliminateVar(String var) {
        List<Variable> newVars = new ArrayList<>();//filter variables
        int i = 0;
        while (i < _variables.size()) {
            Variable v = _variables.get(i);
            if (!v.getName().equals(var)) {
                newVars.add(v);
            }
            i++;
        }
        List<Map<String, String>> newKeys = Tools.generateCombinations(newVars);
        Factor newFactor = new Factor(newVars);

        for (Map<String, String> newFactorKey : newKeys) {
            double sum = 0.0;
            for (Map<String, String> oldFactorKey : _factor.keySet()) { //traverse each row of the old table with the keys corresponding to the keys of the new table.
                if (oldFactorKey.entrySet().containsAll(newFactorKey.entrySet())) {
                    sum += _factor.get(oldFactorKey);
                }
            }
            newFactor.addRow(newFactorKey, sum);
        }

        return newFactor;

    }
    // Normalize the factor so that probabilities sum to 1
    public void normalize() {
        double alpha = 0.0;
        //total probabilities sum
        for (double prob : _factor.values()) {
            alpha += prob;
        }

        if (alpha != 0.0) {
            // Normalize each probability
            for (Map<String, String> key : _factor.keySet()) {
                _factor.put(key, _factor.get(key) / alpha);

            }
        }
    }



    public Map<Map<String, String>, Double> reduceEvidence(Variable evidence, String out) {

        Map<Map<String, String>, Double> newTable = new LinkedHashMap<>();// new reduced table

        Iterator<Map<String, String>> iterator = _factor.keySet().iterator();// iterate over the current factor's keys

        while (iterator.hasNext()) {
            Map<String, String> key = iterator.next();
            String varOut = key.get(evidence.getName());// get the outcome value for the evidence variable in the current row


            if (varOut.equals(out)) {// if the current row matches the desired evidence outcome
                Map<String, String> newKey = new LinkedHashMap<>();
                // create a new key by removing the evidence variable from the assignment
                for (String var : key.keySet()) {
                    if (!var.equals(evidence.getName())) {
                        newKey.put(var, key.get(var));
                    }
                }
                newTable.put(newKey, _factor.get(key));// copy the probability to the new table

            }
        }

        _variables.remove(evidence);// remove the evidence variable from the factor's header
        return newTable;


    }

    public Factor join(Factor f2) {
        // Create the union of all variables from both factors
        Set<Variable> factorVars = new LinkedHashSet<>();
        factorVars.addAll(this.getVariables());
        factorVars.addAll(f2.getVariables());

        // Create the resulting factor
        Factor FactorResult = new Factor(factorVars);
        // Generate all possible assignments over the union of variables
        List<Map<String, String>> combinations = Tools.generateCombinations(factorVars);


        for (Map<String, String> assignment : combinations) {
            // Project the assignment onto the current factor's variables
            Map<String, String> keyF1 = filterKey(assignment, this.getVariables());
            double p1 = this.getProb(keyF1);
            // Project the assignment onto f2's variables
            Map<String, String> keyF2 = filterKey(assignment, f2.getVariables());
            double p2 = f2.getProb(keyF2);
            // Multiply the probabilities and insert the result
            FactorResult.addRow(assignment, p1 * p2);

        }

        return FactorResult;

    }

    // Helper function: extracts a sub-assignment from a complete assignment,
    // keeping only the variables relevant to the factor
    public static Map<String, String> filterKey(Map<String, String> combination, List<Variable> variables) {

        Map<String, String> result = new LinkedHashMap<>();
        for (Variable var : variables) {
            result.put(var.getName(), combination.get(var.getName()));
        }

        return result;
    }



}

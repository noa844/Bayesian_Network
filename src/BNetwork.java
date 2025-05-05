import java.util.*;


public class BNetwork {
    private int multiplicationCount = 0;
    private int additionCount = 0;
    private final Map<String, Variable> _variables = new LinkedHashMap<>();


    public void addVariable(Variable v) {

        _variables.put(v.getName(), v);
    }

    public Variable getVariable(String name) {

        return _variables.get(name);
    }


    public Map<String, Variable> getVariables() {
        return _variables;
    }

    public int getMultiplicationCount() {
        return multiplicationCount;
    }

    public int getAdditionCount() {
        return additionCount;
    }

    public void resetCounters() {
        multiplicationCount = 0;
        additionCount = 0;
    }

    // Return hidden variables: variables that are neither the query nor evidence
    public List<String> getHidden(Variable query, Map<Variable, String> evidence) {
        List<String> hidden = new ArrayList<>();
        for (String var : _variables.keySet()) {
            Variable v = getVariable(var);
            if (!var.equals(query.getName()) && !evidence.containsKey(v)) {
                hidden.add(v.getName());
            }
        }

        return hidden;
    }

    //return -1 if probability not found.
    private double getDirectlyFromCpt(Variable query, String queryVal, Map<Variable, String> evidence){
        // If query has no parents but evidence is not empty, prob not in CPT
        if(query.getParents().isEmpty() && !evidence.isEmpty()){
            return -1;
        }
        // Check if evidence variables are contained in the set of query’s ancestors
        List<Variable> queryAncestors = new ArrayList<>();
        findAncestors(query,queryAncestors);
        Map<String,String> assignmentKey = new HashMap<>(); //evidences
        List<Variable> queryParents= query.getParents();//vars in query cpt

        for (Variable evi : evidence.keySet()){
            if(!queryAncestors.contains(evi)){
                // If a variable is not an ancestor of the query, the probability is not in the query CPT
                  return -1;
            }
            else{
                if(queryParents.contains(evi)) {
                    // Add to assignment only if it’s a direct parent
                    assignmentKey.put(evi.getName(), evidence.get(evi));
                }else{
                    return -1;
                }
            }
        }

        // Add the query itself to the assignment key
        assignmentKey.put(query.getName(),queryVal);

        Cpt queryCpt = query.getCpt();
        try {
            return queryCpt.getProbFromMap(assignmentKey);
        } catch (NullPointerException e) {
            return -1;
        }
    }

    // Compute full joint probability
    public Double fullJointProb(Map<String, String> elements) {
        List<Double> probes = new ArrayList<>();
        for (String key : elements.keySet()) {
            Variable v = getVariable(key);
            List<String> assignment = new ArrayList<>();
            if (v.hasParents()) {
                List<Variable> parents = v.getParents();
                for (Variable p : parents) {
                    assignment.add(p.getName());
                    String out = elements.get(p.getName());
                    assignment.add(out);
                }
            }
            assignment.add(key);
            String out = elements.get(key);
            assignment.add(out);
            Double prob = v.getCpt().getProbFromList(assignment);
            probes.add(prob);

        }

        double result = probes.get(0);
        for (int i = 1; i < probes.size(); i++) {
            result *= probes.get(i);
            multiplicationCount++;
        }

        return result;

    }

    public double naiveAlgo(String queryV, String queryVal, Map<String, String> evidences) {
        Variable queryVar = getVariable(queryV); //convert to variable objects
        Map<Variable, String> evidence = new LinkedHashMap<>();
        for (String var : evidences.keySet()) {
            Variable variable = getVariable(var);
            evidence.put(variable, evidences.get(var));
        }

        //check if probability result already exist in the query variable Cpt
        double result1= getDirectlyFromCpt(queryVar,queryVal,evidence);
        if(result1 != -1){
            return result1;
        }

        // Generate all combinations of hidden variables
        List<String> hidden = getHidden(queryVar, evidence);
        List<Variable> hiddenVars = new ArrayList<>();
        for (String var : hidden) { //convert to variable objects
            Variable variable = getVariable(var);
            hiddenVars.add(variable);
        }
        List<Map<String, String>> combinations = Tools.generateCombinations(hiddenVars);
        for (Map<String, String> comb : combinations) {
            comb.putAll(evidences);
        }

        //numerator prob
        double result = 0.0;
        //denominator prob
        double sum = 0.0;

        //loop over the query's outcomes to generate all combinations for each one
        List<String> queryOuts = getVariable(queryV).getOutcomes();
        for (String outcome : queryOuts) {
            for (Map<String, String> comb : combinations) {
                comb.put(queryV, outcome);
                double prob = fullJointProb(comb);
                if (outcome.equals(queryVal)) {
                    result += prob;
                }
                sum += prob;
            }
        }

        if (sum != 0.0) {
            result = result / sum;
        }

        int addsInDenominator = queryOuts.size() - 1;// num of additions in denominator for normalize
        int additions = ((combinations.size() - 1) * queryOuts.size()) + addsInDenominator;
        additionCount += additions;
        return result;

    }

    //////helpers for VE//////

    // Create factor from variable CPT
    public Factor createFactor(Variable var) {
        Cpt varCpt = var.getCpt();
        Map<Map<String, String>, Double> table = varCpt.getTable();
        List<Variable> varList = new ArrayList<>();
        varList.add(var);
        if (var.hasParents()) {
            varList.addAll(var.getParents());
        }
        Factor varFactor = new Factor(varList, table);

        return varFactor;

    }

    // Recursively collect ancestors
    public void findAncestors(Variable var, List<Variable> ancestors) {
        for (Variable parent : var.getParents()) {
            if (!ancestors.contains(parent)) {
                ancestors.add(parent);
                findAncestors(parent, ancestors);
            }
        }
    }

    // Variable elimination engine
    public Factor eliminateProcessor(List<String> eliminateOrder, List<Factor> factors) {
        List<Factor> currentFactors = new ArrayList<>(factors);
        // Iterate over the list of hidden variables sorted by the selected strategy
        for (String currHidden : eliminateOrder) {
            // Prepare a list of factors that contain the current hidden variable to eliminate
            List<Factor> toJoin = new ArrayList<>();
            Variable hidden = getVariable(currHidden);
            for (Factor fac : currentFactors) {
                if (fac.getVariables().contains(hidden)) {
                    toJoin.add(fac);
                }
            }
            if (toJoin.isEmpty()) {
                // If the hidden variable no longer appears in any factor, skip it
                continue;
            }

            currentFactors.removeAll(toJoin);// Remove all the factors we are about to join
            Factor joined = joinProcessor(toJoin);//Join all selected factors

            int before = joined.getSize();

            Factor newFactor = joined.eliminateVar(currHidden);// Eliminate the hidden variable

            int after = newFactor.getSize();
            additionCount += before - after;

            if (newFactor.getSize() > 1) {
                currentFactors.add(newFactor);// Add the new factor back if it has more than one row
            }

        }
        Factor queryFactor = currentFactors.get(0);
        if (currentFactors.size() > 1) {
            // If more than one factor remains, join them to obtain the final result
            queryFactor = joinProcessor(currentFactors);
        }
        return queryFactor;
    }


    public Factor joinProcessor(List<Factor> factorsToJoin) {
        List<Factor> toJoin = new ArrayList<>(factorsToJoin);
        while (toJoin.size() > 1) {
            Factor f1 = getMin(toJoin);// Select the smallest factor
            toJoin.remove(f1);
            Factor f2 = getMin(toJoin); // Select the second smallest
            toJoin.remove(f2);
            Factor joined = f1.join(f2);// Join both
            multiplicationCount += joined.getSize(); // Count the size of the result as multiplications
            toJoin.add(joined);// Add the joined factor back
        }
        Factor newFactor = toJoin.get(0);
        return newFactor;
    }
    // Get the smallest factor
    private Factor getMin(List<Factor> factors) {
        Factor min = factors.get(0);
        for (int i = 1; i < factors.size(); i++) {
            Factor f = factors.get(i);
            if (min.compareTo(f) == 1) {
                min = f;
            }
        }
        return min;
    }
    // Main function for Variable Elimination algo.
    public double variableElimination(String queryV, String queryVal, Map<String, String> evidences, EliminationStrategy strategy) {
        Variable queryVar = getVariable(queryV); //convert to Variable Objects
        Map<Variable, String> evidence = new LinkedHashMap<>();
        for (String var : evidences.keySet()) {
            Variable variable = getVariable(var);
            evidence.put(variable, evidences.get(var));
        }
        //check if probability result already exist in the query variable Cpt
        double result1= getDirectlyFromCpt(queryVar,queryVal,evidence);
        if(result1 != -1){
            return result1;
        }
        // Prepare list of variables involved in the probability query
        List<Variable> queryVars = new ArrayList<>();
        queryVars.add(queryVar);
        queryVars.addAll(evidence.keySet());
        // Collect all variables that need factor creation
        List<Variable> factorToCreate = new ArrayList<>();
        for (Variable var : queryVars) {
            if (!factorToCreate.contains(var)) {
                factorToCreate.add(var);
            }
            findAncestors(var, factorToCreate);
        }

        // Main loop to create factors and reduce irrelevant rows
        List<Factor> factors = new ArrayList<>();
        for (Variable var : factorToCreate) {
            Factor fac = createFactor(var);
            // Reduce rows for evidence values in each relevant factor
            for (Variable evi : evidence.keySet()) {
                if (fac.getVariables().contains(evi)) {
                    Map<Map<String, String>, Double> reduced = fac.reduceEvidence(evi, evidence.get(evi));
                    fac.setFactor(reduced);
                }
            }
            if (fac.getSize() > 1) { // Only keep factors with more than one row
                factors.add(fac);
            }
        }
        //elimination step
        List<String> toEliminate = getHidden(queryVar, evidence);
        Factor result = new Factor();
        // Sort elimination order based on strategy
        if (strategy == EliminationStrategy.LEX) {
            Collections.sort(toEliminate);
            result = eliminateProcessor(toEliminate, factors);
        } else if (strategy == EliminationStrategy.MIN_FACTOR_SIZE) {
            sortByFactorSize(toEliminate, factors);
            result = eliminateProcessor(toEliminate, factors);
        }
        // Normalize the resulting factor
        result.normalize();
        int addition = result.getSize() - 1;
        additionCount += addition;

        //get probability query from factor result
        Map<String, String> query = new HashMap<>();
        query.put(queryVar.getName(), queryVal);
        return result.getProb(query);
    }


    public void sortByFactorSize(List<String> toEliminate, List<Factor> factors) {
        List<eliminateProduct> productSizesToSort = new ArrayList<>();
        for (String hid : toEliminate) { // For each hidden variable
            Variable hidden = getVariable(hid);
            Set<Variable> productVars = new HashSet<>(); // Variables participating in factors with this hidden var
            for (Factor fac : factors) {
                List<Variable> facVars = fac.getVariables();
                if (facVars.contains(hidden)) {
                    productVars.addAll(facVars); // Add all variables from related factors
                }
            }
            productVars.remove(hidden); // Remove the hidden itself for product calculation

            if (productVars.size() == 0){ // Hidden not in any factor -> irrelevant for elimination-> assign 0.
                eliminateProduct hiddenNoProduct = new eliminateProduct(hidden,0);
                productSizesToSort.add(hiddenNoProduct);
            }
            if (productVars.size() > 0) { //Hidden participating at least in one factor
                int productSize = 1;
                for (Variable var : productVars) {
                    int varOutCount = var.getOutcomesSize();
                    productSize *= varOutCount;
                }
                eliminateProduct product = new eliminateProduct(hidden,productSize);
                productSizesToSort.add(product);
            }
        }
        if(productSizesToSort.size() > 1){
        Collections.sort(productSizesToSort);
        }
        // Overwrite original elimination list with sorted one
        toEliminate.clear();
        for(int i = 0; i < productSizesToSort.size(); i++) {
           toEliminate.add(productSizesToSort.get(i).getVar().getName());
        }
    }





}

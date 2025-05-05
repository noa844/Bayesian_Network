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
//    //return -1 if probability not found.
//    private double getDirectlyFromCpts(Variable query, Map<Variable, String> evidence){
//        List<Variable> queryParents = query.getParents();
//        if(queryParents.size() == 0){
//            return -1;
//        }
//        List<Variable> queryAncestors = new ArrayList<>();
//        findAncestors(query,queryAncestors);
//        for (Variable evi : evidence.keySet()){
//            if(!queryAncestors.contains(evi)){
//                  return -1;
//            }
//        }
//        Cpt queryCpt = query.getCpt();
//        queryCpt.
    //
    //si get hidden = zero, existe.
    //sinon verifie les parent
//
//        return prob;
//    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bayesian Network:\n");

        for (Map.Entry<String, Variable> entry : _variables.entrySet()) {
            sb.append("- ").append(entry.getValue().toString()).append("\n");
            sb.append(entry.getValue().getCpt().toString()).append("\n");
        }

        return sb.toString();
    }

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
            Double prob = v.getCpt().getProb(assignment);
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
        Variable queryVar = getVariable(queryV); //המרת המשתנים לאובייקטים variable
        Map<Variable, String> evidence = new LinkedHashMap<>();
        for (String var : evidences.keySet()) {
            Variable variable = getVariable(var);
            evidence.put(variable, evidences.get(var));
        }
        //יצירת קומבינציות אפשריות של כל משתני הhidden
        List<String> hidden = getHidden(queryVar, evidence);
        List<Variable> hiddenVars = new ArrayList<>();
        for (String var : hidden) { //המרה למשתנים
            Variable variable = getVariable(var);
            hiddenVars.add(variable);
        }
        List<Map<String, String>> combinations = Tools.generateCombinations(hiddenVars);
        for (Map<String, String> comb : combinations) {
            comb.putAll(evidences);
        }

        //ההסתברות של המונה
        double result = 0.0;
        //ההסתברות של המכנה
        double sum = 0.0;

        //מעבר על כל הoutcomes של משתנה הquery ליצירת קומבינציות מתאימות לכל אחת
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

        int addsInDenominator = queryOuts.size() - 1;// מספר החיבורים במכנה לנרמול
        int additions = ((combinations.size() - 1) * queryOuts.size()) + addsInDenominator;
        additionCount += additions;
        System.out.println("success");
        return result;

    }

    //helpers for VE
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

    public void findAncestors(Variable var, List<Variable> ancestors) {
        for (Variable parent : var.getParents()) {
            if (!ancestors.contains(parent)) {
                ancestors.add(parent);
                findAncestors(parent, ancestors);
            }
        }
    }

    public Factor eliminateProcessor(List<String> eliminateOrder, List<Factor> factors) {
        List<Factor> currentFactors = new ArrayList<>(factors);
        for (String currHidden : eliminateOrder) {//מעבר על הרשימה של משתני hidden ממוינת לפי הstrategy שנבחר
            List<Factor> toJoin = new ArrayList<>(); //הכנת רשימה של פקטורים המכילים את currHidden שרוצים לבצע עליו אלימינציה
            Variable hidden = getVariable(currHidden);
            for (Factor fac : currentFactors) {
                if (fac.getVariables().contains(hidden)) {
                    toJoin.add(fac);
                }
            }
            if (toJoin.isEmpty()) { //אם המשתנה hidden נמחק מכל הפקטורים
                continue;
            }

            currentFactors.removeAll(toJoin);//מורידים מהרשימה את כל הפקטורים שאנחנו הולכים לאחד
            Factor joined = joinProcessor(toJoin);//מחזיר את הפקטור הסופי של האיחוד של כל הפקטורים בtoJoin

            int before = joined.getSize();

            Factor newFactor = joined.eliminateVar(currHidden);//ביצוע של eliminate על הפקטור האחרון שמכיל את current hidden

            int after = newFactor.getSize();
            additionCount += before - after;

            if (newFactor.getSize() > 1) {
                currentFactors.add(newFactor);//הוספת הפקטור החדש לרשימת הפקטורים
            }

        }
        Factor queryFactor = currentFactors.get(0);
        if (currentFactors.size() > 1) {//אם רשימת הפקטורים גדולה מאחדת סימן שנשאר לאחד את הפקטורים שמכילים את query
            queryFactor = joinProcessor(currentFactors);
        }
        return queryFactor;
    }


    public Factor joinProcessor(List<Factor> factorsToJoin) {
        List<Factor> toJoin = new ArrayList<>(factorsToJoin);
        while (toJoin.size() > 1) {
            Factor f1 = getMin(toJoin);//תבחר את 2 הפקטורים הקטנים
            toJoin.remove(f1);
            Factor f2 = getMin(toJoin);
            toJoin.remove(f2);
            Factor joined = f1.join(f2);//תבצע עליהם איחוד
            multiplicationCount += joined.getSize();
            toJoin.add(joined);//תחזיר את האיחוד לרשימה
        }
        Factor newFactor = toJoin.get(0);
        return newFactor;
    }

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

    public double variableElimination(String queryV, String queryVal, Map<String, String> evidences, EliminationStrategy strategy) {
        Variable queryVar = getVariable(queryV); //המרת המשתנים לאובייקטים variable
        Map<Variable, String> evidence = new LinkedHashMap<>();
        for (String var : evidences.keySet()) {
            Variable variable = getVariable(var);
            evidence.put(variable, evidences.get(var));
        }
        //הכנת רשימה של כל המשתנים הנמצאים בprobaability query
        List<Variable> queryVars = new ArrayList<>();
        queryVars.add(queryVar);
        queryVars.addAll(evidence.keySet());
        //הכנה של רשימת המשתנים שצריכים ליצור עבורם פקטור
        List<Variable> factorToCreate = new ArrayList<>();
        for (Variable var : queryVars) {
            if (!factorToCreate.contains(var)) {
                factorToCreate.add(var);
            }
            findAncestors(var, factorToCreate);
        }

        // לולאה ראשית ליצירת הפקטורים וצמצום פקטורים עם שורה בודדת
        List<Factor> factors = new ArrayList<>();
        for (Variable var : factorToCreate) {
            Factor fac = createFactor(var);
            //אלימינציה של שורות ועמודות לא רלוונטיות של פקטורים המכיליםoutcome שונה של המשתני evidence
            for (Variable evi : evidence.keySet()) {//לואלה על המשתני evidence כדי לבדוק אם הפקטור שבתהליך מכיל אחד מהם אם כן נוריד את השורות הלא רלוונטיות
                if (fac.getVariables().contains(evi)) {
                    Map<Map<String, String>, Double> reduced = fac.reduceEvidence(evi, evidence.get(evi));
                    fac.setFactor(reduced);
                }
            }
            if (fac.getSize() > 1) { //אם לפקטור לא נותר רק שורה אחת אז מוסיפים לרשימת הפקטורים הסופית
                factors.add(fac);
            }
        }
        //שלב האלימינציה
        List<String> toEliminate = getHidden(queryVar, evidence);
        Factor result = new Factor();
        //קריאה למיון סדר האלמינציה לפי הstrategy שהועבר
        if (strategy == EliminationStrategy.LEX) {
            Collections.sort(toEliminate);
            result = eliminateProcessor(toEliminate, factors);
        } else if (strategy == EliminationStrategy.MIN_FACTOR_SIZE) {
            sortByFactorSize(toEliminate, factors);
            result = eliminateProcessor(toEliminate, factors);
        }
        //נרמול הפקטור הבודד האחרון שהתקבל
        result.normalize();
        int addition = result.getSize() - 1;
        additionCount += addition;

        //get probability query from factor result
        Map<String, String> query = new HashMap<>();
        query.put(queryVar.getName(), queryVal);
        System.out.println("success");
        System.out.println(queryV);
        System.out.println(queryVal);
        return result.getProb(query);
    }

//    public void sortByFactorSize(List<String> toEliminate) {
//        VariableCptPair[] cptsArr = new VariableCptPair[toEliminate.size()];
//        for(int i = 0; i < toEliminate.size();i++){
//            Variable var = getVariable(toEliminate.get(i));
//            Cpt varCpt = var.getCpt();
//            VariableCptPair pair = new VariableCptPair(var,varCpt);
//            cptsArr[i] = pair;
//        }
//        for (int i = cptsArr.length - 1; i >=0 ; i--) {
//            for (int j = 1; j <= i; j++) {
//                int jSize = cptsArr[j].getCPT().getSize();
//                int jMinusOneSize = cptsArr[j-1].getCPT().getSize();
//                if(jSize < jMinusOneSize){
//                    VariableCptPair temp = cptsArr[j];
//                    cptsArr[j] = cptsArr[j - 1];
//                    cptsArr[j - 1] = temp;
//                }
//            }
//        }
//        toEliminate.clear();
//        for(int i = 0; i < cptsArr.length; i++) {
//           toEliminate.add(cptsArr[i].getVariable().getName());
//        }
//    }

    public void sortByFactorSize(List<String> toEliminate, List<Factor> factors) {
        List<eliminateProduct> productSizesToSort = new ArrayList<>();
        for (String hid : toEliminate) { //מעבר על כל hiddens
            Variable hidden = getVariable(hid);
            Set<Variable> productVars = new HashSet<>(); //המשתנים שמשתתפים בפקטורים בהם hidden מופיע
            for (Factor fac : factors) { //לכל hidden תעבור על כל הפקטורים
                List<Variable> facVars = fac.getVariables();
                if (facVars.contains(hidden)) {
                    productVars.addAll(facVars); //תוסיף את המשתנים שמשתתפים בפקטורים בהם hidden מופיע
                }
            }
            productVars.remove(hidden); //תסיר את hidden לטובת ההכפלה

            if (productVars.size() == 0){ //אם hidden לא נמצא בשום פקטור, אז גם לא יהיה רלוונטי בשלב האלמינציה
                eliminateProduct hiddenNoProduct = new eliminateProduct(hidden,0);
                productSizesToSort.add(hiddenNoProduct);
            }
            if (productVars.size() > 0) { //אם hiden נמצא בלפחות פקטור אחד
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

        toEliminate.clear();
        for(int i = 0; i < productSizesToSort.size(); i++) {
           toEliminate.add(productSizesToSort.get(i).getVar().getName());
        }
    }


    public Map<Variable, String> convertEvidence(Map<String, String> stringEvidence) {
        Map<Variable, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : stringEvidence.entrySet()) {
            result.put(getVariable(entry.getKey()), entry.getValue());
        }
        return result;
    }



}

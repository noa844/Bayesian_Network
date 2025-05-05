import java.util.*;

import static java.lang.String.join;
//
//public class Main {
//
//
//    public static void main(String[] args) throws Exception {
//        BNetwork bn = new BNetwork();
//        XmlParser parser = new XmlParser();
//        parser.loadBnFromXml("net_ex.xml", bn);
//        System.out.println(bn);
//        Map<String, String> evidence = new LinkedHashMap<>();
//        evidence.put("B", "true");
////        evidence.put("A", "true");
////        evidence.put("C", "true");
//        double prob = bn.variableElimination("D","true",evidence,EliminationStrategy.LEX);
//////
//////        bn.resetCounters();
//////        double prob = bn.variableElimination("J","T",evidence,EliminationStrategy.LEX);
//////
//////        Map<String, String> evidence = new LinkedHashMap<>();
//////        evidence.put("B", "T");
//////
//////        bn.resetCounters();
//////        double prob = bn.variableElimination("J","T",evidence,EliminationStrategy.LEX);
//////        System.out.println(prob);
//////        int add = bn.getAdditionCount();
//////        int mul = bn.getMultiplicationCount();
//////        System.out.println("addition: " + add + ", multiplication: " + mul);
////
//////        //P(B=T|J=T,M=T),2
//////        Map<String, String> evidence2 = new LinkedHashMap<>();
//////        evidence2.put("M", "T");
//////        evidence2.put("J", "T");
//////        bn.resetCounters();
//////        double prob2= bn.naiveAlgo("B","T",evidence2);
//////        System.out.println(prob2);
//////        int add2 = bn.getAdditionCount();
//////        int mul2 = bn.getMultiplicationCount();
//////        System.out.println("addition: " + add2 + ", multiplication: " + mul2);
////
////       // P(J=T|B=T),1
////        Map<String, String> evidence2 = new LinkedHashMap<>();
////        evidence2.put("B", "T");
////
////        bn.resetCounters();
////        double prob2= bn.naiveAlgo("J","T",evidence2);
////        System.out.println(prob2);
////        int add2 = bn.getAdditionCount();
////        int mul2 = bn.getMultiplicationCount();
////        System.out.println("addition: " + add2 + ", multiplication: " + mul2);
////
////
////
////
//
//
//        List<String> order2 = new ArrayList<>();
//        order2.add("B");
//        order2.add("C");
//        order2.add("A");
//        order2.add("D");
//        Set<String> gg = new HashSet<>();
//        gg.add("B");
//        gg.add("C");
//        gg.add("A");
//
//        if(gg.addAll(order2))
//            System.out.println("added");
//
//        System.out.println(gg);
//
////        //P(B=F,E=T,A=T,M=T,J=F),1
////        Map<String, String> evidence2 = new LinkedHashMap<>();
////        evidence2.put("M", "T");
////        evidence2.put("B", "F");
////        evidence2.put("E", "T");
////        evidence2.put("A", "T");
////        evidence2.put("J", "F");
////        bn.resetCounters();
////        double prob2= bn.fullJointProb(evidence2);
////        System.out.printf("%f",prob2);
////        System.out.println("");
////        int add2 = bn.getAdditionCount();
////        int mul2 = bn.getMultiplicationCount();
////        System.out.println("addition: " + add2 + ", multiplication: " + mul2);
////
//
//
//
//
//
////        List<Factor> testjoin = new ArrayList<>();
////        testjoin.add(listF.get(1));
////        testjoin.add(listF.get(2));
////        System.out.println(testjoin);
////        Factor query = bn.joinProcessor(testjoin);
////        System.out.println(query);
////        Factor newf = query.eliminateVar("B");
////        System.out.println(newf);
//
//
////        Factor query = bn.eliminateProcessor(order,listF);
////        int add = bn.getAdditionCount();
////        int mul = bn.getMultiplicationCount();
////        System.out.println(query);
////        System.out.println("addition:" + add + ", multiplication:" + mul);
//
//        //test sort by factor
////        List<String> order2 = new ArrayList<>();
////        order2.add("B");
////        order2.add("C");
////        order2.add("A");
////        order2.add("D");
////        bn.sortByFactorSize(order2);
////        System.out.println(order2);
//
//
//
//
//
//
//
//
//
//
//
//        //test
//
////        List<Variable> vars = new ArrayList<>();
////        vars.add(new Variable("A", Arrays.asList("1", "2", "3")));
////        vars.add(new Variable("B", Arrays.asList("a", "b", "c")));
////        vars.add(new Variable("c", Arrays.asList("t", "f")));
////        List<Variable> vars2 = new ArrayList<>();
////        vars2.add(new Variable("B", Arrays.asList("t", "f")));
////        vars2.add(new Variable("c", Arrays.asList("t", "f")));
////test: COMPOSITION
////        List<Map<String, String>> combos = generateCombinations(vars);
////        for (Map<String, String> combo : combos) {
////            System.out.println(combo);
////        }
//
//// TEST: Join
////        List<Variable> vars1 = new ArrayList<>();
////        vars1.add(new Variable("A", Arrays.asList("T", "F")));
////        vars1.add(new Variable("B", Arrays.asList("T", "F")));
////
////        List<Variable> vars2 = new ArrayList<>();
////        vars2.add(new Variable("B", Arrays.asList("T", "F")));
////        vars2.add(new Variable("C", Arrays.asList("T", "F")));
////
////        Factor f1 = new Factor(vars1);
////        Factor f2 = new Factor(vars2);
////
////        // Ajouter des lignes à f1
////        Map<String, String> row1 = new LinkedHashMap<>();
////        row1.put("A", "T");
////        row1.put("B", "T");
////        f1.addRow(row1, 1);
////
////        Map<String, String> row2 = new LinkedHashMap<>();
////        row2.put("A", "T");
////        row2.put("B", "F");
////        f1.addRow(row2, 0);
////
////        Map<String, String> row3 = new LinkedHashMap<>();
////        row3.put("A", "F");
////        row3.put("B", "T");
////        f1.addRow(row3, 0.4);
////
////        Map<String, String> row4 = new LinkedHashMap<>();
////        row4.put("A", "F");
////        row4.put("B", "F");
////        f1.addRow(row4, 0.6);
////
////        // Ajouter des lignes à f2
////        Map<String, String> row5 = new LinkedHashMap<>();
////        row5.put("B", "T");
////        row5.put("C", "T");
////        f2.addRow(row5, 1);
////
////        Map<String, String> row6 = new LinkedHashMap<>();
////        row6.put("B", "T");
////        row6.put("C", "F");
////        f2.addRow(row6, 0.5);
////
////        Map<String, String> row7 = new LinkedHashMap<>();
////        row7.put("B", "F");
////        row7.put("C", "T");
////        f2.addRow(row7, 0.5);
////
////        Map<String, String> row8 = new LinkedHashMap<>();
////        row8.put("B", "F");
////        row8.put("C", "F");
////        f2.addRow(row8, 0);
////
////        // Faire le join
////        Factor result = f1.join(f2);
////
////        // Afficher le résultat
////        System.out.println("Result of join(f1, f2):");
////        System.out.println(result);
////
////        Factor eliminate = result.eliminateVar("B");
////        //test eliminate
////        System.out.println("Result of eliminate(f1, f2):");
////        System.out.println(eliminate);
//////
//
//
//
//
////        BNetwork bn = new BNetwork();
////        XmlParser parser = new XmlParser();
////        parser.loadBnFromXml("alarm_net.xml", bn);
////        System.out.println(bn);
////        // test create factor
////        Variable var = bn.getVariable("A");
////        System.out.println(var);
////        Factor fac = bn.createFactor(var);
////        System.out.println(fac);
////        //test restrict
////        fac.setFactor( fac.reduceEvidence(var,"T"));
////        System.out.println(fac);
//
//    }
//}
//
//
//
//
//
//
//
//
public class Main {
    public static void main(String[] args) throws Exception {
        BNetwork bn = new BNetwork();
        XmlParser parser = new XmlParser();
        parser.loadBnFromXml("alarm_net.xml", bn);

        Map<String, String> evidence = new LinkedHashMap<>();
        evidence.put("E", "T");
        evidence.put("A", "T");

        String queryVar = "M";
        List<Factor> initialFactors = new ArrayList<>();

        Variable query = bn.getVariable(queryVar);
        List<String> toEliminate = bn.getHidden(query, bn.convertEvidence(evidence));
        for (Variable v : bn.getVariables().values()) {
            if (toEliminate.contains(v.getName())) {
                initialFactors.add(bn.createFactor(v));
            }
        }

        System.out.println("Before sorting: " + toEliminate);
        bn.sortByFactorSize(toEliminate, initialFactors);
        System.out.println("After sorting by impact size: " + toEliminate);
    }
}


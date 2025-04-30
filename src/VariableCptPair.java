public class VariableCptPair {
    private Variable _variable;
    private Cpt _cpt;
//helper classe for sorting variables by ther cpt/factor table size.
    public VariableCptPair(Variable variable, Cpt cpt) {
        this._variable = variable;
        this._cpt = cpt;
    }

    public Variable getVariable() {
        return _variable;
    }

    public Cpt getCPT() {
        return _cpt;
    }
}


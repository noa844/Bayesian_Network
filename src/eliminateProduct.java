public class eliminateProduct implements Comparable<eliminateProduct> {
    private Variable _var;
    private int _productSize;

    public eliminateProduct(Variable var, int productSize) {
        this._var = var;
        this._productSize = productSize;
    }

    public Variable getVar() {
        return _var;
    }

    public int getProductSize() {
        return _productSize;
    }

    @Override
    public int compareTo(eliminateProduct other) {
        return Integer.compare(this._productSize, other._productSize);
    }

}

package ru.unn.omp4j.parser.tree;

import org.antlr.runtime.Token;

public class Rule {

    private Token start;
    private Token stop;
    private OMPTree tree;

    public void setTree(OMPTree tree) {
        this.tree = tree;
        this.tree.setNode(this);
    }

    public Token getStart() {
        return start;
    }

    public void setStart(Token start) {
        this.start = start;
    }

    public Token getStop() {
        return stop;
    }

    public void setStop(Token stop) {
        this.stop = stop;
    }

    public Rule() {
    }

    public OMPTree getTree() {
        return tree;
    }

    public void accept(OMPRuleVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

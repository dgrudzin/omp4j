package ru.unn.omp4j.parser.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.Token;

public class OMPTree {

    private Rule node;

    private List<OMPTree> children = new ArrayList<OMPTree>();

    private int startIndex;
    private int stopIndex;
    private Token token;

    public OMPTree() {
    }

    public Rule getNode() {
        return node;
    }

    public void setNode(Rule node) {
        this.node = node;
    }

    public OMPTree(Token token) {
        this.startIndex = -1;
        this.stopIndex = -1;
        this.token = token;
    }

    public OMPTree(OMPTree ompTree) {
        startIndex = -1;
        stopIndex = -1;
        token = ompTree.token;
        node = ompTree.node;
    }

    public void addChild(OMPTree tree) {
        children.add(tree);
    }

    public OMPTree dupNode() {
        return new OMPTree(this);
    }

    public OMPTree dupTree() {
        OMPTree copy = dupNode();
        for (int i = 0; i < children.size(); i++) {
            OMPTree childCopy = children.get(i).dupTree();
            copy.addChild(childCopy);
        }
        return copy;
    }

    public int getCharPositionInLine() {
        if (token == null || token.getCharPositionInLine() == -1) {
            if (getChildCount() > 0)
                return getChild(0).getCharPositionInLine();
            else
                return 0;
        } else {
            return token.getCharPositionInLine();
        }
    }

    public OMPTree getChild(int i) {
        return children.get(i);
    }

    public int getChildCount() {
        return children.size();
    }

    public int getLine() {
        if (token == null || token.getLine() == 0) {
            if (getChildCount() > 0)
                return getChild(0).getLine();
            else
                return 0;
        } else {
            return token.getLine();
        }
    }

    public String getText() {
        if (token == null)
            return null;
        else
            return token.getText();
    }

    public int getTokenStartIndex() {
        if (startIndex == -1 && token != null)
            return token.getTokenIndex();
        else
            return startIndex;
    }

    public int getTokenStopIndex() {
        if (stopIndex == -1 && token != null)
            return token.getTokenIndex();
        else
            return stopIndex;
    }

    public int getType() {
        if (token == null)
            return 0;
        else
            return token.getType();
    }

    public boolean isNil() {
        return token == null;
    }

    public void setTokenStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setTokenStopIndex(int stopIndex) {
        this.stopIndex = stopIndex;
    }

    public String toStringTree() {
        StringBuilder strBuilder = new StringBuilder();
        if (token == null) {
            for (OMPTree child : children) {
                strBuilder.append(child.toStringTree());
            }
        } else {
            strBuilder.append(token.getText()).append(" ");
        }
        return strBuilder.toString();
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String toString() {
        return toStringTree();
    }

    public List<OMPTree> getChildren() {
        return Collections.unmodifiableList(children);
    }
}

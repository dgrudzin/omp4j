package ru.unn.omp4j.parser.tree;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.BaseTreeAdaptor;

public class OMPTreeAdaptor extends BaseTreeAdaptor {

    public OMPTreeAdaptor() {
    }

    public void addChild(Object parent, Object child) {
        if (parent.getClass() != OMPTree.class || child.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        ((OMPTree) parent).addChild((OMPTree) child);
    }

    public Object create(Token token) {
        return new OMPTree(token);
    }

    public Object dupNode(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).dupNode();
    }

    public Object dupTree(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).dupTree();
    }

    public Object getChild(Object obj, int i) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getChild(i);
    }

    public int getChildCount(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getChildCount();
    }

    public String getText(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getText();
    }

    public Token getToken(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getToken();
    }

    public int getTokenStartIndex(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getTokenStartIndex();
    }

    public int getTokenStopIndex(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getTokenStopIndex();
    }

    public int getType(Object obj) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        return ((OMPTree) obj).getType();
    }

    public Object rulePostProcessing(Object obj) {
        return obj;
    }

    public void setTokenBoundaries(Object obj, Token token, Token token1) {
        if (obj.getClass() != OMPTree.class) {
            throw new IllegalArgumentException("Only OMPTrees are allowed");
        }
        ((OMPTree) obj).setTokenStartIndex(token != null ? token.getTokenIndex() : 0);
        ((OMPTree) obj).setTokenStopIndex(token1 != null ? token1.getTokenIndex() : 0);
    }

    public Token createToken(Token token) {
        return new OMPToken(token);
    }

    public Token createToken(int i, String s) {
        return new OMPToken(i, s);
    }
}

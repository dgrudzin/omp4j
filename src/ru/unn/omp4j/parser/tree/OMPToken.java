package ru.unn.omp4j.parser.tree;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

public class OMPToken extends CommonToken {

    private static final long serialVersionUID = 7830890280710142527L;

    public OMPToken(CharStream charstream, int i, int j, int k, int l) {
        super(charstream, i, j, k, l);
    }

    public OMPToken(int i, String s) {
        super(i, s);
    }

    public OMPToken(int i) {
        super(i);
    }

    public OMPToken(Token token) {
        super(token);
    }
}

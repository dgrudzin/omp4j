package ru.unn.omp4j.parser.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.antlr.runtime.Token;

import ru.unn.omp4j.parser.javaparser.JavaOMPLexer;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.barrierDirective;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.forClause;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.forConstruct;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.forUpdate;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.importDeclaration;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.iterationStatement;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.localVariableDeclaration;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.packageDeclaration;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.parallelClause;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.parallelConstruct;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.parallelDirective;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.relationalOp;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.statement;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.structuredBlock;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.taskwaitDirective;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.type;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.uniqueForClause;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.uniqueParallelClause;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.variableDeclaratorId;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.variableInitializer;

public class OMPRuleVisitor {

    private AtomicInteger varCounter = new AtomicInteger();

    private StringBuilder output = new StringBuilder();

    private static final String lineSeparator = System.getProperty("line.separator");

    private static final String indent = "    ";

    private int curIndent = 0;

    private Map<String, String> paramsMap = new HashMap<String, String>();

    public void visit(Rule rule) {
        traverse(rule);
    }

    public void visit(packageDeclaration packageDeclaration) {
        traverse(packageDeclaration);
        newLine();
    }

    public void visit(importDeclaration importDeclaration) {
        traverse(importDeclaration);
        output.append("import ru.unn.omp4j.*;");
        newLine();
        output.append("import ru.unn.omp4j.task.*;");
        newLine();
        output.append("import ru.unn.omp4j.directives.workshare.*;");
        newLine();
        newLine();
    }

    public void visit(parallelConstruct parallel) {
        processConstruct(parallel);
    }

    private void processConstruct(Rule construct) {
        int varId = varCounter.incrementAndGet();
        output.append("final OMPParameters shared_").append(varId).append(" = new OMPParameters();");
        newLine();
        output.append("final OMPParameters private_").append(varId).append(" = new OMPParameters();");
        newLine();
        for (OMPTree child : construct.getTree().getChildren()) {
            child.getNode().accept(this);
        }
        curIndent--;
        newLine();
        output.append("}");
        curIndent--;
        newLine();
        output.append("});");
        newLine();
    }

    public void visit(forConstruct forConstruct) {
        String nowait = "false";
        String chunkSize = null;
        String method = "staticFor";

        int varId = varCounter.incrementAndGet();
        output.append("final OMPParameters shared_").append(varId).append(" = new OMPParameters();");
        newLine();
        output.append("final OMPParameters private_").append(varId).append(" = new OMPParameters();");
        newLine();

        iterationStatement stat = null;

        for (OMPTree fchild : forConstruct.getTree().getChildren()) {
            if (fchild.getNode().getClass() == iterationStatement.class) {
                stat = (iterationStatement) fchild.getNode();
            } else {
                for (OMPTree tree : fchild.getChildren()) {
                    Rule rule = tree.getNode();
                    if (rule != null) {
                        forClause clause = (forClause) rule;
                        for (OMPTree child : clause.getTree().getChildren()) {
                            if (child.getNode() == null) {
                                if (child.getText().equals("nowait")) {
                                    nowait = "true";
                                }
                            } else if (child.getNode().getClass() == uniqueForClause.class) {
                                if (child.getChild(0).getText().equals("schedule")) {
                                    if (child.getChild(2).toStringTree().trim().equals("dynamic")) {
                                        method = "dynamicFor";
                                    }
                                    if (child.getChildCount() > 4) {
                                        chunkSize = child.getChild(4).toStringTree().trim();
                                    }
                                }
                            } else {
                                //dataClause
                                if (child.getChild(0).getText().equals("private")
                                        || child.getChild(0).getText().equals("firstprivate")) {

                                } else {

                                }
                            }
                        }
                    }
                }
            }
        }

        output.append("OMPDirectives.").append(method).append("( new OMPForLoop(shared_").append(
                varCounter.get()).append(", private_").append(varCounter.get()).append(") { ");
        curIndent++;
        newLine();
        output
                .append("protected void execute(OMPParameters sharedVars, OMPParameters privateVars, Chunk chunk) { ");
        curIndent++;
        newLine();

        stat.accept(this);
        curIndent--;
        newLine();
        output.append("} ");
        curIndent--;

        newLine();
        output.append("}, ");

        Rule lclVarDecl = findType(stat, localVariableDeclaration.class);
        String start =findType(lclVarDecl, variableInitializer.class).getTree().toStringTree().trim();
        output.append(start).append(", ");
        String stop = stat.getTree().getChild(6).toStringTree().trim();
        String op = findType(stat, relationalOp.class).getTree().toStringTree().trim();
        if (op.equals("<")) {
            stop = stop + "- 1";
        } else if (op.equals(">")) {
            stop= stop + " + 1";
        }

        output.append(stop).append(", ");

        //Stop = 1 for default
        output.append("1, ");

        if (chunkSize != null) {
            output.append(chunkSize).append(", ");
        }

        output.append(nowait).append(");");
        newLine();
        newLine();
    }

    public void visit(iterationStatement stat) {
        String var = findType(stat, variableDeclaratorId.class).getTree().toStringTree();
        String op = findType(stat, relationalOp.class).getTree().toStringTree().trim();
        if (op.equals("<") || op.equals("<")) {
            op += "=";
        }
        output.append("for (").append(findType(stat, type.class).getTree().toStringTree()).append(var).append(" = ")
                .append("chunk.getFirstIndex(); ").append(var).append(op).append("chunk.getLastIndex(); ").append(
                        findType(stat, forUpdate.class).getTree().toStringTree()).append(")");
        findType(stat, statement.class).accept(this);

    }

    public void visit(parallelDirective parallelDirective) {
        String numThreads = null;
        for (OMPTree tree : parallelDirective.getTree().getChildren()) {
            Rule rule = tree.getNode();
            if (rule != null) {
                parallelClause clause = (parallelClause) rule;
                for (OMPTree child : clause.getTree().getChildren()) {
                    if (child.getNode().getClass() == uniqueParallelClause.class) {
                        if (child.getChild(0).getText().equals("num_threads")) {
                            numThreads = child.getChild(2).toStringTree().trim();
                        }
                    } else {
                        //dataClause
                        if (child.getChild(0).getText().equals("private")
                                || child.getChild(0).getText().equals("firstprivate")) {

                        } else {

                        }
                    }
                }
            }
        }
        if (numThreads != null) {
            output.append("OMPDirectives.parallel(").append(numThreads).append(", new OMPParallelTask(shared_").append(
                    varCounter.get()).append(", private_").append(varCounter.get()).append(") { ");
        } else {
            output.append("OMPDirectives.parallel(new OMPParallelTask(shared_").append(varCounter.get()).append(
                    ", private_").append(varCounter.get()).append(") { ");
        }
        curIndent++;
        newLine();
        output.append("protected void execute(OMPParameters sharedVars, OMPParameters privateVars) { ");
        curIndent++;
        newLine();
    }

    public void visit(localVariableDeclaration varDecl) {
        paramsMap.put(findType(varDecl, variableDeclaratorId.class).getTree().toStringTree().trim(), findType(varDecl,
                type.class).getTree().toStringTree().trim());
        traverse(varDecl);
    }

    public void visit(structuredBlock block) {
        if (block.getTree().getChild(0).getChild(0).getNode().getClass() == ru.unn.omp4j.parser.javaparser.JavaOMPParser.block.class) {
            processBlock(block.getTree().getChild(0).getChild(0).getNode());
        }
    }

    public void visit(barrierDirective barrier) {
        output.append("OMPDirectives.barrier();");
        newLine();
    }

    public void visit(taskwaitDirective taskwait) {
        output.append("OMPDirectives.taskwait();");
        newLine();
    }

    public String getOMPRuntimeCode() {
        return output.toString();
    }

    private Rule findType(Rule rule, Class<? extends Rule> clazz) {
        Rule res = null;
        for (OMPTree tree : rule.getTree().getChildren()) {
            Rule child = tree.getNode();
            if (child != null) {
                if (child.getClass() == clazz) {
                    return child;
                }
                res = findType(child, clazz);
                if (res != null) {
                    return res;
                }
            }
        }

        return res;
    }

    private void processBlock(Rule rule) {
        for (OMPTree tree : rule.getTree().getChildren()) {
            Rule child = tree.getNode();
            if (child == null) {
                if (tree.getToken().getType() != JavaOMPLexer.T43 && tree.getToken().getType() != JavaOMPLexer.T44) {
                    processToken(tree.getToken());
                }
            } else {
                traverse(child);
            }
        }
    }

    private void processToken(Token token) {
        switch (token.getType()) {
        //'{'
        case JavaOMPLexer.T43:
            curIndent++;
            output.append(token.getText());
            newLine();
            break;
        //'}'
        case JavaOMPLexer.T44:
            curIndent--;
            newLine();
            output.append(token.getText());
            newLine();
            break;
        //';'
        case JavaOMPLexer.T25:
            output.deleteCharAt(output.length() - 1);
            output.append(token.getText());
            newLine();
            break;
        //'.'
        case JavaOMPLexer.T28:
            output.deleteCharAt(output.length() - 1);
            output.append(token.getText());
            break;
        default:
            output.append(token.getText()).append(" ");
            break;
        }
    }

    private void traverse(Rule rule) {
        for (OMPTree tree : rule.getTree().getChildren()) {
            Rule childRule = tree.getNode();
            if (childRule != null) {
                childRule.accept(this);
            } else {
                processToken(tree.getToken());
            }
        }
    }

    private void newLine() {
        output.append(lineSeparator);
        for (int i = 0; i < curIndent; i++) {
            output.append(indent);
        }
    }
}

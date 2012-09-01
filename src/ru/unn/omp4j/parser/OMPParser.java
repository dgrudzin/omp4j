package ru.unn.omp4j.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;

import ru.unn.omp4j.parser.javaparser.JavaOMPLexer;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser;
import ru.unn.omp4j.parser.javaparser.JavaOMPParser.compilationUnit;
import ru.unn.omp4j.parser.tree.OMPRuleVisitor;

/**
 * The class contains static methods for generation OpenMP runtime library 
 * routines based on the directives specified in the source code. 
 * Also it contains a main method that allows to run the parser 
 * from the command line specifying the path to a file to parse.
 * 
 * @author Dmitry Grudzinskiy
 * 
 * @version 1.0
 *
 */
public final class OMPParser {

    private static Logger log = Logger.getLogger(OMPParser.class);

    /**
     * Translates the code from the file with the specified file name.
     * 
     * @param fileName - path to the file to parse
     * @return - string, containing formatted code of an OpenMP program
     * @throws RecognitionException
     * @throws OMPParserException
     * @throws IOException
     */
    public static String translateFile(String fileName) throws RecognitionException, OMPParserException, IOException {
        return translate(new ANTLRFileStream(fileName));
    }

    /**
     * Translates the code from the specified string.
     * 
     * @param str - string to parse
     * @return - string, containing formatted code of an OpenMP program
     * @throws RecognitionException
     * @throws OMPParserException
     */
    public static String translateString(String str) throws RecognitionException, OMPParserException {
        return translate(new ANTLRStringStream(str));
    }

    /**
     * Main method that allows to translate a file
     * 
     * @param args - path to the file to parse
     */
    public static void main(String[] args) {
        try {
            log.debug("Started OMP Parser!");

            if (args.length != 1) {
                System.out.println(" Please specify file name");
                log.debug("File name not specified - exiting...");
                System.exit(-1);
            }

            String fileName = args[0];

            File file = new File(fileName);

            //Runn translation
            String resultCode = translateFile(fileName);

            if (fileName.substring(fileName.lastIndexOf('.') + 1).equals("java")) {
                file.renameTo(new File(fileName + ".omp"));
            }
            File result = new File(fileName.substring(0, fileName.indexOf('.')) + ".java");

            BufferedWriter writer = new BufferedWriter(new FileWriter(result));

            writer.write(resultCode);
            writer.close();

            log.debug("Finished OMP");
        } catch (OMPParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }

    private static String translate(CharStream cs) throws RecognitionException, OMPParserException {
        JavaOMPLexer lexer = new JavaOMPLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaOMPParser parser = new JavaOMPParser(tokens);
        compilationUnit unit = parser.compilationUnit();

        OMPRuleVisitor visitor = new OMPRuleVisitor();
        unit.accept(visitor);
        return visitor.getOMPRuntimeCode();
    }

    private OMPParser() {
    }
}

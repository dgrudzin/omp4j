package ru.unn.omp4j.examples;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;

public class ExampleRunner {

    public static void main(String[] args) {
        DOMConfigurator.configure("./omp-log4j.xml");

        if (args.length < 3) {
            System.out
                    .println("Usage: java ru.unn.omp4j.examples.ExampleRunner <example class> <repeats> <file> <append>");
            System.exit(-1);
        }

        String className = args[0];

        Example example;

        try {
            example = (Example) Class.forName(className).newInstance();
        } catch (Exception e) {
            System.out.println("Unable to instantiate class " + className);
            System.exit(-1);
            return;
        }

        int repeats = Integer.valueOf(args[1]);

        Map<String, String> parameters = new LinkedHashMap<String, String>();

        for (int i = 4; i < args.length; i++) {
            String[] param = args[i].split("=");
            if (param.length == 2) {
                parameters.put(param[0], param[1]);
            }
        }
        try {

            boolean append = Boolean.valueOf(args[3]);
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[2], append));
            if (!append) {
                writer.write("Example,");
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    writer.write(entry.getKey());
                    writer.write(",");
                }
                writer.write("repeat,");
                writer.write("Sequental,Parallel,Acceleration");
                writer.newLine();
            }

            for (int i = 1; i <= repeats; i++) {
                System.out.println("Repeat number " + i);
                writer.write(className.substring(className.lastIndexOf('.') + 1));
                writer.write(",");
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    writer.write(entry.getValue());
                    writer.write(",");
                }
                writer.write(String.valueOf(i));
                writer.write(",");

                //Sequental
                long seqTime = example.sequental(parameters);
                writer.write(String.valueOf(seqTime));
                writer.write(",");

                //Parallel
                long parTime = example.parallel(parameters);
                writer.write(String.valueOf(parTime));
                writer.write(",");

                double acc = new Double(seqTime) / new Double(parTime);
                writer.write(new DecimalFormat("0.##").format(acc));
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

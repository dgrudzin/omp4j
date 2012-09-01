package ru.unn.omp4j.examples.parser;

import ru.unn.omp4j.examples.ExampleUtils;
import ru.unn.omp4j.*;
import ru.unn.omp4j.task.*;
import ru.unn.omp4j.directives.workshare.*;

public class MatrixMult {
    public static void main ( String [ ] args ) {
        final int n = Integer.valueOf ( args [ 0 ] );
        final double [ ] matrix1 = ExampleUtils.createRandomMatrix ( n );
        final double [ ] matrix2 = ExampleUtils.createRandomMatrix ( n );
        final double [ ] resultMatrix = ExampleUtils.createRandomMatrix ( n );
        final OMPParameters shared_1 = new OMPParameters();
        final OMPParameters private_1 = new OMPParameters();
        OMPDirectives.parallel(2, new OMPParallelTask(shared_1, private_1) { 
            protected void execute(OMPParameters sharedVars, OMPParameters privateVars) { 
                final OMPParameters shared_2 = new OMPParameters();
                final OMPParameters private_2 = new OMPParameters();
                OMPDirectives.staticFor( new OMPForLoop(shared_2, private_2) { 
                    protected void execute(OMPParameters sharedVars, OMPParameters privateVars, Chunk chunk) { 
                        for (int i  = chunk.getFirstIndex(); i <=chunk.getLastIndex(); i ++ ){
                            for ( int j = 0;
                            j < n;
                            j ++ ) {
                                for ( int k = 0;
                                k < n;
                                k ++ ) {
                                    resultMatrix [ i * n + j ] += matrix1 [ i * n + k ] * matrix2 [ k * n + j ];
                                    
                                }
                                
                            }
                            
                        }
                        
                    } 
                }, 0, n- 1, 1, n / 2, false);
                
                
            }
        });
        
    }
    
}

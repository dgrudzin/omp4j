package ru.unn.omp4j.examples.parser;

import ru.unn.omp4j.OMPDirectives;
import ru.unn.omp4j.OMPRuntime;
import ru.unn.omp4j.directives.workshare.Chunk;
import ru.unn.omp4j.directives.workshare.OMPForLoop;
import ru.unn.omp4j.examples.ExampleUtils;
import ru.unn.omp4j.task.OMPParallelTask;
import ru.unn.omp4j.task.OMPParameters;

public class MatrixVectorMult {
    public static void main ( String [ ] args ) {
        final int n = Integer.valueOf ( args [ 0 ] );
        final double [ ] matrix = ExampleUtils.createRandomMatrix ( n );
        final double [ ] vector = ExampleUtils.createRandomVector ( n );
        final double [ ] result = new double [ vector.length ];
        final OMPParameters shared_1 = new OMPParameters();
        final OMPParameters private_1 = new OMPParameters();
        OMPDirectives.parallel(2, new OMPParallelTask(shared_1, private_1) { 
            protected void execute(OMPParameters sharedVars, OMPParameters privateVars) { 
                System.out.println ( "I am thread " + OMPRuntime.getThreadNum ( ) );
                final OMPParameters shared_2 = new OMPParameters();
                final OMPParameters private_2 = new OMPParameters();
                OMPDirectives.dynamicFor( new OMPForLoop(shared_2, private_2) { 
                    protected void execute(OMPParameters sharedVars, OMPParameters privateVars, Chunk chunk) { 
                        for (int i  = chunk.getFirstIndex(); i <=chunk.getLastIndex(); i ++ ){
                            for ( int j = 0;
                            j < vector.length;
                            j ++ ) {
                                result [ i ] += matrix [ i * vector.length + j ] * vector [ j ];
                                
                            }
                            
                        }
                        
                    } 
                }, 0, n- 1, 1, false);
                
                
            }
        });
        
    }
    
}

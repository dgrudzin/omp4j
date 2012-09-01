lexer grammar JavaOMP;
options {
  language=Java;

}
@members {
  protected boolean enumIsKeyword = true;
  protected boolean assertIsKeyword = true;

  private Logger log = Logger.getLogger(getClass());
  public void emitErrorMessage(String msg) {
      log.error(msg);
  }
}
@header {
package ru.unn.omp.parser.javaparser;

import org.apache.log4j.Logger;
}

T24 : 'package' ;
T25 : ';' ;
T26 : 'import' ;
T27 : 'static' ;
T28 : '.' ;
T29 : '*' ;
T30 : 'public' ;
T31 : 'protected' ;
T32 : 'private' ;
T33 : 'abstract' ;
T34 : 'final' ;
T35 : 'strictfp' ;
T36 : 'class' ;
T37 : 'extends' ;
T38 : 'implements' ;
T39 : '<' ;
T40 : ',' ;
T41 : '>' ;
T42 : '&' ;
T43 : '{' ;
T44 : '}' ;
T45 : 'interface' ;
T46 : 'void' ;
T47 : '[' ;
T48 : ']' ;
T49 : 'throws' ;
T50 : '=' ;
T51 : 'native' ;
T52 : 'synchronized' ;
T53 : 'transient' ;
T54 : 'volatile' ;
T55 : 'boolean' ;
T56 : 'char' ;
T57 : 'byte' ;
T58 : 'short' ;
T59 : 'int' ;
T60 : 'long' ;
T61 : 'float' ;
T62 : 'double' ;
T63 : '?' ;
T64 : 'super' ;
T65 : '(' ;
T66 : ')' ;
T67 : '...' ;
T68 : 'this' ;
T69 : 'null' ;
T70 : 'true' ;
T71 : 'false' ;
T72 : '@' ;
T73 : 'default' ;
T74 : ':' ;
T75 : 'if' ;
T76 : 'else' ;
T77 : 'for' ;
T78 : 'while' ;
T79 : 'do' ;
T80 : 'try' ;
T81 : 'finally' ;
T82 : 'switch' ;
T83 : 'return' ;
T84 : 'throw' ;
T85 : 'break' ;
T86 : 'continue' ;
T87 : 'catch' ;
T88 : 'case' ;
T89 : '+=' ;
T90 : '-=' ;
T91 : '*=' ;
T92 : '/=' ;
T93 : '&=' ;
T94 : '|=' ;
T95 : '^=' ;
T96 : '%=' ;
T97 : '||' ;
T98 : '&&' ;
T99 : '|' ;
T100 : '^' ;
T101 : '==' ;
T102 : '!=' ;
T103 : 'instanceof' ;
T104 : '+' ;
T105 : '-' ;
T106 : '/' ;
T107 : '%' ;
T108 : '++' ;
T109 : '--' ;
T110 : '~' ;
T111 : '!' ;
T112 : 'new' ;
T113 : '//omp' ;
T114 : 'parallel' ;
T115 : 'num_threads' ;
T116 : 'nowait' ;
T117 : 'ordered' ;
T118 : 'schedule' ;
T119 : 'dynamic' ;
T120 : 'guided' ;
T121 : 'runtime' ;
T122 : 'sections' ;
T123 : 'section' ;
T124 : 'task' ;
T125 : 'untied' ;
T126 : 'single' ;
T127 : 'master' ;
T128 : 'critical' ;
T129 : 'barrier' ;
T130 : 'atomic' ;
T131 : 'flush' ;
T132 : 'threadprivate' ;
T133 : 'copyprivate' ;
T134 : 'firstprivate' ;
T135 : 'lastprivate' ;
T136 : 'shared' ;
T137 : 'none' ;
T138 : 'reduction' ;
T139 : 'copyin' ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1023
HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1025
DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1027
OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1029
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1032
fragment
IntegerTypeSuffix : ('l'|'L') ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1035
FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1042
fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1045
fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1048
CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1052
StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1056
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1063
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1070
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1075
ENUM:   'enum' {if (!enumIsKeyword) $type=Identifier;}
    ;
    
// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1078
ASSERT
    :   'assert' {if (!assertIsKeyword) $type=Identifier;}
    ;
    

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1083
Identifier 
    :   Letter (Letter|JavaIDDigit)*
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1087
/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1107
fragment	
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1126
WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

// $ANTLR src "./src/ru/unn/omp/parser/javaparser/JavaOMP.g" 1129
COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

/*LINE_COMMENT
    : ('//'~('\n'|'\r')* '\r'? '\n') {$channel=HIDDEN;}
    ;*/

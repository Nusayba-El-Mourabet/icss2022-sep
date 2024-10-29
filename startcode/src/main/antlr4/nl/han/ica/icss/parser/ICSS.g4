grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
VAR_IDENT: [A-Z][a-zA-Z0-9]*;
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';





//--- PARSER: ---

stylesheet: variableassignment* stylerule+  ;
stylerule: selector OPEN_BRACE statement+ CLOSE_BRACE;
statement: declaration+ |variableassignment | ifClause;
selector: idSelector | classSelector | tagSelector;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT ;
tagSelector: LOWER_IDENT;
declaration: property COLON  expression SEMICOLON ;
property: LOWER_IDENT;
expression: |expression MUL expression
            |expression PLUS expression
            |expression MIN expression
            |partial_expression
            |variableReference;
partial_expression: PIXELSIZE #pixelSize
                    | TRUE #true
                    | FALSE #false
                    | PERCENTAGE #percentage
                    |SCALAR #scalar
                    | COLOR #color;
variableassignment: variableReference ASSIGNMENT_OPERATOR  expression SEMICOLON;
variableReference:VAR_IDENT;
ifClause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE statement+ CLOSE_BRACE (elseClause)?;
elseClause: ELSE OPEN_BRACE statement+ CLOSE_BRACE;


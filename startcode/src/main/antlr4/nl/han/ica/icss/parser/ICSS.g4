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
//--- PARSER: ---
stylesheet: stylerule+;
stylerule: tagSelector OPEN_BRACE declaration+ CLOSE_BRACE;
tagSelector: ID_IDENT | LOWER_IDENT | CLASS_IDENT;
declaration: property COLON expression SEMICOLON;
property: LOWER_IDENT;
expression: PIXELSIZE #pixelSize| TRUE #true | FALSE #false | PERCENTAGE #percentage |SCALAR #scalar | COLOR #color;
//p{width: 10px;} --> voor een dropdown van tree, gebruik je ASTListenener

//expression: PIXELSIZE #pixelSize| TRUE | FALSE | PIXELSIZE | PERCENTAGE |SCALAR | COLOR #color;

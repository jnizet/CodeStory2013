grammar Math;

@header {
package com.ninja_squad.jb.codestory.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
}

@members {
}

expr returns [BigDecimal value]
    :   e = multExpr {$value = $e.value;}
        (   '+' e = multExpr {$value = $value.add($e.value);}
        |   '-' e = multExpr {$value = $value.subtract($e.value);}
        )*
    ;

multExpr returns [BigDecimal value]
    :   e = unaryExpr {$value = $e.value;} 
        ('*' e = unaryExpr {$value = $value.multiply($e.value);}
        |'/' e = unaryExpr {$value = $value.divide($e.value, 10, RoundingMode.HALF_UP);}
        )*
    ; 

unaryExpr returns [BigDecimal value]
    :   e = atom {$value = $e.value;}
    |   '-' e = atom {$value = $e.value.negate();}
    ;

atom returns [BigDecimal value]
    :   FLOAT {$value = new BigDecimal($FLOAT.text);}
    |   '(' expr ')' {$value = $expr.value;}
    ;

FLOAT :   '0'..'9'+ '.' '0'..'9'+ 
          | '0'..'9'+;

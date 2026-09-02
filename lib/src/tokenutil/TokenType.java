package lib.src.tokenutil;;

public enum TokenType {
    IDENTIFIER,

    // Terminal operators
    ARITHMETIC_OP,     // plus, minus, times, over, mod
    COMPARISON_OP,     // is, isnt, less, more, lesseq, moreeq
    LOGICAL_OP,        // and, or, not

    // Keywords for control and logic
    ASSIGNMENT,        // let, be
    CONDITIONAL,       // check, orcheck, otherwise
    LOOP,              // while, repeat
    FUNCTION,          // task, give, return, show

    // Data types and values
    DATA_TYPE,         // int, float, string, bool
    LITERAL,           // yes, no, numeric literals, string literals

    // Syntax
    PUNCTUATION,       // (, ), {, }, ;, ,, .

    EOF                // End of file/input
}
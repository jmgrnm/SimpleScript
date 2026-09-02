package lib.src.generators;

public enum SetItems {
    START_PRIME ("START_PRIME"),
    STMT ("STMT"),
    PROGRAM_PRIME ("PROGRAM_PRIME"),
    START("START"),

    SIMPLE_STMT ("SIMPLE_STMT"),
    COMPOUND_STMT ("COMPOUND_STMT"),
    DECL_STMT ("DECL_STMT"),
    ASSIGN_STMT ("ASSIGN_STMT"),
    RETURN_STMT ("RETURN_STMT"),
    IO_STMT ("IO_STMT"),
    LITERAL ("LITERAL"),
    STOP_STMT ("STOP_STMT"),
    SKIP_STMT ("SKIP_STMT"),

    DATA_TYPE ("DATA_TYPE"),
    let ("let"),
    IDENTIFIER ("IDENTIFIER"),
    be ("be"),
    repeat ("repeat"),
    semicolon (";"),
    return_ ("return"),
    string_ ("string"),
    int_ ("int"),
    float_ ("float"),
    bool_ ("bool"),

    SHOW_FUNC ("SHOW_FUNC"),
    GIVE_FUNC ("GIVE_FUNC"),
    show ("show"),
    lparen ("("),
    rparen (")"),
    give ("give"),

    STRING ("STRING"),
    INT ("INT"),
    FLOAT ("FLOAT"),
    BOOL ("BOOL"),
    yes ("yes"),
    no ("no"),

    CONDITIONAL_STMT ("CONDITIONAL_STMT"),
    LOOP_STMT ("LOOP_STMT"),
    FUNC_STMT ("FUNC_STMT"),
    IF_STMT ("IF_STMT"),
    IF_ELSE_STMT ("IF_ELSE_STMT"),
    ELSE_IF_STMT ("ELSE_IF_STMT"),

    check ("check"),
    orcheck ("orcheck"),
    otherwise ("otherwise"),
    lbrace ("{"),
    rbrace ("}"),

    FOR_STMT ("FOR_STMT"),
    WHILE_STMT ("WHILE_STMT"),
    FUNC_DEC ("FUNC_DEC"),
    task ("task"),
    RETURN_TYPE ("RETURN_TYPE"),
    PARAMS ("PARAMS"),

    void_ ("void"),
    PARAMS_PRIME ("PARAMS_PRIME"),
    comma (","),

    ARITHMETIC_OP ("ARITHMETIC_OP"),
    plus ("plus"),
    minus ("minus"),
    times ("times"),
    over ("over"),
    mod ("mod"),

    COMPARISON_OP("COMPARISON_OP"),
    is ("is"),
    isnt ("isnt"),
    less ("less"),
    more ("more"),
    lesseq ("lesseq"),
    moreeq ("moreeq"),

    LOGICAL_OP ("LOGICAL_OP"),
    and ("and"),
    or ("or"),
    not ("not"),
    stop ("stop");


    private final String stringValue;

    SetItems(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}

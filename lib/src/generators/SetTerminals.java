package lib.src.generators;

public enum SetTerminals {
    let ("let"),
    be ("be"),
    semicolon (";"),
    return_ ("return"),
    string_ ("string"),
    int_ ("int"),
    float_ ("float"),
    bool_ ("bool"),
    show ("show"),
    lparen ("("),
    rparen (")"),
    give ("give"),
    yes ("yes"),
    no ("no"),
    check ("check"),
    orcheck ("orcheck"),
    otherwise ("otherwise"),
    lbrace ("{"),
    rbrace ("}"),
    task ("task"),
    void_ ("void"),
    comma (","),
    plus ("plus"),
    minus ("minus"),
    times ("times"),
    over ("over"),
    mod ("mod"),
    is ("is"),
    isnt ("isnt"),
    less ("less"),
    more ("more"),
    lesseq ("lesseq"),
    moreeq ("moreeq"),
    and ("and"),
    or ("or"),
    not ("not"),
    repeat ("repeat"),
    stop ("stop");

    private final String stringValue;

    SetTerminals(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public boolean contains(String word) {
        return stringValue.equals(word);
    }

}

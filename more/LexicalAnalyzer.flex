import java.util.*;
%%// Options of the scanner

%class LexicalAnalyzer	//Class Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  //Says that the return type is Symbol
%standalone		//Standalone mode

%{
	// Create HashMap to store identifiers and there first line
	private HashMap<String,Integer > Identification = new HashMap<String, Integer>();
    //function to add Identifiers
	private void chekIdentification (String id,int line)
	{
		if(!Identification.containsKey(id))
			{
				Identification.put(id.toString(),line);
			}

	}
%}

// Return value of the program
%eofval{
	       // TreeMap to store values of HashMap 
        TreeMap<String, Integer> sorted = new TreeMap<>(); 
  
        // Copy all data from hashMap into TreeMap 
        sorted.putAll(Identification); 
		System.out.println("\nIdentifiers");  
        // Display the TreeMap which is naturally sorted 
        for (Map.Entry<String, Integer> entry : sorted.entrySet())  
            System.out.println(entry.getKey() +" " + entry.getValue());  
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

%xstate YYINITIAL, COMMENT_STATE
// Extended Regular Expressions

	AlphaUpperCase = [A-Z]
	AlphaLowerCase = [a-z]
	Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
	CHIFRE          = [0-9]
	AlphaNumeric   = {Alpha}|{CHIFRE}
	AlphaLowerCaseNumeric={AlphaLowerCase}|{CHIFRE}
	Sign           = [+-]
	CommentLine    ="//".*
    BEGINPROG		="BEGINPROG"
    PROGNAME      ={AlphaUpperCase}+{AlphaNumeric}*
    ENDLINE       = "\r"?"\n"
    ENDPROG       ="ENDPROG"
    VARIABLES	    ="VARIABLES"
    COMMA         =","
    VARNAME       ={AlphaLowerCase}{AlphaLowerCaseNumeric}*
    ASSIGN        =":="
    NUMBER        =([1-9][0-9]*)|0
    LPAREN        ="("
    RPAREN        =")"
    MINUS         ="-"
    PLUS          ="+"
    TIMES         ="*"
    DIVIDE        ="/"
    IF            ="IF"
    THEN          ="THEN"
    ENDIF         ="ENDIF"
    ELSE          ="ELSE"
    NOT           ="NOT"
    AND           ="AND"
    OR            ="OR"
    EQ            ="="
    GEQ           =">="
    GT            =">"
    LEQ           ="<="
    LT            ="<"
    NEQ           ="!="
    WHILE         ="WHILE"
    DO            ="DO"
    ENDWHILE      ="ENDWHILE"
    FOR           ="FOR"
    TO            ="TO"
    ENDFOR        ="ENDFOR"
    PRINT         ="PRINT"
    READ          ="READ"

%%// Identification of tokens


<YYINITIAL> {
	"/*"	   {yybegin(COMMENT_STATE);}
	{IF}     	    {System.out.println(new Symbol(LexicalUnit.IF,yyline, yycolumn,yytext()).toString());}
	{THEN}      	{System.out.println(new Symbol(LexicalUnit.THEN,yyline, yycolumn,yytext()).toString());}
	{ENDIF}      	{System.out.println(new Symbol(LexicalUnit.ENDIF,yyline, yycolumn,yytext()).toString());}
	{ELSE}       	{System.out.println(new Symbol(LexicalUnit.ELSE,yyline, yycolumn,yytext()).toString());}
	{NOT}       	{System.out.println(new Symbol(LexicalUnit.NOT,yyline, yycolumn,yytext()).toString());}
	{AND}       	{System.out.println(new Symbol(LexicalUnit.AND,yyline, yycolumn,yytext()).toString());}
	{OR}        	{System.out.println(new Symbol(LexicalUnit.OR,yyline, yycolumn,yytext()).toString());}
	{EQ}         	{System.out.println(new Symbol(LexicalUnit.EQ,yyline, yycolumn,yytext()).toString());}
	{GEQ}       	{System.out.println(new Symbol(LexicalUnit.GEQ,yyline, yycolumn,yytext()).toString());}
	{GT}	        {System.out.println(new Symbol(LexicalUnit.GT,yyline, yycolumn,yytext()).toString());}
	{LEQ}       	{System.out.println(new Symbol(LexicalUnit.LEQ,yyline, yycolumn,yytext()).toString());}
	{LT}	        {System.out.println(new Symbol(LexicalUnit.LT,yyline, yycolumn,yytext()).toString());}
	{NEQ}   	    {System.out.println(new Symbol(LexicalUnit.NEQ,yyline, yycolumn,yytext()).toString());}
	{WHILE} 	    {System.out.println(new Symbol(LexicalUnit.WHILE,yyline, yycolumn,yytext()).toString());}
	{DO}    	    {System.out.println(new Symbol(LexicalUnit.DO,yyline, yycolumn,yytext()).toString());}
	{ENDWHILE}  	{System.out.println(new Symbol(LexicalUnit.ENDWHILE,yyline, yycolumn,yytext()).toString());}
	{FOR}       	{System.out.println(new Symbol(LexicalUnit.FOR,yyline, yycolumn,yytext()).toString());}
	{TO}     	    {System.out.println(new Symbol(LexicalUnit.TO,yyline, yycolumn,yytext()).toString());}
	{ENDFOR}    	{System.out.println(new Symbol(LexicalUnit.ENDFOR,yyline, yycolumn,yytext()).toString());}
	{PRINT}	        {System.out.println(new Symbol(LexicalUnit.PRINT,yyline, yycolumn,yytext()).toString());}
	{READ}      	{System.out.println(new Symbol(LexicalUnit.READ,yyline, yycolumn,yytext()).toString());} 
	{BEGINPROG} 	{System.out.println(new Symbol(LexicalUnit.BEGINPROG,yyline, yycolumn,yytext()).toString());}
	{ENDPROG}    	{System.out.println(new Symbol(LexicalUnit.ENDPROG,yyline, yycolumn,yytext()).toString());}
	{VARIABLES}		{System.out.println(new Symbol(LexicalUnit.VARIABLES,yyline, yycolumn,yytext()).toString());}
	{COMMA} 		{System.out.println(new Symbol(LexicalUnit.COMMA,yyline, yycolumn,yytext()).toString());}
	{ASSIGN}    	{System.out.println(new Symbol(LexicalUnit.ASSIGN,yyline, yycolumn,yytext()).toString());}
	{LPAREN}        {System.out.println(new Symbol(LexicalUnit.LPAREN,yyline, yycolumn,yytext()).toString());}
	{RPAREN}        {System.out.println(new Symbol(LexicalUnit.RPAREN,yyline, yycolumn,yytext()).toString());}
	{MINUS}     	{System.out.println(new Symbol(LexicalUnit.MINUS,yyline, yycolumn,yytext()).toString());}
	{PLUS}       	{System.out.println(new Symbol(LexicalUnit.PLUS,yyline, yycolumn,yytext()).toString());}
	{TIMES}      	{System.out.println(new Symbol(LexicalUnit.TIMES,yyline, yycolumn,yytext()).toString());}
	{DIVIDE}    	{System.out.println(new Symbol(LexicalUnit.DIVIDE,yyline, yycolumn,yytext()).toString());}	
	{VARNAME}    	{System.out.println(new Symbol(LexicalUnit.VARNAME,yyline, yycolumn,yytext()).toString());chekIdentification(yytext(),yyline);}
	{NUMBER}        {System.out.println(new Symbol(LexicalUnit.NUMBER,yyline, yycolumn,yytext()).toString());}
	{PROGNAME}      {System.out.println(new Symbol(LexicalUnit.PROGNAME,yyline, yycolumn,yytext()).toString());}
	{ENDLINE}   	{System.out.println(new Symbol(LexicalUnit.ENDLINE,yyline, yycolumn,"\\n").toString());}
	{CommentLine}   {}
	.		    	{}
}

<COMMENT_STATE> {
	"*/"     {yybegin(YYINITIAL);}
	[^"*/"]+	{}
}




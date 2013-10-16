/* =========================== MiniVM.java ===============================

The Mini language is a modified small subset of Java/C. 
A Mini program consists of a single possibly recursive function.
The language has no declarations (implicit type is integer).

MiniVM.java is a compiler-interpreter for Mini written in Java.
The compiler generates code for a virtual machine, which is a modified 
small subset of the Java VM (integer code instead of byte code).

The one-pass compiler is implemented by a top-down recursive descent
parser calling the methods of lexical analysis and code generation.
The parser routines correspond to the grammar rules in EBNF notation.
The regular right parts of EBNF are suitable to postfix generation.
Lexical analysis takes advantage of the Java class StreamTokenizer.

====================== source language syntax (EBNF) =====================

Program    = Function
Function   = identifier "(" identifier ")" Block
Block      = "{" [Statements] "}"
Statements = Statement Statements
Statement  = identifier "=" Expression ";" |
             "if" Condition Statement "else" Statement |
             "while" Condition Statement |
             "return" Expression ";" |
             Block |
             ";"
Condition  = "(" Expression ("=="|"!="|">"|"<") Expression ")"
Expression = Term {("+"|"-") Term}
Term       = Factor {("*"|"/") Factor}
Factor     = number |
             identifier |
             "(" Expression ")" |
             identifier "(" Expression ")" 

================================ VM code =================================

 0    do nothing
 1 c  push constant c onto stack
 2 v  load variable v onto stack 
 3 v  store stack value into variable v
 4    add two top elements of stack, replace by result 
 5    subtract ...
 6    multiply ...
 7    divide ...
 8 a  jump to a if the two top elements of stack are equal
 9 a  jump if ... not equal
10 a  jump if ... less or equal
11 a  jump if ... greater or equal
12 a  unconditional jump to a
13 a  jump to subroutine start address a
14    return from function
15    stop ececution

================================ example =================================

source file "fac.mini":
-----------------------
fac(n) {
    if (n == 0)
        return 1;
    else
        return n * fac(n-1);
}

run:
----
<Java Compiler> MiniVM.java
<Java VM> MiniVM fac.mini 8

VMCode: 13 3 15 2 1 1 0 9 14 1 1 14 12 25 2 1 2 1 1 1 5 13 3 6 14 0 
Result: 40320

======================================================================= */

//////////////////////////////////////////////////////////////////////////

import java.io.*;

public class MiniVM 
{
    static int code_max = 1000; 
    static int stack_max = 10000;
    
    public static void main(String args[]) {
        try {
            Lexer.init(args[0]);                // init lexer
            Gen.init(code_max);                 // init generator
            Parser.program();                   // call parser 
            Gen.show();                         // show VM code
            VM.init(Gen.code,stack_max);        // init VM
            int x = Integer.parseInt(args[1]);  // input data
            int y = VM.exec(x);                 // call VM
            System.out.println("Result: "+y); } // print result
        catch (Error e) {
            System.out.println("error "+e.getMessage()); } }
}

class Error extends Exception 
{
    public Error(String msg) {
        super(msg); }
}



//////////////////////////////////////////////////////////////////////////

interface Token 
{
    // token 
    final static int T_num = 1; // number
    final static int T_id  = 2; // identifier
    final static int T_eql = 3; // ==
    final static int T_neq = 4; // !=
    final static int T_grt = '>';
    final static int T_les = '<';
    final static int T_add = '+';
    final static int T_sub = '-';
    final static int T_mul = '*';
    final static int T_div = '/';
    final static int T_lbr = '(';
    final static int T_rbr = ')';
    final static int T_clb = '{';
    final static int T_crb = '}';
    final static int T_com = ',';
    final static int T_sem = ';';
    final static int T_ass = '=';
    final static int T_eof = '$';

    final static String kw[]  = {"if","else","while","return"};
    final static int kw0      = 10;
    final static int T_if     = 10;
    final static int T_else   = 11;
    final static int T_while  = 12;
    final static int T_return = 13;
}

interface Code
{
    // target code (VM)
    final static int M_nop      =  0;
    final static int M_push     =  1;
    final static int M_load     =  2;
    final static int M_store    =  3;
    final static int M_add      =  4;
    final static int M_sub      =  5;
    final static int M_mul      =  6;
    final static int M_div      =  7;
    final static int M_if_cmpeq =  8;
    final static int M_if_cmpne =  9;
    final static int M_if_cmple = 10;
    final static int M_if_cmpge = 11;
    final static int M_goto     = 12;
    final static int M_jsr      = 13;
    final static int M_return   = 14;
    final static int M_stop     = 15;
}

//////////////////////////////////////////////////////////////////////////

// symbol table

class Symtab
{
    static String t[] = new String[100];    // table array 
    static int n = 0;                       // number of variables
        
    static int enter(String s) {
        int i;
        for (i=0; i < n && !s.equals(t[i]); i++) {}
        if (i == n) {
            t[i] = s;
            n++; }
        return i; }
}

//////////////////////////////////////////////////////////////////////////

// lexical analysis

class Lexer implements Token 
{
    static int num_val;        // attribute of number
    static int id_val;         // attribute of id (index of symbol table)

    private static Reader source;
    private static StreamTokenizer st;
    private static int tok;    // tokenizer token 

    static void init(String file_name) throws Error {
        try {
            source = new BufferedReader(new FileReader(file_name)); }
        catch (FileNotFoundException e) {
            throw new Error("file not found"+" "+e.getMessage()); } 
        st = new StreamTokenizer(source);
        st.ordinaryChar('/');
        st.ordinaryChar('-'); } 
    
    static int scan() throws Error {
        try {
            tok = st.nextToken();
            switch(tok) {
            case StreamTokenizer.TT_EOF:
                return T_eof;
            case StreamTokenizer.TT_NUMBER:
                num_val = (int)st.nval;
                return T_num;
            case StreamTokenizer.TT_WORD:
                int i = look_kw(st.sval);
                if (i >= 0)
                    return kw0 + i;
                else {
                    id_val = Symtab.enter(st.sval);
                    return T_id; }
            default:
                char c = (char)tok;
                switch(c) {
                case '=':
                    if ((char)st.nextToken() == '=') 
                        return T_eql;
                    else 
                        st.pushBack();
                    break; 
                case '!':
                    if ((char)st.nextToken() == '=') 
                        return T_neq; 
                    else 
                        st.pushBack();
                    break; } } } 
        catch (IOException e) {
            throw new Error ("IO"+" "+e.getMessage()); }
        return tok; }
        
    private static int look_kw(String s) {
        int i;
        for (i=0; i < kw.length && !s.equals(kw[i]); i++) {}
        if (i < kw.length) 
            return i;
        else
            return -1; }
}

//////////////////////////////////////////////////////////////////////////

// parser (generates VM code)

class Parser implements Token, Code 
{
    private static int token; 

    static void program() throws Error {
        next();
        Gen.start();
        function(); }

    private static void function() throws Error {
        match(T_id);
        match(T_lbr);
        match(T_id);
        match(T_rbr);
        block();
        Gen.instr(M_nop); }

    private static void block() throws Error {
        match(T_clb);
        statements();
        match(T_crb); }

    private static void statements() throws Error {
        if (token != T_crb) { 
            statement();
            statements(); } }
        
    private static void statement() throws Error {
        int p1,p2;
        switch(token) {
        case T_id:
            int adr = Lexer.id_val;
            next();
            match(T_ass);
            expression();
            Gen.instr(M_store,adr);
            match(T_sem);
            break;
        case T_if:
            next();
            condition();
            p1 = Gen.pc-1; 
            statement();
            Gen.instr(M_goto,0);
            p2 = Gen.pc-1;
            match(T_else);
            Gen.setjump(p1);
            statement();
            Gen.setjump(p2);
            break;
        case T_while:
            next();
            p1 = Gen.pc;
            condition();
            p2 = Gen.pc-1;
            statement();
            Gen.instr(M_goto,p1);
            Gen.setjump(p2);
            break;
        case T_return:
            next();
            expression();
            Gen.instr(M_return);
            match(T_sem);
            break;
        case T_clb:
            block();
            break;
        case T_sem:
            next();
            break;
        default:
            throw new Error("statement "+token); } }
        
    private static void condition() throws Error {
        match(T_lbr);
        expression();
        int rop = token;
        next();
        expression();
        match(T_rbr);
        switch(rop) {
        case T_eql:
            Gen.instr(M_if_cmpne,0);
            break;
        case T_neq:
            Gen.instr(M_if_cmpeq,0);
            break;
        case T_grt:
            Gen.instr(M_if_cmple,0);
            break;
        case T_les:
            Gen.instr(M_if_cmpge,0);
            break;
        default:
            throw new Error("condition "+token); } }
        
    private static void expression() throws Error {
        term();
        while (token == T_add || token == T_sub) {
            switch (token) {
            case T_add:
                next();
                term();
                Gen.instr(M_add);
                break;
            case T_sub:
                next();
                term();
                Gen.instr(M_sub);
                break; } } }
        
    private static void term() throws Error {
        factor();
        while (token == T_mul || token == T_div) {
            switch (token) {
            case T_mul:
                next();
                term();
                Gen.instr(M_mul);
                break;
            case T_div:
                next();
                term();
                Gen.instr(M_div);
                break; } } }
        
    private static void factor() throws Error {
        switch(token) {
        case T_num:
            Gen.instr(M_push,Lexer.num_val);
            next();
            break;
        case T_id:
            int id = Lexer.id_val;
            next();
            if (token != T_lbr)
                Gen.instr(M_load,id);
            else {
                next();
                expression();
                match(T_rbr);
                Gen.instr(M_jsr,Gen.start_adr); }
            break;
        case T_lbr:
            next();
            expression();
            match(T_rbr);
            break;
        default:
            throw new Error("expression "+token); } }

    private static void next() throws Error {
        token = Lexer.scan(); }
        
    private static void match(int x) throws Error {
        if (token == x)
            next();
        else
            throw new Error("syntax "+token); }
}

//////////////////////////////////////////////////////////////////////////

// code generator

class Gen implements Code 
{
    static int code[];    // target code
    static int pc;        // program counter
    static int start_adr; // start address
        
    static void init(int code_max) {
        code = new int[code_max];
        pc = 0; }
    
    static void start() {
        instr(M_jsr,3);
        instr(M_stop);
        start_adr = pc; }       
    
    static void instr(int operator) {
        code[pc] = operator;
        pc = pc+1; }

    static void instr(int operator,int operand) {
        code[pc] = operator;
        code[pc+1] = operand;
        pc = pc+2; }
        
    static void setjump(int adr) {
        code[adr] = pc; }
    
    static void show() {
        System.out.print("VMCode: ");
        for (int i=0;i < pc;i++) 
            System.out.print(code[i]+" ");
        System.out.println(); }
}

//////////////////////////////////////////////////////////////////////////

// virtual machine

class VM implements Code 
{
    private static int p[]; // program code
    private static int ip;  // instruction pointer
    private static int s[]; // stack
    private static int sp;  // stack pointer 
    private static int fp;  // frame pointer
    private static int fs;  // frame size
    
    static void init(int code[],int stack_max) {
        p = code;
        ip = 0;
        s = new int[stack_max];
        sp = 0;
        fp = 0;
        fs = Symtab.n; }
            
    static int exec(int arg) throws Error {
        s[0] = arg;
        sp++;
        while (p[ip] != M_stop) {
            switch(p[ip]) {
            case M_push:
                s[sp] = p[ip+1];
                sp++;
                ip = ip+2;
                break;
            case M_load:
                s[sp] = s[fp+p[ip+1]];
                sp++;
                ip = ip+2;
                break;
            case M_store:
                s[fp+p[ip+1]] = s[sp-1];
                sp--;
                ip = ip+2;
                break;
            case M_add:
                sp--;
                s[sp-1] = s[sp-1] + s[sp];
                ip++;
                break;
            case M_sub:
                sp--;
                s[sp-1] = s[sp-1] - s[sp];
                ip++;
                break;
            case M_mul:
                sp--;
                s[sp-1] = s[sp-1] * s[sp];
                ip++;
                break;
            case M_div:
                sp--;
                s[sp-1] = s[sp-1] / s[sp];
                ip++;
                break;
            case M_if_cmpeq:
                sp = sp-2;
                ip = s[sp] == s[sp+1] ? p[ip+1]:ip+2;
                break;
            case M_if_cmpne:
                sp = sp-2;
                ip = s[sp] != s[sp+1] ? p[ip+1]:ip+2;
                break;
            case M_if_cmple:
                sp = sp-2;
                ip = s[sp] <= s[sp+1] ? p[ip+1]:ip+2;
                break;
            case M_if_cmpge:
                sp = sp-2;
                ip = s[sp] >= s[sp+1] ? p[ip+1]:ip+2;
                break;
            case M_goto:
                ip = p[ip+1];
                break;
            case M_jsr:
                s[sp] = ip+2;      // save return address
                s[sp+1] = fp;      // save fp
                fp = sp+2;         // set fp
                sp = fp+fs;        // set sp
                s[fp+1] = s[fp-3]; // copy argument
                ip = p[ip+1];      // goto start address
                break;
            case M_return:
                s[fp-3] = s[sp-1]; // copy return value
                sp = fp-2;         // reset sp
                fp = s[sp+1];      // reset fp
                ip = s[sp];        // goto return address
                break;
            default:
                throw new Error("illegal vm code "+p[ip]); } }
        return s[0]; }
}

//////////////////////////////////////////////////////////////////////////

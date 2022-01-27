import java.util.*;

/**
 *
 * LLVM code writer
 *
 * @author Dhanani, Ali
 *
 */

public class LlvmCode {
    // store llvmcode
    private StringBuffer llvmcode;
    // llvm variables counter
    private int uvars;
    // list of variables
    private Set varriables;
    // FOR counter
    private int forid;
    // IF counter
    private int ifid;
    // WHILE counter
    private int whileid;
    // if read used in code
    private boolean print;
    // if print used in code
    private boolean read;

    public LlvmCode() {
        this.llvmcode = new StringBuffer();
        this.uvars = 0;
        this.varriables = new HashSet();
        this.forid = 1;
        this.ifid = 1;
        this.whileid = 1;
        this.print = false;
        this.read = false;

        this.CreatMain();
    }

    public void CreatMain() {

        // Main
        this.llvmcode.append("define i32 @main() {\n");
        this.llvmcode.append("entry:\n");

    }

    // declare variables
    public boolean DeclareVars(String var) {

        if (varriables.add(var)) {
            this.llvmcode.append("\t%" + var + " = alloca i32\n");
            return true;
        }
        return false;

    }

    // counter for llvm code variables
    public String DeclareUvars() {
        int id = this.uvars;
        this.uvars++;
        return "%" + id;
    }

    public String Uvars(String val) {
        String returnvar, uvar;
        uvar = DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = alloca i32\n");
        StoreValue(uvar, val);
        returnvar = DeclareUvars();
        this.llvmcode.append("\t" + returnvar + " = load i32 , i32* " + uvar + "\n");
        return returnvar;
    }

    // store value in variable
    public void StoreValue(String uvar, String val) {
        this.llvmcode.append("\tstore i32 " + val + ",i32* " + uvar + "\n");
    }

    // load variable
    public String LoadVar(String var) {

        String uvar = DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = load i32 , i32* %" + var + "\n");
        return uvar;

    }

    // check if the variable is alredy exist
    public boolean Isvar(String var) {
        if (this.varriables.contains(var)) {
            return true;
        }
        return false;

    }

    // Expression code
    public String Experssion(String a, String op, String b) {
        String var = DeclareUvars();
        this.llvmcode.append("\t" + var + " = " + op + " i32 " + a + "," + b + "\n");
        return var;
    }

    // Print code
    public void PrintInt(String print) {
        this.llvmcode.append("\tcall void @printInt(i32 " + print + ")\n");
        this.print = true;
    }

    // Read code
    public void ReadInt(String read) {
        this.read = true;
        String var = DeclareUvars();
        this.llvmcode.append("\t" + var + " =call i32 @readInt()\n");

        StoreValue("%" + read, var);
    }

    // create id for 'for loop'
    public int Forid() {
        this.llvmcode.append("\tbr label %forloop" + forid + "\n");
        this.llvmcode.append("forloop" + forid + ":\n");
        return this.forid++;
    }

    // for code
    public void ForLop(String var, String n, int fid) {
        String uvar1 = this.LoadVar(var);
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + "=icmp sle i32 " + uvar1 + "," + n + "\n");
        this.llvmcode.append("\tbr i1 " + uvar + ", label %innerFor" + fid + ", label %afterFor" + fid + "\n");
        this.llvmcode.append("\n innerFor" + fid + ": \n");

    }

    // end For
    public void EndFor(String i, int fid) {
        this.llvmcode.append("\tbr label %endfor" + fid + "\n");
        this.llvmcode.append("endfor" + fid + ":\n");
        String it = this.LoadVar(i);
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " =add i32 " + it + " ,1\n");
        this.StoreValue("%" + i, uvar);
        this.llvmcode.append("\tbr label %forloop" + fid + "\n");
    }

    // exit for
    public void AfterFor(int fid) {
        this.llvmcode.append("afterFor" + fid + ":\n");
    }

    // boolean expression
    public String BooleanOp(String op, String a, String b) {
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " = icmp " + op + " i32 " + a + "," + b + "\n");
        return uvar;
    }

    // Bitwise operations
    public String Bitw(String bit, String a, String b) {
        String uvar = this.DeclareUvars();
        this.llvmcode.append("\t" + uvar + " =  " + bit + " i1 " + a + "," + b + "\n");
        return uvar;
    }

    // startr IF
    public int Iflabel() {

        this.llvmcode.append("\tbr label %if" + this.ifid + "\n");
        this.llvmcode.append("if" + this.ifid + ":\n");
        return this.ifid++;

    }

    // Condition IF
    public void If(String ifcnd, String condlbl, int ifid) {

        this.llvmcode.append("\tbr i1 " + ifcnd + ", label %innerif" + ifid + " ,label %" + condlbl + ifid + " \n");
        this.llvmcode.append("innerif" + ifid + ": \n");

    }

    // Label to start ELSE
    public void Ifstlabel(String lbl, int ifid) {
        this.llvmcode.append("\tbr label %ENDIF" + ifid + "\n");
        this.llvmcode.append(lbl + ifid + ":\n");

    }

    // label to start while
    public int whilelabel() {

        this.llvmcode.append("\t br label %while" + this.whileid + "\n");
        this.llvmcode.append("while" + this.whileid + ":\n");
        return this.whileid++;

    }

    // while code
    public void While(String whilecnd, int whileid) {

        this.llvmcode.append(
                "\t br i1 " + whilecnd + ", label %innerwhile" + whileid + " ,label %afterwhile" + whileid + " \n");
        this.llvmcode.append("innerwhile" + whileid + ": \n");

    }

    // end while
    public void Endwhile(String whilecnd, int whileid) {
        this.llvmcode
                .append("\t br i1 " + whilecnd + ", label %while" + whileid + " ,label %afterwhile" + whileid + " \n");

    }

    // label after wile
    public void Afterwhile(int whileid) {

        this.llvmcode.append("afterwhile" + whileid + ": \n");

    }

    // Function Read
    private String declareRead() {
        return " declare i32 @getchar() \n ; Defining a function wich read integer  \ndefine i32 @readInt() {  \n  entry:  \n  \t	%res = alloca i32  \n  \t	%digit = alloca i32  \n  \t	%mult = alloca i32  \n  \t	store i32 0, i32* %res  \n  \t	store i32 1, i32* %mult  \n  \t	br label %firstread  \n  firstread:  \n  \t	%a = call i32 @getchar()  \n   \t	%b = icmp eq i32 %a, 45  \n  \t	br i1 %b, label %firstminus, label %firstdigit  \n  firstminus:  \n  \t	store i32 -1, i32* %mult  \n  \t	br label %read  \n  firstdigit:  \n  \t	%c = sub i32 %a, 48  \n   \t	store i32 %c, i32* %digit  \n  \t	%d = icmp ne i32 %a, 10  \n  \t	br i1 %d, label %save, label %exit  \n  read:  \n  \t	%0 = call i32 @getchar()  \n  \t	%1 = sub i32 %0, 48  \n  \t	store i32 %1, i32* %digit  \n  \t	%2 = icmp ne i32 %0, 10  \n  \t	br i1 %2, label %save, label %exit  \n  save:  \n   \t	%3 = load i32,i32* %res  \n  \t	%4 = load i32,i32* %digit  \n  \t	%5 = mul i32 %3, 10  \n  \t	%6 = add i32 %5, %4  \n  \t	store i32 %6, i32* %res  \n  \t	br label %read  \n  getminus:  \n  \t	store i32 -1, i32* %mult  \n  \t	br label %read  \n exit:  \n  \t	%7 = load i32,i32* %res  \n \t	%8 = load i32,i32* %mult  \n \t	%9 = mul i32 %7, %8  \n  \t	ret i32 %9  \n }   \n";
    }

    // Function Read
    public String declarePrint() {
        return "declare i32 @putchar(i32)  \n   ; Defining a function wich print integer  \n     define void @printInt(i32 %a) {  \n       entry:  \n\t%size = alloca i32  \n\t %int = alloca i32  \n\t store i32 10, i32* %size  \n\t store i32 %a, i32* %int  \n\t         br label %negativetest   \n     negativetest:  \n     \t         %0 = icmp slt i32 %a, 0  \n     \t  br i1 %0, label %minus, label %sizecmp   \n     minus:  \n     \t  call i32 @putchar(i32 45)  \n     \t  %2 = load i32,i32* %int   \n     \t  %3 = mul i32 %2, -1  \n     \t  store i32 %3, i32* %int   \n     \t  br label %sizecmp  \n     computesize:  \n     \t  %4 = load i32,i32* %size  \n     \t  %5 = mul i32 %4, 10  \n     \t  store i32 %5, i32* %size  \n     \t  br label %sizecmp  \n     sizecmp:  \n     \t  %6 = load i32,i32* %size  \n     \t  %i = load i32,i32* %int  \n     \t  %7 = icmp ugt i32 %6, %i  \n     \t  br i1 %7, label %printloop, label %computesize  \n     printloop:  \n     \t  %j = load i32,i32* %int  \n     \t  %8 = load i32,i32* %size  \n     \t  %9 = udiv i32 %8, 10  \n     \t  store i32 %9, i32* %size  \n     \t  %10 = udiv i32 %j, %9  \n     \t  %11 = urem i32 %10, 10  \n     \t  %12 = add i32 %11, 48  \n     \t  call i32 @putchar(i32 %12)  \n     \t  %14 = icmp ugt i32 %9, 1  \n     \t  br i1 %14, label %printloop, label %exit  \n      exit:  \n       \t  ret void  \n       } \n";
    }

    // llvm code tostring
    public String toString() {
        String code = "";
        this.llvmcode.append("ret i32 0 \n }");

        if (this.read) {
            code += declareRead();
        }
        if (this.print) {
            code += declarePrint();

        }
        return code + this.llvmcode.toString();
    }
}
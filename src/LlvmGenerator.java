import java.util.*;

/**
 *
 * parcour the ParseTree and call LlvmCoda.java to write the code
 * 
 * @see LlvmCode
 *
 * @author Dhanani, Ali
 *
 */
public class LlvmGenerator {
    /**
     * ParseTree
     */
    ParseTree PTree;
    /** LLVM code generator */
    LlvmCode llvmCode;

    /**
     * 
     * @param PTree ParseTree
     */
    public LlvmGenerator(ParseTree PTree) {
        this.PTree = PTree;
        this.llvmCode = new LlvmCode();
    }

    /**
     * Generate the llvm code
     */
    public void Generate() {

        // Childrens of the ParseTree
        for (int i = 0; i < this.PTree.getChildren().size() - 1; i++) {
            // chek if there is a Varriables Node
            if (this.PTree.getChildren().get(i).getLabel().getValue() != null
                    && this.PTree.getChildren().get(i).getLabel().getValue().equals("Varriables")) {
                // Variables Tree
                Varriables(this.PTree.getChildren().get(i));

            } else if (this.PTree.getChildren().get(i).getLabel().getValue() != null
                    && this.PTree.getChildren().get(i).getLabel().getValue().equals("Code")) {
                // Code Tree
                Code(this.PTree.getChildren().get(i));

            }

        }

    }

    private void Varriables(ParseTree var) {
        // the child 0 is 'VARIABLES' we don't need it ,child 1 is varlist Tree
        if (var.getChildren().get(0).getLabel().getValue().equals("VARIABLES")) {
            VarList(var.getChildren().get(1));
        }

    }

    private void VarList(ParseTree varlist) {
        // get the first variable
        String var = varlist.getChildren().get(0).getLabel().getValue().toString();
        // if this var are not alredy declared ,we declare it
        if (!this.llvmCode.DeclareVars(var)) {
            LlvmGeneratecodeError(var + " already declared !");
        }
        // child 1 is varlisp to chek if we have other variables
        VarListp(varlist.getChildren().get(1));

    }

    private void VarListp(ParseTree varlistp) {
        // if varListp is not epsilon means varlistp contain 2 childs , this code is too
        if (varlistp.getChildren().size() == 2) {
            VarList(varlistp.getChildren().get(1));
        }

    }

    private void Code(ParseTree code) {
        // Instructions Tree
        Instructions(code.getChildren().get(0));
        if (code.getChildren().size() == 3) {
            Code(code.getChildren().get(2));
        }

    }

    private void Instructions(ParseTree inst) {
        if (!inst.getLabel().getValue().equals("EPSILON")) {
            switch (inst.getChildren().get(0).getLabel().getType()) {
            // Assign
            case VARNAME:
                String var = inst.getChildren().get(0).getLabel().getValue().toString();
                if (!this.llvmCode.Isvar(var)) {
                    LlvmGeneratecodeError("Varriable " + var + " Not declared !");
                }
                String exp = ExpArth(inst.getChildren().get(2), inst.getChildren().get(3), inst.getChildren().get(4));
                this.llvmCode.StoreValue("%" + var, exp);
                break;

            case IF:
                If(inst);
                break;
            case WHILE:
                While(inst);
                break;
            case FOR:
                // inst = FOR Node
                For(inst);
                break;
            case PRINT:
                // inst.getChildren().get(2) = Explist Node
                ExpList(inst.getChildren().get(2));
                break;
            case READ:
                // inst.getChildren().get(2) = VarList Node
                ReadVarList(inst.getChildren().get(2));
                break;
            }

        }

    }

    private String ExpArth(ParseTree beta, ParseTree tmp, ParseTree ex) {
        return ExprArithTmp(ex, ExprArithPrimTmp(tmp, ExprArithPrimeBeta(beta)));

    }

    private String ExprArithPrimeBeta(ParseTree beta) {
        String var = "";
        switch (beta.getChildren().get(0).getLabel().getType()) {
        case VARNAME:
            String vartoload = beta.getChildren().get(0).getLabel().getValue().toString();
            var = this.llvmCode.LoadVar(vartoload);
            if (var.equals("Not Found")) {
                LlvmGeneratecodeError("Varriable " + var + " Not initialised !");
            }
            return var;
        case NUMBER:
            var = beta.getChildren().get(0).getLabel().getValue().toString();

            return this.llvmCode.Uvars(var);

        case MINUS:
            var = ExprArithPrimeBeta(beta.getChildren().get(1));

            return this.llvmCode.Experssion("0", "sub nsw", var);
        case LPAREN:
            var = ExpArth(beta.getChildren().get(1), beta.getChildren().get(2), beta.getChildren().get(3));
            return var;
        }
        return var;
    }

    private String ExprArithPrimTmp(ParseTree tmp, String valbet) {
        String val = "";
        if (tmp.getChildren().size() > 1) {
            switch (tmp.getChildren().get(0).getLabel().getType()) {
            case TIMES:
                val = this.llvmCode.Experssion(valbet, "mul", ExprArithPrimeBeta(tmp.getChildren().get(1)));

                return ExprArithPrimTmp(tmp.getChildren().get(2), val);

            case DIVIDE:
                val = this.llvmCode.Experssion(valbet, "sdiv", ExprArithPrimeBeta(tmp.getChildren().get(1)));

                return ExprArithPrimTmp(tmp.getChildren().get(2), val);
            }

        }
        return valbet;
    }

    private String ExprArithTmp(ParseTree exp, String tmp) {

        if (exp.getChildren().size() > 1) {
            String val = "";
            switch (exp.getChildren().get(0).getLabel().getType()) {
            case PLUS:
                val = ExpArth(exp.getChildren().get(1), exp.getChildren().get(2), exp.getChildren().get(3));
                return this.llvmCode.Experssion(tmp, "add", val);
            case MINUS:
                val = ExpArth(exp.getChildren().get(1), exp.getChildren().get(2), exp.getChildren().get(3));
                return this.llvmCode.Experssion(tmp, "sub", val);
            }
        }
        return tmp;
    }

    private void ExpList(ParseTree explist) {

        String exp = ExpArth(explist.getChildren().get(0), explist.getChildren().get(1), explist.getChildren().get(2));
        this.llvmCode.PrintInt(exp);
        ExpListPrime(explist.getChildren().get(3));

    }

    private void ExpListPrime(ParseTree ExpListPrime) {

        if (ExpListPrime.getChildren().size() == 2) {
            ExpList(ExpListPrime.getChildren().get(1));
        }
    }

    private void ReadVarList(ParseTree varlist) {

        String var = varlist.getChildren().get(0).getLabel().getValue().toString();
        if (!this.llvmCode.Isvar(var)) {
            LlvmGeneratecodeError(var + " not declared !");

        }
        this.llvmCode.ReadInt(var);
        ReadVarListp(varlist.getChildren().get(1));

    }

    private void ReadVarListp(ParseTree varlistp) {
        if (varlistp.getChildren().size() == 2) {
            ReadVarList(varlistp.getChildren().get(1));
        }

    }

    private void For(ParseTree forTree) {

        String var = forTree.getChildren().get(1).getLabel().getValue().toString();
        if (!this.llvmCode.Isvar(var)) {
            this.llvmCode.DeclareVars(var);
        }
        String n = ExpArth(forTree.getChildren().get(7), forTree.getChildren().get(8), forTree.getChildren().get(9));
        String i = ExpArth(forTree.getChildren().get(3), forTree.getChildren().get(4), forTree.getChildren().get(5));
        this.llvmCode.StoreValue("%" + var, i);
        int forid = this.llvmCode.Forid();

        this.llvmCode.ForLop(var, n, forid);
        Code(forTree.getChildren().get(12));
        this.llvmCode.EndFor(var, forid);
        this.llvmCode.AfterFor(forid);
    }

    private void If(ParseTree iftree) {

        int ifid = this.llvmCode.Iflabel();
        String ifcnd = CondBeta(iftree.getChildren().get(3), CondPrime(iftree.getChildren().get(2)));
        String labelcond = Ifst(iftree.getChildren().get(8));

        this.llvmCode.If(ifcnd, labelcond, ifid);
        Code(iftree.getChildren().get(7));
        ElseCode(iftree.getChildren().get(8), ifid);

    }

    private String Ifst(ParseTree IfstTree) {
        if (IfstTree.getChildren().get(0).getLabel().getValue().equals("ELSE")) {
            return "ELSE";
        }
        return "ENDIF";

    }

    private void ElseCode(ParseTree ElseCode, int ifid) {
        if (ElseCode.getChildren().get(0).getLabel().getValue().equals("ELSE")) {
            this.llvmCode.Ifstlabel("ELSE", ifid);
            Code(ElseCode.getChildren().get(2));
            this.llvmCode.Ifstlabel("ENDIF", ifid);
        } else
            this.llvmCode.Ifstlabel("ENDIF", ifid);

    }

    private void While(ParseTree whiletree) {

        int whileid = this.llvmCode.whilelabel();
        String whilecnd = CondBeta(whiletree.getChildren().get(3), CondPrime(whiletree.getChildren().get(2)));

        this.llvmCode.While(whilecnd, whileid);
        Code(whiletree.getChildren().get(7));
        this.llvmCode.Endwhile(whilecnd, whileid);
        this.llvmCode.Afterwhile(whileid);

    }

    private String CondPrime(ParseTree condp) {

        if (condp.getChildren().get(0).getLabel().getValue().equals("NOT")) {

            String cnd = CondPrimeBeta(condp.getChildren().get(2), SimpleCond(condp.getChildren().get(1)));
            return this.llvmCode.Bitw("xor", cnd, "true");
        }

        return CondPrimeBeta(condp.getChildren().get(1), SimpleCond(condp.getChildren().get(0)));

    }

    private String CondBeta(ParseTree condB, String condP) {
        if (condB.getChildren().get(0).getLabel().getValue().equals("OR")) {
            String orvar = CondBeta(condB.getChildren().get(2), CondPrime(condB.getChildren().get(1)));
            return this.llvmCode.Bitw("or", orvar, condP);

        }
        return condP;
    }

    private String CondPrimeBeta(ParseTree condpb, String condprimeVar) {
        if (condpb.getChildren().get(0).getLabel().getValue().equals("AND")) {

            String andvar = CondPrimeBeta(condpb.getChildren().get(2), CondPrime(condpb.getChildren().get(1)));
            return this.llvmCode.Bitw("and", andvar, condprimeVar);

        }
        return condprimeVar;
    }

    private String SimpleCond(ParseTree SimpleCond) {
        String expA = ExpArth(SimpleCond.getChildren().get(0), SimpleCond.getChildren().get(1),
                SimpleCond.getChildren().get(2));
        String expB = ExpArth(SimpleCond.getChildren().get(4), SimpleCond.getChildren().get(5),
                SimpleCond.getChildren().get(6));
        String comp = Comp(SimpleCond.getChildren().get(3));
        return this.llvmCode.BooleanOp(comp, expA, expB);
    }

    private String Comp(ParseTree comp) {
        switch (comp.getChildren().get(0).getLabel().getValue().toString()) {
        case "=":
            return "eq";
        case ">=":
            return "sge";
        case ">":
            return "sgt";

        case "<=":
            return "sle";

        case "<":
            return "slt";

        case "<>":
            return "ne";
        }
        return null;
    }

    private void LlvmGeneratecodeError(String var) {
        System.out.println(var);
        System.exit(-1);
    }

    /**
     * llvm code to string
     */
    public String toString() {
        return this.llvmCode.toString();
    }
}

/**
 * Created by ramilagger on 28/09/17.
 */
sealed class Statement
data class AssignmentStatement(val variable: Variable, val expr: Expr) : Statement()
data class PrintStatement(val expr: Expr) : Statement()
data class IfStatement(val ifExpr : Expr,val ifBlock : List<Statement>,val elseBlock : List<Statement>?) : Statement()

// TODO add normal memory
fun eval(s : Statement) {

    when(s) {
        is AssignmentStatement -> memory[s.variable.name] = eval(s.expr)
        is PrintStatement -> pw.println(eval(s.expr))
        is IfStatement -> {
            if(eval(s.ifExpr) != IntValue(0)){
                s.ifBlock.forEach {
                    eval(it)
                }
            }else {
                s.elseBlock?.forEach {
                    eval(it)
                }
            }
        }
    }

}

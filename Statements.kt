
/**
 * Created by ramilagger on 28/09/17.
 */
sealed class Statement
data class AssignmentStatement(val variable: Variable,val expected: Token, val expr: Expr) : Statement()
data class PrintStatement(val expr: Expr) : Statement()
data class IfStatement(val ifExpr : Expr,val ifBlock : List<Statement>,val elseBlock : List<Statement>?) : Statement()
data class WhileStatement(val expr : Expr, val whileBlock : List<Statement>) : Statement()
object SemiColonStatement : Statement()




// TODO add normal memory
fun eval(s : Statement) {

    when(s) {
        is AssignmentStatement -> {
            val a = eval(s.expr)
            val ex = when(s.expected) {
                is Var -> memory[s.expected.name]!!.toToken()
                else -> s.expected
            }
            if(a.toToken() != ex)
                throw RuntimeException("Expected type ${s.expected} but encountered ${a.toToken()}")
            else {
                memory[s.variable.name] = eval(s.expr)
            }
        }
        is PrintStatement -> {
            pw.println(eval(s.expr))
        }
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
        is WhileStatement -> {
            while (eval(s.expr) != IntValue(0)) {
                s.whileBlock.forEach {
                    eval(it)
                }
            }
        }
        is SemiColonStatement -> {

        }
    }

}

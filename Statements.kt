/**
 * Created by ramilagger on 28/09/17.
 *
 */

sealed class Statement

data class AssignmentStatement(val variable: Variable, val expected: Token, val expr: Expr) : Statement()
data class PrintStatement(val expr: Expr) : Statement()
data class IfStatement(val ifExpr: Expr, val ifBlock: List<Statement>, val elseBlock: List<Statement>?) : Statement()
data class WhileStatement(val expr: Expr, val whileBlock: List<Statement>) : Statement()
data class ReturnStatement(val expr: Expr) : Statement()
object SemiColonStatement : Statement()
data class FunctionCall(val args: ArrayList<Expr>, val function: Var) : Statement()
data class ExpressionStatement(val a: Expr) : Statement()


var cnt = 0
fun eval(s: Statement, scope: Scope = Scope()) {
    cnt++
    when (s) {
        is AssignmentStatement -> {
            val a = eval(s.expr)
            val ex = when (s.expected) {
                is Var -> Memory.get(s.expected.name).toToken()
                else -> s.expected
            }
            if (a.toToken() != ex)
                throw RuntimeException("Expected type ${s.expected} but encountered ${a.toToken()}")
            else {
                System.err.println(scope.variables)
                if (scope.variables.contains(s.variable.name)) {
                    error("${s.variable.name} is already declared in given scope")
                }
                if (s.expected !is Var) {
                    System.err.println("here")
                    scope.variables.add(s.variable.name)
                    Memory.put(s.variable.name, eval(s.expr))
                } else Memory.replace(s.variable.name, eval(s.expr))
            }
        }
        is PrintStatement -> {
            pw.print(eval(s.expr))
        }
        is IfStatement -> {
            if (eval(s.ifExpr) == BoolValue(true)) {
                val localScope = Scope()
                s.ifBlock.forEach {
                    eval(it, localScope)
                }
                Memory.clear(localScope)
            } else {
                val localScope = Scope()
                s.elseBlock?.forEach {
                    eval(it, localScope)
                }
                Memory.clear(localScope)
            }
        }
        is WhileStatement -> {
            var b = eval(s.expr)
            while (b == BoolValue(true)) {
                val localScope = Scope()
                s.whileBlock.forEach {
                    eval(it, localScope)
                }
                Memory.clear(localScope)
                b = eval(s.expr)
            }
        }
        is SemiColonStatement -> {

        }
        is ExpressionStatement -> eval(s.a)
        is ReturnStatement -> {
            val a = eval(s.expr)
            Memory.poll()
            if (a.toToken() == ReturnTypes.a.peek())
                ReturnTypes.a.pop()
            else error("Function returns unknown value")
            throw ReturnException(a)
        }
        is FunctionCall -> {

            val a = s.args.map { eval(it) }
            val f = Functions.get(s.function.name)
            ReturnTypes.a.add(f.returnType)
            Memory.add()
            for (i in f.args.indices) {
                if (a[i].toToken() == f.args[i].Type)
                    Memory.put(f.args[i].variable.name, a[i])
                else throw RuntimeException("Expected ${f.args[i].Type} but encountered ${a[i].toToken()}")
            }
            val scope = Scope()
            f.statements.forEach {
                eval(it, scope)
            }
            Memory.poll()
        }
    }
}

/*
fun eval1(s : Statement) : Value {

    var v : Value

    v = when(s) {
        is AssignmentStatement -> {
            val a = eval(s.expr)
            val ex = when(s.expected) {
                is Var -> Memory.get(s.expected.name).toToken()
                else -> s.expected
            }
            if(a.toToken() != ex)
                throw RuntimeException("Expected type ${s.expected} but encountered ${a.toToken()}")
            else {
               // Memory.put(s.variable.name,eval(s.expr))
           }
        }
        is PrintStatement -> {
            pw.println(eval(s.expr))
        }
        is IfStatement -> {
            if(eval(s.ifExpr) == BoolValue(true)) {
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
            var b = eval(s.expr)
            while (b == BoolValue(true)) {
                s.whileBlock.forEach {
                    eval(it)
                }
                b = eval(s.expr)
            }
        }
        is SemiColonStatement -> {}
        is ExpressionStatement ->  eval(s.a)
        is ReturnStatement -> {
            val a = eval(s.expr)
            Memory.poll()
            throw ReturnException(a)
        }
        is FunctionCall -> {
            val a = s.args.map { eval(it) }
            val f = Functions.get(s.function.name)
            Memory.add()

            for (i in f.args.indices) {
                if(a[i].toToken() == f.args[i].Type)
                    Memory.put(f.args[i].variable.name,a[i])
                else throw RuntimeException("Expected ${f.args[i].Type} but encountered ${a[i].toToken()}")
            }

            f.statements.forEach {
                eval(it)
            }

        }

    }

}

*/


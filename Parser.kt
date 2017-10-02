
/**
 * Created by ramilagger on 7/23/17.
 *
 */

// regexp grouping
// https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html operator ordering
val memory = HashMap<String,Value>()


// expressions
sealed class Expr
data class Sum(val a:Expr,val b : Expr) : Expr()
data class Times(val a : Expr,val b : Expr) : Expr()
data class Subtract(val a : Expr,val b : Expr) : Expr()
data class Divide(val a : Expr,val b : Expr) : Expr()
data class Constant(val a : NumberValue) : Expr()
data class Variable(val name : String) : Expr()


// Evaluation of expressions
// TODO add return values as one type (float boolean and stuff) and smart casting // done
fun eval(expr: Expr) : NumberValue = when(expr) {
    is Times -> eval(expr.a) * eval(expr.b)
    is Divide -> {
        val res = eval(expr.b)
        if(res == IntValue(0) || res == DoubleValue(0.0))
            throw RuntimeException("Division by 0")
        eval(expr.a) / res
    }
    is Sum -> eval(expr.a) + eval(expr.b)
    is Subtract -> eval(expr.a) - eval(expr.b)
    is Constant -> expr.a
    is Variable -> when(memory[expr.name]) {
        is IntValue, is DoubleValue -> memory[expr.name] as NumberValue
        else -> throw RuntimeException()
    }
}

// Binary expressions
sealed class BExpr
//data class Variable(val a : Boolean)
data class And(val a : BExpr, val b : BExpr) : BExpr()
data class Or(val a : BExpr,val b : BExpr) : BExpr()
data class LessThan(val a : Expr,val b : Expr) : BExpr()
data class MoreThan(val a : Expr, val b : Expr) : BExpr()


/*

fun beval(bexpr: BExpr) = when(bexpr) {
    is And -> eval(bexpr.a)
}
*/


class Parser(val tokens : List<Token>) {

    var cur = 0

    fun peek(relPos: Int): Token {
        val pos = cur + relPos
        if (pos >= tokens.size)
            return EOF
        return tokens[pos]
    }


    fun next(): Token {
        cur++
        return peek(0)
    }

    fun parse() : List<Statement> {
        var token = peek(0)
        val list = ArrayList<Statement>()
        while (token != EOF) {
            val s = parseStatement()
            System.err.println(s)
            list.add(s)
            token = peek(0)
        }
        return list
    }

    fun parseStatement() : Statement {
        val token = peek(0)
        var st  = when(token) {
            is IntType, is DoubleType -> parseAssignment()
            /* add var
            is Var ->  {
                whentoken.name
            }
            */
            is Print -> {
                next() // skip print
                PrintStatement(additive())

            }
            is If -> {
                next() // skip if
                parseIfElse()
            }
            else ->
                throw RuntimeException("at Token $token")
        }
        if(peek(0) == SemiColon) {
            var a = next()

        }
        return st
    }

    private fun  parseIfElse() : Statement {
        val ifExpr = additive()
        var ifStatements : ArrayList<Statement>
        if(peek(0) == CLP) {
            ifStatements = parseCodeBlock()
        }else {
            ifStatements = ArrayList()
            ifStatements.add(parseStatement())
        }
        var elseStatements : List<Statement>? = null
        if(peek(0) == Else) {
            //System.err.println(peek(0))
            //System.err.println(peek(1))
            next() // skip Else

            if(peek(0) == CLP) {
                //System.err.println("here as expected")
                elseStatements = parseCodeBlock()
            }else {
                elseStatements = ArrayList()
                elseStatements.add(parseStatement())
            }
        }
        return IfStatement(ifExpr,ifStatements,elseStatements)
    }

    private fun  parseCodeBlock(): ArrayList<Statement> {
        var list = ArrayList<Statement>()
        var cnt = 1     // number of opening CLPs encountered
        //System.err.println(next()) // skip rp
        next()
        while (cnt > 0) {
            var token = peek(0)

            if (token == CLP) cnt++
            if (token == RLP) cnt--
            System.err.println(token)
            when (token) {
                is IntType,is DoubleType -> list.add(parseAssignment())
                is Print -> {
                    next()  // skip print
                    list.add(PrintStatement(additive()))
                }
                is If -> {
                    next() // skip if
                    list.add(parseIfElse())
                }
            }
            if(peek(0) == SemiColon) next() // skip
        }
        next() // RLP skip
        //System.err.println("here ${peek(0)}")
        return list
    }

    private fun parseAssignment() : AssignmentStatement {
        val type = peek(0)
        System.err.println("here" + type)
        //val a = peek(0) // skip type for now add equal as token
        val a = next()
        if(a !is Var)
            throw Exception("Expected Type Var but encountered ${peek(0)}")
        else {
            var variable = Variable(a.name)
            next() // skip name
            return AssignmentStatement(variable, additive())
        }
    }


    private fun additive(): Expr {
        var expr = multiplicative()
        while (peek(0) == Plus || peek(0) == Subtraction) {
            next()  // skip operator
            expr = if (peek(-1) == Plus)
                Sum(expr, multiplicative())
            else
                Subtract(expr, multiplicative())
        }
        return expr
    }

    private fun multiplicative(): Expr {
        var expr = unary()
        while (peek(0) == Multiplication || peek(0) == Division) {
            next()
            expr = if (peek(-1) == Multiplication) Times(expr, unary()) else Divide(expr, unary())
        }
        return expr
    }

    private fun  unary() : Expr {
        var temp = peek(0)
        return when(temp) {
            Subtraction -> {
                next()                          // skip -
                // TODO normal way ? add type
                Subtract(Constant(IntValue(0)),primary())
            }
            Plus -> {
                next()   // skip +
                primary()
            }
            else -> primary()
        }
    }

    private fun primary(): Expr {
        val temp = peek(0)
        next()
        when (temp) {
            is Number ->
                return Constant(temp.value)
            is Var -> {
                //if(memory[temp.name] == null) throw Exception("${temp.name} is null")
                return Variable(temp.name)
            }
            is LP -> {
                val res = additive()
                next() // skip Right Parenthesis
               // System.err.println("skipped  ${peek(-1)}")
                return res
            }
            else -> throw RuntimeException("Illegal Operation at pos $cur at token $temp")
        }
    }
}


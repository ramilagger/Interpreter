// expressions
sealed class Expr

data class Sum(val a: Expr, val b: Expr) : Expr()
data class Times(val a: Expr, val b: Expr) : Expr()
data class Subtract(val a: Expr, val b: Expr) : Expr()
data class Divide(val a: Expr, val b: Expr) : Expr()
data class Constant(val a: NumberValue) : Expr()
data class Variable(val name: String) : Expr()
data class CharExpression(val a: Char) : Expr()
sealed class StringExpr : Expr()
data class ConstantString(val a: StringValue) : StringExpr()

// Binary expressions
sealed class BExpr : Expr()

object TRUE : BExpr()
object FAlSE : BExpr()
data class And(val a: Expr, val b: Expr) : BExpr()
data class Or(val a: Expr, val b: Expr) : BExpr()
data class Equals(val a: Expr, val b: Expr) : BExpr()
data class NotEquals(val a: Expr, val b: Expr) : BExpr()
data class LessThan(val a: Expr, val b: Expr) : BExpr()
data class MoreThan(val a: Expr, val b: Expr) : BExpr()
data class Not(val a: Expr) : BExpr()

data class FunctionalExpression(val a: FunctionCall) : Expr()


// Evaluation of expressions
fun eval(expr: Expr): Value = when (expr) {
    is Times -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a is NumberValue && b is NumberValue) a * b
        else throw RuntimeException("Cannot multiply $a and $b")
    }
    is Divide -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (b == IntValue(0) || b == DoubleValue(0.0))
            throw RuntimeException("Division by 0")
        if (a is NumberValue && b is NumberValue) a / b
        else throw RuntimeException("Cannot divide $a and $b")
    }
    is Sum -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        a + b
        //else throw RuntimeException("Cannot multiply $a and $b")

    }
    is Subtract -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a is NumberValue && b is NumberValue) a - b
        else throw RuntimeException("Cannot multiply $a and $b")
    }
    is Constant -> expr.a
    is Variable -> Memory.get(expr.name)
    is ConstantString -> expr.a
    is LessThan -> {
        val ans = eval(expr.a) < eval(expr.b)
        BoolValue(ans)
    }
    is MoreThan -> {
        val ans = eval(expr.a) > eval(expr.b)
        BoolValue(ans)
    }
    is TRUE -> BoolValue(true)
    is FAlSE -> BoolValue(false)
    is Not -> {
        val b = eval(expr.a)
        if (b is BoolValue) BoolValue(!b.value)
        else throw RuntimeException("Not applicable only for boolean values")
    }
    is Equals -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a.toToken() == b.toToken())
            when (a) {
                is BoolValue -> BoolValue(a.value == (b as BoolValue).value)
                is IntValue -> BoolValue(a.value == (b as IntValue).value)
                is DoubleValue -> BoolValue(a.value == (b as DoubleValue).value)
                is StringValue -> BoolValue(a.value.equals((b as StringValue).value))
                is CharValue -> BoolValue(a.value.equals((b as CharValue).value))
            } else BoolValue(false)
    }
    is NotEquals -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a.toToken() == b.toToken())
            when (a) {
                is BoolValue -> BoolValue(a.value != (b as BoolValue).value)
                is IntValue -> BoolValue(a.value != (b as IntValue).value)
                is DoubleValue -> BoolValue(a.value != (b as DoubleValue).value)
                is StringValue -> BoolValue(!a.value.equals((b as StringValue).value))
                is CharValue -> BoolValue(!a.value.equals((b as CharValue).value))
            } else BoolValue(true)
    }
    is And -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a is BoolValue && b is BoolValue) {
            BoolValue(a.value.and(b.value))
        } else throw RuntimeException("And operator only supported for bool values ")
    }
    is Or -> {
        val a = eval(expr.a)
        val b = eval(expr.b)
        if (a is BoolValue && b is BoolValue) {
            BoolValue(a.value.or(b.value))
        } else throw RuntimeException("And operator only supported for bool values ")
    }
    is CharExpression -> CharValue(expr.a)
    is FunctionalExpression -> {
        try {
            eval(expr.a)
            error("Function have no return statement at given branch")
        } catch (e: ReturnException) {
            e.returnValue
        }

    }
}


class Parser(val tokens: List<Token>) {

    var cur = 0

    fun peek(relPos: Int): Token {
        val pos = cur + relPos
        if (pos >= tokens.size) return EOF
        return tokens[pos]
    }

    fun next(): Token {
        cur++
        return peek(0)
    }

    fun parse(): List<Method> {
        var token = peek(0)
        val list = ArrayList<Method>()
        while (token != EOF) {
            val s = parseFunction()
            System.err.println(s)
            list.add(s)
            token = peek(0)
        }
        return list
    }

    private fun parseFunction(): Method {
        var token = peek(0) as Type // return type
        var name = next() as Var   // name
        if (next() != LP) throw RuntimeException("Opening bracket expected at line line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}")
        var args = parseArguments()
        ignore(CLP)
        var statements = ArrayList<Statement>()
        while (peek(0) != RLP) {
            statements.add(parseStatement())
        }
        ignore(RLP)
        var a = Method(args = args, name = name.name, statements = statements, returnType = token)
        Functions.add(a)
        return a
    }

    private fun parseArguments(): List<Argument> {
        var list = ArrayList<Argument>()
        while (peek(1) != RP) {
            var a = next() as Type
            var b = next() as Var
            list.add(Argument(a, b))
        }
        next() // last token before RP either RP or Var
        ignore(RP)
        return list
    }

    private fun ignore(token: Token) {
        if (token::class.equals(peek(0)::class)) next()
        else throw RuntimeException("Expected $token but found ${peek(0)} at  line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}")
    }

    fun parseStatement(): Statement {
        val token = peek(0)
        var st = when (token) {
            is IntType, is DoubleType, is StringType, is BooleanType, is CharType -> {
                //System.err.println(peek(0))
                //if(peek(1) == SLP) {
                //   parseArrayAssignment()
                //}
                parseAssignment()
            }
            is Var -> {
                if (peek(1) == Assign) return parseReAssignment()
                else {
                    val a = expression()
                    if (a is FunctionalExpression) {
                        return a.a
                    } else error("$a is not a functional expression at line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}\" ")
                    //return ExpressionStatement(expression())
                }
            }
            is Return -> {
                next()
                ReturnStatement(expression())
            }
            is Print -> {
                next() // skip print
                PrintStatement(expression())

            }
            is If -> {
                next() // skip if
                parseIfElse()
            }
            is While -> {
                next()
                parseWhile()
            }
            else ->
                throw RuntimeException("Unknown token at line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}" + token)
        }
        if (peek(0) == SemiColon) ignore(SemiColon) // semicolon is optional ?
        return st
    }


    private fun parseFunctionCall(): FunctionCall {
        var name = peek(0) as Var
        next()
        val exprs = parseFunctionArguments()
        return FunctionCall(exprs, name)
    }


    private fun parseFunctionArguments(): ArrayList<Expr> {
        ignore(LP)
        val exprs: ArrayList<Expr> = ArrayList()
        while (peek(0) != RP)
            exprs.add(expression())
        ignore(RP)
        return exprs
    }


    private fun parseWhile(): Statement {
        val expr = expression()
        var statements: ArrayList<Statement>
        if (peek(0) == CLP) {
            statements = parseCodeBlock()
        } else {
            statements = ArrayList()
            statements.add(parseStatement())
        }
        return WhileStatement(expr, statements)
    }

    private fun parseReAssignment(): Statement {
        val a = peek(0)
        if (a !is Var)
            throw Exception("Expected Type Var but encountered ${peek(0)} at line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}")
        else {
            var variable = Variable(a.name)

            ignore(a)
            ignore(Assign)

            return AssignmentStatement(variable, a, expression())
        }
    }

    private fun parseIfElse(): Statement {
        val ifExpr = expression()
        var ifStatements: ArrayList<Statement>
        if (peek(0) == CLP) {
            ifStatements = parseCodeBlock()
        } else {
            ifStatements = ArrayList()
            ifStatements.add(parseStatement())
        }
        var elseStatements: List<Statement>? = null
        if (peek(0) == Else) {
            ignore(Else)
            if (peek(0) == CLP) {
                elseStatements = parseCodeBlock()
            } else {
                elseStatements = ArrayList()
                elseStatements.add(parseStatement())
            }
        }
        return IfStatement(ifExpr, ifStatements, elseStatements)
    }

    private fun parseCodeBlock(): ArrayList<Statement> {
        var list = ArrayList<Statement>()
        var cnt = 1     // number of opening CLPs encountered
        ignore(CLP)
        while (cnt > 0) {
            var token = peek(0)

            if (token == CLP) cnt++
            if (token == RLP) cnt--
            //System.err.println(token)
            when (token) {
                is IntType, is DoubleType -> list.add(parseAssignment())
                is Var -> {
                    //print("here")
                    if (peek(1) == Assign) list.add(parseReAssignment())
                    else list.add(ExpressionStatement(expression()))
                    //list.add(parseReAssignment())
                }
                is Print -> {
                    next()  // skip print
                    list.add(PrintStatement(expression()))
                }
                is Return -> {
                    next()
                    list.add(ReturnStatement(expression()))
                }
                is If -> {
                    next() // skip if
                    list.add(parseIfElse())
                }
            }
            if (peek(0) == SemiColon) ignore(SemiColon) // Semicolon optional
        }
        ignore(RLP)
        return list
    }

    private fun parseAssignment(): AssignmentStatement {
        val type = peek(0)
        //System.err.println("here " + type)
        //val a = peek(0) // skip type for now add equal as token
        val a = next()
        if (a !is Var)
            throw Exception("Expected Type Var but encountered ${peek(0)} at line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}")
        else {
            var variable = Variable(a.name)
            ignore(a)
            ignore(Assign)

            return AssignmentStatement(variable, type, expression())
        }
    }

    private fun expression(): Expr {
        return or()
    }


    private fun or(): Expr {
        var expr = and()
        while (peek(0) == OR) {
            next()
            expr = Or(expr, and())
        }
        return expr
    }

    private fun and(): Expr {
        var expr = equality()
        while (peek(0) == AND) {
            next()  // skip operator
            expr = And(expr, equality())
        }
        return expr
    }

    private fun equality(): Expr {
        var expr = conditional()
        while (peek(0) == Equality || peek(0) == NotEquality) {
            next()  // skip operator
            expr = if (peek(-1) == Equality)
                Equals(expr, conditional())
            else
                NotEquals(expr, conditional())
        }
        return expr
    }

    private fun conditional(): Expr {
        var expr = additive()
        while (peek(0) == LessThanSign || peek(0) == MoreThanSign) {
            next()  // skip operator
            expr = if (peek(-1) == LessThanSign)
                LessThan(expr, additive())
            else
                MoreThan(expr, additive())
        }
        return expr
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

    private fun unary(): Expr {
        var temp = peek(0)
        return when (temp) {
            Subtraction -> {
                next()                          // skip -
                // TODO normal way ? add type
                Subtract(Constant(IntValue(0)), primary())
            }
            Plus -> {
                next()   // skip +
                primary()
            }
            NOT -> {
                next()
                Not(primary())
            }
            else -> primary()
        }
    }

    private fun primary(): Expr {
        val temp = peek(0)
        next()
        when (temp) {
            is Number -> return Constant(temp.value)
            is StringToken -> return ConstantString(temp.value)
            is BooleanToken -> return if (temp.value) TRUE else FAlSE
            is CharToken -> return CharExpression(temp.value)
            is Var -> {
                System.err.println(peek(0))
                if (peek(0) is LP) {
                    cur-- // return to token Var
                    return FunctionalExpression(parseFunctionCall())
                }
                return Variable(temp.name)
            }
            is LP -> {
                val res = expression()
                ignore(RP)
                return res
            }
            else -> throw RuntimeException("Illegal Operation at line ${Positions.pos[cur].first} symbol ${Positions.pos[cur].second}")
        }
    }

}


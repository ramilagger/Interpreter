
// Tokens
sealed class Token
object Plus : Token()
object Multiplication : Token()
object Division : Token()
object Subtraction : Token()
object EOF : Token()
object LP : Token()                                   // left parentheses
object RP : Token()
object Print : Token()
object IntType : Token()                              // int type
object DoubleType : Token()
object StringType : Token()
object CharType : Token()
object If : Token()
object Else : Token()
object CLP : Token()                                  // Curly LP
object RLP : Token()
object While : Token()


object SemiColon : Token()
data class Var(val name : String) : Token()
data class StringToken(val value: StringValue) : Token()
data class Number(val value : NumberValue) : Token()
data class CharToken(val value: Char) : Token()


class Lexer(val text : String) {

    var cur = 0

    fun peek(relPos: Int): Char {
        val pos = cur + relPos
        if (pos >= text.length) return 0.toChar()
        return text[pos]
    }

    fun next(): Char {
        cur++
        return peek(0)
    }

    fun lex(): List<Token> {
        val tokens = ArrayList<Token>()
        val a : Int = 39
        println(a.toChar())
        while (peek(0) != 0.toChar()) {
            if (peek(0).isDigit())
                tokens.add(parseDigit())
            if (peek(0).isLetter())
                tokens.add(parseWord())
            if(peek(0) == '"')
                tokens.add(parseString())
            if(peek(0) == a.toChar())
             tokens.add(parseChar())
            else if ("+*-/(){};".indexOf(peek(0)) > -1)
                tokens.add(parseOperator())
            else next()
        }
        tokens.forEach { println(it) }
        return tokens
    }

    private fun  parseChar(): Token {
        next()
        var a = peek(0)
        next()
        next()
        return CharToken(a)
    }

    private fun  parseString(): Token {
        next()
        var sb = StringBuilder()

        while (peek(0) != '"') {
            sb.append(peek(0))
            next()
        }
        next()
        return StringToken(StringValue(sb.toString()))
    }

    private fun parseWord(): Token {
        var sb = StringBuilder()
        sb.append(peek(0))
        next()
        // TODO add all valid characters
        while (Character.isLetterOrDigit(peek(0))) {
            sb.append(peek(0))
            next()
        }
        var s = sb.toString()
        return when(s) {
              "int" -> IntType
              "print" -> Print
              "if" -> If
              "else" -> Else
              "while" -> While
              "double" -> DoubleType
              "string" -> StringType
              "char" -> CharType
              else -> Var(s)
        }
    }

    private fun parseDigit(): Token {
        var sb = StringBuilder()
        sb.append(peek(0))
        next()
        var dot = false
        while (Character.isDigit(peek(0)) || (!dot && peek(0) == '.')) {
            sb.append(peek(0))
            if(peek(0) == '.') dot = true
            next()
        }

        return if(!dot)
            Number(IntValue(Integer.parseInt(sb.toString())))
        else
            Number(DoubleValue(sb.toString().toDouble()))
    }

    private fun parseOperator(): Token {
        next()
        return when (peek(-1)) {
            '+' -> Plus
            '-' -> Subtraction
            '*' -> Multiplication
            '/' -> Division
            '(' -> LP
            ')' -> RP
            '{' -> CLP
            '}' -> RLP
            ';' -> SemiColon
            else -> throw RuntimeException()
        }
    }


}
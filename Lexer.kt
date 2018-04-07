import java.awt.Color
import javax.swing.text.Style
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

// Tokens
sealed class Token

sealed class Operator : Token()
object Plus : Operator()
object Multiplication : Operator()
object Equality : Operator()
object Division : Operator()
object Subtraction : Operator()
object NotEquality : Operator()
object Assign : Operator()
object LessThanSign : Operator()
object MoreThanSign : Operator()
object OR : Operator()
object AND : Operator()
object NOT : Operator()

sealed class Comment : Token()
data class SingleLineComment(val value: String) : Comment()
data class MultilineLineComment(val value: String) : Comment()


object EOF : Token()
object LP : Token()                                   // left parentheses
object RP : Token()
object CLP : Token()                                  // Curly LP
object RLP : Token()                                  // CRP should rename
object SLP : Token()
object SRP : Token()
object SemiColon : Token()

object Print : Token()

sealed class Type : Token()
object IntType : Type()                              // int type
object DoubleType : Type()
object StringType : Type()
object CharType : Type()
object BooleanType : Type()

object If : Token()
object Return : Token()
object Else : Token()
object While : Token()


data class BooleanToken(val value: Boolean) : Token()
data class Var(val name: String) : Token()
data class StringToken(val value: StringValue) : Token()
data class Number(val value: NumberValue) : Token()
data class CharToken(val value: Char) : Token()


class Lexer(val text: String) {

    var cur = 0
    var line = 1
    var symbol = 1

    fun peek(relPos: Int): Char {
        val pos = cur + relPos
        if (pos >= text.length) return 0.toChar()
        return text[pos]
    }

    // returns the token and the data behind the parsed token TODO probably should add to list of tokens here
    private fun <Token> getTokenData(function: () -> Token): Pair<Token, String> {
        val start = cur
        return function() to text.substring(start, cur)
    }

    // append* to styled document :D
    private fun StyledDocument.append(s: String, style: Style, color: Color) {
        try {
            StyleConstants.setForeground(style, color)
            insertString(length, s, style)
        } catch (e: Exception) {
            throw RuntimeException("inserting error should never happen")
        }
    }

    // lexers to tokens with highlighting
    fun lexToString(style: Style, doc: StyledDocument): StyledDocument {
        val a: Int = 39 // symbol '
        while (peek(0) != 0.toChar()) {
            doc.apply {
                if (peek(0).isDigit())
                    append(getTokenData { parseDigit() }.second, style, Color.CYAN)
                else if (peek(0).isLetter()) {
                    val (a, b) = getTokenData { parseWord() }
                    var color = when (a) {
                        is Type -> Color.RED
                        else -> Color.GREEN
                    }
                    append(b, style, color)
                } else if (peek(0) == '"')
                    append(getTokenData { parseString() }.second, style, Color.BLUE)
                else if (peek(0) == a.toChar())
                    append(getTokenData { parseChar() }.second, style, Color.YELLOW)
                else if ("+*-/(){};<>!=|&".indexOf(peek(0)) > -1) {
                    val (a, b) = getTokenData { parseOperator() }
                    append(b, style, if (a is Operator) Color.magenta else if (a is Comment) Color.LIGHT_GRAY else Color.ORANGE)
                } else {
                    append(getTokenData { next() }.second, style, Color.LIGHT_GRAY)
                }
            }
        }
        return doc
    }

    fun next(): Char {
        cur++
        if (peek(0) == '\n') {
            line++
            symbol = 1
        } else symbol++
        return peek(0)
    }

    fun lex(): List<Token> {
        val tokens = ArrayList<Token>()
        //val pos = ArrayList<Pair<Int,Int>>()
        Positions.pos.clear()
        val a: Int = 39 // symbol '
        //println(a.toChar())
        while (peek(0) != 0.toChar()) {
            if (peek(0).isDigit()) {
                Positions.pos.add(line to symbol)
                tokens.add(parseDigit())
            }
            if (peek(0).isLetter()) {
                Positions.pos.add(line to symbol)
                tokens.add(parseWord())
            }
            if (peek(0) == '"') {
                Positions.pos.add(line to symbol)
                tokens.add(parseString())
            }
            if (peek(0) == a.toChar()) {
                Positions.pos.add(line to symbol)
                tokens.add(parseChar())
            } else if ("+*-/(){};<>!=|&".indexOf(peek(0)) > -1) {
                Positions.pos.add(line to symbol)
                tokens.add(parseOperator())
            } else next()
        }
        tokens.forEach { println(it.javaClass.typeName) }
        return tokens
    }

    private fun ignore(c: Char): Char {
        return if (peek(0) == c) next()
        else throw RuntimeException("Expected $c but found ${peek(0)} at line $line symbol $symbol")
    }

    private fun parseChar(): Token {
        val c = 39.toChar()
        ignore(c)
        var newline = false
        var a = peek(0)
        if (a == '\\') {
            val a = peek(1)
            newline = (a == 'n')
            if (newline) next()
        }
        if (newline) a = '\n'
        next()
        ignore(c)
        return CharToken(a)
    }

    private fun parseString(): Token {
        ignore('"')
        var sb = StringBuilder()

        while (peek(0) != '"') {
            sb.append(peek(0))
            next()
        }
        ignore('"')
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
        return when (s) {
            "char" -> CharType
            "int" -> IntType
            "double" -> DoubleType
            "string" -> StringType
            "bool" -> BooleanType
            "print" -> Print
            "if" -> If
            "else" -> Else
            "while" -> While
            "true" -> BooleanToken(true) // creating object overhead ?
            "false" -> BooleanToken(false)
            "return" -> Return
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
            if (peek(0) == '.') dot = true
            next()
        }

        return if (!dot)
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
            '/' -> if (peek(0) != '/' && peek(0) != '*') Division else parseComment() // comment
            '(' -> LP
            ')' -> RP
            '{' -> CLP
            '}' -> RLP
        //'[' -> SLP
        //']' -> SRP
            ';' -> SemiColon
            '<' -> LessThanSign
            '>' -> MoreThanSign
            '|' -> {
                if (peek(0) == '|') {
                    next()
                    OR
                } else throw UnsupportedOperationException("| operation is not supported at line $line symbol $symbol")
            }
            '&' -> {
                if (peek(0) == '&') {
                    next()
                    AND
                } else throw UnsupportedOperationException("& operation is not supported at line $line symbol $symbol")
            }
            '!' -> if (peek(0) == '=') {
                next(); NotEquality
            } else NOT
            '=' -> if (peek(0) == '=') {
                next(); Equality
            } else Assign
            else -> throw RuntimeException()
        }
    }

    private fun parseComment(): Comment {
        if (peek(0) == '/') return parseSingleComment()
        else {
            ignore('*')
            val sb = StringBuilder()
            while (!(peek(0) == '*' && peek(1) == '/')) {
                sb.apply {
                    append(peek(0))
                    next()
                }
            }
            ignore('*')
            ignore('/')
            return MultilineLineComment(sb.toString())
        }
    }

    private fun parseSingleComment(): Comment {
        ignore('/')
        val sb = StringBuilder()
        sb.apply {
            while (peek(0) != '\n') {
                append(peek(0))
                next()
            }
        }
        ignore('\n')
        return SingleLineComment(sb.toString())
    }

}
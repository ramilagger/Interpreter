import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter


var program = """
/*
*  The following program demonstrates all of the currently supported features
*
*/

int max(int a int b) {
    if(b < a) return a
    return b
}

int fibonacci(int n) {
    if(n < 2) return 1
    return fibonacci(n - 1) + fibonacci(n - 2)
}

int main() {
    int a = 15
    int b = 4
    double PI = 3.14
    print "PI is " + PI
    print max(square(square(b)) a) + " " + '\n'
    int cnt = 1
    while(cnt < a && (true || 5 == 5)) {
        int a = fibonacci(cnt)  // a in the scope is different
        print "fibonacci " + a + "= " + a +'\n'
        cnt = cnt + 1
    }
}

"""

// order and the or
fun go() {

    Memory.put("PI", DoubleValue(3.14))

    /*
    var frame = JFrame()

    frame.contentPane = Window().panel1
    frame.isVisible = true
    frame.setLocationRelativeTo(null)
    frame.isResizable = false
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(800,600)
    var form = MyForm()
      form.size = Dimension(400,400)
   form.setLocationRelativeTo(null)
 */


    time {
        Parser(Lexer(program).lex().removeComments()).parse().last().statements.forEach {
            eval(it)
        }
    }

    /*
    time {

        var fibonacci : (Int) -> Int = {
            if(it < 1) 1
            else  fibonacci(it - 1) + fibonacci(it - 2)
        }

        var a = 0
        var b = 30
        while (a < b) {
            a++
            pw.println("fibonacci " + a + " = " + fibonacci(a))
        }

    }
    */
}

inline fun <T> time(a: () -> T) {
    val st = System.currentTimeMillis()
    a()
    val end = System.currentTimeMillis()
    println("Time ${end - st} ms")
}

fun fibonacci(n: Int): Int {
    if (n < 1) return 1
    else return fibonacci(n - 1) + fibonacci(n - 2)
}

fun List<Token>.removeComments() = this.filter({ it !is Comment })

public var pw = PrintWriter(BufferedWriter(FileWriter("out.txt")))


fun main(args: Array<String>) {
    go()
    pw.close()
}
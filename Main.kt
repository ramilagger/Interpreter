import java.io.*
import java.util.*


// order and the or
fun go() {

    memory["PI"] = DoubleValue(3.14)

    var a = """
            double b = 10 + PI * 2;
            print(b);
            int a1 = 6 + 6 * 2;
            print(a1);
            if(0)
            print(a1 + b);
            else {
              double x = b + 1;
              print(b);
              if(x) {
                print(x);
              }
            }
         """

    Parser(Lexer(a).lex()).parse().forEach {
        eval(it)
    }
}

fun hasNext() : Boolean {
        while (!st.hasMoreTokens())
            st = StringTokenizer(br.readLine() ?: return false)
    return true
}

fun next() = if(hasNext()) st.nextToken()!! else throw RuntimeException("No tokens")

fun nextInt() = next().toInt()

fun nextLong() = next().toLong()

fun nextLine() = if(hasNext()) st.nextToken("\n")!! else throw RuntimeException("No tokens")

fun nextArray(n : Int) = IntArray(n,{nextInt()})

val ONLINE_JUDGE = System.getProperty("ONLINE_JUDGE") != null

val br = when(ONLINE_JUDGE) {
    true -> BufferedReader(InputStreamReader(System.`in`))
    else -> BufferedReader(FileReader("in.txt"))
}

val pw = when(ONLINE_JUDGE) {
    true -> PrintWriter(BufferedWriter(OutputStreamWriter(System.out)))
    else -> PrintWriter(BufferedWriter(FileWriter("out.txt")))
}

var st = StringTokenizer("")

fun main(args: Array<String>) {
    var start = System.currentTimeMillis()
    go()
    pw.close()
    br.close()
    //if(!ONLINE_JUDGE)
       // System.err.println("${System.currentTimeMillis() - start} ms")
}
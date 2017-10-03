import java.io.*
import java.util.*


// order and the or
fun go() {

    memory["PI"] = DoubleValue(3.14)

    var a = """
            int a = 34
            string b = 3 + 3 + "A" + "76";
            print(a + b)
            b = "666";
            int cnt = 1;
            string s = "1";
            while(cnt - 11) {
                print("iteration " + cnt + ": " + s);
                cnt = cnt + 1;
                s = s + ", " + cnt;
            }
            if(1) print(b + (6 + 6));
            else print("well 2 + 2 * 2 = " + (2 + 2 * 2));
            print(b);
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
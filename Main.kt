import java.io.*

// order and the or
fun go() {

    memory["PI"] = DoubleValue(3.14)

    var a = """
            double a = 34.0;
            string b = 3 + 3 + "A" + "76";
            print(a + b);
            b = "Hello World!";
            int cnt = 1;
            string s = "1";
            while(cnt - 11) {
                print("iteration " + cnt + ": " + s);
                cnt = cnt + 1;
                s = s + ", " + cnt;
            }
            if(1) print(b + (6 + 6));
            else {
            print("well 2 + 2 * 2 = " + (2 + 2 * 2));
            print(b);
            }
         """

    Parser(Lexer(a).lex()).parse().forEach {
        eval(it)
    }

}

val br = BufferedReader(FileReader("in.txt"))
val pw = PrintWriter(BufferedWriter(FileWriter("out.txt")))

fun main(args: Array<String>) {
    go()
    pw.close()
    br.close()
}
import java.io.*

// order and the or
fun go() {

    memory["PI"] = DoubleValue(3.14)

    var program = """
            int a = 4 * 100;
            int c = 6 / 2 * (2 + 1);
            print(c);
            string b = 3 + 3 + "A" + ((4 * 5) / 10) + "76";
            print(a + b);
            b = "Hello";
            int cnt = 1;
            string s = "1";
            bool all = true;
            bool st = 9 != 10
            print(st)
            while(cnt < 10) {
                if(cnt > 3 && cnt < 7)
                print("iteration " + cnt + ": " + s);
                cnt = cnt + 1;
                all = cnt - 5 > 0;
                s = s + ", " + cnt;
            }
            if(5 > 2 + 2 * 2) print(b + (6 + 6));
            else {
            print("well 2 + 2 * 2 = " + (2 + 2 * 2));
            print(b + " World!");
            }
         """

    Parser(Lexer(program).lex()).parse().forEach {
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
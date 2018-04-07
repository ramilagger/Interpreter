/**
 * Created by ramilagger on 10/11/17.
 */
object Functions {


    private val a = HashMap<String, Method>()

    init {
        a["square"] = Method("square", listOf(Argument(IntType, Var("c"))), listOf(ReturnStatement(Times(Variable("c"), Variable("c")))), IntType)
    }

    fun add(f: Method) {
        a[f.name] = f
    }

    fun get(s: String) = a[s]!!


}
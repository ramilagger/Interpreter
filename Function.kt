/**
 * Created by ramilagger on 09/10/17.
 */

data class Argument(val Type: Token, val variable: Var)

sealed class Function {
    abstract fun eval(): Value
}

//class Procedure(name: String,args : List<Argument>,statements : List<Statement>) : Function()
data class Method(val name: String, val args: List<Argument>, val statements: List<Statement>, val returnType: Token?) : Function() {
    override fun eval(): Value {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


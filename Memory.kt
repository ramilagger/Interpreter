import java.util.*

/**
 * Created by ramilagger on 10/11/17.
 */
object Memory {

    var memory: Stack<HashMap<String, Stack<Value>>> = Stack()

    init {
        memory.push(HashMap())
    }

    fun put(s: String, v: Value) {
        val cur = memory.peek()
        if (cur[s] == null) cur[s] = Stack()
        cur[s]?.add(v)
    }

    fun replace(s: String, v: Value) {
        val cur = memory.peek()
        cur[s]!!.pop()
        cur[s]!!.add(v)
    }


    fun get(s: String): Value {
        return memory.peek()[s]?.peek()!!
    }

    fun add() = memory.push(HashMap())

    fun poll() = memory.pop()

    fun poll(variable: String) {
        val a = memory.peek()
        a[variable]!!.pop()
    }

    fun clear(s: Scope) {
        s.variables.forEach {
            memory.peek()[it]?.pop()
        }
    }
}
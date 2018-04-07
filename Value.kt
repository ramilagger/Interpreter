/**
 * Created by ramilagger on 30/09/17.
 */

sealed class Value {


    fun toToken(): Token = when (this) {
        is IntValue -> IntType
        is DoubleValue -> DoubleType
        is StringValue -> StringType
        is BoolValue -> BooleanType
        is CharValue -> CharType
    }

    // + operator between different data types
    operator fun plus(a: Value): Value = when (this) {
        is IntValue -> {
            when (a) {
                is IntValue -> IntValue(a.value + this.value)
                is DoubleValue -> DoubleValue(a.value + this.value)
                is StringValue -> StringValue(this.value.toString() + a.value)
                is BoolValue -> StringValue(this.value.toString() + (if (a.value) "true" else "false"))
                is CharValue -> IntValue(this.value + a.value.toInt())
            }
        }
        is DoubleValue -> {
            when (a) {
                is DoubleValue -> DoubleValue(a.value + this.value)
                is IntValue -> DoubleValue(a.value + this.value)
                is StringValue -> StringValue(this.value.toString() + a.value)
                is BoolValue -> TODO()
                is CharValue -> TODO()
            }
        }
        is StringValue -> {
            when (a) {
                is IntValue -> StringValue(this.value + a.value)
                is DoubleValue -> StringValue(this.value + a.value)
                is StringValue -> StringValue(this.value + a.value)
                is BoolValue -> StringValue(this.value + if (a.value) "true" else "false")
                is CharValue -> StringValue(this.value + a.value.toString())
            }
        }

        is BoolValue -> TODO()
        is CharValue -> {
            when (a) {
                is IntValue -> IntValue(this.value.toInt() + a.value)
                is DoubleValue -> TODO()
                is StringValue -> StringValue(this.value + a.value)
                is BoolValue -> TODO()
                is CharValue -> IntValue(a.value.toInt() + this.value.toInt())
            }
        }
    }


    operator fun compareTo(a: Value): Int = when (this) {


        is IntValue ->
            when (a) {
                is IntValue -> this.value.compareTo(a.value)
                is DoubleValue -> this.value.compareTo(a.value)
                else -> TODO()
            }
        else -> TODO()
    }
}


// + - / * TODO add %
sealed class NumberValue : Value() {


    operator fun minus(a: NumberValue): NumberValue = when (this) {
        is IntValue -> {
            when (a) {
                is IntValue -> IntValue(this.value - a.value)
                is DoubleValue -> DoubleValue(this.value - a.value)

            }
        }
        is DoubleValue -> {
            when (a) {
                is DoubleValue -> DoubleValue(this.value - a.value)
                is IntValue -> DoubleValue(this.value - a.value)
            }
        }
    }


    operator fun div(a: NumberValue): NumberValue = when (this) {
        is IntValue -> {
            when (a) {
                is IntValue -> IntValue(this.value / a.value)
                is DoubleValue -> DoubleValue(this.value / a.value)
            }
        }
        is DoubleValue -> {
            when (a) {
                is DoubleValue -> DoubleValue(this.value / a.value)
                is IntValue -> DoubleValue(this.value / a.value)
            }
        }
    }


    operator fun times(a: NumberValue): NumberValue = when (this) {
        is IntValue -> {
            when (a) {
                is IntValue -> IntValue(a.value * this.value)
                is DoubleValue -> DoubleValue(a.value * this.value)
            }
        }
        is DoubleValue -> {
            when (a) {
                is DoubleValue -> DoubleValue(a.value * this.value)
                is IntValue -> DoubleValue(a.value * this.value)
            }
        }
    }


}

data class IntValue(val value: Int) : NumberValue() {
    override fun toString(): String {
        return value.toString()
    }
}

data class DoubleValue(val value: Double) : NumberValue() {
    override fun toString(): String {
        return value.toString()
    }
}

data class StringValue(val value: String) : Value() {
    override fun toString(): String {
        return value
    }
}

data class BoolValue(val value: Boolean) : Value() {
    override fun toString(): String {
        return value.toString()
    }
}

data class CharValue(val value: Char) : Value() {
    override fun toString(): String {
        return value.toString()
    }
}

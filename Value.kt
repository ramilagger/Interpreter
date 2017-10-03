/**
 * Created by ramilagger on 30/09/17.
 */

sealed class Value {


    // + operator between different data types
    operator fun plus(a: Value): Value = when (this) {
        is IntValue -> {
            when (a) {
                is IntValue -> IntValue(a.value + this.value)
                is DoubleValue -> DoubleValue(a.value + this.value)
                is StringValue -> StringValue(a.value + this.value)
                is BoolValue -> StringValue((if (a.value) "true" else "false") + this.value)
                is CharValue -> IntValue(a.value.toInt() + this.value)
            }
        }
        is DoubleValue -> {
            when (a) {
                is DoubleValue -> DoubleValue(a.value + this.value)
                is IntValue -> DoubleValue(a.value + this.value)
                is StringValue -> TODO()
                is BoolValue -> TODO()
                is CharValue -> TODO()
            }
        }
        is StringValue -> {
            when (a) {
                is IntValue -> StringValue(this.value + a.value)
                is DoubleValue -> StringValue(this.value + a.value)
                is StringValue -> StringValue(a.value + this.value)
                is BoolValue -> StringValue((if (a.value) "true" else "false") + this.value)
                is CharValue -> StringValue(this.value + a.value.toString())
            }
        }

        is BoolValue -> TODO()
        is CharValue -> {
            when (a) {
                is IntValue -> IntValue(a.value + this.value.toInt())
                is DoubleValue -> TODO()
                is StringValue -> StringValue(a.value + this.value)
                is BoolValue -> TODO()
                is CharValue -> IntValue(a.value.toInt() + this.value.toInt())
            }
        }
    }
}


// + - / * TODO add %
sealed class NumberValue : Value() {


    operator fun minus(a: NumberValue): NumberValue = when(this) {
        is IntValue -> {
            when(a) {
                is IntValue -> IntValue(a.value - this.value)
                is DoubleValue -> DoubleValue(a.value - this.value)

            }
        }
        is DoubleValue -> {
            when(a) {
                is DoubleValue -> DoubleValue(a.value - this.value)
                is IntValue -> DoubleValue(a.value - this.value)
            }
        }
    }



    operator fun div(a: NumberValue): NumberValue = when(this) {
        is IntValue -> {
            when(a) {
                is IntValue -> IntValue(a.value / this.value)
                is DoubleValue -> DoubleValue(a.value / this.value)
            }
        }
        is DoubleValue -> {
            when(a) {
                is DoubleValue -> DoubleValue(a.value / this.value)
                is IntValue -> DoubleValue(a.value / this.value)
            }
        }
    }


    operator fun times(a: NumberValue): NumberValue = when(this) {
        is IntValue -> {
            when(a) {
                is IntValue -> IntValue(a.value * this.value)
                is DoubleValue -> DoubleValue(a.value * this.value)
            }
        }
        is DoubleValue -> {
            when(a) {
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

data class DoubleValue(val value : Double) : NumberValue() {
    override fun toString(): String {
        return value.toString()
    }
}

data class StringValue(val value: String) : Value() {
    override fun toString(): String {
        return value
    }
}

data class BoolValue(val value: Boolean) : Value()

data class CharValue(val value: Char) : Value()
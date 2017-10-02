/**
 * Created by ramilagger on 30/09/17.
 */

sealed class Value
sealed class NumberValue : Value() {


    operator fun plus(a: NumberValue): NumberValue = when(this) {
            is IntValue -> {
                when(a) {
                    is IntValue -> IntValue(a.value + this.value)
                    is DoubleValue -> DoubleValue(a.value + this.value)
                }
            }
            is DoubleValue -> {
                when(a) {
                    is DoubleValue -> DoubleValue(a.value + this.value)
                    is IntValue -> DoubleValue(a.value + this.value)
                }
            }
    }


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
data class StringValue(val value: String) : Value()
data class BoolValue(val value: Boolean) : Value()
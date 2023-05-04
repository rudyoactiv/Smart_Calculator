//https://hyperskill.org/projects/88/stages/492/implement#solutions solution stage
//https://hyperskill.org/projects/88/stages/493/implement#comment

import java.math.*
import java.util.*
import kotlin.math.ln
import kotlin.math.exp

var current = mutableMapOf<String, String>()
fun main() {
    do {
        print("\nMemory: ") // start of memory print
        for ((key, value) in current) {
            if (value.length > 8) {
                if (value.toDouble() > 1000) {
                    val scientificNotation = "%.2E".format(value.toDouble())
                    print("$key = $scientificNotation, ")
                }
                else {
                    val num = String.format("%.2f", value.toDouble())
                    print("$key = $num, ")
                }
            }
            else {
                print("$key = $value, ")
            }
        }   // end of memory print
        print("\n> ")
        var data = readln().trim()
        val regexDisp = Regex("""(((\++)|(-+))*[0-9]+|([a-zA-Z]+)*|([a-zA-Z]+[0-9]+[a-zA-Z]*))""")  // display variable value
        when {
            data.isEmpty() -> continue
            data.startsWith("/") -> {
                command(data)
                continue
            }
            regexDisp.matches(data) -> display(data)
            else -> {
                if (data.contains('=')) {
                    assignment(data)
                } else if (isExp(data)) {
                    data = reformat(data)
                    if (data.isNotEmpty()) {
                        expression(data)
                    }
                    continue
                } else {
                    println("Invalid expression")
                    continue
                }
            }
        }
    } while (data != "/exit")
}

fun expression(input: String) {
    var data = input
    val parts = data.split(" ").toMutableList()   // parts is a list WITHOUT the spaces
    for (i in 0 until parts.size) {
        if (parts[i].matches("[a-zA-Z]+".toRegex())) {      //checking if part is value or variable
            if (!current.containsKey(parts[i])) {           // check if variable exists in current List
                display(parts[i])                           //to throw unknown variable error
                return
            } else {
                parts[i] = current[parts[i]].toString()     // REPLACING VARIABLES WITH VALUES
            }
        }
    }
    data = parts.joinToString(" ")
    //println(data)
    data = data.replace(",", "")

    data=data.trim()
    //println(data)
    //convert data to postfix
    data = infixToPostfix(data)
    //println("Postfix is: $data")
    //evaluate postfix
    val expression = data
    //println(data)
    if (expression.isNotEmpty()) {
        evaluate(expression)
        //println(result.toBigInteger())
    }
}

fun reformat (input: String): String {
    var data = input
    data = data.replace("+-", "-").replace("-+", "-")
    val pattern = Regex("[*^/]{2}|\\+-|\\+\\*|\\+/|\\+\\^|-\\+|-\\*|-/|-\\^|\\*\\+|/\\+|\\^\\+|\\*-|/-|\\^-")
    if (pattern.containsMatchIn(data)) {
        println("Invalid expression")
        return ""
    }
    data = data.replace(" ", "")
    var flag: Boolean
    do {    //removing multiple occurrences of '+' and '-'
        flag = data.contains("--") || data.contains("+-") || data.contains("-+") || data.contains("++")
        data = data.replace(Regex("\\+-|-\\+"), "-")
        data = data.replace(Regex("--"), "+")
        data = data.replace(Regex("\\+\\+"), "+")
    } while (flag)  //finished removing multiple occurrences of multiple sequential operators
    data = data.replace("+", " + ")     // add padding to operators
    data = data.replace("-", " - ")
    data = data.replace("*", " * ")
    data = data.replace("/", " / ")
    data = data.replace("^", " ^ ")
    data = data.replace("(", " ( ").replace("{", " ( ").replace("}", " ) ")
    data = data.replace(")", " ) ").replace("[", " ( ").replace("]", " ) ")
    return data
}

fun command(input: String) {
    when (input) {
        "/exit" -> {
            println("Bye!")
            return
        }
        "/help" -> {
            println("The program calculates the result of expressions")
            println("Store variables and later use them in an expression.")
            return
        }
        else -> {
            println("Unknown command")
        }
    }
}

fun assignment(input: String) {
    var data = input.replace(" ", "")
    val regVal = Regex("""([a-zA-Z]+(\s*)=(\s*)[+-]?[0-9]+(\.[0-9]+)?)*""")  // val = num  [+-]?[0-9]+(\.[0-9]+)?
    val regVar = Regex("""([a-zA-Z]+(\s*)=(\s*)[a-zA-Z]+)*""")  // val = val
    if(regVal.matches(data)) {
        //println("value assignment")
        var variable = ""
        for (i in input) {
            if (i == ' ' || i == '=') {
                break
            } else {
                variable += i
            }
        }
        //println("variable name = $variable")
        //var i = if (input.lastIndexOf('=') > input.lastIndexOf(' ')) input.lastIndexOf('=') else input.lastIndexOf(' ')
        var value: String = data.substring(data.indexOf("=")+1, data.length)
        //println("Value of $variable is = $value")
        current[variable] = value.toBigDecimal().toPlainString()
        //println("Current set: $current")
    }
    else if(regVar.matches(input)) {
        //println("variable assignment")
        var variable = ""
        for (i in input) {
            if (i == ' ' || i == '=') {
                break
            } else {
                variable += i
            }
        }
        //println("variable name = $variable")
        var value: String
        var i = if (input.lastIndexOf('=') > input.lastIndexOf(' ')) input.lastIndexOf('=') else input.lastIndexOf(' ')
        value = input.substring(i+1, input.length)
        //println("Value is = $value")
        if (!current.containsKey(value)) {
            println("Unknown variable")
        } else {
            var temp: BigDecimal = current[value]!!.toBigDecimal()
            current[variable] = temp.toPlainString()
            //print(current)
        }
    }
    else {
        bad(input)
    }
}

fun bad(input: String) {
    val regexI = Regex("""(([a-zA-Z]+[0-9]+[a-zA-Z]*)|([a-zA-Z]+[0-9]+[a-zA-Z]*\s*=\s*[a-zA-Z]*[0-9]*[a-zA-Z]*))""")
    if (regexI.matches(input)) {
        println("Invalid identifier")
    }
    else {
        println("Invalid assignment")
    }
}

fun display(input: String) {
    val regex = Regex("""((\++)|(-+))*[0-9]+|([a-zA-Z]+)*""")
    if (!regex.matches(input)) {
        bad(input)
    }
    else {
        if(input.matches("((\\++)|(-+))*[0-9]+".toRegex()))
        {
            println(input)
        } else {
            println(if (!current.containsKey(input)) "Unknown variable" else current[input]?.toBigDecimal())
        }
    }
}

fun isExp(input: String): Boolean {
    val string = "+-/*^"
    for (c in string) {
        if (input.contains(c)) {
            return true
        }
    }
    return false
}

fun infixToPostfix(data: String): String {
    var output: String = ""
    val operators = Stack<String>()

    val tokens = data.split(" ")

    for (token in tokens) {
        when {
            token.matches(Regex("[+-]?[0-9]+(\\.[0-9]+)?|[a-zA-Z]+")) -> output += "$token "
            token == "(" -> operators.push(token)
            token == ")" -> {
                while (operators.isNotEmpty() && operators.peek() != "(") {
                    output += "${operators.pop()} "
                }
                if (operators.isEmpty() || operators.pop() != "(") {
                    return "1Invalid Expression"
                }
            }
            token in setOf("+", "-", "*", "/", "^") -> {
                while (operators.isNotEmpty() && precedence(token) <= precedence(operators.peek())) {
                    output += "${operators.pop()} "
                }
                operators.push(token)
            }
            //else -> println("2Invalid Expression")
        }
    }

    while (operators.isNotEmpty()) {
        if (operators.peek() == "(") {
            println("Invalid expression")
            return ""
        }
        output += "${operators.pop()} "
    }
    return output.toString().trim()
}

fun precedence(operator: String): Int {
    return when (operator) {
        "^" -> 3
        "*", "/" -> 2
        "+", "-" -> 1
        else -> 0
    }
}

fun evaluate(expression: String) {
    val stack = Stack<String>()
    val tokens = expression.trim().split("\\s+".toRegex())
    for (token in tokens) {
        when (token) {
            "+" -> {
                val operand2 = stack.pop().toBigDecimal()
                val operand1 = stack.pop().toBigDecimal()
                stack.push((operand1 + operand2).toPlainString())
            }
            "-" -> {
                val operand2 = stack.pop().toBigDecimal()
                val operand1 = stack.pop().toBigDecimal()
                stack.push((operand1 - operand2).toPlainString())
            }
            "*" -> {
                val operand2 = stack.pop().toBigDecimal()
                val operand1 = stack.pop().toBigDecimal()
                stack.push((operand1 * operand2).toPlainString())
            }
            "/" -> {
                val operand2 = stack.pop().toBigDecimal()
                val operand1 = stack.pop().toBigDecimal()
                stack.push((operand1 / operand2).toPlainString())
            }
            "^" -> {
                val operand2 = stack.pop().toDouble()
                val operand1 = stack.pop().toDouble()
                //stack.push((operand1.pow(operand2)).toString())
                stack.push((exp(ln(operand1) * operand2)).toString())
            }
            else -> {
                val number = token.toDoubleOrNull()
                if (number != null) {
                    stack.push(number.toString())
                } else {
                    //println("Invalid token: $token")
                }
            }
        }
    }

    if (stack.size != 1) {
        println("Invalid expression")
    }
    if(stack.isNotEmpty()) {
        println(stack.pop().toBigDecimal().toPlainString())
    }
}
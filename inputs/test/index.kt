val a = {
    console.log("Hey")
}

fun main(args: Array<String>) {
    val hello = "Hello,"
    val hey = {
        console.log(hello, "how are you?")
    }
    hey()
    subFunc()
}

fun subFunc() {
    console.log("Hello from sub function!")
    a()
}
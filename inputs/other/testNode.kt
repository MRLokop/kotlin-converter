val a = {
    console.log("Hey")
    return "Hello from a in main ;^)"
}

fun main(args: Array<String>) {
    fun sub() {
        fun sub1() {
            fun sub2() {
                return "sub2"
            }
            return sub2() + " sub1"
        }
        return sub1() + " sub"
    }


    val hello = "Hello,"
    val hey = {
        console.log(hello, "how are you?")
    }
    hey()
    subFunc()
    setTimeout({
        console.log("This message after 1s")
    }, 1000)
    var fs = require("fs")
    var path = require("path")
    var http = require("http")
    console.log(path.resolve("./"))
    val server = http.createServer({ req: String, res: String ->
        console.log("Request!")
        res.write("Hello World!");
        res.end();
    })
    console.log(subFunc())
    console.log(sub())
}

fun subFunc() {
    console.log("Hello from sub function!")
    return a();
}
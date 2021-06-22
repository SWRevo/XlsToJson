package id.indosw.xlstojson.kotlin.xlstojsonstring

class Customer {
    var id: String? = null
    var name: String? = null
    var address: String? = null
    var age = 0

    constructor() : super()
    constructor(id: String?, name: String?, address: String?, age: Int) : super() {
        this.id = id
        this.name = name
        this.address = address
        this.age = age
    }

    override fun toString(): String {
        return "Customer [id=$id, name=$name, address=$address, age=$age]"
    }
}
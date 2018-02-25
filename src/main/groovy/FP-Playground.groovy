import groovy.transform.Immutable

def closure = { -> }

def closureWithArgs = { arg1, arg2 ->
    println "statements"
}

def closureWithImplicit = {
    println it
}

closureWithArgs(1, 2)
closureWithImplicit([test: true])

interface Functor<A> {
   Functor<A> fmap(Closure<A> body)
}

class Sth implements Functor<String> {
    private String value

    private Sth(String value) {
        this.value = value
    }

    static Sth of(String value) {
       return new Sth(value)
    }

    String unfold() {
        return this.value
    }

    @Override
    Sth fmap(Closure<String> body) {
        return Sth.of(body(this.value))
    }
}

def t = Sth
  .of("1000")
  .fmap {val -> val + "1000"}
  .fmap {val -> val + "1000"}
  .unfold()

println(t)

void 'Declaring and executing a closure'() {
    given: 'a variable and a closure'
    def x = 1
    def c = { ->
        def y = 1
        x + y
    }
    when: 'executing the closure'
    def result = c()
    then: 'the value should be the expected'
    result == 2
}

// CURRYING
def fn1 = { a, b, c -> a + b + c }

def fn2 = fn1.curry(1)
def fn3 = fn1.curry(1, 2)

fn2(4, 5) == 10
fn3(3) == 6

// MAPPING
def expendables = ['Stallone', 'Staham', 'Couture']
def upperCaseVersion = expendables.collect { name -> name.toUpperCase() }

println upperCaseVersion

def words = [' animal', ' person', 'something else ']

def trim = { it.trim() }
def toUpperCase = { it.toUpperCase() }
def trimAndUpperCase = toUpperCase << trim

def result = words.collect(trimAndUpperCase)
println(result)

// anonymous Objects

def obj = [a: '1']
println obj.a

// Pattern matching + Immutable
@Immutable(copyWith=true)
class Person {
    String name
    Integer age

    static Person of(String name, Integer age) {
        new Person(name, age)
    }
}

def p = Person.of('carl', 22)
def pc1 = p.copyWith([name: 'john'])

println(Person.of('carl', 22) == p)
println(pc1)

Closure<String> example1 = {Person person ->
    switch (person) {
        case Person.of('carl', 22): return 'carl'
        case Person.of('john', 34): return 'john'

        default:
            return 'nobody'
    }
}

println(({x -> x + x} << toUpperCase << example1)(Person.of('carl', 22)))

//
class Car {
    String make
    String model
}

def cars = [
    new Car(make:'bmw', model:'428')
]
// agg make
println(cars*.make)

// extending map / object
def m1 = [c:1, d:2]
def map = [a:1, b:2, *:m1, d:3]
println(map)

def items = [4, 5]
def list = [1, 2, 3, *items]
println(list)

// to dive in
// getAt / putAt

// elvis operator
def displayName = m1.e ?: "#1: E is not defined" // eq sth.a ? sth.a : sthElse
println(displayName)

// uuu nice
Map<String, Closure<String>> m2 = [
  c: {  String arg ->
        println(arg)
        return arg
     }
]

print("""
  safe access to ${m2.c('test')},
  has key ${m2.containsKey('c')}
""")

// APPLY args on Closure
int testFn(int x, int y, int z) { x * y * z }
Closure<Integer> testFn2 = {Integer x, Integer y, Integer z -> x * y * z}

def fn = this.&testFn
def args = [4,5]

println(fn(*args, 6)) // better type interference
println(testFn([*args, 6]))
println(testFn2.curry(10)(*args))

//
// todo try spock


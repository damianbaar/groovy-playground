import fj.data.Array
import fj.data.Either
import fj.data.optic.Lens
import groovy.transform.Immutable

import static fj.Show.intShow
import static fj.Show.arrayShow
import static fj.data.Array.array

import static groovyx.gpars.GParsPool.withPool
import static Thread.sleep

import static groovyx.gpars.actor.Actors.actor

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
        return of(body(this.value))
    }
}

// MAPPING

def t = Sth
  .of("1000")
  .fmap {val -> val + "1000"}
  .fmap {val -> val + "1000"}
  .unfold()

println(t)

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

// Get all obj.make = list.map(property('make'))
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

// Concurrency - GPars

def decryptor = actor {
  loop {
    react { message ->
      if (message instanceof String) reply message.reverse()
      else stop()
    }
  }
}

def console = actor {
  decryptor.send 'lellarap si yvoorG'
  react {
    println 'Decrypted message: ' + it
    decryptor.send false
  }
}

[decryptor, console]*.join()

def a = {->
  sleep(1000)
  1
}

def b = {->2}
def c = {->3}

def result2 = withPool { [a, b, c].collectParallel {f -> f()}}
println(result2)

// Destructuring
def (passed, failed) = [49, 58, 76, 82, 88, 90].split{it > 60}
println("""passed: ${passed}, failed: ${failed}""")

def (ele1,ele2,ele4)= [1,2,3]
println("""${ele1}${ele2}""")

// string to enum
enum State {
  up,
  down
}

def testEnum = {State s ->
  switch (s) {
    case State.up:
      return 'up'
    case State.down:
      return 'down'
  }
}
println(testEnum(State.up))

//
interface Greeter {
  void greet()
}
def greeter = { println 'Hello, Groovy!' } as Greeter // Greeter is known statically
greeter.greet()

//def operations = {
//  declare 5
//  sum 4
//  divide 3
//  print
//}
//
//operations()
Either<String, String> nan = Either.right("test")
//def error = nan.bind({x -> Either.left("error")})
//println("""either, ${error}""")

Array<Integer> arrayMonad = array(10, 20)

def arr = arrayMonad
  .bind({x-> array(x, 0, 0)})
  .filter({it > 1})

arrayShow intShow println arr

// Lenses

//Lens<Car, String> carMakeLens = Lens
//  .lens({c -> c.make}, {s, c -> new Car(s, c.make)})
//
//Lens<Car, String> carBrandLens = Lens
//  .lens({p -> p.brand}, {a, p -> new Car(p.brand, a)})

import spock.lang.Specification

class HelloSpoc extends Specification {
  def "length of Spock's and his friends' names"() {
    expect:
    name.size() == length

    where:
    name     | length
    "Spock"  | 5
    "Kirk"   | 4
    "Scotty" | 6
  }

  def "Testing unfold with spock"() {
    expect:
      Sth
        .of(name)
        .unfold() == result

    where:
      name | result
    "test" | "test"
      null | null
  }

  def 'Declaring and executing a closure'() {
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

  def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c
    1 | 3 | 3
    7 | 4 | 7
    0 | 0 | 0
  }

}

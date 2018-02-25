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
}

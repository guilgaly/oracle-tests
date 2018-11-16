package example

object Main {

  def main(args: Array[String]): Unit = {
    Class.forName("oracle.jdbc.driver.OracleDriver")

    TimestampWithTimeZoneExamples()
  }
}

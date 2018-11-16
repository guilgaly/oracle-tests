package example

import java.sql.{Connection, DriverManager}

trait WithDatabase {
  protected def withConnection[A](block: Connection => A): A = {
    val connection = DriverManager.getConnection(
      "jdbc:oracle:thin:@localhost:1521:ORCLCDB",
      "test",
      "test"
    )
    connection.setAutoCommit(true)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }
}

package example

import java.time._

import oracle.sql.TIMESTAMPTZ

/**
  * Other possibilities not tested here:
  *
  * - with the JDBC 4.2 compatible driver, we also get methods such as
  * [[oracle.sql.TIMESTAMPTZ#offsetDateTimeValue(java.sql.Connection)]]
  * - maybe others ?
  */
object TimestampWithTimeZoneExamples extends WithDatabase {

  def apply(): Unit = {
    jdbc42Zoned()
    jdbc42Offset()
    manualConversionZoned()
    manualConversionOffset()
  }

  private val zonedDateTime: ZonedDateTime =
    LocalDateTime
      .parse("2018-08-18T14:21:45")
      .atZone(ZoneId.of("America/New_York"))
  private val offsetDateTime = OffsetDateTime.parse("2018-08-18T14:21:45-05:00")

  /**
    * Requires JDBC 4.2
    * - DOES work with Oracle JDBC driver 12.2+
    * - DOES NOT work with Oracle JDBC driver 12.1
    */
  object jdbc42Zoned {
    private val id = 1

    def apply(): Unit = {
      insert()
      select()
    }

    private def insert(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "INSERT INTO test_tmstmp_w_tz(id, tmstmp_w_tz) VALUES(?, ?)"
      )

      pstmt.setInt(1, id)
      pstmt.setObject(2, zonedDateTime)
      pstmt.execute()
    }

    private def select(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "SELECT tmstmp_w_tz FROM test_tmstmp_w_tz WHERE id = ?"
      )
      pstmt.setInt(1, id)
      val res = pstmt.executeQuery()
      res.next()
      val zdt: ZonedDateTime =
        res.getObject("tmstmp_w_tz", classOf[ZonedDateTime])

      println()
      println(s"jdbc42Zoned.select(): $zdt")
    }
  }

  /**
    * Requires JDBC 4.2
    * - DOES work with Oracle JDBC driver 12.2+
    * - DOES NOT work with Oracle JDBC driver 12.1
    */
  object jdbc42Offset {
    private val id = 2

    def apply(): Unit = {
      insert()
      select()
    }

    private def insert(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "INSERT INTO test_tmstmp_w_tz(id, tmstmp_w_tz) VALUES(?, ?)"
      )
      pstmt.setInt(1, id)
      pstmt.setObject(2, offsetDateTime)
      pstmt.execute()
    }

    private def select(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "SELECT tmstmp_w_tz FROM test_tmstmp_w_tz WHERE id = ?"
      )
      pstmt.setInt(1, id)
      val res = pstmt.executeQuery()
      res.next()
      val odt: OffsetDateTime =
        res.getObject("tmstmp_w_tz", classOf[OffsetDateTime])

      println()
      println(s"jdbc42Offset.select(): $odt")
    }
  }

  /**
    * DOES work with Oracle JDBC driver 12.1 (JDBC < 4.2).
    *
    * The conversion from Oracle's TIMESTAMPTZ type is implemented by directly
    * reading its internal byte array structure (which at least is documented)
    */
  object manualConversionZoned {
    private val id = 3

    def apply(): Unit = {
      insert()
      select()
    }

    private def insert(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "INSERT INTO test_tmstmp_w_tz(id, tmstmp_w_tz) VALUES(?, ?)"
      )
      val tstz: TIMESTAMPTZ =
        TimestamptzConverter.zonedDateTimeToTimestamptz(zonedDateTime)

      pstmt.setInt(1, id)
      pstmt.setObject(2, tstz)
      pstmt.execute()
    }

    private def select(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "SELECT tmstmp_w_tz FROM test_tmstmp_w_tz WHERE id = ?"
      )
      pstmt.setInt(1, id)
      val res = pstmt.executeQuery()
      res.next()

      // TIMESTAMPTZ is the default return type for getObject on a TIMESTAMP WITH TIME ZONE column
      val tstz: TIMESTAMPTZ =
        res.getObject("tmstmp_w_tz").asInstanceOf[TIMESTAMPTZ]
      val zdt: ZonedDateTime =
        TimestamptzConverter.timestamptzToZonedDateTime(tstz)

      println()
      println(s"manualConversionZoned.select(): $zdt")
    }
  }

  /**
    * DOES work with Oracle JDBC driver 12.1 (JDBC < 4.2).
    *
    * The conversion from Oracle's TIMESTAMPTZ type is implemented by directly
    * reading its internal byte array structure (which at least is documented)
    */
  object manualConversionOffset {
    private val id = 4

    def apply(): Unit = {
      insert()
      select()
    }

    private def insert(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "INSERT INTO test_tmstmp_w_tz(id, tmstmp_w_tz) VALUES(?, ?)"
      )
      val tstz: TIMESTAMPTZ =
        TimestamptzConverter.offsetDateTimeToTimestamptz(offsetDateTime)
      pstmt.setInt(1, id)
      pstmt.setObject(2, tstz)
      pstmt.execute()
    }

    private def select(): Unit = withConnection { con =>
      val pstmt = con.prepareStatement(
        "SELECT tmstmp_w_tz FROM test_tmstmp_w_tz WHERE id = ?"
      )
      pstmt.setInt(1, id)
      val res = pstmt.executeQuery()
      res.next()

      // TIMESTAMPTZ is the default return type for getObject on a TIMESTAMP WITH TIME ZONE column
      val tstz: TIMESTAMPTZ =
        res.getObject("tmstmp_w_tz").asInstanceOf[TIMESTAMPTZ]
      val odt: OffsetDateTime =
        TimestamptzConverter.timestamptzToOffsetDateTime(tstz)

      println()
      println(s"manualConversionOffset.select(): $odt")
    }
  }
}

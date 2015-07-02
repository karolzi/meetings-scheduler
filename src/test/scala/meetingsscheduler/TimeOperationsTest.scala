package meetingsscheduler

import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

import TimeOperations._

@RunWith(classOf[JUnitRunner])
class TimeOperationsTest extends SpecificationWithJUnit {

  sequential

  "TimeOperations" should {

    "[10:00-18:00] subtract [10:00-16:00] equal [16:00-18:00]" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 18:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1 must beNone
      result._2.get must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-04 18:00")

    }

    "[09:00-18:00] subtract [10:00-16:00] equal ([09:00-10:00],[16:00-18:00])" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 09:00", "2015-04-04 18:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1.get must_== parseWarsawDatesToInterval("2015-04-04 09:00", "2015-04-04 10:00")
      result._2.get must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-04 18:00")

    }

    "[09:00-10:00] subtract [11:00-16:00] equal ([09:00-10:00], None)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 09:00", "2015-04-04 10:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 16:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1.get must_== i1
      result._2 must beNone

    }

    "[10:00-16:00] subtract [10:00-16:00] equal (None, None)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1 must beNone
      result._2 must beNone

    }

    "[10:00-16:00] subtract [10:00-18:00] equal (None, None)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 18:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1 must beNone
      result._2 must beNone

    }

    "[10:00-16:00] subtract [12:01-18:00] equal ([10:00-12:01], None)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 12:01", "2015-04-04 18:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1.get must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 12:01")
      result._2 must beNone

    }

    "[10:00-16:00] subtract [12:00-16:00] equal ([10:00-12:00], None)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 16:00")
      val i2 = parseWarsawDatesToInterval("2015-04-04 12:00", "2015-04-04 16:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1.get must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 12:00")
      result._2 must beNone

    }

    "[10:00-18:00]+Warsaw subtract [04:00-10:00]+New York equal (None, [16:00-18:00]+Warsaw)" in new TimeOperationsTestScope {

      //given
      val i1 = parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 18:00")
      val i2 = parseNewYorkDatesToInterval("2015-04-04 04:00", "2015-04-04 10:00")

      //when
      val result = TimeOperations.subtractIntervals(i1, i2)

      //then
      result._1 must beNone
      result._2.get must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-04 18:00")

    }

    "[10:00-12:00] merge [11:00-11:20] equal [10:00-12:00]" in new TimeOperationsTestScope {

      //given
      val intervals = List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 12:00"),
          parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 11:20")
        )

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 1
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 12:00")
    }

    "[10:00-11:00] merge [10:30-11:20] equal [10:00-11:20]" in new TimeOperationsTestScope {

      //given
      val intervals = List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
          parseWarsawDatesToInterval("2015-04-04 10:30", "2015-04-04 11:20")
        )

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 1
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:20")
    }

    "[10:00-11:00] merge [11:30-12:20] equal ([10:00-11:00], [11:30-12:20])" in new TimeOperationsTestScope {

      //given
      val intervals = List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
          parseWarsawDatesToInterval("2015-04-04 11:30", "2015-04-04 12:20")
        )

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 2
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00")
      result(1) must_== parseWarsawDatesToInterval("2015-04-04 11:30", "2015-04-04 12:20")
    }

    "merge multiple intervals" in new TimeOperationsTestScope {

      //given
      val intervals = List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
          parseWarsawDatesToInterval("2015-04-04 11:30", "2015-04-04 12:20"),
          parseWarsawDatesToInterval("2015-04-05 13:00", "2015-04-05 14:00"),
          parseWarsawDatesToInterval("2015-04-05 13:30", "2015-04-05 13:45"),
          parseWarsawDatesToInterval("2015-04-04 14:15", "2015-04-04 14:30"),
          parseWarsawDatesToInterval("2015-04-04 14:20", "2015-04-04 15:00")
        )

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 4
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00")
      result(1) must_== parseWarsawDatesToInterval("2015-04-04 11:30", "2015-04-04 12:20")
      result(2) must_== parseWarsawDatesToInterval("2015-04-04 14:15", "2015-04-04 15:00")
      result(3) must_== parseWarsawDatesToInterval("2015-04-05 13:00", "2015-04-05 14:00")
    }

    "[10:00-11:00] merge [11:00-11:20] equal [10:00-11:20]" in new TimeOperationsTestScope {

      //given
      val intervals = List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
          parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 11:20")
        )

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 1
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:20")
    }

    "return empty result from empty input" in new TimeOperationsTestScope {

      //given
      val intervals = List()

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 0
    }

    "return the same interval when input contains only one interval" in new TimeOperationsTestScope {

      //given
      val intervals = List(parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"))

      //when
      val result = TimeOperations.mergeIntervals(intervals)

      //then
      result must have size 1
      result.head must_== parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00")
    }

  }

  protected trait TimeOperationsTestScope extends Scope {

  }
}

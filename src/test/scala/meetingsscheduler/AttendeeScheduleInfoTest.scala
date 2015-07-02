package meetingsscheduler

import org.joda.time._
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import TimeOperations._

@RunWith(classOf[JUnitRunner])
class AttendeeScheduleInfoTest extends SpecificationWithJUnit {

  sequential

  "AttendeeScheduleInfo isWorkTime" should {

    "pass 1st use case" in new AttendeeTestScope {

      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 08:00"))

      result must_== true

    }

    "pass 2nd use case" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 10:00"))

      result must_== false

    }


    "pass 3rd use case" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 15:00"))

      result must_== true

    }

    "pass 4th use case" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 18:00"))

      result must_== true

    }
    "pass 5th use case" in new AttendeeTestScope {

      val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("19:01")), List())

      val result = attendee.isWorkTime(parseWarsawDateToUTC("2015-04-04 01:00"))

      result must_== true

    }

    "pass 6th use case" in new AttendeeTestScope {

      val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("19:00")), List())

      val result = attendee.isWorkTime(parseWarsawDateToUTC("2015-04-04 01:00"))

      result must_== false

    }


    "pass 7th use case" in new AttendeeTestScope {


      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 10:00"))

      result must_== true

    }

    "minute before start hour not be a work hour" in new AttendeeTestScope {


      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 07:59"))

      result must_== false

    }

    "pass 9th use case" in new AttendeeTestScope {


      //when
      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 16:00"))

      //then
      result must_== false

    }

    "minute after finish hour not be a work hour" in new AttendeeTestScope {

      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 16:01"))

      result must_== false

    }

  }

"AttendeeScheduleInfo" should {

  "find nextWorkSlot 1st" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 02:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseWarsawDatesToInterval("2015-04-04 08:00", "2015-04-04 16:00")

  }

  "find nextWorkSlot 2nd" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 18:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseWarsawDatesToInterval("2015-04-05 08:00", "2015-04-05 16:00")
  }

  "find nextWorkSlot 3rd" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 12:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseWarsawDatesToInterval("2015-04-04 12:01", "2015-04-04 16:00")

  }

  "find nextWorkSlot 4th" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 09:00")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseNewYorkDatesToInterval("2015-04-04 09:00", "2015-04-04 17:00")
  }

  "find nextWorkSlot 5th" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 08:59")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseNewYorkDatesToInterval("2015-04-04 09:00", "2015-04-04 17:00")
  }

  "find nextWorkSlot 6th" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 09:01")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseNewYorkDatesToInterval("2015-04-04 09:01", "2015-04-04 17:00")
  }

  "find nextWorkSlot 7th" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 17:00")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== parseNewYorkDatesToInterval("2015-04-05 09:00", "2015-04-05 17:00")
  }

  "find free time slots 1st" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List()
    )
    val from = parseWarsawDateToUTC("2015-04-03 22:00")
    val to = parseWarsawDateToUTC("2015-04-05 02:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== parseWarsawDatesToInterval("2015-04-03 22:00", "2015-04-04 08:00")
    result(1) must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-05 02:00")

  }

  "find free time slots 2nd" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 02:00")
    val to = parseWarsawDateToUTC("2015-04-04 22:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== parseWarsawDatesToInterval("2015-04-04 02:00", "2015-04-04 08:00")
    result(1) must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-04 22:00")

  }

  "find free time slots 3rd" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-03 22:00")
    val to = parseWarsawDateToUTC("2015-04-05 22:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 3
    result.head must_== parseWarsawDatesToInterval("2015-04-03 22:00", "2015-04-04 08:00")
    result(1) must_== parseWarsawDatesToInterval("2015-04-04 16:00", "2015-04-05 08:00")
    result(2) must_== parseWarsawDatesToInterval("2015-04-05 16:00", "2015-04-05 22:00")

  }

  "find free time slots 4th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 10:00")
    val to = parseWarsawDateToUTC("2015-04-04 18:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 1
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-04 18:00"))

  }

  "find free time slots 5th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 10:00")
    val to = parseWarsawDateToUTC("2015-04-05 18:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 08:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-05 16:00"), parseWarsawDateToUTC("2015-04-05 18:00"))

  }

  "find free time slots 6th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-04 16:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 0

  }

  "find free time slots 7th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-05 16:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 1
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 08:00"))

  }

  "find free time slots 8th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneNewYork, slot_9_17, List())
    val from = parseNewYorkDateToUTC("2015-04-04 23:00")
    val to = parseNewYorkDateToUTC("2015-04-06 02:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseNewYorkDateToUTC("2015-04-04 23:00"), parseNewYorkDateToUTC("2015-04-05 09:00"))
    result(1) must_== new Interval(parseNewYorkDateToUTC("2015-04-05 17:00"), parseNewYorkDateToUTC("2015-04-06 02:00"))

  }

  "find free time slots 9th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 13:00"), parseWarsawDateToUTC("2015-04-04 13:20"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-03 06:00")
    val to = parseWarsawDateToUTC("2015-04-05 18:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to))

    result must have size 5
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-03 08:00"), parseWarsawDateToUTC("2015-04-03 16:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 08:00"), parseWarsawDateToUTC("2015-04-04 10:00"))
    result(2) must_== new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 13:00"))
    result(3) must_== new Interval(parseWarsawDateToUTC("2015-04-04 13:20"), parseWarsawDateToUTC("2015-04-04 16:00"))
    result(4) must_== new Interval(parseWarsawDateToUTC("2015-04-05 08:00"), parseWarsawDateToUTC("2015-04-05 16:00"))

  }

  "find free time slots 9th" in new AttendeeTestScope {

    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 13:00"), parseWarsawDateToUTC("2015-04-04 13:20"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-03 22:00")
    val to = parseWarsawDateToUTC("2015-04-05 02:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to))

    result must have size 3
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 08:00"), parseWarsawDateToUTC("2015-04-04 10:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 13:00"))
    result(2) must_== new Interval(parseWarsawDateToUTC("2015-04-04 13:20"), parseWarsawDateToUTC("2015-04-04 16:00"))

  }

  "find available slot with duration 1st" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 13:00"), parseWarsawDateToUTC("2015-04-04 15:40"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-04 16:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to), 30)

    result must have size 2
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 08:00"), parseWarsawDateToUTC("2015-04-04 10:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 13:00"))
  }

  "find available slot with duration 2nd" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 08:20"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 11:20"), parseWarsawDateToUTC("2015-04-04 15:40"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-04 16:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to), 30)

    result must have size 0
  }

  "find available slot with duration 3rd" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 08:20"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 11:20"), parseWarsawDateToUTC("2015-04-04 15:30"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-04 16:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to), 30)

    result must have size 1
    result.head must_== parseWarsawDatesToInterval("2015-04-04 15:30", "2015-04-04 16:00")
  }

  "find available slot with duration 4rd" in new AttendeeTestScope {
    //given
    val attendee = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
        parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
        parseWarsawDatesToInterval("2015-04-04 13:00", "2015-04-04 15:40")
      )
    )
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-05 16:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to), 30)

    result must have size 3
    result.head must_== parseWarsawDatesToInterval("2015-04-04 08:00", "2015-04-04 10:00")
    result(1) must_== parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 13:00")
    result(2) must_== parseWarsawDatesToInterval("2015-04-05 08:00", "2015-04-05 16:00")
  }

}

  protected trait AttendeeTestScope extends Scope {

    protected def slot_8_16: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("08:00"), new LocalTime("16:00"))
    }

    protected def slot_9_17: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00"))
    }

    protected val attendeeWarsaw = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_16,
      List(
            parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 12:00"),
            parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 11:20"),
            parseWarsawDatesToInterval("2015-04-03 11:00", "2015-04-03 11:30"),
            parseWarsawDatesToInterval("2015-04-05 14:00", "2015-04-05 14:15")
            )
      )

    protected val attendeeNewYork = new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17,
      List(parseNewYorkDatesToInterval("2015-04-04 10:40", "2015-04-04 11:10")))

}

}

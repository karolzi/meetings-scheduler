package meetingsscheduler

import org.joda.time._
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import TimeOperations._

@RunWith(classOf[JUnitRunner])
class AttendeeTest extends SpecificationWithJUnit {

  sequential

  "test" should {

    "start hour be a work hour" in new AttendeeTestScope {

      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 08:00"))

      result must_== true

    }

    "is work hour in New Your time zone 1" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 10:00"))

      result must_== false

    }


    "is work hour in New Your time zone 2" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 15:00"))

      result must_== true

    }

    "is work hour in New Your time zone 3" in new AttendeeTestScope {

      val result = attendeeNewYork.isWorkTime(parseWarsawDateToUTC("2015-04-04 18:00"))

      result must_== true

    }
    "is work hour in New Your time zone 4" in new AttendeeTestScope {

      val attendee = new Attendee("John", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("19:01")), List())

      val result = attendee.isWorkTime(parseWarsawDateToUTC("2015-04-04 01:00"))

      result must_== true

    }

    "is work hour in New Your time zone 4" in new AttendeeTestScope {

      val attendee = new Attendee("John", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("19:00")), List())

      val result = attendee.isWorkTime(parseWarsawDateToUTC("2015-04-04 01:00"))

      result must_== false

    }


    "is work hour in Warsaw time zone" in new AttendeeTestScope {


      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 10:00"))

      result must_== true

    }

    "minute before start hour not be a work hour" in new AttendeeTestScope {


      val result = attendeeWarsaw.isWorkTime(parseWarsawDateToUTC("2015-04-04 07:59"))

      result must_== false

    }

    "finish hour be a work hour" in new AttendeeTestScope {


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

"aaa" should {

  "nextWorkSlot 1" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 02:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(
      parseWarsawDateToUTC("2015-04-04 08:00"),
      parseWarsawDateToUTC("2015-04-04 16:00")
    )

  }

  "nextWorkSlot 2" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 18:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(
      parseWarsawDateToUTC("2015-04-05 08:00"),
      parseWarsawDateToUTC("2015-04-05 16:00")
    )

  }

  "nextWorkSlot 3" in new AttendeeTestScope {

    //given
    val dateSearchStart = parseWarsawDateToUTC("2015-04-04 12:01")

    //when
    val result = attendeeWarsaw.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(
      parseWarsawDateToUTC("2015-04-04 12:01"),
      parseWarsawDateToUTC("2015-04-04 16:00")
    )

  }

  "nextWorkSlot 4" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 09:00")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(
      parseNewYorkDateToUTC("2015-04-04 09:00"),
      parseNewYorkDateToUTC("2015-04-04 17:00")
    )
  }

  "nextWorkSlot 5" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 08:59")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(parseNewYorkDateToUTC("2015-04-04 09:00"), parseNewYorkDateToUTC("2015-04-04 17:00"))
  }

  "nextWorkSlot 6" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 09:01")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(parseNewYorkDateToUTC("2015-04-04 09:01"), parseNewYorkDateToUTC("2015-04-04 17:00"))
  }

  "nextWorkSlot 7" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("John", ZoneNewYork, slot_9_17, List())
    val dateSearchStart = parseNewYorkDateToUTC("2015-04-04 17:00")

    //when
    val result = attendee.nextWorkSlot(dateSearchStart)

    //then
    result must_== new Interval(parseNewYorkDateToUTC("2015-04-05 09:00"), parseNewYorkDateToUTC("2015-04-05 17:00"))
  }

  "workFreeTimeSlots 1" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List()
    )
    val from = parseWarsawDateToUTC("2015-04-03 22:00")
    val to = parseWarsawDateToUTC("2015-04-05 02:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-03 22:00"), parseWarsawDateToUTC("2015-04-04 08:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 02:00"))

  }

  "workFreeTimeSlots 2" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 02:00")
    val to = parseWarsawDateToUTC("2015-04-04 22:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 02:00"), parseWarsawDateToUTC("2015-04-04 08:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-04 22:00"))

  }

  "workFreeTimeSlots 3" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-03 22:00")
    val to = parseWarsawDateToUTC("2015-04-05 22:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 3
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-03 22:00"), parseWarsawDateToUTC("2015-04-04 08:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 08:00"))
    result(2) must_== new Interval(parseWarsawDateToUTC("2015-04-05 16:00"), parseWarsawDateToUTC("2015-04-05 22:00"))

  }

  "workFreeTimeSlots 4" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 10:00")
    val to = parseWarsawDateToUTC("2015-04-04 18:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 1
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-04 18:00"))

  }

  "workFreeTimeSlots 5" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 10:00")
    val to = parseWarsawDateToUTC("2015-04-05 18:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 08:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-05 16:00"), parseWarsawDateToUTC("2015-04-05 18:00"))

  }

  "workFreeTimeSlots 6" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-04 16:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 0

  }

  "workFreeTimeSlots 7" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16, List())
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-05 16:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 1
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 16:00"), parseWarsawDateToUTC("2015-04-05 08:00"))

  }

  "workFreeTimeSlots 8" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneNewYork, slot_9_17, List())
    val from = parseNewYorkDateToUTC("2015-04-04 23:00")
    val to = parseNewYorkDateToUTC("2015-04-06 02:00")

    //when
    val result = attendee.workFreeTimeSlots(new Interval(from, to))

    result must have size 2
    result.head must_== new Interval(parseNewYorkDateToUTC("2015-04-04 23:00"), parseNewYorkDateToUTC("2015-04-05 09:00"))
    result(1) must_== new Interval(parseNewYorkDateToUTC("2015-04-05 17:00"), parseNewYorkDateToUTC("2015-04-06 02:00"))

  }

  "allAvailableSlots 1" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
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

  "allAvailableSlots 2" in new AttendeeTestScope {

    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
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

  "availableSlotsWithDuration 1" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
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

  "availableSlotsWithDuration 3" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
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

  "availableSlotsWithDuration 3" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
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
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 15:30"), parseWarsawDateToUTC("2015-04-04 16:00"))
  }

  "availableSlotsWithDuration 2" in new AttendeeTestScope {
    //given
    val attendee = new Attendee("Maria", ZoneWarsaw, slot_8_16,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 13:00"), parseWarsawDateToUTC("2015-04-04 15:40"))
      )
    )
    val from = parseWarsawDateToUTC("2015-04-04 08:00")
    val to = parseWarsawDateToUTC("2015-04-05 16:00")

    //when
    val result = attendee.availableSlots(new Interval(from, to), 30)

    result must have size 3
    result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 08:00"), parseWarsawDateToUTC("2015-04-04 10:00"))
    result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 13:00"))
    result(2) must_== new Interval(parseWarsawDateToUTC("2015-04-05 08:00"), parseWarsawDateToUTC("2015-04-05 16:00"))
  }

}

  protected trait AttendeeTestScope extends Scope {

    protected def slot_8_16: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("08:00"), new LocalTime("16:00"))
    }

    protected def slot_9_17: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00"))
    }

    protected val attendeeWarsaw = new Attendee("Maria", ZoneWarsaw, slot_8_16,
      List(
            new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 12:00")),
            new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 11:20")),
            new Interval(parseWarsawDateToUTC("2015-04-03 11:00"), parseWarsawDateToUTC("2015-04-03 11:30")),
            new Interval(parseWarsawDateToUTC("2015-04-05 14:00"), parseWarsawDateToUTC("2015-04-05 14:15"))
            )
      )

    protected val attendeeNewYork = new Attendee("John", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00")),
      List(new Interval(parseNewYorkDateToUTC("2015-04-04 10:40"), parseNewYorkDateToUTC("2015-04-04 11:10"))))

}

}

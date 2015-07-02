package meetingsscheduler

import org.joda.time.{Interval, LocalTime}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import TimeOperations._

@RunWith(classOf[JUnitRunner])
class CalendarTest extends SpecificationWithJUnit {

  sequential

  "test" should {
    "find one common slot for 4 users" in new CalendarScope {

      //given
      val attendees = List(maria, john, tom, jack)
      val meetingLengthMinutes = 30
      val nrOfSlotsToBeFound = 2
      val timePeriodToBeScanned = new Interval(parseWarsawDateToUTC("2015-04-03 19:00"), parseWarsawDateToUTC("2015-04-04 19:00"))

      val result = new Calendar().findSlots(attendees, timePeriodToBeScanned, meetingLengthMinutes, nrOfSlotsToBeFound)

      result must have size 1
      result.head must_== new Interval(parseDateUTC("2015-04-04 14:00"), parseDateUTC("2015-04-04 15:00"))
    }

    "find two common slots for 4 users" in new CalendarScope {

      //given

      val attendee1 = new Attendee("Attendee 1", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List(
          new Interval(parseWarsawDateToUTC("2015-04-04 16:50"), parseWarsawDateToUTC("2015-04-04 17:30"))
        )
      )

      val attendee2 = new Attendee("Attendee 2", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00")), List())

      val attendee3 = new Attendee("Attendee 3", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("19:00")),
        List(
          new Interval(parseWarsawDateToUTC("2015-04-04 15:00"), parseWarsawDateToUTC("2015-04-04 16:00"))
        )
      )

      val attendees = List(attendee1, attendee2, attendee3)
      val meetingLengthMinutes = 30
      val nrOfSlotsToBeFound = 2
      val timePeriodToBeScanned = new Interval(parseWarsawDateToUTC("2015-04-03 19:00"), parseWarsawDateToUTC("2015-04-04 19:00"))

      val result = new Calendar().findSlots(attendees, timePeriodToBeScanned, meetingLengthMinutes, nrOfSlotsToBeFound)

      result must have size 2
      result.head must_== new Interval(parseDateUTC("2015-04-04 14:00"), parseDateUTC("2015-04-04 14:50"))
      result(1) must_== new Interval(parseDateUTC("2015-04-04 15:30"), parseDateUTC("2015-04-04 16:00"))
    }

    "find two common slots for 4 users fwe wf w" in new CalendarScope {

      //given

      val attendee1 = new Attendee("Attendee 1", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List(
          new Interval(parseWarsawDateToUTC("2015-04-04 16:50"), parseWarsawDateToUTC("2015-04-04 17:30"))
        )
      )

      val attendee2 = new Attendee("Attendee 2", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00")), List())

      val attendee3 = new Attendee("Attendee 3", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("19:00")),
        List(
          new Interval(parseWarsawDateToUTC("2015-04-04 15:00"), parseWarsawDateToUTC("2015-04-04 16:00"))
        )
      )

      val attendees = List(attendee1, attendee2, attendee3)
      val meetingLengthMinutes = 300
      val nrOfSlotsToBeFound = 2
      val timePeriodToBeScanned = new Interval(parseWarsawDateToUTC("2015-04-03 19:00"), parseWarsawDateToUTC("2015-04-05 19:00"))

      val result = new Calendar().findSlots(attendees, timePeriodToBeScanned, meetingLengthMinutes, nrOfSlotsToBeFound)

      result must have size 0
    }

    "find two common slots for 4 sfesrfwerwe" in new CalendarScope {

      //given

      val attendee1 = new Attendee("Attendee 1", ZoneWarsaw, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00")),
        List()
      )

      val attendee2 = new Attendee("Attendee 2", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List()
      )

      val attendees = List(attendee1, attendee2)
      val meetingLengthMinutes = 30
      val nrOfSlotsToBeFound = 2
      val timePeriodToBeScanned = new Interval(parseWarsawDateToUTC("2015-04-03 19:00"), parseWarsawDateToUTC("2015-04-05 19:00"))

      val result = new Calendar().findSlots(attendees, timePeriodToBeScanned, meetingLengthMinutes, nrOfSlotsToBeFound)

      result must have size 2
      result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 17:00"))
      result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-05 10:00"), parseWarsawDateToUTC("2015-04-05 17:00"))
    }

  }

  protected trait CalendarScope extends Scope {

    protected def workingHours_8_16: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("08:00"), new LocalTime("16:00"))
    }

    protected def workingHours_8_17: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("08:00"), new LocalTime("17:00"))
    }

    protected def workingHours_9_17: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00"))
    }

    protected def workingHours_12_20: LocalTimeSlot = {
      LocalTimeSlot(new LocalTime("12:00"), new LocalTime("20:00"))
    }

    protected val maria = new Attendee("Maria", ZoneWarsaw, workingHours_8_17,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00")),
        new Interval(parseWarsawDateToUTC("2015-04-04 11:00"), parseWarsawDateToUTC("2015-04-04 11:20"))
      )
    )

    protected val john= new Attendee("John", ZoneNewYork, workingHours_9_17,
      List(
        new Interval(parseNewYorkDateToUTC("2015-04-04 09:00"), parseNewYorkDateToUTC("2015-04-04 10:00")),
        new Interval(parseNewYorkDateToUTC("2015-04-04 13:00"), parseNewYorkDateToUTC("2015-04-04 13:20"))
      )
    )

    protected val tom = new Attendee("Tom", ZoneWarsaw, workingHours_9_17,
      List(
        new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 11:00"))
      )
    )

    protected val jack = new Attendee("Jack", ZoneWarsaw, workingHours_12_20, List())

  }

}

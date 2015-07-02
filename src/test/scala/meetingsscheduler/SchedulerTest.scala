package meetingsscheduler

import org.joda.time.{Interval, LocalTime}
import org.junit.runner.RunWith
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import TimeOperations._

@RunWith(classOf[JUnitRunner])
class SchedulerTest extends SpecificationWithJUnit {

  sequential

  "test" should {

    "find one common slot for 4 users" in new CalendarScope {

      //given
      protected val attendee1 = new AttendeeScheduleInfo("Maria", ZoneWarsaw, slot_8_17,
        List(
          parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"),
          parseWarsawDatesToInterval("2015-04-04 11:00", "2015-04-04 11:20")
        )
      )
      protected val attendee2= new AttendeeScheduleInfo("John", ZoneNewYork, slot_9_17,
        List(
          parseNewYorkDatesToInterval("2015-04-04 09:00", "2015-04-04 10:00"),
          parseNewYorkDatesToInterval("2015-04-04 13:00", "2015-04-04 13:20")
        )
      )
      protected val attendee3 = new AttendeeScheduleInfo("Tom", ZoneWarsaw, slot_9_17,
        List(parseWarsawDatesToInterval("2015-04-04 10:00", "2015-04-04 11:00"))
      )
      protected val attendee4 = new AttendeeScheduleInfo("Jack", ZoneWarsaw, slot_12_20, List())
      val attendees = List(attendee1, attendee2, attendee3, attendee4)
      val timePeriodToBeScanned = parseWarsawDatesToInterval("2015-04-03 19:00", "2015-04-04 19:00")

      //when
      val result = new AttendeesScheduler().findCommonFreeSlots(attendees, timePeriodToBeScanned, 20, 2)

      //then
      result must have size 1
      result.head must_== new Interval(parseDateUTC("2015-04-04 14:00"), parseDateUTC("2015-04-04 15:00"))
    }

    "find two common slots for 4 users" in new CalendarScope {

      //given
      val attendee1 = new AttendeeScheduleInfo("Attendee 1", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List(new Interval(parseWarsawDateToUTC("2015-04-04 16:50"), parseWarsawDateToUTC("2015-04-04 17:30")))
      )
      val attendee2 = new AttendeeScheduleInfo("Attendee 2", ZoneNewYork, LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00")), List())
      val attendee3 = new AttendeeScheduleInfo("Attendee 3", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("19:00")),
        List(new Interval(parseWarsawDateToUTC("2015-04-04 15:00"), parseWarsawDateToUTC("2015-04-04 16:00")))
      )
      val attendees = List(attendee1, attendee2, attendee3)
      val timePeriodToBeScanned = new Interval(parseWarsawDateToUTC("2015-04-03 19:00"), parseWarsawDateToUTC("2015-04-04 19:00"))

      //when
      val result = new AttendeesScheduler().findCommonFreeSlots(attendees, timePeriodToBeScanned, 30, 2)

      //then
      result must have size 2
      result.head must_== new Interval(parseDateUTC("2015-04-04 14:00"), parseDateUTC("2015-04-04 14:50"))
      result(1) must_== new Interval(parseDateUTC("2015-04-04 15:30"), parseDateUTC("2015-04-04 16:00"))
    }

    "find two common slots for 4 users fwe wf w" in new CalendarScope {

      //given
      val attendee1 = new AttendeeScheduleInfo("Attendee 1", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List(
          new Interval(parseWarsawDateToUTC("2015-04-04 16:50"), parseWarsawDateToUTC("2015-04-04 17:30"))
        )
      )
      val attendee2 = new AttendeeScheduleInfo("Attendee 2", ZoneNewYork, slot_9_17, List())
      val attendee3 = new AttendeeScheduleInfo("Attendee 3", ZoneWarsaw, slot_10_19,
        List( parseWarsawDatesToInterval("2015-04-04 15:00", "2015-04-04 16:00"))
      )
      val attendees = List(attendee1, attendee2, attendee3)
      val timePeriodToBeScanned = parseWarsawDatesToInterval("2015-04-03 19:00", "2015-04-05 19:00")

      //when
      val result = new AttendeesScheduler().findCommonFreeSlots(attendees, timePeriodToBeScanned, 300, 2)

      //then
      result must have size 0
    }

    "find two common slots for 4 sfesrfwerwe" in new CalendarScope {

      //given
      val attendee1 = new AttendeeScheduleInfo("Attendee 1", ZoneWarsaw, slot_9_17,
        List()
      )
      val attendee2 = new AttendeeScheduleInfo("Attendee 2", ZoneWarsaw, LocalTimeSlot(new LocalTime("10:00"), new LocalTime("18:00")),
        List()
      )
      val attendees = List(attendee1, attendee2)
      val timePeriodToBeScanned = parseWarsawDatesToInterval("2015-04-03 19:00", "2015-04-05 19:00")

      //when
      val result = new AttendeesScheduler().findCommonFreeSlots(attendees, timePeriodToBeScanned, 30, 2)

      //then
      result must have size 2
      result.head must_== new Interval(parseWarsawDateToUTC("2015-04-04 10:00"), parseWarsawDateToUTC("2015-04-04 17:00"))
      result(1) must_== new Interval(parseWarsawDateToUTC("2015-04-05 10:00"), parseWarsawDateToUTC("2015-04-05 17:00"))
    }

  }

  protected trait CalendarScope extends Scope {

    protected def slot_8_16: LocalTimeSlot = LocalTimeSlot(new LocalTime("08:00"), new LocalTime("16:00"))

    protected def slot_8_17: LocalTimeSlot = LocalTimeSlot(new LocalTime("08:00"), new LocalTime("17:00"))

    protected def slot_9_17: LocalTimeSlot =  LocalTimeSlot(new LocalTime("09:00"), new LocalTime("17:00"))

    protected def slot_12_20 = LocalTimeSlot(new LocalTime("12:00"), new LocalTime("20:00"))

    protected def slot_10_19 = LocalTimeSlot(new LocalTime("10:00"), new LocalTime("19:00"))

  }

}

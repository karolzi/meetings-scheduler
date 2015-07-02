package meetingsscheduler

import org.joda.time._
import TimeOperations._

class AttendeeScheduleInfo(name : String, zone: DateTimeZone, workingHours: LocalTimeSlot, meetings: List[Interval]) {

  private implicit def dateTimeOrdering: Ordering[Interval] = Ordering.fromLessThan(_.getStart isBefore _.getStart)

  private val flatMeetings = mergeOverlappingMeetings()

  def nextWorkSlot(from: DateTime): Interval = {
    val fromWithZone = from.withZone(zone)
    isWorkTime(from) match {
      case true => new Interval(from, fromWithZone.withFields(workingHours.end).withZone(ZoneUTC))
      case false =>
        if (fromWithZone.toLocalTime.isAfter(workingHours.end) || fromWithZone.toLocalTime.isEqual(workingHours.end)) {
          new Interval(fromWithZone.plusDays(1).withFields(workingHours.start).withZone(ZoneUTC), fromWithZone.plusDays(1).withFields(workingHours.end).withZone(ZoneUTC))
        } else {
          new Interval(fromWithZone.withFields(workingHours.start).withZone(ZoneUTC), fromWithZone.withFields(workingHours.end).withZone(ZoneUTC))
        }
    }
  }

  def availableSlots(timeLine: Interval): List[Interval] = {
    var result = List[Interval]()
    var timeLineRest: Option[Interval] = Some(timeLine)
    val meetingsAndFreeTimeSlots = mergeOverlappingIntervals(flatMeetings ::: workFreeTimeSlots(timeLine)).sorted
    for (i <- 0 to meetingsAndFreeTimeSlots.length - 1 if timeLineRest.isDefined) {
      val subtractResult = subtractIntervals(timeLineRest.get, meetingsAndFreeTimeSlots(i))
      result = subtractResult._1.map(value => result :+ value).getOrElse(result)
      if (i == meetingsAndFreeTimeSlots.length - 1) {
        result = subtractResult._2.map(value => result :+ value).getOrElse(result)
      }
      timeLineRest = subtractResult._2
    }
    result
  }

  def availableSlots(timeLine: Interval, durationMinutes: Int): List[Interval] = {
    val allSlots = availableSlots(timeLine)
    allSlots.filter(slot => slot.toDuration.getStandardMinutes >= durationMinutes)
  }

  def workFreeTimeSlots(timeLine: Interval): List[Interval] = {
    val nextWorkSlotOption = Option(nextWorkSlot(timeLine.getStart))
    nextWorkSlotOption match {
      case None  => List()
      case Some(workSlot) =>
        val subtractResult = subtractIntervals(timeLine, workSlot)
        subtractResult match {
          case (Some(left), Some(right)) => left :: workFreeTimeSlots(right)
          case (Some(left), None) => List(left)
          case (None, Some(right)) => workFreeTimeSlots(right)
          case (None, None) => List()
        }
    }
  }

  def isWorkTime(dateTime: DateTime): Boolean = {
    val dateTimeWithZone = dateTime.withZone(zone)
    val hour = dateTimeWithZone.toLocalTime
    (hour.isAfter(workingHours.start) || hour.isEqual(workingHours.start)) && hour.isBefore(workingHours.end)
  }


  private def mergeOverlappingMeetings(): List[Interval] = mergeIntervals(meetings.sorted)

  private def mergeOverlappingIntervals(meetingsInternal: List[Interval]): List[Interval] =
    mergeIntervals(meetingsInternal)

}

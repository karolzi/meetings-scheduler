package meetingsscheduler

import org.joda.time.format.DateTimeFormat
import org.joda.time.{LocalTime, DateTime, DateTimeZone, Interval}

case class LocalTimeSlot(start: LocalTime, end: LocalTime) {
  require(start.isBefore(end))
}

object TimeOperations {

  private implicit def dateTimeOrdering: Ordering[Interval] = Ordering.fromLessThan(_.getStart isBefore _.getStart)

  val ZoneUTC = DateTimeZone.UTC
  val ZoneWarsaw = DateTimeZone.forID("Europe/Warsaw")
  val ZoneNewYork = DateTimeZone.forID("America/New_York")
  val DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")

  def parseDate(dateString: String, zone: DateTimeZone): DateTime = {
    DTF.parseDateTime(dateString).withZoneRetainFields(zone)
  }

  def parseDateToUTC(dateString: String, zone: DateTimeZone): DateTime = {
    parseDate(dateString, zone).withZone(ZoneUTC)
  }

  def parseDateUTC(dateString: String): DateTime = {
    parseDateToUTC(dateString, ZoneUTC)
  }

  def parseWarsawDateToUTC(dateString: String): DateTime = {
    parseDateToUTC(dateString, ZoneWarsaw)
  }

  def parseNewYorkDateToUTC(dateString: String): DateTime = {
    parseDateToUTC(dateString, ZoneNewYork)
  }

  def subtractIntervals(i1: Interval, i2: Interval): (Option[Interval], Option[Interval]) = {

    requireZone(i1)
    requireZone(i2)
    require(i1.getStart.isBefore(i2.getStart.withZone(i1.getStart.getZone)) || i1.getStart.equals(i2.getStart.withZone(i1.getStart.getZone)), "intervals must be sorted by before date")

    if(!i1.overlaps(i2)) {//no common interval
      (Some(i1), None)
    } else if (i1.equals(i2)) {//intervals are the same
      (None, None)
    } else if (i1.getStart.isEqual(i2.getStart)) {//intervals start in the same time
      if (i2.getEnd.isBefore(i1.getEnd)) {//i2 ends inside i1
        (None, Some(new Interval(i2.getEnd, i1.getEnd)))
      } else {//i2 ends after i1
        (None, None)
      }
    } else if (i2.overlap(i1).equals(i2)) {//i2 inside i1
      if(i1.getEnd.equals(i2.getEnd)) {
        (Some(new Interval(i1.getStart, i2.getStart)), None)
      } else {
        (Some(new Interval(i1.getStart, i2.getStart)), Some(new Interval(i2.getEnd, i1.getEnd)))
      }
    } else if (i1.getStart.isBefore(i2.getStart) && i1.getEnd.isAfter(i2.getStart) && i1.getEnd.isBefore(i2.getEnd)) {
      (Some(new Interval(i1.getStart, i2.getStart)), None)
    } else {
      throw new RuntimeException(s"unexpected intervals [i1=$i1][i2=$i2]")
    }
  }

  /*
  * There are cases.
  * Examples:
  * ((8:00-9:00), (10:00-11:00) -> (8:00-9:00), (10:00-11:00))
  * ((8:00-9:00), (08:30-09:30) -> (8:00-9:30), None)
  * ((8:00-9:00), (09:00-10:00) -> (8:00-10:00), None)
  * ((8:00-9:00), (08:30-08:45) -> (8:00-9:00), None)
  */
  def mergeTwoIntervals(i1: Interval, i2: Interval): (Interval, Option[Interval]) = {

    requireZone(i1)
    requireZone(i2)

    val overlapOption = Option(i1.overlap(i2))
    overlapOption match {
      case None =>
        i1.abuts(i2) match {
          case true => (new Interval(i1.getStart, i2.getEnd), None)
          case false => (i1, Some(i2))
        }
      case Some(_) =>
        (
          new Interval(
            i1.getStart,
            if (i1.getEnd.isAfter(i2.getEnd)) i1.getEnd else i2.getEnd
          ),
          None
          )
    }
  }

  def mergeIntervals(intervals: List[Interval]): List[Interval] = {
    intervals.sorted match {
      case current :: next :: tail =>
        mergeTwoIntervals(current, next) match {
          case (interval1, Some(interval2)) => interval1 :: mergeIntervals(interval2 :: tail)
          case (interval1, None) => interval1 :: mergeIntervals(tail)
        }
      case actual :: Nil => List(actual)
      case Nil => List()
    }
  }

  def intervalsAfter(from: DateTime, intervals: List[Interval]): List[Interval] = {
    val res = intervals.map { interval =>
      if(interval.getStart.isAfter(from) || interval.getEnd.isAfter(from)) {
        if (interval.getStart.isBefore(from)) {
          //take only end part of interval after "from" parameter
          Some(new Interval(from, interval.getEnd))
        } else {
          Some(interval)
        }
      } else {
        //interval is entirely before "from" parameter
        None
      }
    }
    res.collect { case Some(b) => b }
  }

  def requireZone(interval: Interval) = {
    require(interval.getStart.getZone.equals(DateTimeZone.UTC) && interval.getEnd.getZone.equals(DateTimeZone.UTC),
      "Zone has to be set to UTC")
  }

  def parseWarsawDatesToInterval(from: String, to: String) = {
    new Interval(parseWarsawDateToUTC(from), parseWarsawDateToUTC(to))
  }
  def parseNewYorkDatesToInterval(from: String, to: String) = {
    new Interval(parseNewYorkDateToUTC(from), parseNewYorkDateToUTC(to))
  }


}

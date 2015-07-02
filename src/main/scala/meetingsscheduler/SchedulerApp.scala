package meetingsscheduler

import org.joda.time.Interval
import TimeOperations._

object SchedulerApp {

  def main(args: Array[String]) {

    if(args.length != 5) {
      printUsage()
    } else {
      val dataFilePath = args(0)
      val timePeriodToBeScannedFrom = parseDateUTC(args(1)) //"2015-04-03 19:00"
      val timePeriodToBeScannedTo = parseDateUTC(args(2)) //"2015-04-04 19:00"
      val meetingLengthMinutes = args(3).toInt
      val nrOfSlotsToBeFound = args(4).toInt

      val result = new AttendeesScheduler().findCommonFreeSlots(
        JsonReader.read(dataFilePath),
        new Interval(timePeriodToBeScannedFrom, timePeriodToBeScannedTo),
        meetingLengthMinutes,
        nrOfSlotsToBeFound
      )

      if(result.length < nrOfSlotsToBeFound) {
        println(s"Not all expected slots were found. Expected: $nrOfSlotsToBeFound. Found: ${result.length}.")
      }
      result.foreach(slot => println(s"Slot: [${slot.getStart} - ${slot.getEnd}]"))
    }

  }

  private def printUsage() = {
    println(s"Arguments:")
    println(s"[jsonFilePath] - file with attendees' calendars - json format, e.g. './data/attendees1.json'")
    println(s"[start] - start of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm")
    println(s"[end] - end of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm")
    println(s"[meetingLength] - minimal length of slots to be found, unit: minutes")
    println(s"[numberOfPossibleSlots] - number of slots to be found")
    println("Example: ./data/attendees1.json \"2015-04-03 17:00\" \"2015-04-4 17:00\" 40 2")
  }

}

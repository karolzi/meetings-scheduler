package meetingsscheduler

import org.joda.time.Interval
import TimeOperations._

import scala.util.control.NonFatal

object SchedulerApp {

  def main(args: Array[String]) {

    try {
      if(args.length != 5) {
        printUsage()
      } else {
        val dataFilePath = args(0)
        val timePeriodToBeScannedFrom = parseDateUTC(args(1)) //"2015-04-03 19:00"
        val timePeriodToBeScannedTo = parseDateUTC(args(2)) //"2015-04-04 19:00"
        val meetingLengthMinutes = args(3).toInt
        val nrOfSlotsToBeFound = args(4).toInt

        val result = new AttendeesScheduler().findCommonFreeSlots(
          SchedulesJsonReader.read(dataFilePath),
          new Interval(timePeriodToBeScannedFrom, timePeriodToBeScannedTo),
          meetingLengthMinutes,
          nrOfSlotsToBeFound
        )

        if(result.length < nrOfSlotsToBeFound) {
          log(s"Not all expected slots were found. Expected: $nrOfSlotsToBeFound. Found: ${result.length}.")
        }
        result.foreach(slot => println(s"Slot: [${slot.getStart} - ${slot.getEnd}]"))
      }
    } catch {
      case NonFatal(ex) =>
        log("Application error occurred")
        ex.printStackTrace()
    }
  }

  private def printUsage() = {
    log(s"Arguments:")
    log(s"[jsonFilePath] - file with attendees' calendars - json format, e.g. './data/attendees1.json'")
    log(s"[start] - start of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm")
    log(s"[end] - end of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm")
    log(s"[meetingLength] - minimal length of slots to be found, unit: minutes")
    log(s"[numberOfPossibleSlots] - number of slots to be found")
    log("Example: ./data/attendees1.json \"2015-04-03 17:00\" \"2015-04-4 17:00\" 40 2")
  }

  private def log(message: String) = println(message)

}

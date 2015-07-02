package meetingsscheduler

import org.joda.time.{Interval, LocalTime, DateTimeZone}

import scala.io.Source
import scala.util.parsing.json.JSON

import TimeOperations._

object JsonReader {

  private val FieldName = "name"
  private val FieldZone = "zone"
  private val FieldWorkingHours = "workingHours"
  private val FieldWorkingHoursStart = "start"
  private val FieldWorkingHoursEnd = "end"
  private val FieldMeetings = "meetings"

  def read(filePath: String): List[AttendeeScheduleInfo] = {
    val attendeesJson = JSON.parseFull(Source.fromFile(filePath).getLines().mkString).get.asInstanceOf[List[Map[String, Any]]]

    val attendees = attendeesJson.map {
      attendeeJson =>
        val name = attendeeJson(FieldName).toString
        val zone = DateTimeZone.forID(attendeeJson(FieldZone).toString)
        val workingHours = attendeeJson(FieldWorkingHours).asInstanceOf[Map[String, String]]
        val workingHoursSlot =
          LocalTimeSlot(new LocalTime(workingHours(FieldWorkingHoursStart)), new LocalTime(workingHours(FieldWorkingHoursEnd)))
        val meetings = attendeeJson(FieldMeetings).asInstanceOf[List[Map[String, String]]]
        val meetingsList = meetings.map(meeting =>
          new Interval(parseDateToUTC(meeting(FieldWorkingHoursStart).toString, zone),
            TimeOperations.parseDateToUTC(meeting(FieldWorkingHoursEnd).toString, zone)
          )
        )
        new AttendeeScheduleInfo(name, zone, workingHoursSlot, meetingsList)
    }

    attendees
  }

}

package meetingsscheduler

import org.joda.time._


class Scheduler {

  private val MeetingMaxDurationMinutes = 480

  def findSlots(attendees: List[AttendeeScheduleInfo], timePeriodToBeScanned: Interval,
                meetingDurationMinutes: Int, nrOfSlotsToBeFound: Int): List[Interval] = {

    require(meetingDurationMinutes < MeetingMaxDurationMinutes, s"Meeting may not take more then $MeetingMaxDurationMinutes minutes")

    val attendeesWithAvailableSlots: List[(AttendeeScheduleInfo, List[Interval])] = attendees.map(
      attendee => (attendee, attendee.availableSlots(timePeriodToBeScanned, meetingDurationMinutes))
    )

    var result = List[Interval]()
    attendeesWithAvailableSlots.length match {
      case 0 => result = List()
      case 1 => result = attendeesWithAvailableSlots(0)._2
      case other =>

        val userFirstSlots = attendeesWithAvailableSlots(0)._2
        val userSecondSlots = attendeesWithAvailableSlots(1)._2

        var firstTwoUsersOverlappingSlotOption = findFirstOverlappingSlot(timePeriodToBeScanned.getStart, userFirstSlots, userSecondSlots, meetingDurationMinutes)

        while (firstTwoUsersOverlappingSlotOption.isDefined && result.length < nrOfSlotsToBeFound) {

          var commonSlotOption = firstTwoUsersOverlappingSlotOption

          for (nextUserIndex <- 0 to attendeesWithAvailableSlots.length - 1 if commonSlotOption.isDefined) {
            commonSlotOption = findFirstOverlappingSlot(commonSlotOption.get, attendeesWithAvailableSlots(nextUserIndex)._2, meetingDurationMinutes)
              .map(slotForTwoUsers => slotForTwoUsers._1)
          }

          commonSlotOption match {
            case Some(commonSlot) =>
              result = result :+ commonSlot
              firstTwoUsersOverlappingSlotOption = findFirstOverlappingSlot(commonSlot.getEnd, userFirstSlots, userSecondSlots, meetingDurationMinutes)
            case None =>
              firstTwoUsersOverlappingSlotOption = None
          }

        }
    }

    result

  }

  private def findFirstOverlappingSlot(from: DateTime, intervalsLeft: List[Interval], intervalsRight: List[Interval], meetingDurationMinutes: Int): Option[Interval] = {
    val intervalsLeftAfterFrom = TimeOperations.intervalsAfter(from, intervalsLeft)
    val intervalsRightAfterFrom = TimeOperations.intervalsAfter(from, intervalsRight)
    intervalsLeftAfterFrom match {
      case head :: tail =>
        findFirstOverlappingSlot(head, intervalsRightAfterFrom, meetingDurationMinutes) match {
          case Some((commonSlot, restOfSlots)) => Some(commonSlot)
          case None => findFirstOverlappingSlot(from, tail, intervalsRightAfterFrom, meetingDurationMinutes)
        }
      case Nil => None
    }
  }

  private def findFirstOverlappingSlot(slot: Interval, slotsToCompare: List[Interval], overlapDurationMinutes: Int): Option[(Interval, List[Interval])] = {
    slotsToCompare match {
      case Nil => None
      case head :: tail =>
        Option(slot.overlap(head)) match {
          case Some(overlapInterval) if overlapInterval.toDuration.getStandardMinutes >= overlapDurationMinutes =>
             Some(overlapInterval, head :: tail)
          case _ =>
            findFirstOverlappingSlot(slot, tail, overlapDurationMinutes)
        }
    }
  }

}

package meetingsscheduler

import org.joda.time._
import TimeOperations._

trait Scheduler {

  def findCommonFreeSlots(attendees: List[ScheduleInfo], timeFrameToBeScanned: Interval,
                          meetingDurationMinutes: Int, nrOfSlotsToBeFound: Int): List[Interval]

}

class AttendeesScheduler extends Scheduler {

  private val MeetingMaxDurationMinutes = 480

  def findCommonFreeSlots(attendees: List[ScheduleInfo], timeFrameToBeScanned: Interval,
                  meetingDurationMinutes: Int, nrOfSlotsToBeFound: Int): List[Interval] = {

    require(meetingDurationMinutes < MeetingMaxDurationMinutes, s"Meeting may not take more then $MeetingMaxDurationMinutes minutes")

    val attendeesWithAvailableSlots = attendees.map(
      attendee => (attendee, attendee.availableSlots(timeFrameToBeScanned, meetingDurationMinutes))
    )

    var result = List[Interval]()
    attendeesWithAvailableSlots.length match {
      case 0 => result = List()
      case 1 => result = attendeesWithAvailableSlots.head._2
      case other =>

        val userFirstSlots = attendeesWithAvailableSlots.head._2
        val userSecondSlots = attendeesWithAvailableSlots(1)._2

        var firstTwoUsersCommonSlotOption = findFirstOverlappingSlot(timeFrameToBeScanned.getStart, userFirstSlots, userSecondSlots, meetingDurationMinutes)

        while (firstTwoUsersCommonSlotOption.isDefined && result.length < nrOfSlotsToBeFound) {

          var allUsersCommonSlotOption = firstTwoUsersCommonSlotOption
          for (nextUserIndex <- 0 to attendeesWithAvailableSlots.length - 1 if allUsersCommonSlotOption.isDefined) {
            allUsersCommonSlotOption = findFirstOverlappingSlot(allUsersCommonSlotOption.get, attendeesWithAvailableSlots(nextUserIndex)._2, meetingDurationMinutes)
              .map(slotForTwoUsers => slotForTwoUsers._1)
          }

          allUsersCommonSlotOption match {
            case Some(commonSlot) =>
              result = result :+ commonSlot
              firstTwoUsersCommonSlotOption = findFirstOverlappingSlot(commonSlot.getEnd, userFirstSlots,
                userSecondSlots, meetingDurationMinutes)
            case None =>
              val intervalsAfterLastSearch = intervalsAfter(firstTwoUsersCommonSlotOption.get.getEnd, userFirstSlots)
              if(intervalsAfterLastSearch.length > 0) {
                firstTwoUsersCommonSlotOption = findFirstOverlappingSlot(intervalsAfterLastSearch(0).getStart,
                  userFirstSlots, userSecondSlots, meetingDurationMinutes)
              } else {
                firstTwoUsersCommonSlotOption = None
              }
          }

        }
    }

    result

  }

  private def findFirstOverlappingSlot(from: DateTime, intervalsLeft: List[Interval], intervalsRight: List[Interval], meetingDurationMinutes: Int): Option[Interval] = {
    val intervalsLeftAfterFrom = intervalsAfter(from, intervalsLeft)
    val intervalsRightAfterFrom = intervalsAfter(from, intervalsRight)
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

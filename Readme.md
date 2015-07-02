## Build and test
    mvn clean package
    
## Info
    Inside app_to_test folder is placed packed jar file for test. It is useful for tests where compilation
    environment is missing.
    
## Print usage
    java -classpath ./app_to_test/meetings-scheduler-1.0.jar meetingsscheduler.SchedulerApp
    
    # Output
    
    Arguments:
    [jsonFilePath] - file with attendees' calendars - json format, e.g. './data/attendees1.json'
    [start] - start of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm
    [end] - end of time-frame which we want to scan for possible time slots, UTC time, format yyyy-MM-dd HH:mm
    [meetingLength] - minimal length of slots to be found, unit: minutes
    [numberOfPossibleSlots] - number of slots to be found
    Example: ./data/attendees1.json "2015-04-03 17:00" "2015-04-4 17:00" 40 2
    
## Run example

    Command:
    java -classpath ./app_to_test/meetings-scheduler-1.0.jar meetingsscheduler.SchedulerApp ./data/attendees1.json "2015-04-03 17:00" "2015-04-04 17:00" 40 1
    
    Result. List of intervals (UTC time)
    Slot: [2015-04-04T14:00:00.000Z - 2015-04-04T15:00:00.000Z]
    
## Data description
 
    ```json
     {
        "name": "Tom",
        "zone": "Europe/Warsaw",
        "workingHours": { # working hours, from 00:00 to 23:59
          "start": "10:00",
          "end": "19:00"
        },
        "meetings": [ # meetings in local time, will be converted to UTC with zone fields specified above
          {
            "start": "2015-04-04 15:00",
            "end": "2015-04-04 16:00"
          }
        ]
      }
    ```
    
    In ./data folder there are sample attendees' calendars. Commands to run them and expected results:
    
    attendees1.json
    
    Command:
    java -classpath ./app_to_test/meetings-scheduler-1.0.jar meetingsscheduler.SchedulerApp ./data/attendees1.json "2015-04-03 17:00" "2015-04-04 17:00" 40 1
    
    Result:
    Slot: [2015-04-04T14:00:00.000Z - 2015-04-04T15:00:00.000Z]
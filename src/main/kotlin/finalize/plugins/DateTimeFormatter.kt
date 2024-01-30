package finalize.plugins

import finalize.models.DateComponents
import finalize.models.DateTimeSeparator
import finalize.models.TimeComponents
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// generate the current data and time
fun timeDate(): String {
    val currentTime = LocalDateTime.now()
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    return currentTime.format(format)
}

// generate the date and time into readable format
fun timeDateReadable(timeDate: String): String {
    val dateSplit = dateTimeSeparator(timeDate)[0].date
    val timeSplit = dateTimeSeparator(timeDate)[0].time
    val dateFormatted = dateSeparator(dateSplit)[0]
    val timeFormatted = timeSeparator(timeSplit)[0]
    val localDate = LocalDate.of(dateFormatted.year, dateFormatted.month, dateFormatted.day).format(
        DateTimeFormatter.ofLocalizedDate(
            FormatStyle.FULL
        )
    )
    val localTime = LocalTime.of(timeFormatted.hours, timeFormatted.minutes, timeFormatted.seconds).format(
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    )
    return "$localDate | $localTime"
}

// adding 5 days from current time
fun addingFiveDays(dateTime: String ?): String ?{
    var date = ""
    return if (dateTime != null){
        dateTimeSeparator(dateTime).map {
            dateSeparator(it.date).map { dateData ->
                var year = dateData.year
                var month = dateData.month
                var day = dateData.day
                if (day+5 > 31) {
                    day -= 31
                    month += 1
                    if (month > 12){
                        month -= 12
                        year += 1
                    }
                }else day += 5
                date += "$year-$month-$day ${it.time}"
            }
        }.toString()
        date
    }else null
}

// created for comparing date stamp
fun toDateStamp(date: String) : LocalDate{
    val dateData = dateTimeSeparator(date)[0].date
    return LocalDate.of(dateSeparator(dateData)[0].year,dateSeparator(dateData)[0].month,dateSeparator(dateData)[0].day)
}

fun addingTenMin(time: String ?): String ?{
    var timeResult = ""
    return if (time != null){
        timeSeparator(time).map {timeData ->
            var hour = timeData.hours
            var minutes = timeData.minutes
            val sec = timeData.seconds
            if (minutes+1 >= 60) {
                hour += 1
                minutes -= 60
            } else minutes += 1
            timeResult += "$hour:$minutes:$sec"
        }.toString()
        timeResult
    }else null
}

// separating the date and time
fun dateTimeSeparator(dateTime: String): List<DateTimeSeparator>{
    val dateTimeData = mutableListOf<DateTimeSeparator>()
    val newDataTime = dateTime.split(" ").toTypedArray()
    if (newDataTime.size > 1) dateTimeData.add(DateTimeSeparator(newDataTime[0], newDataTime[1]))
    else dateTimeData.add(DateTimeSeparator(newDataTime[0], ""))

    return dateTimeData
}

// separating the date into specific components
fun dateSeparator(date: String): List<DateComponents>{
    val dateData = mutableListOf<DateComponents>()
    val newDate = date.split("-").toTypedArray()
    dateData.add(DateComponents(newDate[0].toInt(), newDate[1].toInt(), newDate[2].toInt()))

    return dateData
}

// separating the time into specific components
fun timeSeparator(date: String): List<TimeComponents>{
    val timeData = mutableListOf<TimeComponents>()
    val newTime = date.split(":").toTypedArray()
    timeData.add(TimeComponents(newTime[0].toInt(), newTime[1].toInt(), newTime[2].toInt()))

    return timeData
}

// removing curly brackets in array dateTime format
fun dateTimeRefactor (dateTime: String): String{
    var dateTimeResult = ""
    for (char in dateTime.indices){
        if (dateTime[char] != '[' && dateTime[char] != ']') dateTimeResult += dateTime[char]
    }

    return dateTimeResult
}
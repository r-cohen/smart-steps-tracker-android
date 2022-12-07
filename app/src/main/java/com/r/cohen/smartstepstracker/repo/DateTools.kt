package com.r.cohen.smartstepstracker.repo

import java.util.*

class DateTools {
    companion object {
        fun getStartOfDayTimeStamp(): Long = with (Calendar.getInstance()) {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            return timeInMillis
        }

        fun isSameDay(date1: Long, date2: Long): Boolean {
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = date1
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = date2
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }

        fun getFirstDayOfWeekFrom(timestamp: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            var dow = cal.get(Calendar.DAY_OF_WEEK)
            while (dow > 1) {
                cal.add(Calendar.DATE, -1)
                dow = cal.get(Calendar.DAY_OF_WEEK)
            }
            return cal.timeInMillis
        }

        fun get30DaysAgo(timestamp: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            cal.add(Calendar.DATE, -30)
            return cal.timeInMillis
        }
    }
}
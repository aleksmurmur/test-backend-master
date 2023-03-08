package mobi.sevenwinds.app.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DateTimeUtils {
    fun DateTime.toStringResponse() =
        toString(DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"))

}
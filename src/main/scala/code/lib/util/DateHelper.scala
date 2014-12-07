package code.lib.util

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by andrea on 12/7/14.
 */
object DateHelper {
  val dateFormat =  new SimpleDateFormat("MMM d, y h:mm:ss a")

  implicit def format(date: java.util.Date): String = dateFormat.format(date)
}

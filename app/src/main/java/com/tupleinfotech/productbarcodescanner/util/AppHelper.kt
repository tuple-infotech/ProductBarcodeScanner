@file:Suppress("DEPRECATION")
package com.tupleinfotech.productbarcodescanner.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.tupleinfotech.productbarcodescanner.R
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("NewApi","SimpleDateFormat","SetTextI18n","InflateParams","StaticFieldLeak","Range")
class AppHelper {

    companion object{

        //region VARIABLES

        private const val PRICE_FORMAT                  = "###########0.00"
        private const val PRICE_FORMAT1                 = "###########0.0"
        private val decimal2                            = DecimalFormat(PRICE_FORMAT)
        private val decimal1                            = DecimalFormat(PRICE_FORMAT1)
        val emailPattern                    : Pattern   = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.(com|net|in|org)")

        //endregion VARIABLES

        //region ALL FUNCTIONS

        //region Encryption/Decryption

        fun Base64Decryption(encodedString : String) : String = String(java.util.Base64.getDecoder().decode(encodedString))

        //endregion Encryption/Decryption

        //region GPS Location Permission

        /**fun displayLocationSettingsRequest(requireactivity: Activity, context: Context) {

            val googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).build()
            googleApiClient.connect()

            val locationRequest : LocationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(10000)
            locationRequest.setFastestInterval(10000 / 2)

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result : PendingResult<LocationSettingsResult> = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

            result.setResultCallback { it ->
                val status : Status = it.status
                when (status.statusCode) {

                    LocationSettingsStatusCodes.SUCCESS                     -> {
                        Log.i(ContentValues.TAG,SettingVerified)
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED         -> {
                        Log.i(ContentValues.TAG,SettingnotVerified)
                        try {
                            status.startResolutionForResult(requireactivity,REQUEST_CHECK_SETTINGS)
                        }
                        catch (e: IntentSender.SendIntentException) {
                            Log.i(ContentValues.TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.i(ContentValues.TAG,Locationissue)
                    }

                }
            }
        }**/

        //endregion GPS Location Permission

        //region Formatted Text Functions

        //region String Values return functions

        // returns "$0.00"
        fun get_amount_with_currency_format_in_string(value: String?): String {
            val isMinus : Boolean   = value
                .toString()
                .contains("(")
            val new                 = value
                .toString()
                .replace(GlobalVariables.CurrencySymbol, "")
                .replace("$", "")
                .replace(",", "")
                .replace("(", "")
                .replace(")", "")

            try {
                val credits = decimal2.parse(new)
                credits?.let {
                    val result = decimal2.format(it)
                    if (it.toDouble() < 0 || isMinus) return "-" + GlobalVariables.CurrencySymbol + result.replace("-", "") else return GlobalVariables.CurrencySymbol + result
                }
                return GlobalVariables.CurrencySymbol + "0.00"
            } catch (ex: Exception) {
                ex.printStackTrace()
                return GlobalVariables.CurrencySymbol + "0.00"
            }

        }

        // returns "0.00"
        fun get_amount_without_currency_format_in_string(value: String?): String {
            val isMinus : Boolean   = value
                .toString()
                .contains("(")
            val new                 = value
                .toString()
                .replace(GlobalVariables.CurrencySymbol, "")
                .replace("$", "")
                .replace(",", "")
                .replace("(", "")
                .replace(")", "")
                .replace("%", "")
                .replace("=", "")

            try {
                val credits = decimal2.parse(new)
                credits?.let {
                    val result = decimal2.format(it)
                    if (it.toDouble() < 0 || isMinus) return "-${result.replace("-", "")}" else return result
                }
                return "0.00"
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "0.00"
            }
        }

        // returns "0.0"
        fun get_value_with_single_decimal_point_in_string(value: String?): String {
            val isMinus : Boolean   = value
                .toString()
                .contains("(")
            val new                 = value
                .toString()
                .replace(GlobalVariables.CurrencySymbol, "")
                .replace("$", "")
                .replace(",", "")
                .replace("(", "")
                .replace(")", "")
            try {
                val credits = decimal1.parse(new)
                credits?.let {
                    val result = decimal1.format(it)
                    if (it.toDouble() < 0 || isMinus)  return result else return result
                }
                return "0.0"
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "0.0"
            }
        }

        // returns "$0"
        fun get_value_in_string(value: String?): String {
            return try {
                val number          : Double    = value
                    .toString()
                    .replace(GlobalVariables.CurrencySymbol, "")
                    .replace("$", "")
                    .replace(",", "")
                    .replace("(", "")
                    .replace(")", "")
                    .toDouble()
                val number2digits   : Int       = String.format("%.2f", number).toDouble().toInt()
                GlobalVariables.CurrencySymbol + number2digits
            } catch (ex: Exception) {
                ex.printStackTrace()
                GlobalVariables.CurrencySymbol + "0"
            }
        }

        //return "0.0000000000"
        fun get_Location_Value_ten_digits_in_string(value: String): String {
            val isNegative      = value.startsWith("-")
            val cleanedValue    = value
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
            val decimal10       = DecimalFormat("#.##########", DecimalFormatSymbols(Locale.US))

            try {
                val credits = decimal10.parse(cleanedValue)
                credits?.let {
                    val result = decimal10.format(it)
                    return if (isNegative) "-$result" else result
                }
                return "0.0000000000"
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "0.0000000000"
            }
        }

        //endregion String Values return functions

        //region Double Values return functions

        // returns 0.00
        fun get_amount_without_currency_format_in_double(value: String?): Double {
            val newValue = value.toString()
                .replace(GlobalVariables.CurrencySymbol, "")
                .replace(",", "")
                .replace("(", "")
                .replace(")", "")

            return try {
                val credits = decimal2.parse(newValue)?.toDouble()
                String.format("%.2f", credits).toDouble()
            } catch (ex: Exception) {
                ex.printStackTrace()
                0.00
            }
        }

        //endregion Double Values return functions

        //region Float Values return functions

        // returns 0.0f
        fun get_value_in_float(value: String?): Float {
            return try {
                val number : Double         = value.toString()
                    .replace(GlobalVariables.CurrencySymbol, "")
                    .replace("$", "")
                    .replace(",", "")
                    .replace("(", "")
                    .replace(")", "")
                    .toDouble()
                val number2digits : Float   = String.format("%.2f", number).toDouble().toFloat()
                number2digits
            } catch (ex: Exception) {
                ex.printStackTrace()
                0.0f
            }
        }

        //endregion Float Values return functions

        //region Int Values return functions

        // returns 0
        fun get_value_in_int(value: String?): Int {
            return try {
                val number: Double = value
                    .toString()
                    .replace(GlobalVariables.CurrencySymbol, "")
                    .replace("$", "")
                    .replace(",", "")
                    .replace("(", "")
                    .replace(")", "")
                    .toDouble()
                val number2digits: Int = String.format("%.2f", number).toDouble().toInt()
                number2digits
            } catch (ex: Exception) {
                ex.printStackTrace()
                0
            }
        }

        //endregion Int Values return functions

        //endregion Formatted Text Functions

        //region Time and Date functionality

        fun getCurrentWeekdayInfo(): Pair<Int, String> {
            val calendar = Calendar.getInstance()

            calendar.firstDayOfWeek = Calendar.SUNDAY

            var dayOfWeek   = calendar.get(Calendar.DAY_OF_WEEK)
            val dayName     = when (dayOfWeek) {
                Calendar.SUNDAY     -> "Sunday"
                Calendar.MONDAY     -> "Monday"
                Calendar.TUESDAY    -> "Tuesday"
                Calendar.WEDNESDAY  -> "Wednesday"
                Calendar.THURSDAY   -> "Thursday"
                Calendar.FRIDAY     -> "Friday"
                Calendar.SATURDAY   -> "Saturday"
                else                -> "Unknown"
            }

            // Adjust Sunday (which is returned as 1) to be the last day of the week (7)
            if (dayOfWeek == Calendar.SATURDAY) dayOfWeek = 7 else dayOfWeek -= 0
            return Pair(dayOfWeek, dayName)
        }

        fun get_utc_current_date_with_time() : String{
            val calender        = Calendar.getInstance()
            calender.timeZone   = TimeZone.getTimeZone("UTC")
            val year            = calender.get(Calendar.YEAR)
            val month           = calender.get(Calendar.MONTH) + 1
            val day             = calender.get(Calendar.DAY_OF_MONTH)
            val hour            = calender.get(Calendar.HOUR_OF_DAY)
            val minute          = calender.get(Calendar.MINUTE)
            val second          = calender.get(Calendar.SECOND)
            val UTCcurrentDate  = String.format("%04d-%02d-%02d %02d:%02d:%02d",year,month , day, hour, minute, second)
            return UTCcurrentDate
        }

        fun getCurrentUTCTime(): String {
            val calendar    = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val hour        = calendar.get(Calendar.HOUR_OF_DAY)
            val minute      = calendar.get(Calendar.MINUTE)
            val second      = calendar.get(Calendar.SECOND)
            return String.format("%02d:%02d:%02d", hour, minute, second)
        }

        fun getCurrentDate1(): String {
            val calendar    = Calendar.getInstance()
            val year        = calendar.get(Calendar.YEAR)
            val month       = calendar.get(Calendar.MONTH) + 1
            val day         = calendar.get(Calendar.DAY_OF_MONTH)
            return String.format("%04d-%02d-%02d", year,month,day)
        }

        fun get_current_date_with_time() : String{
            val calendar    = Calendar.getInstance()
            val year        = calendar.get(Calendar.YEAR)
            val month       = calendar.get(Calendar.MONTH) + 1
            val day         = calendar.get(Calendar.DAY_OF_MONTH)
            val hour        = calendar.get(Calendar.HOUR_OF_DAY)
            val minute      = calendar.get(Calendar.MINUTE)
            val second      = calendar.get(Calendar.SECOND)
            val CurrentDate = String.format("%04d-%02d-%02d %02d:%02d:%02d",year,month,day,hour,minute,second)
            return CurrentDate
        }

        fun getCurrentDate() : String = SimpleDateFormat("MM/dd/yyyy").format(Date())

        fun getUtcOffset(): String {
            val timeZone    = TimeZone.getDefault()
            val rawOffset   = timeZone.rawOffset / (1000 * 60)          // Convert milliseconds to minutes
            val sign        = if (rawOffset >= 0) "+" else "-"          // Determine the sign
            val hours       = abs(rawOffset / 60)                    // Get absolute hours
            val minutes     = abs(rawOffset % 60)                    // Get absolute minutes
            return "$sign${String.format("%02d", hours)}:${String.format("%02d", minutes)}"
        }

        fun formatDate(inputDateFormat : Pattern,inputDate: String): String {
            val inputFormat     = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val outputFormat    = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date            = LocalDate.parse(inputDate, inputFormat)
            return outputFormat.format(date)
        }
        fun convertIso8601ToReadable(iso8601Date: String): String {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateTime: LocalDateTime = LocalDateTime.parse(iso8601Date, inputFormat)

            return outputFormat.format(dateTime)
        }

        fun formatDate1(inputDate: String): String {
            val inputFormat     = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat    = SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault())
            try {
                val date = inputFormat.parse(inputDate)
                return outputFormat.format(date!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return inputDate // Return the input string if parsing fails
        }

        fun convertDateFormat(originalDate: String): String {
            val originalFormat  = SimpleDateFormat("HH:mm a", Locale.getDefault())
            val targetFormat    = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date : Date     = originalFormat.parse(originalDate) as Date
            return targetFormat.format(date)
        }

        fun converttimeFormat(originalDate: String): String {
            val originalFormat  = SimpleDateFormat("HH:mm a", Locale.getDefault())
            val date:  Date     = originalFormat.parse(originalDate) as Date
            return originalFormat.format(date)
        }

        fun convertTimestampToFormattedTime(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

        //endregion Time and Date functionality

        //region Validations

        fun validEmail(emailText : String): String? = if (emailText.trim().isBlank()) AlertMsgs.emailMissing else if (!emailPattern.matcher(emailText.trim()).matches()) AlertMsgs.invalidEmail else null

        fun validPhone(phoneText : String): String? = if (phoneText.trim().isBlank()) AlertMsgs.phoneMissing else if (!phoneText.trim().matches(".*[0-9].*".toRegex())) AlertMsgs.invalidPhone else if (phoneText.length != 10 ) AlertMsgs.invalidPhone else null

        fun validzipcode(zipcode : String) : String? = if (zipcode.isBlank()) AlertMsgs.zipCodeMissing else if (zipcode.length > 10) AlertMsgs.invalidZipCode else if(zipcode.length < 5) AlertMsgs.invalidZipCode else null

        fun isValidIPAddress(ip: String): Boolean {

            // Regex for digit from 0 to 255.
            val zeroTo255 = ("(\\d{1,2}|(0|1)\\"
                    + "d{2}|2[0-4]\\d|25[0-5])")

            // Regex for a digit from 0 to 255 and
            // followed by a dot, repeat 4 times.
            // this is the regex to validate an IP address.
            val regex = (zeroTo255 + "\\."
                    + zeroTo255 + "\\."
                    + zeroTo255 + "\\."
                    + zeroTo255)

            // Compile the ReGex
            val p: Pattern = Pattern.compile(regex)

            // If the IP address is empty
            // return false

            // Pattern class contains matcher() method
            // to find matching between given IP address
            // and regular expression.
            val m: Matcher = p.matcher(ip)

            // Return if the IP address
            // matched the ReGex
            return m.matches()
        }


        fun validemailorphonenumber(emailOrPhone : String): String? {
            if (emailOrPhone.isBlank() || emailOrPhone.isEmpty()){
                return AlertMsgs.missingValue
            }
            else{
                if (emailOrPhone.isNotBlank() || emailOrPhone.isNotEmpty()){
                    if (!isValidEmailOrPhone(emailOrPhone)) {
                        return AlertMsgs.invalidEmailorPhone
                    }
                }else{
                    return AlertMsgs.missingValue
                }
            }
            return null
        }

        fun isValidEmailOrPhone(input: String): Boolean = if (isNumeric(input) && input.length == 10) true else emailPattern.matcher(input).matches()

        fun validPassword(passwordText : String): String? = when {
            passwordText.isBlank() -> AlertMsgs.passMissing
            passwordText.isEmpty() -> AlertMsgs.passMissing
            else -> null
        }

        fun isNumeric(toCheck: String): Boolean = toCheck.all { char -> char.isDigit() }

        fun validname(nameText : String): String? = if (nameText.isBlank()) AlertMsgs.firstNameMissing else if (!nameText.trim().matches("^[A-Z a-z]+\$".toRegex())) AlertMsgs.invalidName else null

        fun svalidname(nameText : String): String? = if (nameText.isBlank()) AlertMsgs.lastNameMissing else if (!nameText.trim().matches("^[A-Z a-z]+\$".toRegex())) AlertMsgs.invalidName else null

        fun validcoPassword(passwordText : String,confirmpasswordText : String): String? = if (confirmpasswordText.isNotEmpty()) if (passwordText != confirmpasswordText) AlertMsgs.passNotMatch else null else AlertMsgs.conPassMissing

        fun isvalidpassword(passwordText : String,oldpassword : String = ""): String? =
            if (passwordText.trim().isBlank()) AlertMsgs.passMissing
            else if(passwordText.trim().isNotBlank()){
                if (!passwordText.trim().matches(".*[0-9].*".toRegex())) AlertMsgs.passwordcontaindigit
                else if (!passwordText.trim().matches(".*[a-z].*".toRegex())) AlertMsgs.passwordcontainlowercase
                else if (!passwordText.trim().matches(".*[A-Z].*".toRegex())) AlertMsgs.passwordcontainuppercase
                else if (!passwordText.trim().matches(".*[a-zA-Z].*".toRegex())) AlertMsgs.passwordcontainletter
                else if(!passwordText.trim().matches(".*[!@#$%^&*+=/?].*".toRegex())) AlertMsgs.passwordcontainspecialcharacter
                else if (!passwordText.trim().matches(".{4,30}".toRegex())) "Password length should be in between 4 to 30 characters"
                else if (oldpassword.isNotEmpty()){
                    if (passwordText == oldpassword) "Old password and new password can't be similar" else null
                }
                else null
            }else null

        //endregion Validations

        //region Distance Calculators

        fun getCoordinatesFromAddress(context: Context, address: String, callback: (Location?) -> Unit) {
            val geocoder    = Geocoder(context)
            val maxResults  = 1 // Number of results to retrieve

            Thread {
                try {
                    val addressList : List<Address> = geocoder.getFromLocationName(address, maxResults)!!

                    if (addressList.isNotEmpty()) {
                        val location        = Location("")
                        location.latitude   = addressList[0].latitude
                        location.longitude  = addressList[0].longitude
                        callback(location)
                    } else {
                        callback(null)
                    }
                } catch (e: Exception) {
                    callback(null)
                }
            }.start()
        }

        fun degreesToRadians(degrees: Double): Double = degrees * Math.PI / 180.0

        fun distanceBetweenTwoPointsInMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val earthRadiusInMiles  = 3958.8 // Earth's radius in miles
            val lat1Rad             = degreesToRadians(lat1)
            val lon1Rad             = degreesToRadians(lon1)
            val lat2Rad             = degreesToRadians(lat2)
            val lon2Rad             = degreesToRadians(lon2)
            val latDistance         = lat2Rad - lat1Rad
            val lonDistance         = lon2Rad - lon1Rad
            val a = kotlin.math.sin(latDistance / 2) * kotlin.math.sin(latDistance / 2) + kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) * kotlin.math.sin(lonDistance / 2) * kotlin.math.sin(lonDistance / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return earthRadiusInMiles * c
        }

        fun calculateDistance(lat1: Double,lon1: Double,lat2: Double,lon2: Double): Double {
            val radiusOfEarthMiles = 3958.8 // Earth's radius in miles
            val lat1Rad             = Math.toRadians(lat1)
            val lon1Rad             = Math.toRadians(lon1)
            val lat2Rad             = Math.toRadians(lat2)
            val lon2Rad             = Math.toRadians(lon2)
            val dLat                = lat2Rad - lat1Rad
            val dLon                = lon2Rad - lon1Rad
            val a = kotlin.math.sin(dLat / 2).pow(2) + kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) * kotlin.math.sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1.minus(a)))
            return radiusOfEarthMiles * c
        }

        fun shortestDistance(points: List<Pair<Double, Double>>) : Double {
            if (points.size < 2) throw IllegalArgumentException("At least two points are required to calculate the shortest distance.")
            var shortestDistance = Double.MAX_VALUE
            for (i in 0 until points.size - 1) {
                for (j in i + 1 until points.size) {
                    val distance = calculateDistance(points[i].first, points[i].second,points[j].first, points[j].second)
                    if (distance < shortestDistance) shortestDistance = distance
                }
            }
            return shortestDistance
        }

        //endregion Distance Calculators

        fun setSelectedRowColor(convertView: View) {
            convertView.setBackgroundColor(ContextCompat.getColor(convertView.context,R.color.selected_row))
        }

        fun setSelectedRowTextViewColor(textView: TextView, colorType: TEXT_COLOR) {
            when (colorType) {
                TEXT_COLOR.DEFAULT -> textView.setTextColor(Color.BLACK)
                TEXT_COLOR.SELECTED -> textView.setTextColor(ContextCompat.getColor(textView.context,R.color.white))
                TEXT_COLOR.BLUE -> textView.setTextColor(Color.BLUE)
                TEXT_COLOR.PURPLE -> textView.setTextColor(ContextCompat.getColor(textView.context,R.color.dark_red))
                else ->{

                }
            }
        }

        //region Menu Item Selections

        fun menuitemselection(navView: BottomNavigationView, id: Int, visibility: Boolean) {

            when (visibility) {
                true -> navView.visibility  = View.VISIBLE
                false -> navView.visibility = View.GONE
            }
            navView.menu.getItem(id).isChecked = true
        }

        /**fun sidemenu(menu: NavigationView) {

            val headerView          : View          = menu.getHeaderView(0)
            val headerImageView     : ImageView     = headerView.findViewById(R.id.side_menu_image)
            val headerImagetextview : TextView      = headerView.findViewById(R.id.side_menu_text)
            val headerfirstname     : TextView      = headerView.findViewById(R.id.side_nav_menu_firstName_txt)
            val headerlastname      : TextView      = headerView.findViewById(R.id.side_nav_menu_lastName_txt)

            if (GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim().isEmpty() && GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim().isBlank()){
                headerImagetextview.text = GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim()
            }
            else if (GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim().isEmpty() && GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim().isBlank()){
                headerImagetextview.text = GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim()
            }
            else if (GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim().isNotEmpty() && GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim().isNotBlank()
                && GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim().isNotEmpty() && GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim().isNotBlank()) {
                headerImagetextview.text = GlobalVariables.Firstname.toString()[0].uppercaseChar().toString().trim() + GlobalVariables.Lastname.toString()[0].uppercaseChar().toString().trim()
            }

            Glide.with(headerImageView)
                .load(GlobalVariables.ImageBaseUrl+ GlobalVariables.imagepath)
                .fitCenter()
                .listener(object : RequestListener<Drawable?> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        headerImagetextview.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        headerImagetextview.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(headerImageView)
            headerfirstname.text    = GlobalVariables.Firstname
            headerlastname.text     = GlobalVariables.Lastname
        }**/

        //endregion Menu Item Selections

        //region Custom Toast Message

        /**fun ShowToastMessage(context: Context, Message: String, durationMillis: Int = 1000) {
            val layoutInflater      = LayoutInflater.from(context)
            val layout : View       = layoutInflater.inflate(R.layout.layout_custom_toast_message, null)
            val textView : TextView = layout.findViewById(R.id.toast_text)
            textView.text           = Message
            val toast               = Toast(context)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({toast.cancel()}, durationMillis.toLong())
            toast.view = layout
            toast.setGravity(Gravity.FILL_HORIZONTAL or Gravity.FILL_VERTICAL, 0, 0)
            toast.show()
        }**/

        //endregion Custom Toast Message
        
        //region Screen size Limit
        fun isWithinBounds(x: Float,y: Float,screenWidth: Int,screenHeight: Int,topMargin: Int,bottomMargin: Int,viewWidth: Int,viewHeight: Int): Boolean = x >= 0 && x + viewWidth <= screenWidth && y >= topMargin && y + viewHeight <= screenHeight - bottomMargin

        //endregion Screen size Limit

        //region IP Address Method

        fun getIPV4address() : String {
            NetworkInterface.getNetworkInterfaces()?.toList()?.map {networkInterface -> networkInterface.inetAddresses?.toList()?.find {!it.isLoopbackAddress && it is Inet4Address}?.let {return it.hostAddress?.toString() ?: ""}}
            return ""
        }

        //endregion IP Address Method

        //region CONVERT STRING INTO JSON
        inline fun <reified T> convertJsonToModel(jsonString: String): T? {
            return try {
                Gson().fromJson(jsonString, T::class.java)
            } catch (e: Exception) {
                Log.i("==>", "ERROR: Unable to parse JSON into model")
                null
            }
        }
        //endregion CONVERT STRING INTO JSON

        //region LOGOUT FUNCTIONALITY
        
        /**fun Logout(context: Context,status:String = "Unauthorized"){

            if (status.equals("Unauthorized", true)) {
                val prefs = PreferenceHelper.customPreference(context, GlobalVariables.CUSTOM_PREF_NAME)

                GlobalVariables.loginkey                        = "false"

                val editor          = prefs.edit()
                val authtoken       = prefs.getString("SPauthtoken", null).toString()
                val basseip         = prefs.getString("SPBASEURL", null).toString()
                val imageip         = prefs.getString("ImageUrl", null).toString()
                val MINSALEPRICE    = prefs.getString("SPMINSALEPRICE", "").toString()
                val MAXSAKEPRICE    = prefs.getString("SPMaxSALEPRICE", "").toString()
                val storeid         = prefs.getString("StoreID", null).toString()
                val page_size       = prefs.getString("PageSize", null).toString()
                val currency        = prefs.getString("CurrencySymbol", null).toString()
                val alerttype       = prefs.getString("ALERTTYPE", null).toString()
                val alerttitle      = prefs.getString("ALERTTITLE", null).toString()
                val alertimage      = prefs.getString("ALERTIMAGE", null).toString()
                val Countrycode     = prefs.getString("COUNTRYCODE","").toString()
                val Password        = prefs.getString("SPpassword","").toString()

                editor.clear().apply()
                editor.apply {
                    putString("KEY", GlobalVariables.loginkey)
                    putString("SPauthtoken", authtoken)
                    putString("SPBASEURL", basseip)
                    putString("ImageUrl", imageip)
                    putString("SPMINSALEPRICE", MINSALEPRICE)
                    putString("SPMaxSALEPRICE", MAXSAKEPRICE)
                    putString("StoreID", storeid)
                    putString("PageSize", page_size)
                    putString("CurrencySymbol", currency)
                    putString("ALERTTYPE", alerttype)
                    putString("ALERTTITLE", alerttitle)
                    putString("ALERTIMAGE", alertimage)
                    putString("COUNTRYCODE",Countrycode)
                    putString("SPpassword",Password)
                    apply()
                }
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        }**/
        
        //endregion LOGOUT FUNCTIONALITY

        //region Read JSON

        fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                //jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.use { it.read(buffer) }
                jsonString = String(buffer)

            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }

        //endregion Read JSON
        //endregion ALL FUNCTIONS

        //region Shared Preferences
        
        object PreferenceHelper {
            fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
        
        //endregion Shared Prefrences

    }

}


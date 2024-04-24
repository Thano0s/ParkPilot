package com.application.parkpilot.viewModel

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.parkpilot.Book
import com.application.parkpilot.Time
import com.application.parkpilot.User
import com.application.parkpilot.activity.Feedback
import com.application.parkpilot.module.DatePicker
import com.application.parkpilot.module.TimePicker
import com.application.parkpilot.module.firebase.Storage
import com.application.parkpilot.module.firebase.database.Booking
import com.application.parkpilot.module.firebase.database.StationAdvance
import com.application.parkpilot.module.firebase.database.StationBasic
import com.application.parkpilot.module.firebase.database.StationLocation
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import com.application.parkpilot.StationAdvance as StationAdvanceDataClass
import com.application.parkpilot.StationBasic as StationBasicDataClass
import com.application.parkpilot.module.firebase.database.Feedback as FS_Feedback

class SpotPreviewViewModel : ViewModel() {

    lateinit var stationUID: String
    val carouselImages = MutableLiveData<List<Any>>()
    val stationBasicInfo = MutableLiveData<StationBasicDataClass>()
    val stationAdvanceInfo = MutableLiveData<StationAdvanceDataClass>()
    val stationRating = MutableLiveData<Pair<Float, Int>>()
    val liveDataDistance = MutableLiveData<String>()
    var fromDate: Long? = null
    var toDate: Long? = null
    var fromTime: Time? = null
    var toTime: Time? = null
    private var stationLocation: GeoPoint? = null
    private var currentLocation: Location? = null
    val timePicker = TimePicker("pick the time", TimePicker.CLOCK_12H)
    val datePicker = Calendar.getInstance().let {
        val startTime = it.timeInMillis
        it.add(Calendar.DAY_OF_MONTH, 30)
        val endTime = it.timeInMillis
        DatePicker(startTime, endTime)
    }

    fun loadCarousel(stationUID: String) {
        viewModelScope.launch {
            carouselImages.value = Storage().parkSpotPhotoGet(stationUID)
        }
    }

    fun getTimeStamp(time: Time, date: Long): Timestamp {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.add(Calendar.HOUR, time.hours)
        calendar.add(Calendar.MINUTE, time.minute)

        val seconds = calendar.timeInMillis / 1000
        return Timestamp(seconds, 0)
    }

    fun loadBasicInfo(stationUID: String) {
        viewModelScope.launch {
            stationBasicInfo.value = StationBasic().basicGet(stationUID)
        }
    }

    fun loadAdvanceInfo(stationUID: String) {
        viewModelScope.launch {
            stationAdvanceInfo.value = StationAdvance().advanceGet(stationUID)
        }
    }

    fun getDistance(context: Context, stationUID: String) {
        viewModelScope.launch {
            stationLocation = StationLocation().locationGet(stationUID)
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                currentLocation = fusedLocationClient.lastLocation.await()
                stationLocation?.let { station ->
                    currentLocation?.let { current ->
                        liveDataDistance.value =
                            String.format("%.1f", current.distanceTo(Location("").apply {
                                longitude = station.longitude
                                latitude = station.latitude
                            }) / 1000) + "km"
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun book(fromTimestamp: Timestamp, toTimestamp: Timestamp) {
        val booking = Booking()
        val ticket = Book(fromTimestamp, toTimestamp, stationUID, User.UID)
        viewModelScope.launch {
            booking.getCountBetween(ticket)
            booking.bookingSet(ticket)
        }
    }

    fun redirect(context: Context) {
        stationLocation?.let { station ->
            currentLocation?.let { current ->
                val uri =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${current.latitude},${current.longitude}&destination=${station.latitude},${station.longitude}")

                // Create an Intent to open Google Maps with the specified URI
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(mapIntent)
            }
        }
    }

    fun loadRating(stationUID: String) {
        viewModelScope.launch {
            val feedbacks = FS_Feedback().feedGet(stationUID)
            var totalRatting = 0.0f
            for (i in feedbacks) {
                totalRatting += i.value.rating
            }
            stationRating.value = Pair(totalRatting, feedbacks.size)
        }
    }

    fun feedback(context: Context, stationUid: String) {
        context.startActivity(Intent(context, Feedback::class.java).apply {
            putExtra("stationUID", stationUid)
        })
    }
}
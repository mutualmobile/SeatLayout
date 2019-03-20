package com.mutualmobile.android.seatlayout

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mutualmobile.android.library.seatlayout.SeatListener
import com.google.gson.Gson
import com.mutualmobile.android.library.seatlayout.view.SeatView
import com.mutualmobile.android.seatlayout.databinding.SeatSelectionActivityBinding

class SeatSelectionActivity : AppCompatActivity(), SeatListener {

    private val seatView by lazy { SeatView() }

    private var binding: SeatSelectionActivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUI()
        readJsonFromAssets()
    }

    private fun readJsonFromAssets() {
        var jsonString = ""
        try {
            val inputStream = assets.open("seatLayout.json")
            val sizeOfJSONFile = inputStream.available()
            val bytes = ByteArray(sizeOfJSONFile)
            inputStream.read(bytes)
            inputStream.close()
            jsonString = String(bytes)
            val gson = Gson()
            provideSeatLayout(gson.fromJson<SeatLayoutResponse>(jsonString, SeatLayoutResponse::class.java))
        } catch (error: Error) {
            Log.e("Reading JSON", error.message)
        }
    }


    private fun provideSeatLayout(dataResponse: SeatLayoutResponse) {
        val seatLayout = dataResponse.data
        val seats = getSeatScheme(seatLayout.seatingData.areas)
        seatView.setSeats(
            seats, false,
            Pair(10, 99999)
        )
    }


    private fun setUI() {

        binding?.let {
            seatView.setupScheme(
                it.seatLayout,
                it.seatLayoutTooltip,
                it.zoomButton,
                it.seatLayoutRippleView
            )
        }

        seatView.setSeatListener(this)
    }

    override fun maxSeatsReached() {
        //
    }

    override fun updateSelectedSeatDisplay(selectedSeats: Int, maxSelectedSeats: Int) {

    }

    override fun seatTapped() {
        // Callback called when seat is tapped on seat layout
    }
}

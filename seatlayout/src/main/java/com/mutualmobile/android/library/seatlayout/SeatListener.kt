package com.mutualmobile.android.library.seatlayout

interface SeatListener {
    fun maxSeatsReached()
    fun updateSelectedSeatDisplay(selectedSeats: Int, maxSelectedSeats: Int)
    fun seatTapped()
}
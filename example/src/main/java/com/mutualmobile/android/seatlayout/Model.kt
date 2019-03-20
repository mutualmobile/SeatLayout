package com.mutualmobile.android.seatlayout

import com.google.gson.annotations.SerializedName

data class SeatLayoutResponse(var data: SeatLayout)

data class Image(
    @SerializedName("caption")
    val caption: String,
    @SerializedName("uri")
    val uri: String
)


data class ScreenImages(
    @SerializedName("overview")
    val overview: String?,
    val images: List<Image>?
)

data class SeatingData(
    @SerializedName("cinemaId") val cinemaId: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("screenNumber") val screenNumber: String,
    @SerializedName("areas") val areas: List<Area>,
    @SerializedName("screenImages") val screenImages: ScreenImages
)

data class SeatLayout(@SerializedName("seatingData") val seatingData: SeatingData)


data class Area(
    val id: Int,
    val areaIndex: Int,
    val vistaId: Int,
    val vistaAreaNumber: Int,
    val areaCategoryCode: String,
    val description: String,
    val columnCount: Int,
    val rowCount: Int,
    val numberOfSeats: Int,
    val isSofaSeatingEnabled: Boolean,
    val isInSeatDeliveryEnabled: Boolean,
    val rows: List<Row>,
    val isAreaForSale: Boolean,
    val ticketsOnOrder: Int,
    val totalSeatsToAllocateCount: Int,
    val seatsAllocatedCount: Int,
    val seatsToAllocate: Int,
    val seatsRemainingToAllocateCount: Int
)

data class Row(
    val areaIndex: Int,
    val rowIndex: Int,
    val name: String,
    val rowNumber: String,
    val vistaRow: Int,
    val vistaAreaNumber: Int,
    val vistaRowIndex: Int,
    val isEmpty: Boolean,
    val seats: List<Seat>
)

data class Seat(
    val id: String?,
    val seatNumber: String?,
    val areaIndex: Int,
    val rowIndex: Int,
    val columnIndex: Int,
    val areaId: Int,
    val vistaAreaNumber: Int,
    val vistaRowIndex: Int,
    val vistaColumnIndex: Int,
    val priority: Int,
    val price: Int,
    val seatStyle: String,
    val seatDescription: String?,
    val seatStatus: String,
    val tableStyle: String,
    val warnings: List<Warning>
)

data class Warning(
    val category: Int,
    val code: Int,
    val description: String
)

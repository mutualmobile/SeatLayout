package com.mutualmobile.android.seatlayout

import com.mutualmobile.android.library.seatlayout.SeatData
import com.mutualmobile.android.library.seatlayout.view.SeatView

fun getSeatScheme(areas: List<Area>): Array<Array<SeatData?>> {
    var rowCount = 0
    var columnCount = 0
    val rows = mutableListOf<Row>()
    areas.forEach { area ->
        rowCount += area.rowCount
        rows.addAll(area.rows)
        columnCount = Math.max(columnCount, area.columnCount)
    }
    val seats = Array(rowCount) { arrayOfNulls<SeatData>(columnCount) }
    for (rowIndex in 0 until rowCount) for (columnIndex in 0 until columnCount) {
        val row = rows[rowIndex]
        var seatData: Seat
        seatData = if (columnIndex >= row.seats.size) {
            Seat(
                "", "", 0, 0, 0, 0, 0, 0, 0, 0, 0, SeatView.SeatStatus.NONE.name,
                "", SeatView.SeatStyle.NONE.name,
                SeatView.TableStyle.NONE.name, emptyList()
            )
        } else {
            row.seats[columnIndex]
        }
        val seat = SeatData()
        seat.id = seatData.id
        seat.areaIndex = seatData.areaIndex
        seat.rowIndex = seatData.rowIndex
        seat.rowNumber = row.rowNumber
        seat.seatNumber = seatData.seatNumber;
        seat.columnIndex = seatData.columnIndex
        seat.price = seatData.price
        seat.seatDescription = seatData.seatDescription ?: ""
        if (areas[seat.areaIndex].isAreaForSale) {
            SeatView.getSeatStatus(seatData.seatStatus).let { seat.seatStatus = it }
        } else {
            seat.seatStatus = SeatView.SeatStatus.SOLD
        }
        SeatView.getSeatStyle(seatData.seatStyle).let { seat.seatStyle = it }
        SeatView.getTableStyle(seatData.tableStyle).let { seat.tableStyle = it }
        seats[rowIndex][columnIndex] = seat
    }
    return seats
}

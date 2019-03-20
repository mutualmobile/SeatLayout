package com.mutualmobile.android.library.seatlayout

import android.graphics.PointF
import android.graphics.RectF
import com.mutualmobile.android.library.seatlayout.view.SeatView

class SeatData {
    var id: String? = null
    var areaIndex: Int = 0
    var rowIndex: Int = 0
    var columnIndex: Int = 0
    var seatNumber: String? = null
    var rowNumber: String? = null
    var price: Int = 0
    var seatDescription: String = ""
    lateinit var containerbound: RectF
    lateinit var seatCenterPoint: PointF
    lateinit var seatStatus: SeatView.SeatStatus
    lateinit var seatStyle: SeatView.SeatStyle
    lateinit var tableStyle: SeatView.TableStyle
    fun isSeatPressed(x: Float, y: Float): Boolean {
        return containerbound.contains(x, y)
    }
}
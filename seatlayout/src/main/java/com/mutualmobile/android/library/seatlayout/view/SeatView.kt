package com.mutualmobile.android.library.seatlayout.view

import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.core.content.ContextCompat
import android.text.TextPaint
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.mutualmobile.android.library.seatlayout.SeatListener
import com.mutualmobile.android.library.R
import com.mutualmobile.android.library.seatlayout.SeatData
import com.mutualmobile.android.library.seatlayout.listener.OnMatrixChangedListener
import com.mutualmobile.android.library.seatlayout.listener.OnPhotoTapListener
import com.mutualmobile.android.library.seatlayout.model.SeatSelection
import com.mutualmobile.android.library.seatlayout.photoview.SeatPhotoViewAttacher
import com.mutualmobile.android.library.seatlayout.utils.DensityUtil
import java.util.*

class SeatView {

    private val DEFAULT_AREA_INDEX = 99999

    private val seatGap = 0
    var bookedSeats = LinkedList<SeatData>()
    private var offsetX = 12
    private val offsetY = 12
    private var bitmapHeight: Int = 0
    private var bitmapWidth: Int = 0
    private var rows: Int = 0
    private var columns: Int = 0
    private var tablePaintStrokeWidth: Int = 0
    private var maxSelectedSeats: Int = 0
    private var measuredWidth: Int = 0
    private var measuredHeight: Int = 0
    private var clickX: Float = 0.toFloat()
    private var clickY: Float = 0.toFloat()
    private lateinit var rectPaint: Paint
    private lateinit var clearPaint: Paint
    private lateinit var boundPaint: Paint
    private lateinit var seatPaint: Paint
    private lateinit var bitmapPaint: Paint
    private lateinit var tablePaintWithRoundButt: Paint
    private lateinit var tablePaintWithRoundCap: Paint
    private lateinit var reserveIconPaint: Paint
    private var seatBottomPadding: Float = 0.toFloat()
    private var screenBottomPadding: Float = 0.toFloat()
    private var zoomButtonHeight: Float = 0.toFloat()
    private var minSeatWidth: Float = 0.toFloat()
    private lateinit var seats: Array<Array<SeatData?>>
    private var screen: Screen? = null
    private lateinit var canvasBitmap: Bitmap
    private lateinit var soldSeatBitmap: Bitmap
    private lateinit var handicapBitmap: Bitmap
    private lateinit var companionBitmap: Bitmap
    private lateinit var handicapSelectedBitmap: Bitmap
    private lateinit var companionSelectedBitmap: Bitmap
    private lateinit var canvas: Canvas
    private lateinit var seatListener: SeatListener
    private lateinit var context: Context
    private lateinit var seatPhotoView: SeatPhotoView
    private var zoomButton: ImageButton? = null
    private lateinit var tooltipView: ToolTipRelativeLayout
    private lateinit var rippleView: RippleView
    private lateinit var containerRect: RectF
    private lateinit var layoutParams: ViewGroup.MarginLayoutParams
    private var isEditMode: Boolean = false
    private var isAutoSelectionFailed: Boolean = false
    private var SELECTED_AREA_INDEX = DEFAULT_AREA_INDEX // Default index

    fun setupScheme(
        seatPhotoView: SeatPhotoView, tooltipView: ToolTipRelativeLayout, zoomButton: ImageButton,
        rippleView: RippleView
    ) {
        this.tooltipView = tooltipView
        this.zoomButton = zoomButton
        this.seatPhotoView = seatPhotoView
        this.rippleView = rippleView
        this.seatPhotoView.scaleType = ImageView.ScaleType.FIT_START
        initialize()
    }

    fun setupScheme(
        seatPhotoView: SeatPhotoView, tooltipView: ToolTipRelativeLayout,
        rippleView: RippleView
    ) {
        this.tooltipView = tooltipView
        this.zoomButton = null
        this.seatPhotoView = seatPhotoView
        this.rippleView = rippleView
        this.seatPhotoView.scaleType = ImageView.ScaleType.FIT_START
        initialize()
    }

    private fun initialize() {
        this.context = seatPhotoView.context
        handicapBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.accessible)
        handicapSelectedBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.accessible_selected
        )
        companionBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.companion)
        companionSelectedBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.companion_selected
        )
        if(seatPhotoView.unavailableDrawable==null) {
            soldSeatBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.unavailable)
        }

        tablePaintWithRoundCap = Paint(
            Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
        )
        tablePaintWithRoundCap.color = ContextCompat.getColor(context, R.color.table_color)
        tablePaintWithRoundCap.style = Paint.Style.FILL // set to STOKE
        tablePaintWithRoundCap.strokeCap = Paint.Cap.ROUND  // set the paint cap to round too

        tablePaintWithRoundButt = Paint(
            Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
        )
        tablePaintWithRoundButt.color = ContextCompat.getColor(context, R.color.table_color)
        tablePaintWithRoundButt.style = Paint.Style.FILL // set to STOKE
        tablePaintWithRoundButt.strokeCap = Paint.Cap.BUTT  // set the paint cap to round too

        seatPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        seatPaint.style = Paint.Style.FILL
        seatPaint.color = seatPhotoView.unselectedColor

        bitmapPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        bitmapPaint.style = Paint.Style.FILL

        reserveIconPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        reserveIconPaint.style = Paint.Style.FILL
        reserveIconPaint.color = seatPhotoView.selectedColor

        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint.style = Paint.Style.STROKE
        rectPaint.color = Color.RED

        clearPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clearPaint.color = Color.TRANSPARENT
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        boundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        boundPaint.style = Paint.Style.STROKE
        boundPaint.color = Color.GREEN

        seatPhotoView.setOnPhotoTapListener(PhotoTapListener())
        seatPhotoView.setOnMatrixChangeListener(MatrixChangeListener())
        if (seatPhotoView.zoomInDrawable != null && seatPhotoView.zoomOutDrawable != null) {
            zoomButton?.setOnClickListener(zoomClickListener)
            zoomButtonHeight = context.resources.getDimension(
                R.dimen.zoom_button_height
            ) + context.resources
                .getDimension(R.dimen.zoom_button_margin_bottom)
        } else {
            zoomButton?.visibility = View.INVISIBLE
        }
        minSeatWidth = DensityUtil.dip2px(context, 30f)
        seatBottomPadding = DensityUtil.dip2px(context, 8f)
        layoutParams = seatPhotoView.layoutParams as ViewGroup.MarginLayoutParams
    }

    fun setSeats(
        seats: Array<Array<SeatData?>>, isEditMode: Boolean, countPair: Pair<Int, Int>
    ) {
        bookedSeats.clear()

        this.isEditMode = isEditMode
        maxSelectedSeats = countPair.first
        updateBookedSeats(seats)
        SELECTED_AREA_INDEX = if (bookedSeats.size > 0) {
            bookedSeats[0].areaIndex
        } else {
            countPair.second
        }
        this@SeatView.seats = seats
        rows = seats.size
        columns = seats[0].size
        seatPhotoView.setDisplayMatrix(Matrix())
        seatPhotoView.post {
            if (screen == null || measuredWidth == 0) {
                measuredWidth = seatPhotoView.measuredWidth
                measuredHeight = seatPhotoView.measuredHeight
                screen = Screen(measuredWidth.toFloat())
            }
            seatPhotoView.setImageBitmap(seatLayout)
        }
    }

    private fun updateBookedSeats(
        seats: Array<Array<SeatData?>>
    ) {
        seats.forEach2D {
            if (it?.seatStatus == SeatStatus.RESERVED) {
                bookedSeats.add(it)
            }
        }

        if (this.isEditMode) {
            isAutoSelectionFailed = bookedSeats.size < maxSelectedSeats
            if (isAutoSelectionFailed) {
                seatListener.updateSelectedSeatDisplay(bookedSeats.size, maxSelectedSeats)
            }
        }

    }

    val seatLayout: Bitmap
        get() {
            var seatWidth: Float
            var seatHeight: Float
            val computedSeatWidth = (measuredWidth / columns - seatGap).toFloat()
            screen?.let {
                screenBottomPadding = it.baseLine + it.textOffsetY

                if (computedSeatWidth > minSeatWidth) {
                    seatWidth = minSeatWidth
                    tablePaintStrokeWidth = Math.round(seatWidth * 0.15).toInt()
                    seatHeight = seatWidth + tablePaintStrokeWidth.toFloat() + seatBottomPadding
                    offsetX = (measuredWidth - (seatWidth + seatGap) * columns).toInt()
                    bitmapHeight = (measuredHeight + it.baseLine).toInt()
                } else {
                    seatWidth = computedSeatWidth
                    offsetX = 12
                    tablePaintStrokeWidth = Math.round(seatWidth * 0.15).toInt()
                    seatHeight = seatWidth + tablePaintStrokeWidth.toFloat() + seatBottomPadding
                    bitmapHeight =
                            (rows * (seatHeight + seatGap) + offsetY.toFloat() + screenBottomPadding + zoomButtonHeight).toInt()
                }
                bitmapWidth = measuredWidth
                tablePaintWithRoundCap.strokeWidth = tablePaintStrokeWidth.toFloat()
                tablePaintWithRoundButt.strokeWidth = tablePaintStrokeWidth.toFloat()

                canvasBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                canvas = Canvas(canvasBitmap)
                canvas.save()
                it.drawScreen()
                drawSeatsAndTables(seatWidth, seatHeight)
                canvas.restore()
            }
            return canvasBitmap
        }

    private fun updateSeatLayout(selectedSeat: SeatData): Bitmap {
        canvas.save()
        canvas.drawRect(selectedSeat.containerbound, clearPaint)
        drawSeat(selectedSeat)
        drawTable(selectedSeat)
        if (isEditMode) {
            if (bookedSeats.size > 1 && bookedSeats.size > maxSelectedSeats) {
                val seatToUnselect = bookedSeats.first
                seatToUnselect.seatStatus = seatToUnselect.seatStatus.pressSeat()
                canvas.drawRect(seatToUnselect.containerbound, clearPaint)
                drawSeat(seatToUnselect)
                drawTable(seatToUnselect)
                bookedSeats.removeFirst()
            }
            if (isAutoSelectionFailed) {
                seatListener.updateSelectedSeatDisplay(bookedSeats.size, maxSelectedSeats)
            }
        }
        canvas.restore()
        return canvasBitmap
    }

    private fun drawSeatsAndTables(seatWidth: Float, seatHeight: Float) {
        var left: Float
        var right: Float
        var top: Float
        var bottom: Float
        for (row in 0..rows - 1) {
            for (column in 0..columns - 1) {
                left = offsetX / 2 + (seatWidth + seatGap) * column
                right = left + seatWidth
                top = (offsetY / 2).toFloat() + (seatHeight + seatGap) * row + screenBottomPadding
                bottom = top + seatHeight
                val seat = seats[row][column]
                seat?.let {
                    seat.containerbound = RectF(left, top, right, bottom)
                    drawSeat(seat)
                    drawTable(seat)
                }
            }
        }
    }

    private fun drawSeat(seat: SeatData) {
        if (seat.tableStyle == TableStyle.SIDE_TABLE_LEFT
            || seat.tableStyle == TableStyle.SIDE_TABLE_RIGHT
            || seat.seatStyle == SeatStyle.NONE
            || seat.seatStyle == SeatStyle.UNKNOWN
            || seat.seatStatus == SeatStatus.NONE
            || seat.seatStatus == SeatStatus.PLACEHOLDER
            || seat.seatStatus == SeatStatus.UNKNOWN
        ) {
            return
        }
        if (seat.seatStatus == SeatStatus.SOLD || seat.seatStatus == SeatStatus.BROKEN) {
            drawBitmap(soldSeatBitmap, seat)
        } else if (seat.seatStyle == SeatStyle.NORMAL || seat.seatStyle == SeatStyle.BARSEAT) {
            if (seat.seatStatus == SeatStatus.EMPTY) {
                drawCircle(seat, seatPaint)
            } else if (seat.seatStatus == SeatStatus.CHOSEN || seat.seatStatus == SeatStatus.RESERVED) {
                drawCircle(seat, reserveIconPaint)
            }
        } else if (seat.seatStyle == SeatStyle.HANDICAP) {
            if (seat.seatStatus == SeatStatus.EMPTY) {
                drawBitmap(handicapBitmap, seat)
            } else if (seat.seatStatus == SeatStatus.CHOSEN || seat.seatStatus == SeatStatus.RESERVED) {
                drawBitmap(handicapSelectedBitmap, seat)
            }
        } else if (seat.seatStyle == SeatStyle.COMPANION) {
            if (seat.seatStatus == SeatStatus.EMPTY) {
                drawBitmap(companionBitmap, seat)
            } else if (seat.seatStatus == SeatStatus.CHOSEN || seat.seatStatus == SeatStatus.RESERVED) {
                drawBitmap(companionSelectedBitmap, seat)
            }
        }
    }

    private fun drawTable(seat: SeatData) {
        if (seat.tableStyle == TableStyle.NONE
            || seat.tableStyle == TableStyle.LONG_GAP
            || seat.tableStyle == TableStyle.UNKNOWN
        ) {
            return
        } else if (seat.tableStyle == TableStyle.SINGLE) {
            canvas.drawLine(
                seat.containerbound.left + tablePaintStrokeWidth,
                seat.containerbound.top + tablePaintStrokeWidth / 2,
                seat.containerbound.right - tablePaintStrokeWidth,
                seat.containerbound.top + tablePaintStrokeWidth / 2, tablePaintWithRoundCap
            )
        } else if (seat.tableStyle == TableStyle.PAIR_LEFT) {
            drawTableRect(canvas, seat, tablePaintStrokeWidth, 0)
        } else if (seat.tableStyle == TableStyle.PAIR_RIGHT) {
            drawTableRect(canvas, seat, 0, tablePaintStrokeWidth)
        } else if (seat.tableStyle == TableStyle.SIDE_TABLE_LEFT || seat.tableStyle == TableStyle.SIDE_TABLE_RIGHT) {
            canvas.drawLine(
                seat.containerbound.left + tablePaintStrokeWidth,
                seat.containerbound.bottom - tablePaintStrokeWidth.toFloat() - seatBottomPadding,
                seat.containerbound.right - tablePaintStrokeWidth,
                seat.containerbound.bottom - tablePaintStrokeWidth.toFloat() - seatBottomPadding,
                tablePaintWithRoundButt
            )
        } else if (seat.tableStyle == TableStyle.LONG_LEFT) {
            drawTableRect(canvas, seat, tablePaintStrokeWidth, 0)
        } else if (seat.tableStyle == TableStyle.LONG_CENTER) {
            drawTableRect(canvas, seat, 0, 0)
        } else if (seat.tableStyle == TableStyle.LONG_RIGHT) {
            drawTableRect(canvas, seat, 0, tablePaintStrokeWidth)
        } else if (seat.tableStyle == TableStyle.LONG_GAP_LEFT) {
            drawTableRect(canvas, seat, 0, tablePaintStrokeWidth)
        } else if (seat.tableStyle == TableStyle.LONG_GAP_RIGHT) {
            drawTableRect(canvas, seat, tablePaintStrokeWidth, 0)
        }
    }

    private fun drawCircle(seat: SeatData, paint: Paint) {
        val centerX = seat.containerbound.centerX()
        val centerY = seat.containerbound.bottom - seat.containerbound.width() / 2 - seatBottomPadding
        val radius = seat.containerbound.width() / 3
        seat.seatCenterPoint = PointF(centerX + radius / 2, centerY)
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawBitmap(bitmap: Bitmap, seat: SeatData) {
        val radius = seat.containerbound.width() / 3
        val centerY = seat.containerbound.bottom - seat.containerbound.width() / 2 - seatBottomPadding
        val left = seat.containerbound.centerX() - radius
        val top = centerY - radius
        val right = seat.containerbound.centerX() + radius
        val bottom = centerY + radius
        val bitmapRect = RectF(left, top, right, bottom)
        seat.seatCenterPoint = PointF(bitmapRect.centerX() + radius / 2, bitmapRect.centerY())
        canvas.drawBitmap(bitmap, null, bitmapRect, bitmapPaint)
    }

    private fun drawTableRect(
        tempCanvas: Canvas, seat: SeatData, roundedRectLeft: Int,
        roundedRectRight: Int
    ) {
        tempCanvas.drawLine(
            seat.containerbound.left + tablePaintStrokeWidth,
            seat.containerbound.top + tablePaintStrokeWidth / 2,
            seat.containerbound.right - tablePaintStrokeWidth,
            seat.containerbound.top + tablePaintStrokeWidth / 2, tablePaintWithRoundCap
        )
        tempCanvas.drawLine(
            seat.containerbound.left + roundedRectLeft,
            seat.containerbound.top + tablePaintStrokeWidth / 2,
            seat.containerbound.right - roundedRectRight,
            seat.containerbound.top + tablePaintStrokeWidth / 2, tablePaintWithRoundButt
        )
    }

    private fun clickScheme() {
        for (row in 0..rows - 1) {
            for (column in 0..columns - 1) {
                val pressedSeat = seats[row][column]
                pressedSeat?.let {
                    if (pressedSeat.isSeatPressed(clickX, clickY)) {
                        if (SeatStatus.canSeatBePressed(
                                pressedSeat.seatStatus,
                                isEditMode
                            ) && SeatStyle.canSeatBePressed(pressedSeat.seatStyle)
                        ) {
                            if (isSeatingAreaSame(pressedSeat.areaIndex)) {
                                if (pressedSeat.seatStyle == SeatStyle.COMPANION && pressedSeat.seatStatus == SeatStatus.EMPTY) {
                                    //todo implement callback
                                } else if (pressedSeat.seatStyle == SeatStyle.HANDICAP && pressedSeat.seatStatus == SeatStatus.EMPTY) {
//todo implement callback
                                } else if (pressedSeat.seatStyle == SeatStyle.BARSEAT && pressedSeat.seatStatus == SeatStatus.EMPTY) {
//todo implement callback
                                } else {
                                    onSeatPressed(pressedSeat)
                                }
                            } else {
                                if (pressedSeat.seatStatus == SeatStatus.SOLD) {
                                    showToolTipView(pressedSeat)
                                } else {
                                    //todo implement callback
                                }
                            }
                        }
                        return
                    }
                }
            }
        }
    }

    fun isSeatingAreaSame(index: Int): Boolean {
        if ((bookedSeats.size == 0 && SELECTED_AREA_INDEX == DEFAULT_AREA_INDEX) || index == SELECTED_AREA_INDEX) {
            return true
        }
        return false
    }

    private fun onSeatPressed(pressedSeat: SeatData) {
        if (isMaximumCountNotReached(pressedSeat)) {
            notifySeatListener(pressedSeat)
            pressedSeat.seatStatus = pressedSeat.seatStatus.pressSeat()
            val matrix = Matrix()
            seatPhotoView.getDisplayMatrix(matrix)
            seatPhotoView.setImageBitmap(updateSeatLayout(pressedSeat))
            seatPhotoView.setDisplayMatrix(matrix)
            showToolTipView(pressedSeat)
        } else {
            seatListener.maxSeatsReached()
        }
    }

    private fun notifySeatListener(seat: SeatData) {
        if (seat.seatStatus == SeatStatus.EMPTY) {
            bookedSeats.add(seat)
        } else {
            bookedSeats.remove(seat)
        }
        SELECTED_AREA_INDEX = if (bookedSeats.size > 0) {
            bookedSeats[0].areaIndex
        } else {
            DEFAULT_AREA_INDEX
        }
        seatListener.seatTapped()
    }

    private fun showToolTipView(pressedSeat: SeatData) {
        if ((pressedSeat.seatStatus == SeatStatus.CHOSEN || pressedSeat.seatStatus == SeatStatus.SOLD || pressedSeat.seatStatus == SeatStatus.BROKEN)) {
            tooltipView.showToolTipForView(
                seatPhotoView.scale, pressedSeat,
                containerRect.left + layoutParams.leftMargin, containerRect.top
            )
        }
        rippleView.startRipple(
            seatPhotoView.scale, pressedSeat,
            containerRect.left + layoutParams.leftMargin, containerRect.top
        )
        seatPhotoView.playSoundEffect(SoundEffectConstants.CLICK)
    }

    private fun isMaximumCountNotReached(seat: SeatData): Boolean {
        if (isEditMode && bookedSeats.size >= maxSelectedSeats) {
            return true
        }
        if (seat.seatStatus == SeatStatus.EMPTY) {
            return bookedSeats.size < maxSelectedSeats

        }
        return true
    }

    fun setSeatListener(listener: SeatListener) {
        this.seatListener = listener
    }

    enum class SeatStatus {
        NONE, EMPTY, SOLD, RESERVED, BROKEN, PLACEHOLDER, UNKNOWN, BUSY, CHOSEN, INFO;

        fun pressSeat(): SeatStatus {
            if (this == EMPTY) return CHOSEN
            if (this == CHOSEN || this == RESERVED) return EMPTY
            return this
        }

        companion object {
            fun canSeatBePressed(status: SeatStatus, isEditMode: Boolean): Boolean {
                return status == EMPTY || (status == CHOSEN && !isEditMode) || (status == SOLD || status == BROKEN || status == RESERVED)
            }
        }
    }

    enum class SeatStyle {
        NONE, NORMAL, BARSEAT, HANDICAP, COMPANION, UNKNOWN;

        companion object {
            fun canSeatBePressed(style: SeatStyle): Boolean {
                return style != NONE
            }
        }
    }

    enum class TableStyle {
        NONE, SINGLE, PAIR_LEFT, PAIR_RIGHT, SIDE_TABLE_LEFT, SIDE_TABLE_RIGHT, LONG_LEFT, LONG_CENTER, LONG_RIGHT, LONG_GAP, LONG_GAP_LEFT, LONG_GAP_RIGHT, UNKNOWN
    }

    private inner class MatrixChangeListener : OnMatrixChangedListener {

        override fun onMatrixChanged(rect: RectF) {
            tooltipView.removeViews()
            containerRect = rect
            if (seatPhotoView.scale == SeatPhotoViewAttacher.DEFAULT_MIN_SCALE) {
                zoomButton?.tag = "ZoomedIn"
                zoomButton?.setImageDrawable(seatPhotoView.zoomInDrawable)
            } else if (seatPhotoView.scale > SeatPhotoViewAttacher.DEFAULT_MIN_SCALE) {
                zoomButton?.tag = "ZoomedOut"
                zoomButton?.setImageDrawable(seatPhotoView.zoomOutDrawable)
            }
        }
    }

    private val zoomClickListener = View.OnClickListener {
        if (zoomButton?.tag == "ZoomedOut") {
            seatPhotoView.setScale(SeatPhotoViewAttacher.DEFAULT_MIN_SCALE, true)
            zoomButton?.tag = "ZoomedIn"
            zoomButton?.setImageDrawable(seatPhotoView.zoomInDrawable)
        } else if (zoomButton?.tag == "ZoomedIn") {
            seatPhotoView.setScale(SeatPhotoViewAttacher.DEFAULT_MID_SCALE, true)
            zoomButton?.tag = "ZoomedOut"
            zoomButton?.setImageDrawable(seatPhotoView.zoomOutDrawable)
        }
    }

    private inner class PhotoTapListener : OnPhotoTapListener {
        override fun onPhotoTap(
            view: ImageView, percentX: Float, percentY: Float, tX: Float,
            tY: Float
        ) {
            clickX = Math.floor((percentX * bitmapWidth).toDouble()).toFloat()
            clickY = Math.floor((percentY * bitmapHeight).toDouble()).toFloat()
            clickScheme()
        }
    }

    private inner class Screen(totalWidth: Float) {
        private val screenWidth: Float
        private val screenHeight: Float = DensityUtil.dip2px(context, 5f)
        private val left: Float
        private val top: Float
        private val cornerRadius: Float
        private val textOffsetX: Float
        private val screenPaint: Paint
        private val screenTextPaint: Paint
        private val backgroundPaint: Paint
        private val message: String
        val baseLine: Float
        val textOffsetY: Float

        init {
            val widthCenter = totalWidth / 2
            screenWidth = totalWidth * 6 / 7
            left = widthCenter - screenWidth / 2
            top = DensityUtil.dip2px(context, 24f)
            cornerRadius = screenHeight

            backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            backgroundPaint.style = Paint.Style.FILL
            backgroundPaint.color = Color.BLACK

            screenPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            screenPaint.style = Paint.Style.FILL
            screenPaint.color = Color.WHITE

            screenTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            screenTextPaint.color = Color.WHITE
            screenTextPaint.textSize = DensityUtil.sip2px(context, 14f)
            screenTextPaint.isFilterBitmap = true
            screenTextPaint.isDither = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                screenTextPaint.letterSpacing = 0.13f
            }
            message = "SCREEN THIS WAY"
            textOffsetX = screenTextPaint.measureText(message) * 0.5f
            textOffsetY = screenTextPaint.fontMetrics.ascent * -0.8f + screenHeight
            baseLine = top + screenHeight + textOffsetY
        }

        fun drawScreen() {
            val screenRect = RectF(left, top, left + screenWidth, top + screenHeight)
            canvas.drawRoundRect(screenRect, cornerRadius, cornerRadius, screenPaint)
            screenRect.top = screenRect.top + screenHeight / 2
            screenRect.bottom = screenRect.bottom + screenHeight / 2
            canvas.drawText(
                message, screenRect.centerX() - textOffsetX,
                screenRect.bottom + textOffsetY, screenTextPaint
            )
        }
    }

    companion object {
        fun getSeatStatus(seatStatus: String): SeatStatus {
            return SeatStatus.values().firstOrNull { seatStatus.equals(it.name, ignoreCase = true) }
                ?: SeatStatus.NONE
        }

        fun getSeatStyle(seatStyle: String): SeatStyle {
            return SeatStyle.values().firstOrNull { seatStyle.equals(it.name, ignoreCase = true) }
                ?: SeatStyle.NONE
        }

        fun getTableStyle(tableStyle: String): TableStyle {
            return TableStyle.values().firstOrNull { tableStyle.equals(it.name, ignoreCase = true) }
                ?: TableStyle.NONE
        }
    }

    fun getSeatSelectionList(): MutableList<SeatSelection> {
        val seatSelectionList: MutableList<SeatSelection> = mutableListOf()
        bookedSeats.mapTo(seatSelectionList) {
            SeatSelection(it.areaIndex, it.rowIndex, it.columnIndex)
        }
        return seatSelectionList
    }

    fun isBookedSeatEmpty(): Boolean {
        return bookedSeats.isEmpty()
    }
}

inline fun <T> Array<Array<T>>.forEach2D(action: (T) -> Unit): Unit {
    for (array in this) for (element in array) action(element)
}
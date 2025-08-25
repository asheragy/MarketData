package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix

data class ViewportPayload(val matrix: Matrix, val version: Long = System.nanoTime())
package org.cerion.stockcharts.ui.charts.compose

import android.graphics.Matrix
import org.cerion.marketdata.core.charts.StockChart

data class ViewportPayload(val matrix: Matrix, val version: Long = System.nanoTime())

data class ChartModel(val value: StockChart, val version: Long = System.nanoTime())

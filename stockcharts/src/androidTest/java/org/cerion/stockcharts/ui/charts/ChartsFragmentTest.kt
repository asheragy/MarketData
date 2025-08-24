package org.cerion.stockcharts.ui.charts

import android.widget.FrameLayout
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.core.view.get
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.mikephil.charting.charts.LineChart
import fakes.FakePreferenceRepository
import fakes.FakePriceHistoryDataSource
import fakes.FakePriceListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cerion.marketdata.webclients.PriceHistoryDataSource
import org.cerion.stockcharts.R
import org.cerion.stockcharts.repository.CachedPriceListRepository
import org.cerion.stockcharts.repository.PreferenceRepository
import org.cerion.stockcharts.repository.PriceListRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

val testModules = module {
    single<PreferenceRepository> { FakePreferenceRepository() }
    single<PriceListRepository> { FakePriceListRepository() }
    single<PriceHistoryDataSource> { FakePriceHistoryDataSource() }
    single { CachedPriceListRepository(get(), get()) }
}

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ChartsFragmentTest : KoinTest {

    @Before
    fun before() {
        loadKoinModules(listOf(testModules))
    }

    @Test
    fun chartsFragment_defaults() = runTest {
        val scenario = launchFragmentInContainer<ChartsFragment>(null, R.style.AppTheme)

        // 2 charts by default
        scenario.onFragment {
            val rv = it.view?.findViewById<RecyclerView>(R.id.recycler_view)!!
            Assert.assertEquals(2, rv.adapter!!.itemCount)
        }
    }

    @Test
    fun chartsFragment_addOverlay() = runTest {
        val scenario = launchFragmentInContainer<ChartsFragment>(null, R.style.AppTheme)

        // Click first item -> add new overlay -> save
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.add_overlay)).perform(click())
        onView(withId(R.id.save)).perform(click())

        scenario.onFragment {
            val rv = it.view?.findViewById<RecyclerView>(R.id.recycler_view)!!
            val frame = rv[0].findViewById<FrameLayout>(R.id.chart_frame)
            val chart = frame[0] as LineChart
            Assert.assertEquals(2, chart.data!!.dataSetCount)
        }
    }

    @Test
    fun chartsFragment_addChart() = runTest {
        val scenario = launchFragmentInContainer<ChartsFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            val menuItem = ActionMenuItem(it.context, 0, R.id.add_price, 0, 0, null)
            it.onOptionsItemSelected(menuItem)
        }

        // 3 after adding
        scenario.onFragment {
            val rv = it.view?.findViewById<RecyclerView>(R.id.recycler_view)!!
            Assert.assertEquals(3, rv.adapter!!.itemCount)
        }
    }

}
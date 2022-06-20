import org.cerion.marketdata.core.platform.KMPDate

fun main() {
    val date = KMPDate(2000,2,3)
    println(date.toISOString())
}
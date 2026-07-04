package train

/*
sealed interface BucketStrategy {
    data class Quantiles(
        val count: Int = 5
    ) : BucketStrategy

    data class FixedRanges(
        val ranges: List<BucketRange>
    ) : BucketStrategy

// For boolean or small dist buckets
// TODO should return a label/Int pair
    data class Custom(
        val assign: (Double) -> Int?
    ) : BucketStrategy

    data class SplitBySignQuantiles(
        val negativeBuckets: Int,
        val positiveBuckets: Int,
        val zeroBucket: Int? = null
    ) : BucketStrategy
}

data class BucketRange(
    val bucket: Int,
    val minInclusive: Double? = null,
    val maxExclusive: Double? = null,
    val label: String? = null
)

example
    buckets = BucketStrategy.FixedRanges(
        listOf(
            BucketRange(bucket = 1, minInclusive = 0.0, maxExclusive = 30.0, label = "Oversold"),
            BucketRange(bucket = 2, minInclusive = 30.0, maxExclusive = 40.0, label = "Low"),
            BucketRange(bucket = 3, minInclusive = 40.0, maxExclusive = 60.0, label = "Neutral"),
            BucketRange(bucket = 4, minInclusive = 60.0, maxExclusive = 70.0, label = "High"),
            BucketRange(bucket = 5, minInclusive = 70.0, maxExclusive = 100.01, label = "Overbought")
        )
    )

 */
data class Bucket(private val list: List<Pair<Float, Float>>) {
    val rangeStart = list.first().first
    val rangeEnd = list.last().first

    val averageInd = list.map { it.first }.average()
    val averageGain = list.map { it.second }.average()
    val medianGain = list.map { it.second }.median().toDouble()
    val wins = list.count { it.second > 0 }
    val winRate = 100 * wins.toDouble() / list.size

    var rank = 0
}

fun createBuckets(results: List<Pair<Float, Float>>, split: Int): List<Bucket> {
    val buckets = results.sortedBy { it.first }.splitIntoExactly(split).map { Bucket(it) }

    buckets.sortedBy { it.averageGain }.forEachIndexed { index, bucket ->
        bucket.rank += index + 1
    }
    buckets.sortedBy { it.medianGain }.forEachIndexed { index, bucket ->
        bucket.rank += index + 1
    }
    buckets.sortedBy { it.winRate }.forEachIndexed { index, bucket ->
        bucket.rank += index + 1
    }

    buckets.sortedWith( compareByDescending<Bucket> { it.rank }
        .thenByDescending { it.averageGain + it.medianGain }).forEachIndexed { index, bucket ->
            bucket.rank = index + 1
    }

    return buckets
}

fun <T> List<T>.splitIntoExactly(parts: Int): List<List<T>> {
    val baseSize = size / parts
    val remainder = size % parts

    var index = 0

    return List(parts) { part ->
        val partSize = baseSize + if (part < remainder) 1 else 0
        subList(index, index + partSize).also {
            index += partSize
        }
    }
}

// TODO FloatArray ext method
private fun List<Float>.median(): Float {
    if (isEmpty()) return Float.NaN

    val sorted = this.sorted()

    val middle = sorted.size / 2

    return if (sorted.size % 2 == 1) {
        sorted[middle]
    } else {
        (sorted[middle - 1] + sorted[middle]) / 2f
    }
}

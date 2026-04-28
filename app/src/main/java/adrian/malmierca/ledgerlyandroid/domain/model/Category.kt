package adrian.malmierca.ledgerlyandroid.domain.model

enum class Category(val key: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    BILLS("Bills"),
    OTHER("Other");

    companion object {
        fun fromKey(key: String): Category =
            entries.firstOrNull { it.key == key } ?: OTHER
    }
}

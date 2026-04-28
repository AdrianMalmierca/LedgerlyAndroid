package adrian.malmierca.ledgerlyandroid.domain.model

import java.util.Date
import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val amount: Double = 0.0,
    val date: Date = Date(),
    val category: String = "Other",
    val userId: String = ""
)
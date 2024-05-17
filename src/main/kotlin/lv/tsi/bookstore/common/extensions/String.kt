package lv.tsi.bookstore.common.extensions

import java.util.*

fun String.toCapitalCase(): String {
    return this.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

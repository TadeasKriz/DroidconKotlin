package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    // To select a specific time zone, use `TimeZone.of("TIMEZONE_ID")`.
    val conferenceTimeZone = TimeZone.UTC

    object Firestore {
        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"
        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019"
        const val collectionName = "sponsors-sf-2019"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {
        const val scheduleId = "jmuc9diq"
    }
}

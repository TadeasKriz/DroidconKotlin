package co.touchlab.droidcon.domain.entity

import kotlinx.datetime.LocalDateTime

class Session(
    override val id: Id,
    val title: String,
    val description: String,
    val startsAt: LocalDateTime,
    val endsAt: LocalDateTime,
    val isServiceSession: Boolean,
    val room: Room.Id,
    val speakers: List<Profile.Id>,
    var isAttending: Boolean,
    var feedback: Feedback?,
): DomainEntity<Session.Id>() {
    data class Id(val value: String)

    data class Feedback(
        val rating: Int,
        val comment: String,
        val isSent: Boolean,
    )
}

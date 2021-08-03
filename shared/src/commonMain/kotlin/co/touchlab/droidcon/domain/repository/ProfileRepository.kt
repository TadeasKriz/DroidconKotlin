package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session

interface ProfileRepository: Repository<Profile.Id, Profile> {

    suspend fun getSpeakersBySession(id: Session.Id): List<Profile>

    suspend fun setSessionSpeakers(session: Session, speakers: List<Profile.Id>)
}
package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.domain.service.fromConferenceDateTime
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto.LinkType
import co.touchlab.droidcon.domain.service.impl.dto.SponsorSessionsDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

@OptIn(ExperimentalSettingsApi::class)
class DefaultSyncService(
    private val logger: Kermit,
    private val settings: ObservableSettings,
    private val dateTimeService: DateTimeService,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val seedDataSource: DataSource,
    private val apiDataSource: DataSource,
): SyncService {
    private companion object {
        // MARK: Settings keys
        private const val LOCAL_REPOSITORIES_SEEDED_KEY = "LOCAL_REPOSITORIES_SEEDED"
        private const val LAST_SESSIONIZE_SYNC_KEY = "LAST_SESSIONIZE_SYNC_TIME"

        // MARK: Delays
        // 5 minutes
        private const val SESSIONIZE_SYNC_POLL_DELAY: Long = 5 * 60 * 1000
        // 2 hours
        private const val SESSIONIZE_SYNC_NEXT_DELAY: Long = 2 * 60 * 60 * 1000
        // 5 minutes
        private const val RSVP_SYNC_DELAY: Long = 5 * 60 * 1000
        // 5 minutes
        private const val FEEDBACK_SYNC_DELAY: Long = 5 * 60 * 1000
    }

    private var isLocalRepositoriesSeeded: Boolean
        get() = settings[LOCAL_REPOSITORIES_SEEDED_KEY, false]
        set(value) {
            settings[LOCAL_REPOSITORIES_SEEDED_KEY] = value
        }

    private var lastSessionizeSync: Instant?
        get() = settings.getLongOrNull(LAST_SESSIONIZE_SYNC_KEY)?.let { Instant.fromEpochMilliseconds(it) }
        set(value) {
            settings[LAST_SESSIONIZE_SYNC_KEY] = value?.toEpochMilliseconds()
        }

    override suspend fun runSynchronization() {
        seedLocalRepositoriesIfNeeded()

        coroutineScope {
            launch {
                while (isActive) {
                    val lastSessionizeSync = lastSessionizeSync
                    // If this is the first Sessionize sync or if the last sync occurred more than 2 hours ago.
                    if (lastSessionizeSync == null || lastSessionizeSync <= dateTimeService.now().minus(2, DateTimeUnit.HOUR)) {
                        logger.d { "Will sync all repositories from API data source." }
                        updateRepositoriesFromDataSource(apiDataSource)
                        logger.d { "Sync successful, waiting for next sync in $SESSIONIZE_SYNC_NEXT_DELAY ms." }
                        this@DefaultSyncService.lastSessionizeSync = dateTimeService.now()
                        delay(SESSIONIZE_SYNC_NEXT_DELAY)
                    } else {
                        logger.d { "The sync didn't happen, so we'll try again in a short while ($SESSIONIZE_SYNC_POLL_DELAY ms)." }
                        delay(SESSIONIZE_SYNC_POLL_DELAY)
                    }
                }
            }

            launch {
                while (isActive) {
                    // TODO: Send RSVPs to a remote server.
                    delay(RSVP_SYNC_DELAY)
                }
            }

            launch {
                while (isActive) {
                    // TODO: Send Feedbacks to a remote server.
                    delay(FEEDBACK_SYNC_DELAY)
                }
            }
        }
    }

    private suspend fun seedLocalRepositoriesIfNeeded() {
        if (isLocalRepositoriesSeeded) {
            return
        }

        updateRepositoriesFromDataSource(seedDataSource)

        isLocalRepositoriesSeeded = true
    }

    private suspend fun updateRepositoriesFromDataSource(dataSource: DataSource) {
        updateSpeakersFromDataSource(dataSource)
        updateScheduleFromDataSource(dataSource)
        updateSponsorsFromDataSource(dataSource)
    }

    private suspend fun updateSpeakersFromDataSource(dataSource: DataSource) {
        val speakerDtos = dataSource.getSpeakers()
        val profiles = speakerDtos.map(::profileFactory)

        // Remove deleted speakers.
        profileRepository.all().map { it.id }
            .subtract(profiles.map { it.id })
            .forEach { profileRepository.remove(it) }

        profiles.forEach {
            profileRepository.addOrUpdate(it)
        }
    }

    private suspend fun updateScheduleFromDataSource(dataSource: DataSource) {
        val days = dataSource.getSchedule()
        val roomDtos = days.flatMap { it.rooms }

        val rooms = roomDtos.map { room ->
            Room(
                id = Room.Id(room.id),
                name = room.name,
            )
        }

        val sessionsAndSpeakers = roomDtos.flatMap { room ->
            room.sessions.map { dto ->
                Session(
                    dateTimeService = dateTimeService,
                    id = Session.Id(dto.id),
                    title = dto.title,
                    description = dto.description,
                    startsAt = LocalDateTime.parse(dto.startsAt).fromConferenceDateTime(dateTimeService),
                    endsAt = LocalDateTime.parse(dto.endsAt).fromConferenceDateTime(dateTimeService),
                    isServiceSession = dto.isServiceSession,
                    room = Room.Id(dto.roomID),
                    isAttending = false,
                    feedback = null,
                ) to dto.speakers.map { Profile.Id(it.id) }
            }
        }

        // Remove deleted rooms.
        roomRepository.all().map { it.id }
            .subtract(rooms.map { it.id })
            .forEach { roomRepository.remove(it) }

        rooms.forEach { room ->
            roomRepository.addOrUpdate(room)
        }

        // Remove deleted sessions.
        sessionRepository.all()
            .map { it.id }
            .subtract(sessionsAndSpeakers.map { it.first.id })
            .forEach { sessionRepository.remove(it) }

        sessionsAndSpeakers.forEach { (updatedSession, speakers) ->
            val existingSession = sessionRepository.find(updatedSession.id)
            if (existingSession != null) {
                updatedSession.isAttending = existingSession.isAttending
                updatedSession.feedback = existingSession.feedback
                sessionRepository.update(updatedSession)
            } else {
                sessionRepository.add(updatedSession)
            }

            profileRepository.setSessionSpeakers(updatedSession, speakers)
        }
    }

    private suspend fun updateSponsorsFromDataSource(dataSource: DataSource) {
        val sponsorSessions = dataSource.getSponsorSessions().flatMap { it.sessions }.associateBy { it.id }
        val sponsors = dataSource.getSponsors()

        val sponsorGroupsToSponsorDtos = sponsors.groups.map { group ->
            val groupName = (group.name.split('/').lastOrNull() ?: group.name)
                .split(' ').joinToString(" ") { it.capitalize() }

            SponsorGroup(
                id = SponsorGroup.Id(groupName),
                displayPriority = group.fields.displayOrder.integerValue.toInt(),
                isProminent = group.fields.prominent?.booleanValue ?: false
            ) to group.fields.sponsors.arrayValue.values.map { it.mapValue.fields }
        }

        val sponsorsAndRepresentativeIds = sponsorGroupsToSponsorDtos.flatMap { (group, sponsorDtos) ->
            sponsorDtos.map { sponsorDto ->
                val sponsorSession = sponsorDto.sponsorId?.stringValue?.let(sponsorSessions::get)
                val representativeIds = sponsorSession?.speakers?.map { Profile.Id(it.id) } ?: emptyList()

                Sponsor(
                    id = Sponsor.Id(sponsorDto.name.stringValue, group.name),
                    hasDetail = sponsorSession != null,
                    description = sponsorSession?.description,
                    icon = Url(sponsorDto.icon.stringValue),
                    url = Url(sponsorDto.url.stringValue),
                ) to representativeIds
            }
        }

        sponsorRepository.all().map { it.id }
            .subtract(sponsorsAndRepresentativeIds.map { it.first.id })
            .forEach { sponsorRepository.remove(it) }

        sponsorGroupRepository.all().map { it.id }
            .subtract(sponsorGroupsToSponsorDtos.map { it.first.id })
            .forEach { sponsorGroupRepository.remove(it) }

        sponsorGroupsToSponsorDtos.forEach { (group, _) ->
            sponsorGroupRepository.addOrUpdate(group)
        }

        sponsorsAndRepresentativeIds.forEach { (sponsor, representativeIds) ->
            sponsorRepository.addOrUpdate(sponsor)

            profileRepository.setSponsorRepresentatives(sponsor, representativeIds)
        }
    }

    private fun profileFactory(speakerDto: SpeakersDto.SpeakerDto): Profile {
        val groupedLinks = speakerDto.links.filter { it.url.isNotBlank() } .groupBy { it.linkType }
        return Profile(
            id = Profile.Id(speakerDto.id),
            fullName = speakerDto.fullName,
            bio = speakerDto.bio,
            tagLine = speakerDto.tagLine,
            profilePicture = speakerDto.profilePicture?.let(::Url),
            twitter = groupedLinks[LinkType.Twitter]?.firstOrNull()?.url?.let(::Url),
            linkedIn = groupedLinks[LinkType.LinkedIn]?.firstOrNull()?.url?.let(::Url),
            website = (
                groupedLinks[LinkType.CompanyWebsite] ?: groupedLinks[LinkType.Blog] ?: groupedLinks[LinkType.Other]
            )?.firstOrNull()?.url?.let(::Url),
        )
    }

    interface DataSource {
        enum class Kind {
            Seed, Api
        }

        suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto>

        suspend fun getSchedule(): List<ScheduleDto.DayDto>

        suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto>

        suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto
    }
}
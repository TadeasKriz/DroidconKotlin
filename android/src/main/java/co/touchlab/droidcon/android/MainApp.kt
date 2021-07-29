package co.touchlab.droidcon.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.initKoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.dsl.module
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class MainApp : Application() {

    @OptIn(ExperimentalTime::class)
    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS", Context.MODE_PRIVATE)
                }

                single<SessionGateway> {
                    object: SessionGateway {
                        private val items = listOf(
                            ScheduleItem(
                                session = Session(
                                    id = Session.Id("juli-session"),
                                    title = "Juli Session",
                                    description = "Best session",
                                    startsAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                    endsAt = (Clock.System.now() + Duration.hours(2)).toLocalDateTime(TimeZone.currentSystemDefault()),
                                    isServiceSession = false,
                                    room = Room.Id(1),
                                    speakers = listOf(
                                        Profile.Id("juli")
                                    ),
                                    isAttending = false,
                                    feedback = null,
                                ),
                                isInConflict = false,
                                room = Room(
                                    id = Room.Id(1),
                                    name = "Track 1 - Blue Room",
                                ),
                                speakers = listOf(
                                    Profile(
                                        id = Profile.Id("juli"),
                                        fullName = "Juli a Tabi",
                                        bio = null,
                                        tagLine = null,
                                        profilePicture = null,
                                        twitter = null,
                                        linkedIn = null,
                                        website = null,
                                    )
                                )
                            ),
                            ScheduleItem(
                                session = Session(
                                    id = Session.Id("juli-session-2"),
                                    title = "Juli Session 2",
                                    description = "Best session",
                                    startsAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                    endsAt = (Clock.System.now() + Duration.hours(2)).toLocalDateTime(TimeZone.currentSystemDefault()),
                                    isServiceSession = false,
                                    room = Room.Id(2),
                                    speakers = listOf(
                                        Profile.Id("juli")
                                    ),
                                    isAttending = true,
                                    feedback = null,
                                ),
                                isInConflict = false,
                                room = Room(
                                    id = Room.Id(2),
                                    name = "Track 2 - Blue Room",
                                ),
                                speakers = listOf(
                                    Profile(
                                        id = Profile.Id("juli"),
                                        fullName = "Juli a Tabi",
                                        bio = null,
                                        tagLine = null,
                                        profilePicture = null,
                                        twitter = null,
                                        linkedIn = null,
                                        website = null,
                                    )
                                )
                            ),
                        )

                        override suspend fun getSchedule(): List<ScheduleItem> {
                            return items
                        }

                        override suspend fun getAgenda(): List<ScheduleItem> {
                            return items.filter { it.session.isAttending }
                        }

                        override suspend fun getScheduleItem(id: Session.Id): ScheduleItem {
                            return items.single { it.session.id == id }
                        }

                        override fun observeScheduledItem(id: Session.Id): Flow<ScheduleItem> {
                            return flow { emit(getScheduleItem(id)) }
                        }
                    }
                }

                single<SettingsGateway> {
                    object: SettingsGateway {
                        val settings = MutableStateFlow(Settings(true, true))
                        override fun settings(): Settings {
                            return settings.value
                        }

                        override fun observeSettings(): Flow<Settings> {
                            return settings
                        }

                        override suspend fun setFeedbackEnabled(enabled: Boolean) {
                            settings.value = settings.value.copy(isFeedbackEnabled = enabled)
                        }

                        override suspend fun setRemindersEnabled(enabled: Boolean) {
                            settings.value = settings.value.copy(isRemindersEnabled = enabled)
                        }
                    }
                }
            }
        )
    }
}

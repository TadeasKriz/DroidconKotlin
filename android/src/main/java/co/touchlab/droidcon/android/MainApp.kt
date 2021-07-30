package co.touchlab.droidcon.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.android.service.impl.DefaultDateTimeFormatterViewService
import co.touchlab.droidcon.android.service.impl.DefaultParseUrlViewService
import co.touchlab.droidcon.application.composite.AboutItem
import co.touchlab.droidcon.application.composite.Settings
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.initKoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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

                single<ParseUrlViewService> {
                    DefaultParseUrlViewService()
                }

                single<DateTimeFormatterViewService> {
                    DefaultDateTimeFormatterViewService()
                }

                // TODO: Remove mock data when real gateway is implemented.
                single<SessionGateway> {
                    object: SessionGateway {
                        private val items = listOf(
                            ScheduleItem(
                                session = Session(
                                    id = Session.Id("juli-session"),
                                    title = "Juli Session",
                                    description = "Best session",
                                    startsAt = (Clock.System.now() + Duration.hours(2)).toLocalDateTime(TimeZone.currentSystemDefault()),
                                    endsAt = (Clock.System.now() + Duration.hours(4)).toLocalDateTime(TimeZone.currentSystemDefault()),
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
                                        bio = "Mauris venenatis tempus magna et accumsan. Morbi mi urna, rutrum in urna in, rhoncus elementum elit. Nam ornare suscipit dolor, ut elementum nisi tempor ut.",
                                        tagLine = "Android developer",
                                        profilePicture = Url("https://juliajakubcova.com/img/image-01.jpg"),
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
                                    isAttending = false,
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

                // TODO: Remove mock data when real gateway is implemented.
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

                // TODO: Remove mock data when real repository is implemented.
                single<AboutRepository> {
                    object: AboutRepository {
                        override suspend fun getAboutItems(): List<AboutItem> {
                            return listOf(
                                AboutItem(
                                    title = "About Touchlab",
                                    detail = "This is some long text about Touchlab.\n\nhttps://touchlab.com",
                                    icon = R.drawable.about_touchlab.toString(),
                                ),
                                AboutItem(
                                    title = "Droidcon App",
                                    detail = "This is some long text about Droidcon App.",
                                    icon = R.drawable.about_kotlin.toString(),
                                ),
                                AboutItem(
                                    title = "Droidcon",
                                    detail = "This is some long text about Droidcon.",
                                    icon = R.drawable.about_droidcon.toString(),
                                ),
                            )
                        }
                    }
                }

                // TODO: Remove mock data when real repository is implemented.
                single<RoomRepository> {
                    object: RoomRepository {
                        override suspend fun get(id: Room.Id): Room {
                            return Room(
                                id = Room.Id(2),
                                name = "Track 2 - Blue Room",
                            )
                        }

                        override fun observe(id: Room.Id): Flow<Room> {
                            return flowOf(Room(
                                id = Room.Id(2),
                                name = "Track 2 - Blue Room",
                            ))
                        }

                        override fun observe(entity: Room): Flow<Room> {
                            return flowOf(entity)
                        }

                        override suspend fun all(): List<Room> {
                            TODO("Not yet implemented")
                        }

                        override fun observeAll(): Flow<List<Room>> {
                            TODO("Not yet implemented")
                        }

                        override suspend fun add(entity: Room) {
                            TODO("Not yet implemented")
                        }

                        override suspend fun remove(entity: Room): Boolean {
                            TODO("Not yet implemented")
                        }

                        override suspend fun remove(id: Room.Id): Boolean {
                            TODO("Not yet implemented")
                        }

                        override suspend fun update(entity: Room) {
                            TODO("Not yet implemented")
                        }
                    }
                }
            }
        )
    }
}

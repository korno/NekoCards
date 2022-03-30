package uk.me.mikemike.kotlinwani

/*

KotlinWani - Kotlin Library for accessing the WaniKani Rest API.

Copyright 2022 Michael Hall

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


typealias UserResource = Resource<User>
typealias UserRequestResult = RequestResult<UserResource>

@Serializable
enum class LessonPresentationOrder {
    @SerialName("ascending_level_then_subject")
    ASCLEVEL_THEN_SUBJECT,
    @SerialName("shuffled")
    SHUFFLED,
    @SerialName("ascending_level_then_shuffled")
    ASCLEVEL_THEN_SHUFFLED
}

@Serializable
enum class SubscriptionType {
    @SerialName("free")
    FREE,
    @SerialName("recurring")
    RECURRING,
    @SerialName("lifetime")
    LIFETIME
}




@Serializable
data class UserSubscription(
    @SerialName("active")
    val active: Boolean,
    @SerialName("max_level_granted")
    val maxLevel: Int,
    @SerialName("period_ends_at")
    val endDate: Instant?,
    @SerialName("type")
    val type: SubscriptionType
)


@Serializable
data class UserPreferences(
    @SerialName("lessons_autoplay_audio")
    val autoPlayLessonAudio: Boolean,
    @SerialName("lessons_batch_size")
    val lessonBatchSize: Int,
    @SerialName("lessons_presentation_order")
    val lessonPresentationOrder: LessonPresentationOrder = LessonPresentationOrder.ASCLEVEL_THEN_SHUFFLED,
    @SerialName("reviews_display_srs_indicator")
    val displaySRSIndicator: Boolean
)

@Serializable
data class User(

    @SerialName("id")
    val id: String,
    @SerialName("username")
    val userName: String,
    @SerialName("level")
    val level: Int,
    @SerialName("profile_url")
    val profileUrl: String,
    @SerialName("started_at")
    val createdDate: Instant,
    @SerialName("current_vacation_started_at")
    val holidayStartedDate: Instant?,
    @SerialName("subscription")
    val subscription: UserSubscription,
    @SerialName("preferences")
    val preferences: UserPreferences

){
    val isOnHoliday: Boolean get() {return holidayStartedDate != null}
}


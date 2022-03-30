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

typealias SubjectResource = Resource<Subject>
typealias SubjectResourceRequestResult = RequestResult<SubjectResource>

@Serializable
enum class AuxiliaryMeaningType {
    @SerialName("whitelist")
    WHITELIST,
    @SerialName("blacklist")
    BLACKLIST
}


@Serializable
enum class ReadingType {
    @SerialName("kunyomi")
    KUNYOMI,
    @SerialName("nanori")
    NANORI,
    @SerialName("onyomi")
    ONYOMI,
    // Used as the default for vocabulary readings
    VOCABULARY
}

@Serializable
data class Meaning (
    @SerialName("meaning")
    val meaning: String,
    @SerialName("primary")
    val primary: Boolean,
    @SerialName("accepted_answer")
    val acceptedAnswer: Boolean
)

@Serializable
data class Reading(
    @SerialName("reading")
    val reading: String,
    @SerialName("primary")
    val primary: Boolean,
    @SerialName("accepted_answer")
    val acceptedAnswer: Boolean,
    @SerialName("type")
    // if none is present assume it is a vocbaular reading
    // as per the v2 spec this is not present in kanji data
    val type: ReadingType = ReadingType.VOCABULARY
)


@Serializable
data class AuxiliaryMeaning(
    @SerialName("meaning")
    val meaning: String,
    @SerialName("type")
    val type: AuxiliaryMeaningType
)


@Serializable
enum class ContentType{
    @SerialName("image/png")
    IMAGE_PNG,
    @SerialName("image/svg+xml")
    IMAGE_SVGXML
}

@Serializable
data class CharacterImage(
    @SerialName("url")
    val url: String,
    @SerialName("content_type")
    val type: ContentType,
    @SerialName("metadata")
    val metaData: MetaData
    )

@Serializable
data class ContextSentence(
    @SerialName("en")
    val english: String,
    @SerialName("ja")
    val japanse: String
)


@Serializable
data class MetaData(
    @SerialName("color")
    val color: String = "",
    @SerialName("dimensions")
    val dimensions: String = "",
    @SerialName("style_name")
    val styleName: String = "",
    @SerialName("inline_styles")
    val inlineStyles: Boolean = false
)

// A "mega" class that can accommodate radicals, kanji and vocabulary subjects
// As Wanikani uses the same endpoint to return all these kinds of subjects,
// we wont know which type is returned until we get the response.
// We could parse the object part of the response for the type but that means the
// the api would have to provide methods for each subject type and handle
// the situations when a different type is returned than the caller
// expected (i.e the caller passes
// a subject id of 1 to the api expecting a kanji subject to be returned but
// a radical subject is returned instead)
// Nullable strings (according to the wanikani api documents) have
// default values of empty strings. Nullable dates are defined as nullable properties
@Serializable
data class Subject(

    @SerialName("created_at")
    val createdDate: Instant,
    @SerialName("characters")
    val characters: String = "",
    @SerialName("document_url")
    val documentUrl: String,
    @SerialName("hidden_at")
    val hiddenDate: Instant?,
    @SerialName("lesson_position")
    val lessonPosition: Integer,
    @SerialName("level")
    val level: Integer,
    @SerialName("meaning_mnemonic")
    val meaningMnemonic: String,
    @SerialName("meanings")
    val meanings: List<Meaning> = listOf(),
    @SerialName("slug")
    val slug: String,
    @SerialName("spaced_repetition_system_id")
    val spacedRepitionId: Int,
    @SerialName("auxiliary_meanings")
    val auxiliaryMeanings: List<AuxiliaryMeaning> = listOf(),
    @SerialName("meaning_hint")
    val meaningHint: String = "",
    @SerialName("reading_hint")
    val readingHint: String = "",
    @SerialName("amalgamation_subject_ids")
    val relatedSubjectIds: List<Int> = listOf(),
    @SerialName("character_images")
    val characterImages: List<CharacterImage> = listOf(),
    @SerialName("component_subject_ids")
    val componentRadicalsIds: List<Int> = listOf(),
    @SerialName("reading_mnemonic")
    val readingMnemonic: String ="",
    @SerialName("visually_similar_subject_ids")
    val visualSimilarSubjectIds: List<Int> = listOf(),
    @SerialName("readings")
    val readings: List<Reading> = listOf(),
    val partsOfSpeech: List<String> = listOf(),
    @SerialName("context_sentences")
    val contextSentences: List<ContextSentence> = listOf()



)
{
    val isHidden: Boolean = hiddenDate == null
}
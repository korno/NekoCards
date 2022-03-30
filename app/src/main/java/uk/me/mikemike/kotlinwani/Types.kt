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
import uk.me.mikemike.nekocards.Empty

enum class RequestStatus { LOADING, FAILED, LOADED}
class RequestResult<T> (var data: T?, var status: RequestStatus = RequestStatus.LOADING, var error: String = String.Empty )

@Serializable
data class Resource<T>(
    @SerialName("object")
    val obj: String,
    @SerialName("data_updated_at")
    val lastUpdateDate: Instant? = null,
    @SerialName("url")
    val url: String,
    @SerialName("data")
    val data: T
)
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

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class APIResultValue { UNAUTHORISED, NOTFOUND, OTHER, TOOMANYREQUESTS, SUCCESS, FORBIDDEN, MALFORMEDREQUEST, SERVERERROR,
    SERVICEUNAVIALABLE, JSONDECODERROR}


data class APIResult<T>(val data: T?, val result: APIResultValue = APIResultValue.SUCCESS, val message: String = ""){
    val error: Boolean = result != APIResultValue.SUCCESS
}


class RestAPIRequest(
    method: Int, url: String, private val apiKey: String, cont: Continuation<APIResult<String>>
) :
    StringRequest(
        method, url,

        Response.Listener { response ->
            cont.resume(APIResult(response));
        },

        Response.ErrorListener {

            var errorString : String = it.networkResponse.statusCode.toString()
            val error: APIResultValue = when (it.networkResponse.statusCode) {
                401 -> APIResultValue.UNAUTHORISED
                404 -> APIResultValue.NOTFOUND
                429 -> APIResultValue.TOOMANYREQUESTS
                403 -> APIResultValue.FORBIDDEN
                422 ->  {errorString = it.networkResponse.data.toString()
                    APIResultValue.MALFORMEDREQUEST}
                500 -> APIResultValue.SERVERERROR
                503 -> APIResultValue.SERVICEUNAVIALABLE
                else -> APIResultValue.OTHER
            }
            cont.resume(APIResult(errorString, error))
        }) {
    override fun getHeaders(): MutableMap<String, String> {
        return RestAPIRequest.createWaniKaniHeader(apiKey)
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return super.parseNetworkResponse(response)
    }

    companion object {
        public fun createWaniKaniHeader(apiKey: String): MutableMap<String, String> {
            val headerMap = mutableMapOf<String, String>()
            headerMap["Content-Type"] = "application/json"
            headerMap["Authorization"] = "Bearer $apiKey"
            return headerMap
        }
    }

}




class RestAPI(
    private val endPoints: APIEndPoints,
    private val volleyQueue: RequestQueue,
    private val apiKey: String,
    private val json: Json
) {

    public suspend fun getUser(): APIResult<UserResource> {
        val result = getUserStringData()
        return if(result.error){
            APIResult(null, result.result, result.data!!)
        } else{
            try{
                val user: UserResource = json.decodeFromString<UserResource>(result.data!!)
                APIResult(user)
            } catch(e: Exception){
                APIResult(null, APIResultValue.JSONDECODERROR, e.message!!)
            }
        }
    }

    public suspend fun getSingleSubject(subjectId: Int): APIResult<SubjectResource>{
        val result = getSingleSubjectStringData(subjectId)
        return if(result.error){
            APIResult(null, result.result, result.data!!)
        } else{
            try{
               val subject: SubjectResource = json.decodeFromString(result.data!!)
                APIResult(subject)
            }catch(e: Exception){
                APIResult(null, APIResultValue.JSONDECODERROR, e.message!!)
            }
        }
    }

    public suspend fun getSingleSubjectStringData(subjectId: Int) = suspendCoroutine<APIResult<String>>{ cont ->
        val request = RestAPIRequest(
            Request.Method.GET, String.format(endPoints.singleSubject, subjectId), apiKey,
            cont
        )
        volleyQueue.add(request)
    }


    public suspend fun getUserStringData() = suspendCoroutine<APIResult<String>> { cont ->
        val request = RestAPIRequest(
            Request.Method.GET, endPoints.user, apiKey,
            cont
        )
        volleyQueue.add(request)
    }



}
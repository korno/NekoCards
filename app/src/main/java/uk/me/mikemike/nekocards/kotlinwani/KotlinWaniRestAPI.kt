package uk.me.mikemike.nekocards.kotlinwani

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import uk.me.mikemike.kotlinwani.APIEndPoints
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class APIResultValue { UNAUTHORISED, NOTFOUND, OTHER, TOOMANYREQUESTS, SUCCESS, FORBIDDEN, MALFORMEDREQUEST, SERVERERROR,
                                SERVICEUNAVIALABLE}
data class APIResult(val data: String, val result: APIResultValue = APIResultValue.SUCCESS) {
    val error: Boolean = result != APIResultValue.SUCCESS
}

class KotlinWaniRequest(
    method: Int, url: String, private val apiKey: String, cont: Continuation<APIResult>
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
        return KotlinWaniRequest.createWaniKaniHeader(apiKey)
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







class KotlinWaniRestAPI(
    private val endPoints: APIEndPoints,
    private val volleyQueue: RequestQueue,
    private val apiKey: String
) {

    public suspend fun doUserAPIRequest() = suspendCoroutine<APIResult> { cont ->
        val request = KotlinWaniRequest(
            Request.Method.GET, endPoints.user, apiKey,
            cont
        )
        volleyQueue.add(request)
    }
}
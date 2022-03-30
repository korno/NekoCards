/*MIT License

Copyright (c) 2022 Michael Hall

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package uk.me.mikemike.nekocards.kotlinwani

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uk.me.mikemike.kotlinwani.*


class KotlinWaniRepository(
    val apiKey: String,
    endPoints: APIEndPoints = APIEndPoints.Defaults,
    volleyQueue: RequestQueue,
    private val scope: CoroutineScope
) {

    private val json = Json { ignoreUnknownKeys = true }

    private val restAPI = RestAPI(endPoints, volleyQueue, apiKey, json)


    public fun getUser(): LiveData<UserRequestResult> {
        val liveData = MutableLiveData<UserRequestResult>(
            UserRequestResult(null)
        )
        scope.launch(Dispatchers.IO) {
            val apiResult = restAPI.getUser()
            if(apiResult.error){
                liveData.postValue(UserRequestResult(null, RequestStatus.FAILED, apiResult.result.toString()))
            }
            else{
                liveData.postValue(UserRequestResult(apiResult.data, RequestStatus.LOADED))
            }
        }
        return liveData;
    }

    public fun getSingleSubject(subjectId: Int): LiveData<SubjectResourceRequestResult>{
        val liveData = MutableLiveData<SubjectResourceRequestResult>(
            SubjectResourceRequestResult(null)
        )
        scope.launch(Dispatchers.IO){
            val apiResult = restAPI.getSingleSubject(subjectId)
            liveData.postValue(
                if (apiResult.error)
                    SubjectResourceRequestResult(null, RequestStatus.FAILED,
                                apiResult.message)
                else
                    SubjectResourceRequestResult(apiResult.data, RequestStatus.LOADED)
            )
        }
        return liveData
    }


}
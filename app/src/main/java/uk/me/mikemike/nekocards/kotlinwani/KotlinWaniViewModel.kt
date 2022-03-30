package uk.me.mikemike.nekocards.kotlinwani

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.android.volley.toolbox.Volley
import uk.me.mikemike.kotlinwani.APIEndPoints
import uk.me.mikemike.kotlinwani.SubjectResourceRequestResult
import uk.me.mikemike.kotlinwani.UserRequestResult

class KotlinWaniViewModel(val userAPIToken: String, val app: Application): AndroidViewModel(app) {

    val repository = KotlinWaniRepository(apiKey = userAPIToken, APIEndPoints.Defaults, Volley.newRequestQueue(app as Context), viewModelScope)

    val user: LiveData<UserRequestResult> by lazy {
        repository.getUser()
    }

    val theFirstSubject: LiveData<SubjectResourceRequestResult> by lazy {
        repository.getSingleSubject(440)
    }

    class Factory(val userAPIToken: String, val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return KotlinWaniViewModel(userAPIToken, app) as T
        }

    }

}
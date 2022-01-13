package uk.me.mikemike.nekocards

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class QuizViewModel(private val deckDataRepository: DeckDataRepository, private val deckId: Long) : ViewModel() {

    val lastResult: MutableLiveData<QuestionResult> = MutableLiveData(null)

    val quiz: MutableLiveData<MultipleChoiceQuiz> by lazy {
        val result = MutableLiveData<MultipleChoiceQuiz>(null)
        viewModelScope.launch(Dispatchers.IO){
            val deck = deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull()
            if(deck != null) {
                result.postValue(MultipleChoiceQuiz(deck, 3))
            }
        }
        result
    }

    fun answerCurrentQuestion(answer: String){
        if(quiz.value != null) {
            val q = quiz.value!!
            val result = q.answerCurrentQuestion(answer)
            quiz.postValue(q)
            lastResult.postValue(result)
        }
    }

}

class QuizViewModelFactory(private val repository: DeckDataRepository, private val deckId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuizViewModel::class.java)){
            return QuizViewModel(repository, deckId) as T
        }
        throw IllegalArgumentException("Passed ViewModel is unknown")
    }
}
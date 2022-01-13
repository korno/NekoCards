package uk.me.mikemike.nekocards

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DeckEditViewModel(val deckDataRepository: DeckDataRepository, val deckId: Long) : ViewModel() {

    val currentlyEditingDeck: MutableLiveData<DeckWithCards> by lazy {
        val c = MutableLiveData<DeckWithCards>(null)
        viewModelScope.launch(Dispatchers.IO){
            c.postValue(deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull())
        }
        c
    }


    private val _lastCardInsertId: MutableLiveData<Long> = MutableLiveData(0)
    val lastCardInsertId: LiveData<Long>
        get() =  _lastCardInsertId

    private val _lastCardDeleteId: MutableLiveData<Long> = MutableLiveData(0)
    val lastCardDeleteId: LiveData<Long>
        get() = _lastCardDeleteId


    init {
       // loadCurrentlyEditingDeck()
    }

    fun loadCurrentlyEditingDeck(){
        viewModelScope.launch(Dispatchers.IO){
            currentlyEditingDeck.postValue(deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull())
        }
    }


    fun updateCurrentlyEditingDeck(newValues:Deck): LiveData<Boolean>{
        var result = MutableLiveData<Boolean>(false)
        if(currentlyEditingDeck.value != null) {
            newValues.deckId = currentlyEditingDeck.value!!.deck.deckId
            viewModelScope.launch(Dispatchers.IO) {
                val updateResult = deckDataRepository.updateDeck(newValues) > 0
                if (updateResult) {
                    result.postValue(true)
                    currentlyEditingDeck.postValue(
                        deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull()
                    )
                } else {
                    result.postValue(false)
                }
            }
        }
        return result
    }

    fun addCardToCurrentlyEditingDeck(c: Card): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>(false)
        c.deckId = currentlyEditingDeck.value!!.deck.deckId
        viewModelScope.launch(Dispatchers.IO){
            val newRowId = deckDataRepository.insert(c)
            if(newRowId > 0){
                currentlyEditingDeck.postValue(
                    deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull()
                )
                _lastCardInsertId.postValue(newRowId)
                res.postValue(true)
            }
        }
        return res;

    }


    fun updateCard(c: Card): MutableLiveData<Boolean>{
        val result = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO){
            val updateRowCount = deckDataRepository.updateCard(c)
            if(updateRowCount > 0){
                if(c.deckId == currentlyEditingDeck.value?.deck?.deckId){
                    currentlyEditingDeck.postValue(
                        deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull())
                    result.postValue(true)
                }
            }
        }
        return result
    }

    fun deleteCard(c: Card){
        val res = MutableLiveData<Boolean>(false)
        viewModelScope.launch(Dispatchers.IO){
            val result = deckDataRepository.delete(c)
            if(result > 0){
                if(c.deckId == currentlyEditingDeck.value!!.deck.deckId) {
                    currentlyEditingDeck.postValue(
                        deckDataRepository.getDeckWIthCardsById(deckId).firstOrNull()
                    )
                }
                _lastCardDeleteId.postValue(c.cardId)
                res.postValue(true)
            }
        }
    }



}



class DeckViewModel(private val deckDataRepository: DeckDataRepository) : ViewModel() {

    val allDecks = deckDataRepository.allDecks.asLiveData()
    val lastInsertId = MutableLiveData<Long>(0)
    val lastDeleteId = MutableLiveData<Long>( 0)


    fun clearLastInsertId(){
        lastInsertId.postValue(0)
    }

    fun clearLastDeleteId(){
        lastDeleteId.postValue(0)
    }

    fun insertDeck(deck: DeckWithCards): LiveData<Long>
    {
        val res = MutableLiveData<Long>();

        viewModelScope.launch(Dispatchers.IO) {
            val result = deckDataRepository.insert(deck);
            lastInsertId.postValue(result)
            res.postValue(result)
        }

        return res
    }

    fun deleteDeck(deck: Deck): LiveData<Int>{
        val result = MutableLiveData<Int>()
        viewModelScope.launch ( Dispatchers.IO){
            val res = deckDataRepository.deleteDeck(deck)
            result.postValue(res)
            if(res > 0){
                lastDeleteId.postValue(deck.deckId)
            }
        }
        return result
    }
}


class DeckViewModelFactory(private val repository: DeckDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(DeckViewModel::class.java)){
           return DeckViewModel(repository) as T
       }
        throw IllegalArgumentException("Passed ViewModel is unknown")
    }
}

class DeckEditViewModelFactory(private val repository: DeckDataRepository, private val deckId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DeckEditViewModel::class.java)){
            return DeckEditViewModel(repository, deckId) as T
        }
        throw IllegalArgumentException("Passed ViewModel is unknown")
    }
}


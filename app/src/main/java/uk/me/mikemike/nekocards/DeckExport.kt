package uk.me.mikemike.nekocards

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DeckJsonExporter(){
    public fun export(deck: DeckWithCards):String{
        return Json.encodeToString(value = deck)
    }

    public fun export(card: Card): String{
        return Json.encodeToString(value = card)
    }

    public fun export(deck: Deck): String{
        return Json.encodeToString(value = deck)
    }


}


data class ImportResult<T>(val success:Boolean, val result: T?, val errorMessage: String = String.Empty)

class DeckJsonImporter(){

    public var lastError: String = String.Empty
        private set

    public fun importDeckWithCard(json: String): ImportResult<DeckWithCards> {
        var errorMessage = String.Empty
        var success = true
        val deckWithCards =
        try {
            Json.decodeFromString<DeckWithCards>(json)
        }
        catch(e: Exception){
            errorMessage = e.message!!
            success = false
            null
        }
        return ImportResult(success, deckWithCards, errorMessage)
    }

    public  fun < T: Any> internalImport(json: String, s: KSerializer<T>): ImportResult<T>{
        var errorMessage = String.Empty
        var success = true
        val deckWithCards =
            try {
                Json.decodeFromString(s, json)
            }
            catch(e: Exception){
                errorMessage = e.message!!
                success = false
                null
            }
        return ImportResult(success, deckWithCards, errorMessage)
    }


    public fun importDeck(json: String): ImportResult<Deck> {
        return internalImport<Deck>(json, Deck.serializer())
    }

    public fun importCard(json: String): Card? {
        return try { Json.decodeFromString<Card>(json)}catch(e: Exception){lastError = e.message!!; null}
    }


}
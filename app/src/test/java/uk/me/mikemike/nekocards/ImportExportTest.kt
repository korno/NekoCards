@file:Suppress("IllegalIdentifier")

package uk.me.mikemike.nekocards

import org.junit.Assert.assertEquals
import org.junit.Test


class ImportExportTest {

    @Test
    fun `export to json`(){
        //val deck = createTestDeck("name", "description", 20)
        val exporter = DeckJsonExporter()

        val deck = Deck(1, "Name", "Description", "A", "B")
        val card = Card(1, 1, "a", "b")


        assertEquals("Banana", exporter.export(card))
    }

    @Test
    fun `import from json`(){
        val deckjson = "{\"deckId\":1,\"name\":\"Name\",\"description\":\"Description\",\"sideAName\":\"A\",\"sideBName\":\"B\"}"
        val cardjson = "{\"cardId\":1,\"deckId\":1,\"sideA\":\"a\",\"sideB\":\"b\"}"

        val importer = DeckJsonImporter()

        val res = importer.importDeckWithCard(deckjson)

        assertEquals(false, res.success)
        assertEquals(null, res.result)




        //assertEquals(importer.lastError, res)

    }


}
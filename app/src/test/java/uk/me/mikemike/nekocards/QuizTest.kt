@file:Suppress("IllegalIdentifier")

package uk.me.mikemike.nekocards


import org.junit.Assert.*
import org.junit.Test


class QuizTest {


    private fun createDeckWithCards(cardCount: Int): DeckWithCards{
        val cards = mutableListOf<Card>()
        repeat(cardCount){
            cards.add(Card(it.toLong(),0, "A$it", "B$it"))
        }
        return DeckWithCards(Deck(0, "Deck", "Description", "sideA", "sideB"), cards)
    }

    @Test
    fun `create quiz with deck with no cards`(){
        /*
            In this test we create a quiz from a deck with no cards,
            this should finish the test right away
         */
        val deck = DeckWithCards(Deck(0, "deck", "description", "side a", "side b"), listOf())
        val quiz = MultipleChoiceQuiz(deck, 3)
        assertEquals(true, quiz.isFinished)
        assertEquals(null, quiz.currentQuestion)
    }

    @Test
    fun `create quiz with more questions than cards`(){
        /*
            In this test we create a deck with 5 cards but request a quiz with
            10 questions - the quiz should only make 5 questions
         */
        val deck = createDeckWithCards(5)
        val quiz = MultipleChoiceQuiz(deck, 3, 10)
        assertEquals(deck.cards.size, quiz.questionCount)
    }

    @Test
    fun `create quiz with more wrong answers than total cards`() {
        /*
            In this test we create a quiz with 2 cards but request  3 wrong answers
            (more than are available to be used) so there should only be 1 wrong answer
         */
        val deck = createDeckWithCards(2)
        val quiz = MultipleChoiceQuiz(deck, 3)
        assertEquals(quiz.currentQuestion!!.choices.size, 2)

    }

    @Test
    fun `create a simple valid quiz`(){
        /* Create a simple valid test */
        val deck = createDeckWithCards(5)
        val quiz = MultipleChoiceQuiz(deck, 3)
        assertEquals(quiz.questionCount, deck.cards.size)
        assertEquals(quiz.questionResults.size, 0)
        assertNotNull(quiz.currentQuestion)
        assertEquals(quiz.isFinished, false)
        // -1 because the first question has been set up already
        assertEquals(4, quiz.remainingQuestionCount)
    }


    @Test
    fun `answer one question correctly`(){

        val deck = createDeckWithCards(5)
        val quiz = MultipleChoiceQuiz(deck, 3)
        val question = quiz.currentQuestion
        val result = quiz.answerCurrentQuestion(quiz.currentQuestion!!.correctAnswer).result

        assertEquals(true, result)
        assertEquals(quiz.questionResults.size, 1)
        assertEquals(1, quiz.numberOfCorrectAnswers)
        assertEquals(0, quiz.numberOfWrongAnswers)
        assertEquals(false, quiz.isFinished)
        assertEquals(question, quiz.questionResults[0].question)
        assertEquals(true, quiz.questionResults[0].result)
        assertEquals(question!!.correctAnswer, quiz.questionResults[0].answer)
        assertEquals(3, quiz.remainingQuestionCount)
        assertEquals(true, quiz.lastQuestionAnswer!!.result)


    }

    @Test
    fun `answer one question incorrectly`(){
        val deck = createDeckWithCards(5)
        val quiz = MultipleChoiceQuiz(deck, 3)
        val question = quiz.currentQuestion
        val result = quiz.answerCurrentQuestion("this is definitely wrong").result

        assertEquals(false, result)
        assertEquals(quiz.questionResults.size, 1)
        assertEquals(false, quiz.isFinished)
        assertEquals(question, quiz.questionResults[0].question)
        assertEquals(false, quiz.questionResults[0].result)
        assertNotEquals(question!!.correctAnswer, quiz.questionResults[0].answer)
        assertEquals(3, quiz.remainingQuestionCount)
        assertEquals(0, quiz.numberOfCorrectAnswers)
        assertEquals(1, quiz.numberOfWrongAnswers)
        assertEquals(false, quiz.lastQuestionAnswer!!.result)
    }

    @Test
    fun `complete a quiz 100% correctly`(){
        val deck = createDeckWithCards(5)
        val quiz = MultipleChoiceQuiz(deck, 3)
        repeat(5){
            var question = quiz.currentQuestion
            var result = quiz.answerCurrentQuestion(quiz.currentQuestion!!.correctAnswer)
            assertEquals(true, result.result)
            assertEquals(true, quiz.questionResults[it].result)
            assertEquals(question!!.correctAnswer, quiz.questionResults[it].answer)
            //assertEquals(4 - (it+1), quiz.remainingQuestionCount)
        }

        assertEquals(true, quiz.isFinished)
        assertEquals(0, quiz.remainingQuestionCount)
        assertEquals(5, quiz.questionResults.size)
    }

    @Test
    fun `complete a large quiz 50% correctly`(){
        val deck = createDeckWithCards(500)
        val quiz = MultipleChoiceQuiz(deck, 3)
        repeat(250){
            quiz.answerCurrentQuestion("banana")
        }
        assertEquals(false, quiz.isFinished)
        assertEquals(0, quiz.numberOfCorrectAnswers)
        assertEquals(250, quiz.numberOfWrongAnswers)
        assertEquals(249, quiz.remainingQuestionCount)
        repeat(250){
            quiz.answerCurrentQuestion(quiz.currentQuestion!!.correctAnswer)
        }
        assertEquals(true, quiz.isFinished)
        assertEquals(250, quiz.numberOfCorrectAnswers)
        assertEquals(250, quiz.numberOfWrongAnswers)
        assertEquals(0, quiz.remainingQuestionCount)
    }

    @Test
    fun `complete a quiz and restart and complete again`(){
        val deck = createDeckWithCards(100)
        val quiz = MultipleChoiceQuiz(deck, 3)
        repeat(50){
            quiz.answerCurrentQuestion("banana")
        }
        assertEquals(false, quiz.isFinished)
        assertEquals(0, quiz.numberOfCorrectAnswers)
        assertEquals(50, quiz.numberOfWrongAnswers)
        assertEquals(49, quiz.remainingQuestionCount)
        repeat(50){
            quiz.answerCurrentQuestion(quiz.currentQuestion!!.correctAnswer)
        }
        assertEquals(true, quiz.isFinished)
        assertEquals(50, quiz.numberOfCorrectAnswers)
        assertEquals(50, quiz.numberOfWrongAnswers)
        assertEquals(0, quiz.remainingQuestionCount)

        quiz.restartQuiz()

        repeat(50){
            quiz.answerCurrentQuestion("banana")
        }
        assertEquals(false, quiz.isFinished)
        assertEquals(0, quiz.numberOfCorrectAnswers)
        assertEquals(50, quiz.numberOfWrongAnswers)
        assertEquals(49, quiz.remainingQuestionCount)
        repeat(50){
            quiz.answerCurrentQuestion(quiz.currentQuestion!!.correctAnswer)
        }
        assertEquals(true, quiz.isFinished)
        assertEquals(50, quiz.numberOfCorrectAnswers)
        assertEquals(50, quiz.numberOfWrongAnswers)
        assertEquals(0, quiz.remainingQuestionCount)


    }







}
package ru.johnspade.taskobot.handlers

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import ru.johnspade.taskobot.BotControllerContainer
import ru.johnspade.taskobot.MessageHandler
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Import(MessageHandler::class, BotControllerContainer::class)
class CreatePersonalTaskTest: UpdateHandlerTest() {

	@Autowired
	private lateinit var messageHandler: MessageHandler

	@Test
	fun createPersonalTask() {
		val taskText = "new personal task"
		val replyTo = mock<Message> {
			on { hasText() } doReturn true
			on { text } doReturn messages.get("tasks.create.personal")
		}
		val message = mock<Message> {
			on { from } doReturn aliceTelegram
			on { hasText() } doReturn true
			on { text } doReturn taskText
			on { isReply } doReturn true
			on { replyToMessage } doReturn replyTo
		}
		val update = mock<Update> {
			on { hasMessage() } doReturn true
			on { getMessage() } doReturn message
		}
		val answer = messageHandler.handle(emptyExecutor, update) as SendMessage
		answer.validate()
		assertNotNull(answer)
		val task = taskRepository.findAll().first { it.sender == alice && it.text == taskText }
		assertNotNull(task.createdAt)
		assertEquals(messages.get("tasks.created", arrayOf(task.text)), answer.text)
	}

}

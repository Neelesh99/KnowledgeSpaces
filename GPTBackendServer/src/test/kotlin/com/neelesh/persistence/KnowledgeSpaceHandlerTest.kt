package com.neelesh.persistence

import arrow.core.right
import com.neelesh.model.KnowledgeSpace
import com.neelesh.routes.SimpleKnowledgeSpaceCreationRequest
import com.neelesh.routes.SimpleKnowledgeSpaceUpdateRequest
import com.neelesh.util.UUIDGenerator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KnowledgeSpaceHandlerTest {

    val knowledgeSpaceStore = mockk<KnowledgeSpaceStore>()
    val uuidGenerator = mockk<UUIDGenerator>()
    val knowledgeFileHandler = KnowledgeSpaceHandler(knowledgeSpaceStore, uuidGenerator)

    @Test
    fun `will create new knowledge file`() {
        val simpleKnowledgeSpaceCreationRequest = SimpleKnowledgeSpaceCreationRequest(
            "someSpaceName",
            "someEmail"
        )

        every { uuidGenerator.get() } returns "someId"
        val expectedKnowledgeSpace = KnowledgeSpace(
            "someId",
            "someSpaceName",
            "someEmail",
            emptyList(),
        )
        every { knowledgeSpaceStore.saveKnowledgeSpace(expectedKnowledgeSpace) } returns expectedKnowledgeSpace.right()

        val id = knowledgeFileHandler.create(simpleKnowledgeSpaceCreationRequest)

        Assertions.assertEquals("someId".right(), id)
    }

    @Test
    fun `will update knowledge space`() {
        val simpleKnowledgeSpaceUpdateRequest = SimpleKnowledgeSpaceUpdateRequest(
            "someId",
            "someEmail",
            null,
            listOf("someFileId")
        )

        every { uuidGenerator.get() } returns "someId"
        val oldKnowledgeSpace = KnowledgeSpace(
            "someId",
            "knowledgeFileName",
            "someEmail",
            emptyList()
        )
        every { knowledgeSpaceStore.getKnowledgeSpace("someId", "someEmail") } returns oldKnowledgeSpace.right()
        val knowledgeSpace = oldKnowledgeSpace.copy(files = listOf("someFileId"))
        every { knowledgeSpaceStore.saveKnowledgeSpace(knowledgeSpace) } returns knowledgeSpace.right()

        val id = knowledgeFileHandler.update(simpleKnowledgeSpaceUpdateRequest)

        Assertions.assertEquals("someId".right(), id)
    }

}
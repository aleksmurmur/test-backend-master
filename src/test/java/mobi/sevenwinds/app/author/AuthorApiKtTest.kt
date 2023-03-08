package mobi.sevenwinds.app.author

import io.restassured.RestAssured
import mobi.sevenwinds.app.budget.BudgetTable
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthorApiKtTest : ServerTest() {
    @BeforeEach
    internal fun setUp() {
        transaction { BudgetTable.deleteAll() }
    }
    @Test
    fun successfullyCreatesAuthor() {
        val authorRequest = AuthorRecordRequest("John Jo Johnes")
        RestAssured.given()
            .jsonBody(authorRequest)
            .post("/author/add")
            .toResponse<AuthorRecordResponse>().let { response ->
                Assert.assertEquals(authorRequest.fullName, response.fullName)
            }
        transaction {
            val saved = AuthorEntity.find { AuthorTable.fullName eq authorRequest.fullName }
                .firstOrNull()
            Assert.assertNotNull(saved)
            Assert.assertEquals(authorRequest.fullName, saved!!.fullName)
        }
    }
}
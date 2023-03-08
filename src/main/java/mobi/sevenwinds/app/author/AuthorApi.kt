package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route


fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorRecordResponse, AuthorRecordRequest>(info("Добавить автора")) {_, body ->
            respond(AuthorService.addAuthor(body))
        }
    }
}

data class AuthorRecordRequest(
    val fullName: String,

)
data class AuthorRecordResponse(
    val id: Int,
    val fullName: String,
    val creationDate: String//DateTime
)

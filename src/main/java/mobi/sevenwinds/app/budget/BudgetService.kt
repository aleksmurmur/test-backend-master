package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecordRequest): BudgetRecordResponse = withContext(Dispatchers.IO) {
        transaction {
            val author = AuthorEntity.find { AuthorTable.id eq body.authorId }.firstOrNull()
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.authorEntity = author
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val yearData = (BudgetTable leftJoin AuthorTable)
                .select { (BudgetTable.year eq param.year) }
                .also { query ->
                    param.authorName?.let {
                        query.andWhere {
                            AuthorTable.fullName.ilike("%$it%")
                        }
                    }
                }
                .orderBy(BudgetTable.month to SortOrder.ASC, BudgetTable.amount to SortOrder.DESC)
                .run { BudgetEntity.wrapRows(this) }

            val total = yearData.count()
            val sumByType = yearData.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }
            val pageData = yearData.limit(param.limit, param.offset)
                .map { it.toResponse() }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = pageData
            )
        }
    }
}

class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun <T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> =
    ILikeOp(this, QueryParameter(pattern, columnType))
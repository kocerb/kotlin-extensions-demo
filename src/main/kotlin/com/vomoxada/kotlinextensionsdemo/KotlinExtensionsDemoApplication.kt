package com.vomoxada.kotlinextensionsdemo

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Repository

@SpringBootApplication
class KotlinExtensionsDemoApplication

fun main(args: Array<String>) {
	runApplication<KotlinExtensionsDemoApplication>(*args)
}

@Table
data class Customer(
	val id: Long? = null,
	val email: String? = null,
	val phoneNumber: String? = null,
	val firstName: String? = null,
	val lastName: String? = null,
)

@Repository
class CustomerRepository(
	private val r2dbcEntityTemplate: R2dbcEntityOperations
) {

	suspend fun insert(customer: Customer) {
		val query = """
            INSERT INTO Customers( email, phone_number, first_name, last_name ) 
            VALUES ( :email, :phoneNumber, :firstName, :lastName )
            """.trimIndent()

		var executeSpec = r2dbcEntityTemplate.databaseClient.sql(query)

		executeSpec = customer.email?.let { executeSpec.bind("email", customer.email) } ?: executeSpec.bindNull("email", String::class.java)
		executeSpec = customer.phoneNumber?.let { executeSpec.bind("phoneNumber", customer.phoneNumber) } ?: executeSpec.bindNull("phoneNumber", String::class.java)
		executeSpec = customer.firstName?.let { executeSpec.bind("firstName", customer.firstName) } ?: executeSpec.bindNull("firstName", String::class.java)
		executeSpec = customer.lastName?.let { executeSpec.bind("lastName", customer.lastName) } ?: executeSpec.bindNull("lastName", String::class.java)

		executeSpec.then().awaitFirstOrNull()
	}
}

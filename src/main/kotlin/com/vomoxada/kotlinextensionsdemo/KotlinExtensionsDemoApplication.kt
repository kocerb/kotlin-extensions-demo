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

		r2dbcEntityTemplate.databaseClient
			.sql(query)
			.apply { customer.email?.let { bind("email", customer.email) } ?: bindNull("email", String::class.java) }
			.apply { customer.phoneNumber?.let { bind("phoneNumber", customer.phoneNumber) } ?: bindNull("phoneNumber", String::class.java) }
			.apply { customer.firstName?.let { bind("firstName", customer.firstName) } ?: bindNull("firstName", String::class.java) }
			.apply { customer.lastName?.let { bind("lastName", customer.lastName) } ?: bindNull("lastName", String::class.java) }
			.then()
			.awaitFirstOrNull()
	}
}

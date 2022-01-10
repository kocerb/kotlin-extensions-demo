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

		if (customer.email != null) {
			executeSpec = executeSpec.bind("email", customer.email)
		} else {
			executeSpec = executeSpec.bindNull("email", String::class.java)
		}

		if (customer.phoneNumber != null) {
			executeSpec = executeSpec.bind("phoneNumber", customer.phoneNumber)
		} else {
			executeSpec = executeSpec.bindNull("phoneNumber", String::class.java)
		}

		if (customer.firstName != null) {
			executeSpec = executeSpec.bind("firstName", customer.firstName)
		} else {
			executeSpec = executeSpec.bindNull("firstName", String::class.java)
		}

		if (customer.lastName != null) {
			executeSpec = executeSpec.bind("lastName", customer.lastName)
		} else {
			executeSpec = executeSpec.bindNull("lastName", String::class.java)
		}

		executeSpec.then().awaitFirstOrNull()
	}
}

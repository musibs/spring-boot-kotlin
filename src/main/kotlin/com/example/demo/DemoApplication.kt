package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args) {
        addInitializers(beans {
            bean {
                fun user(user: String, password: String, vararg roles: String)
                        = User.withDefaultPasswordEncoder().username(user).password(password).roles(*roles).build()
                InMemoryUserDetailsManager(user("user", "password", "USER"),
                        user("admin", "password", "ADMIN"))
            }

            bean {
                router {
                    GET("/greetings") {
                        request -> request.principal().map { it.name }.map { ServerResponse.ok().body(mapOf("greeting" to "Hello $it")) }.orElseGet { ServerResponse.badRequest().build() }
                    }
                }
            }
        })
    }
}

@Entity
@Table(name = "COURSES")
data class Course (@Id val id: Int?, val name: String, val rating: Int, val category: String, val description: String)

@RestController
class CourseResource(val courseService: CourseService) {

    /*@GetMapping
    fun index() : List<Course> = listOf(
            Course(1, "Spring Boot", 5, "Spring", "Excellent Spring Boot Course"),
            Course(2, "Kotlin in a Nutshell", 5, "Kotlin", "Kotlin in a Nutshell"),
            Course(3, "Kotlin: up & running", 5, "Kotlin", "Kotlin in a Nutshell")
    )*/

    @GetMapping
    fun index() : MutableIterable<Course> = courseService.findCourses();

    fun post(@RequestBody course: Course) {
        courseService.post(course);
    }
}

@Repository
interface CourseRepository : CrudRepository<Course, Int> {

    //@Query("select * from courses")
    //fun getCourses() : List<Course>
}
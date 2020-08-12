package com.example.demo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Hello(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @Column(unique = true, name = "name")
    var name: String,
    @Column(name = "greeting")
    var greeting: String
)

interface HelloRepository : JpaRepository<Hello, Long> {
    fun findByName(name: String): Hello?
}

@RestController
@RequestMapping("/hello")
class HelloController(
    val helloRepository: HelloRepository
) {
    @GetMapping
    fun get(name: String): Mono<Hello> = Mono
        .fromCallable { helloRepository.findByName(name) }

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): Mono<Hello> {
        return helloRepository.findById(id).get().toMono()
    }

    @PostMapping
    fun post(@RequestBody request: PostRequest): Mono<Hello> = Mono
        .fromCallable {
            Hello(
                name = request.name,
                greeting = request.greetings
            )
        }
        .map { hello -> helloRepository.save(hello) }
}

data class PostRequest(
    val name: String,
    val greetings: String
)

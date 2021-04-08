package com.example.demo

import org.springframework.stereotype.Service

@Service
class CourseService(val repository: CourseRepository) {

    fun findCourses() : MutableIterable<Course> = repository.findAll()

    fun post(course: Course) {
        repository.save(course)
    }

}
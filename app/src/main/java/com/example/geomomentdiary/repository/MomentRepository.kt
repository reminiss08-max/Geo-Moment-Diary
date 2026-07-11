package com.example.geomomentdiary.repository

import com.example.geomomentdiary.model.Moment

object MomentRepository {

    private val moments = mutableListOf<Moment>()

    fun getAllMoments(): List<Moment> {
        return moments
    }

    fun addMoment(moment: Moment) {
        moments.add(moment)
    }

    fun removeMoment(moment: Moment) {
        moments.remove(moment)
    }

    fun clearMoments() {
        moments.clear()
    }
}
package com.strand.finaid.model.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}
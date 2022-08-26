package com.strand.finaid.data.network

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}
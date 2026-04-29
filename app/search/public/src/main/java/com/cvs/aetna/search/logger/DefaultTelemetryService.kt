package com.cvs.aetna.search.logger

import javax.inject.Inject

interface TelemetryService {
    fun logEvent(eventName: String, properties: Map<String, String> = emptyMap())
}
class DefaultTelemetryService @Inject constructor() : TelemetryService {
    override fun logEvent(eventName: String, properties: Map<String, String>) {
        println("Event: $eventName, Properties: $properties")
    }
}

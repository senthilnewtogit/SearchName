package com.cvs.aetna.search.fake

import com.cvs.aetna.search.logger.TelemetryService

class FakeTelemetryService :
    TelemetryService,
    FakeFunctionHelper<FakeTelemetryService.Function> {
    sealed class Function {
        data class LogEvent(val eventName: String, val params: Map<String, String>) : Function()
    }

    override fun logEvent(eventName: String, properties: Map<String, String>) {
        recordCalledFunction(Function.LogEvent(eventName, properties))
    }

    override val timesFunctionCalled: MutableMap<Function, Int> = mutableMapOf()
}

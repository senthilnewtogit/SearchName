package com.cvs.aetna.search.fake

interface FakeFunctionHelper<T : Any> {
    val timesFunctionCalled: MutableMap<T, Int>
    fun recordCalledFunction(function: T) {
        timesFunctionCalled[function] = (timesFunctionCalled[function] ?: 0) + 1
    }

    fun verifyNoFunctionsCalled() {
        if (timesFunctionCalled.isEmpty()) return

        val message = buildString {
            appendLine("${this@FakeFunctionHelper::class.java.simpleName} - Unexpected calls:")
            timesFunctionCalled.forEach { (key, count) ->
                appendLine("• $key called $count times")
            }
        }

        throw AssertionError(message)
    }

    fun verifyFunctionCalled(function: T, times: Int = 1) {
        val current = timesFunctionCalled[function]
            ?: throw AssertionError("$function was never called")

        if (current != times) {
            throw AssertionError("$function called $current times. Expected $times.")
        }

        timesFunctionCalled.remove(function)
    }
}


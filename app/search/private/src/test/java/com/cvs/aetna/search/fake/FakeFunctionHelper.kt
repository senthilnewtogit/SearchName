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

    fun verifyFunctionNeverCalled(function: T, checkAllReferences: Boolean = true) {
        val directCalls = timesFunctionCalled[function] ?: 0

        if (directCalls > 0) {
            throw AssertionError("$function called $directCalls times. Expected 0.")
        }

        if (checkAllReferences) {
            val similarCalls = timesFunctionCalled.keys.count {
                it::class == function::class
            }

            if (similarCalls > 0) {
                throw AssertionError(
                    "$similarCalls similar calls found for ${function::class.simpleName}. Expected 0.",
                )
            }
        }
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

fun List<FakeFunctionHelper<*>>.verifyNoMoreFakesCalled() {
    forEach { it.verifyNoFunctionsCalled() }
}

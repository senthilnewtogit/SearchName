package com.cvs.aetna.search.repository

import com.cvs.aetna.search.data.mapper.toRequest
import com.cvs.aetna.search.data.model.request.toQueryMap
import com.cvs.aetna.search.data.model.response.CharacterResponse
import com.cvs.aetna.search.data.remote.CharacterNameAPI
import com.cvs.aetna.search.data.transformer.CharacterResponseToDomainTransform
import com.cvs.aetna.search.domain.model.CharacterError
import com.cvs.aetna.search.domain.model.CharacterList
import com.cvs.aetna.search.domain.model.CharacterSearch
import com.cvs.aetna.search.domain.repo.CharacterListRepository
import com.cvs.aetna.search.logger.TelemetryService
import retrofit2.Response
import java.net.ConnectException
import javax.inject.Inject

private const val DEFAULT_RETRY_SECONDS = 5L

class DefaultCharacterListRepository @Inject constructor(
    private val characterNameAPI: CharacterNameAPI,
    private val characterResponseToDomainTransform: CharacterResponseToDomainTransform,
    private val telemetryService: TelemetryService,
) : CharacterListRepository {

    override suspend fun getCharacterList(characterSearch: CharacterSearch): CharacterList = fetchCharacterList(characterSearch = characterSearch)
    override suspend fun getMoreCharacterList(url: String): CharacterList = fetchMoreListResponse(url = url)

    private suspend fun fetchMoreListResponse(url: String): CharacterList = try {
        val response: Response<CharacterResponse> = characterNameAPI.getMoreListOfCharacter(url = url)
        if (response.isSuccessful) {
            characterResponseToDomainTransform.transform(response.body())
        } else {
            handleErrorResponse(response)
        }
    } catch (e: Throwable) {
        val error = e.mapException()

        telemetryService.logEvent(
            eventName = "CharacterListError",
            properties = mapOf("error" to (e.message ?: "Unknown")),
        )

        CharacterList(errorMsg = error)
    }
    private suspend fun fetchCharacterList(characterSearch: CharacterSearch): CharacterList = try {
        val queryMap = characterSearch.toRequest().toQueryMap()
        val response: Response<CharacterResponse> = characterNameAPI.getListOfCharacter(queryMap)
        if (response.isSuccessful) {
            characterResponseToDomainTransform.transform(response.body())
        } else {
            handleErrorResponse(response)
        }
    } catch (e: Throwable) {
        val error = e.mapException()

        telemetryService.logEvent(
            eventName = "CharacterListError",
            properties = mapOf("error" to (e.message ?: "Unknown")),
        )
        CharacterList(errorMsg = error)
    }
    private fun handleErrorResponse(response: Response<CharacterResponse>): CharacterList = when (val code = response.code()) {
        404 -> {
            CharacterList(
                characters = emptyList(),
                totalCount = 0,
                nextPageUrl = null,
                errorMsg = CharacterError.NoResultFound(response.message()),
            )
        }
        429, 1015 -> {
            val retryAfter = response.headers()["retry-after"]
                ?.toLongOrNull() ?: DEFAULT_RETRY_SECONDS
            CharacterList(errorMsg = CharacterError.RateLimit(retryAfter))
        }

        in 400..499 -> {
            CharacterList(errorMsg = CharacterError.Http(code, response.message()))
        }

        in 500..599 -> {
            CharacterList(errorMsg = CharacterError.Http(code, response.message()))
        }
        else -> {
            CharacterList(errorMsg = CharacterError.Unknown("Unexpected HTTP error"))
        }
    }
    fun Throwable.mapException(): CharacterError = when (this) {
        is ConnectException -> CharacterError.NoInternet(message)
        is java.net.UnknownHostException -> CharacterError.UnknownHost(message)
        is java.net.SocketTimeoutException -> CharacterError.Timeout(message)
        is retrofit2.HttpException -> {
            when (val code = code()) {
                404 -> CharacterError.Unknown("Unexpected 404 via exception")
                429 -> CharacterError.RateLimit(DEFAULT_RETRY_SECONDS)
                else -> CharacterError.Http(code, message())
            }
        }

        is javax.net.ssl.SSLException -> CharacterError.SSL(message)
        is com.google.gson.stream.MalformedJsonException,
        is com.google.gson.JsonSyntaxException,
        is IllegalStateException,
        -> CharacterError.Parsing(message)
        is java.io.IOException -> CharacterError.NetworkIO(message)
        else -> CharacterError.Unknown(message)
    }
}

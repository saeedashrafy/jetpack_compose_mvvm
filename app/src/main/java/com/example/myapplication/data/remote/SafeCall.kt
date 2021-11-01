package com.example.myapplication.data.remote



import android.util.Log
import com.example.myapplication.core.Mapper
import com.example.myapplication.core.dispatchers.CoroutineDispatchers
import com.example.myapplication.data.mapper.ErrorMapper
import com.example.myapplication.data.remote.model.ResultResponse
import com.example.myapplication.domain.entity.DomainResult
import com.example.myapplication.domain.entity.Either
import com.example.myapplication.domain.entity.Failure
import com.example.myapplication.domain.entity.NetworkError
import kotlinx.coroutines.withContext

suspend  fun <T, R> safeCall(
    dispatcher: CoroutineDispatchers,
    transform: Mapper<T, R>,
    call : suspend () -> ResultResponse<T>
): DomainResult<R>  {


    return withContext(dispatcher.io) {
        return@withContext when (val response = call.invoke()) {
            is ResultResponse.Success -> Either.Right(transform(((response.data!!))))
            is ResultResponse.Failure -> Either.Left(ErrorMapper.getError(response.statusCode))
            is ResultResponse.NetworkError -> Either.Left(NetworkError)
        }
    }
}
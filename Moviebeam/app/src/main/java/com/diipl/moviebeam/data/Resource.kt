package com.diipl.moviebeam.data

// A generic class that contains data and status about loading this data.
sealed class  Resource<T>(
        val data: T? = null,
        val errorCode : Int? = null,
        val errorMsg : String?= null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Loading<T>() : Resource<T>()
    class DataError<T>(val msg : String?=null, val code : Int?= null) : Resource<T>(null, code,msg)

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is DataError -> "Error[error=$msg]"
            is Loading<T> -> "Loading"
        }
    }
}

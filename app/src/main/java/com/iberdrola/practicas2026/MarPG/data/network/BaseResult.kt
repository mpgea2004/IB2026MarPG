package com.iberdrola.practicas2026.MarPG.data.network

sealed class BaseResult<out T> {

    data class Success<T>(var data: T): BaseResult<T>()

    data class Error(var exception: Exception): BaseResult<Nothing>()
}
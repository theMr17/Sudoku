package com.mr_17.sudoku.domain

interface ISettingsStorage {
    suspend fun getSettings(): SettingsStorageResult
    suspend fun updateSettings(settings: Settings): SettingsStorageResult
}

sealed class SettingsStorageResult {
    data class OnSuccess(val settings: Settings): SettingsStorageResult()
    data object OnComplete : SettingsStorageResult()
    data class OnError(val exception: Exception): SettingsStorageResult()
}
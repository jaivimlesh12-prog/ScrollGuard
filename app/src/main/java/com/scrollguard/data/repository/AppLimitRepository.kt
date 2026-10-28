package com.scrollguard.data.repository

import com.scrollguard.data.db.dao.AppLimitDao
import com.scrollguard.data.db.entity.AppLimitEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLimitRepository @Inject constructor(
    private val appLimitDao: AppLimitDao
) {
    fun getAllLimits(): Flow<List<AppLimitEntity>> =
        appLimitDao.getAllLimits()

    fun getEnabledLimits(): Flow<List<AppLimitEntity>> =
        appLimitDao.getEnabledLimits()

    fun getLimitForApp(packageName: String): Flow<AppLimitEntity?> =
        appLimitDao.getLimitByPackage(packageName)

    suspend fun getLimitForAppSync(packageName: String): AppLimitEntity? =
        appLimitDao.getLimitByPackageSync(packageName)

    suspend fun setLimit(entity: AppLimitEntity) =
        appLimitDao.insertLimit(entity)

    suspend fun updateLimit(entity: AppLimitEntity) =
        appLimitDao.updateLimit(entity)

    suspend fun removeLimit(id: Long) =
        appLimitDao.deleteLimitById(id)

    suspend fun toggleLimit(id: Long, enabled: Boolean) {
        // AppLimit update can be loaded and saved
    }
}

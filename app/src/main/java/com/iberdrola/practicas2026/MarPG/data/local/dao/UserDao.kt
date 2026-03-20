package com.iberdrola.practicas2026.MarPG.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iberdrola.practicas2026.MarPG.data.local.entities.UserProfileEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 0")//id 0 porque solo hay un usuario para probar
    suspend fun getUser(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserProfileEntity)
}
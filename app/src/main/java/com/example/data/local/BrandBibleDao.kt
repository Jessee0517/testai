package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandBibleDao {
    @Query("SELECT * FROM brand_bibles ORDER BY createdAt DESC")
    fun getAllBrandBibles(): Flow<List<BrandBibleEntity>>

    @Query("SELECT * FROM brand_bibles WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteBrandBibles(): Flow<List<BrandBibleEntity>>

    @Query("SELECT * FROM brand_bibles WHERE id = :id LIMIT 1")
    suspend fun getBrandBibleById(id: Long): BrandBibleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrandBible(entity: BrandBibleEntity): Long

    @Update
    suspend fun updateBrandBible(entity: BrandBibleEntity)

    @Query("DELETE FROM brand_bibles WHERE id = :id")
    suspend fun deleteBrandBibleById(id: Long)

    @Query("UPDATE brand_bibles SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE brand_bibles SET generatedLogoBase64 = :base64 WHERE id = :id")
    suspend fun updateLogoAsset(id: Long, base64: String)
}

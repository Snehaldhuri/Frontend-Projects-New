package com.diipl.moviebeam.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diipl.moviebeam.data.dto.epg.ChannelEpgDTO

@Dao
interface ProgramGuideDao {

    @Insert(entity = ChannelEpgDTO::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(epgChannel: List<ChannelEpgDTO>)

    @Query("SELECT * FROM channelDetails WHERE [key] = :key")
    fun getChannels(key: String?): LiveData<MutableList<ChannelEpgDTO>>

    @Query("DELETE FROM channelDetails")
    suspend fun removeAllChannels()

}
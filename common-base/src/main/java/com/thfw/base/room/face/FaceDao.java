package com.thfw.base.room.face;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Author:pengs
 * Date: 2022/1/4 16:04
 * Describe:Todo
 */
@Dao
public interface FaceDao {

    @Query("SELECT * FROM face")
    List<Face> getAll();

    @Query("SELECT * FROM face WHERE uid IN (:userIds)")
    List<Face> loadAllByIds(int[] userIds);

    @Insert
    void insertAll(Face... faces);

    @Insert
    void insert(Face user);

    @Delete
    void delete(Face user);
}

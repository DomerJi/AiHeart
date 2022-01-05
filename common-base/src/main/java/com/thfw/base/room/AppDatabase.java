package com.thfw.base.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.thfw.base.room.face.Face;
import com.thfw.base.room.face.FaceDao;

@Database(entities = {Face.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FaceDao faceDao();
}
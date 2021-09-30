//package com.thfw.base.roomtest;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//@Database(entities = {User.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
////    public abstract UserDao userDao();
//
//    public static AppDatabase getInstance(Context context) {
//        AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
//                AppDatabase.class, "database-name").build();
//        return db;
//    }
//
//}
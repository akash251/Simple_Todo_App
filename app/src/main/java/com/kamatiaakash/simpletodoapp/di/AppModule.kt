package com.kamatiaakash.simpletodoapp.di

import android.app.Application
import androidx.room.Room
import com.kamatiaakash.simpletodoapp.data.TodoDatabase
import com.kamatiaakash.simpletodoapp.data.TodoRepo
import com.kamatiaakash.simpletodoapp.data.TodoRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app:Application):TodoDatabase{
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db:TodoDatabase):TodoRepo{
        return TodoRepoImpl(db.dao)
    }
}
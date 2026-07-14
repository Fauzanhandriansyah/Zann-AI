package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "study_goals")
data class StudyGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val targetDate: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_tasks",
    foreignKeys = [
        ForeignKey(
            entity = StudyGoal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goalId"])]
)
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Dao
interface PlannerDao {
    @Query("SELECT * FROM study_goals ORDER BY timestamp DESC")
    fun getAllGoals(): Flow<List<StudyGoal>>

    @Query("SELECT * FROM study_tasks WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getTasksForGoal(goalId: Long): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: StudyGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask): Long

    @Update
    suspend fun updateTask(task: StudyTask)

    @Delete
    suspend fun deleteTask(task: StudyTask)

    @Query("DELETE FROM study_goals WHERE id = :goalId")
    suspend fun deleteGoal(goalId: Long)

    @Query("UPDATE study_tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
}

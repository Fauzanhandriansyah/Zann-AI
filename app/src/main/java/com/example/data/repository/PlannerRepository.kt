package com.example.data.repository

import com.example.data.db.PlannerDao
import com.example.data.db.StudyGoal
import com.example.data.db.StudyTask
import kotlinx.coroutines.flow.Flow

class PlannerRepository(private val plannerDao: PlannerDao) {
    val allGoals: Flow<List<StudyGoal>> = plannerDao.getAllGoals()

    fun getTasksForGoal(goalId: Long): Flow<List<StudyTask>> = plannerDao.getTasksForGoal(goalId)

    suspend fun insertGoal(goal: StudyGoal): Long = plannerDao.insertGoal(goal)

    suspend fun insertSubTasks(tasks: List<StudyTask>) = plannerDao.insertTasks(tasks)

    suspend fun addManualSubTask(task: StudyTask): Long = plannerDao.insertTask(task)

    suspend fun updateSubTask(task: StudyTask) = plannerDao.updateTask(task)

    suspend fun deleteSubTask(task: StudyTask) = plannerDao.deleteTask(task)

    suspend fun deleteGoal(goalId: Long) = plannerDao.deleteGoal(goalId)

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) = plannerDao.updateTaskCompletion(taskId, isCompleted)
}

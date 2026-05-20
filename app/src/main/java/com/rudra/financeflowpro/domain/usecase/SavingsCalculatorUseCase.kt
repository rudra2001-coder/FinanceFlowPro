package com.rudra.financeflowpro.domain.usecase

import com.rudra.financeflowpro.domain.model.SavingsGoal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class SavingsCalculatorUseCase @Inject constructor() {
    fun dailyAmountRequired(goal: SavingsGoal): Double {
        val remaining = goal.targetAmount - goal.currentAmount
        val daysLeft = if (goal.deadline != null) {
            ChronoUnit.DAYS.between(
                LocalDate.now(),
                Instant.ofEpochMilli(goal.deadline)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
            )
        } else 0L
        return if (daysLeft > 0) remaining / daysLeft else remaining
    }

    fun projectedCompletionDate(goal: SavingsGoal, avgMonthlyContribution: Double): LocalDate? {
        if (avgMonthlyContribution <= 0) return null
        val remaining = goal.targetAmount - goal.currentAmount
        val monthsNeeded = (remaining / avgMonthlyContribution).toLong()
        return LocalDate.now().plusMonths(monthsNeeded)
    }

    fun savingsRate(monthlyIncome: Double, monthlySavings: Double): Double {
        return if (monthlyIncome > 0) (monthlySavings / monthlyIncome) * 100 else 0.0
    }
}

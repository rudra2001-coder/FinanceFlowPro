package com.rudra.financeflowpro.util

object CategoryConstants {
    val incomeCategories = listOf(
        "Salary", "Freelance", "Business", "Investment Returns",
        "Gift", "Bonus", "Other Income"
    )

    val expenseCategories = mapOf(
        "Food" to listOf("Groceries", "Restaurant", "Fast Food", "Coffee", "Delivery"),
        "Transport" to listOf("Fuel", "Rickshaw/CNG", "Bus/Train", "Ride Share", "Vehicle Maintenance"),
        "Bills" to listOf("Electricity", "Water", "Gas", "Internet", "Mobile Recharge", "Rent"),
        "Health" to listOf("Doctor", "Medicine", "Hospital", "Lab Tests", "Gym"),
        "Shopping" to listOf("Clothing", "Electronics", "Home Items", "Personal Care"),
        "Education" to listOf("Tuition", "Books", "Online Courses", "School Fees"),
        "Entertainment" to listOf("Movies", "Games", "Streaming", "Events"),
        "Savings" to listOf("Goal Contribution", "Emergency Fund"),
        "Investment" to listOf("Stocks", "Mutual Fund", "Crypto", "Gold", "Fixed Deposit"),
        "Other" to listOf("Miscellaneous", "Emergency", "Charity")
    )

    val allCategories: List<String>
        get() = expenseCategories.keys.toList()

    val allSubcategories: List<String>
        get() = expenseCategories.values.flatten()

    fun getSubcategories(category: String): List<String> =
        expenseCategories[category] ?: emptyList()
}

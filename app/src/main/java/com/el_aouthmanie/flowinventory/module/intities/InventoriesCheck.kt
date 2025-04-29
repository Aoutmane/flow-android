package com.el_aouthmanie.flowinventory.module.intities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object InventoriesCheck : Table("inventory_check") {
    val id = integer("id").autoIncrement()
    val startDate = datetime("start_date")
    val endDate = datetime("end_date").nullable()
    val performedBy = integer("performed_by").references(InventoriesCheck.id)
    val status = enumerationByName("status",10,INVCheckStatus::class)
}

enum class INVCheckStatus {
    Done,
    Canceled,
    Going
}
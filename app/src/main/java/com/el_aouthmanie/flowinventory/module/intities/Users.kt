package com.el_aouthmanie.flowinventory.module.intities

import org.jetbrains.exposed.sql.Table

class Users : Table("users") {
    val id = integer("id").autoIncrement()
    val userName = varchar("username",100)
    val passHash = varchar("password_hash",255)

    val name = varchar("name",255)
    val status = enumerationByName("status",10, UserStat::class)

}

enum class UserStat{
    Active, Inactive
}
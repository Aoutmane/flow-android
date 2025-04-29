package com.el_aouthmanie.flowinventory.module

import org.jetbrains.exposed.sql.Database

object Connection {

    init {
        Database.connect(
            url = "jdbc:mysql://10.0.2.2:3306/azzi-aoutmane_flow",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "406096",
            password = "othman20040604"
        )
    }

}
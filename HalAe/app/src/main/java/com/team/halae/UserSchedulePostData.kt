package com.team.halae

data class UserSchedulePostData(
        var date : String,
        var hal_idx : Int,
        var text : String,
        var start_time : Int,
        var end_time : Int,
        var inter : ArrayList<Int>
)
package com.arman.dev.physioexamprep.core.util

data class Topic(
    val topicName : String ,
    val topicId :Int,
    val totalQuestion:Int
)

fun provideTopics(): List<Topic>{
    return listOf(
        Topic(
            "Upper Limb",
            1,
            20
        ),
        Topic(
            "Upper Limb",
            2,
            20
        ),
        Topic(
            "Upper Limb",
            3,
            20
        ),
        Topic(
            "Upper Limb",
            4,
            20
        ),
        Topic(
            "Upper Limb",
            5,
            20
        ),
        Topic(
            "Upper Limb",
            6,
            20
        ),
        Topic(
            "Upper Limb",
            7,
            20
        ),
        Topic(
            "Upper Limb",
            8,
            20
        ),
        Topic(
            "Upper Limb",
            9,
            20
        ),
        Topic(
            "Upper Limb",
            10,
            20
        )
    )
}
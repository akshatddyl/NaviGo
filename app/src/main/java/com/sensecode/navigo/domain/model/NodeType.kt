package com.sensecode.navigo.domain.model

/**
 * Node types for indoor navigation with TalkBack-friendly audio instructions.
 */
enum class NodeType(val typeKey: String) {
    ROOM("room") {
        override fun talkBackInstruction(nodeName: String): String =
            "You have reached $nodeName."
    },
    STAIRS("staircase") {
        override fun talkBackInstruction(nodeName: String): String =
            "You are approaching stairs at $nodeName. Use the handrail on your right."
    },
    ELEVATOR("elevator") {
        override fun talkBackInstruction(nodeName: String): String =
            "Elevator ahead at $nodeName. Press the call button on your right."
    },
    RESTROOM("toilet") {
        override fun talkBackInstruction(nodeName: String): String =
            "Restroom is nearby. $nodeName."
    },
    ENTRANCE("entrance") {
        override fun talkBackInstruction(nodeName: String): String =
            "You are near the entrance at $nodeName. The door is ahead of you."
    },
    EXIT("exit") {
        override fun talkBackInstruction(nodeName: String): String =
            "You are near the exit at $nodeName. Push the door forward."
    },
    CORRIDOR("junction") {
        override fun talkBackInstruction(nodeName: String): String =
            "You are at a corridor junction: $nodeName."
    },
    CANTEEN("canteen") {
        override fun talkBackInstruction(nodeName: String): String =
            "Canteen area: $nodeName is nearby."
    };

    abstract fun talkBackInstruction(nodeName: String): String

    companion object {
        fun fromString(type: String): NodeType {
            return entries.find { it.typeKey.equals(type, ignoreCase = true) }
                ?: ROOM // Default fallback
        }
    }
}

package com.eb.schedule.model


sealed abstract class TaskResult(val status: Int)

case object Failed extends TaskResult (-1)
case object Pending extends TaskResult (0)
case object Finished extends TaskResult (1)
package com.scala.commands
import com.scala.filesystem.State

class Pwd extends Command {

  override def apply(state: State): State =
    state.setMessage(state.workingDir.path)
}

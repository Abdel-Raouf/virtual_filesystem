package com.scala.commands

import com.scala.filesystem.State

trait Command {

  def apply(state: State): State
}

object Command {

  def from(input: String): Command =
    new UnknownCommand
}

package com.scala.commands

import com.scala.files.{DirEntry, File}
import com.scala.filesystem.State

class Touch(name: String) extends CreateEntry(name) {

  override def createSpecificEntry(state: State): DirEntry =
    File.empty(state.workingDir.path, name)
}

package com.scala.commands

import com.scala.files.{DirEntry, Directory}
import com.scala.filesystem.State

class Mkdir(name: String) extends CreateEntry(name) {

  override def createSpecificEntry(state: State): DirEntry =
  // create new directory entry in the working directory
    Directory.empty(state.workingDir.path, name)
}

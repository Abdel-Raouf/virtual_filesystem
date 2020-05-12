package com.scala.commands

import com.scala.files.{DirEntry, Directory}
import com.scala.filesystem.State

abstract class CreateEntry(name: String) extends Command {

  override def apply(state: State): State = {
    val workingDir = state.workingDir
    if (workingDir.hasEntry(name)) {
      state.setMessage("Entry " + name + " already exists!")
    } else if (name.contains(Directory.SEPARATOR)) {
      state.setMessage(name + " must not contain separators!")
    } else if (checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    } else {
      doCreateEntry(state, name)
    }
  }

  def checkIllegal(name: String): Boolean = {
    name.contains(".")
  }

  def doCreateEntry(state: State, name: String): State = {

    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      if (path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }

      /*
        /a/b
            (contents)
            (new entry) /e

        updateStructure(root, ["a", "b"], /e)
          => path.isEmpty?
          => oldEntry = /a
          root.replaceEntry("a", updateStructure(/a, ["b"], /e)
            => path.isEmpty?
            => oldEntry = /b
            /a.replaceStructure("b", [], /e)
              => path.isEmpty? => /b.addEntry(/e)
      */

    }

    val workingDir = state.workingDir

    // 1-  all the directories in the full path
    val allDirsInPath = workingDir.getAllFoldersInPath

    // 2- create new directory entry in the working directory
    val newEntry: DirEntry = createSpecificEntry(state, name)

    // 3- update the whole directory structure starting from the root
    // (the directory structure is IMMUTABLE)
    val newRoot = updateStructure(state.root, allDirsInPath, newEntry)

    // 4- find new working directory INSTANCE given working directory's full path, in the NEW directory structure
    val newWorkingDir = newRoot.findDescendant(allDirsInPath)

    State(newRoot, newWorkingDir)

  }

  def createSpecificEntry(state: State, entryName: String): DirEntry
}
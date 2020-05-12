package com.scala.commands

import com.scala.files.{DirEntry, Directory}
import com.scala.filesystem.State

import scala.annotation.tailrec

class Cd(dir: String) extends Command {

  override def apply(state: State): State = {
    /*
      cd /something/somethingElse/.../  = absolute path
      cd a/b/c = relative to the current working directory (relative path)

      // relative tokens
      cd ..
      cd .
      cd a/./.././a/
    */

    // 1- find root
    val root = state.root
    val workingDir = state.workingDir

    // 2- find the absolute path of the directory I want to cd to
    val absolutePath =
      if(dir.startsWith(Directory.SEPARATOR)) dir // / => /
      else if(workingDir.isRoot) workingDir.path + dir // a/b/c/ + d
      else workingDir.path + Directory.SEPARATOR + dir // a/b/c + / + d

    // 3- find the directory to cd to, given the path
    val destinationDirectory = doFindEntry(root, absolutePath)

    // 4- change the state given the new directory
  if(destinationDirectory == null || !destinationDirectory.isDirectory)
    state.setMessage(dir + ": no such directory")
  else
    State(root, destinationDirectory.asDirectory)
  }

  def doFindEntry(root: Directory, path: String): DirEntry = {
    @tailrec
    def findEntryHelper(currentDirectory: Directory, path: List[String]): DirEntry =
      if(path.isEmpty || path.head.isEmpty) currentDirectory
      else if(path.tail.isEmpty) currentDirectory.findEntry(path.head)
      else {
        val nextDir = currentDirectory.findEntry(path.head)
        if(nextDir == null || !nextDir.isDirectory) null
        else findEntryHelper(nextDir.asDirectory, path.tail)
      }

    @tailrec
    def collapseRelativeTokens(path: List[String], result: List[String]): List[String] = {
      /*
        /a/b => ["a", "b"]

        path.isEmpty? => no
          CRT(["b"], result = List :+ "a" = ["a"])
            path.isEmpty? => no
              CRT([], result = LIST :+ "b" = ["a", "b"])
                path.isEmpty? => yes
                  result

        /a/.. => ["a", ".."]

        path.isEmpty? => no
          CRT([".."], result = [] :+ "a" = ["a"])
             path.isEmpty? => no
              CRT([], []) = []

        /a/b/.. => ["a", "b", ".."]

        path.isEmpty? => no
          CRT(["b", ".."], result = [] :+ "a" = ["a"])
            path.isEmpty? => no
              CRT([".."], result = ["a"] :+ ".." = ["a", "b"])
                path.isEmpty()? => no
                  CRT([], "a")

        /a/b/c/.. => ["a", "b", "c", ".."]
        etc...

      */

      if(path.isEmpty) result
      else if(".".equals(path.head)) collapseRelativeTokens(path.tail, result)
      else if("..".equals(path.head)) {
        if(result.isEmpty) null
        else collapseRelativeTokens(path.tail, result.init)
      } else collapseRelativeTokens(path.tail, result :+ path.head)
    }

    // 1- tokens
    val tokens: List[String] = path.substring(1).split(Directory.SEPARATOR).toList

    // 1.5 - eliminate/collapse relative tokens

    /*

      ["a", "."] => ["a"]
      ["a", "b", ".", "."] => ["a", "b"]

      /a/../ => ["a", ".."] => []
      /a/b/.. => ["a", "b", ".."] => ["a"]

    */

    val newTokens = collapseRelativeTokens(tokens, List())

    // 2- navigate to the correct entry
    if(newTokens == null) null
    else findEntryHelper(root, newTokens)
  }
}

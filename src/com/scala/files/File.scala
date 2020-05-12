package com.scala.files

import com.scala.filesystem.FilesystemException


class File(override val parentPath: String, override val name: String, contents: String)
  extends DirEntry(parentPath, name) {

  def asDirectory: Directory =
    throw new FilesystemException("A file cannot be converted to a directory!")

  def asFile: File = this

  def isFile: Boolean = true
  def isDirectory: Boolean = false

  def getType: String = "File"
}

object File {

  def empty(parentPath: String, name: String): File =
    new File(parentPath, name, "")
}
package com.scala.files

abstract class DirEntry(val parentPath: String, val name: String) {

  def path: String = {

    val separatorIfNecessary =
      if(Directory.SEPARATOR.equals(parentPath)) ""
      else Directory.SEPARATOR

    parentPath + separatorIfNecessary + name
  }

  def asDirectory: Directory
  def asFile: File

  def isDirectory: Boolean
  def isFile: Boolean

  def getType: String
}

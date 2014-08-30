package me.masahito.sleveldb

import java.nio.file.Path
import org.iq80.leveldb.{WriteBatch, ReadOptions, DB, Options}
import org.fusesource.leveldbjni.JniDBFactory._
import collection.JavaConversions._

object LevelDBWrapper {

  def create(path: Path, options: Options): DB = {
    factory.open(path.toFile, options)
  }

  def close(db: DB) {
    db.close()
  }

  def openAutoClose[T](path: Path, body: DB => T, options: Options = new Options().createIfMissing(true)) : T = {
    val db = factory.open(path.toFile, options)
    try {
      body(db)
    } finally {
      db.close()
    }
  }

  def iterator[T](body: Iterator[java.util.Map.Entry[Array[Byte], Array[Byte]]] => T)(implicit db: DB, roo: Option[ReadOptions]=None) :T = {
    val iterator = roo match {
      case Some(x) => db.iterator(x)
      case None    => db.iterator()
    }

    try {
      iterator.seekToFirst()
      body(iterator.toIterator)
    } finally {
      iterator.close()
    }
  }

  def writeBatch[A](body:WriteBatch => A)(implicit db: DB) : A = {
    val writeBatch = db.createWriteBatch
    try {
      val result = body(writeBatch)
      db.write(writeBatch)
      result
    }
    finally
      writeBatch.close()
  }
  def readOnly[T](body:(DB, ReadOptions) => T)(implicit db: DB) : T = {
    val ro = new ReadOptions()
    ro.snapshot(db.getSnapshot)
    try {
      body(db, ro)
    } finally {
      ro.snapshot().close()
    }
  }
}

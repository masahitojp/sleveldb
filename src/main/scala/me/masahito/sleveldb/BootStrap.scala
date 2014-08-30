package me.masahito.sleveldb

import org.iq80.leveldb.{ReadOptions, DB, WriteBatch, Options}
import org.fusesource.leveldbjni.JniDBFactory.factory
import java.nio.file.{Path, Paths}
import collection.JavaConversions._
import java.nio.ByteBuffer


object LevelDBWrapper {
  class LevelDBWrapped(val db: DB) {
    def write[K,V](key:K, value:V) = {
      //db.put(K, V)
    }
  }

  def open[T](path: Path, body: DB => T, options: Options = new Options().createIfMissing(true)) : T = {
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

object BytesUtils{
  /**
   * toBytes Interface
   * @param v
   * @param c
   * @tparam A
   * @return
   */
  def toBytes[A](v: A)(implicit c: BytesConcept[A]) = c.byteArray(v)
  trait BytesConcept[A] {
    // 共通インタフェース
    def byteArray(v: A): Array[Byte]
  }
  implicit val intToBytes = new BytesConcept[Int] {
    // Int の double 定義
    def byteArray(v: Int) = {
      ByteBuffer.allocate(8).putInt(v).array()
    }
  }
  implicit val doubleToBytes = new BytesConcept[Double] {
    // Double の double 定義
    def byteArray(v: Double) = {
      val l = java.lang.Double.doubleToLongBits(v)
      ByteBuffer.allocate(8).putLong(l).array()
    }
  }
  implicit val stringToBytes = new BytesConcept[String] {
    // String の double 定義
    def byteArray(v: String) = v.getBytes
  }



  def printByteArray(ba: Array[Byte]) =  {
    var a = ""
    ba.foreach(bytes => {
      a += "%02X ".format(bytes)
    })
    a
  }

  def getValueFeelSoGood[A](x: Array[Byte])(implicit c: GetValueConcept[A]) = c.get(x)

  trait GetValueConcept[T] {
    def get(v: Array[Byte]): T
  }

  implicit val bytesToInt = new GetValueConcept[Int] {
    def get(v: Array[Byte]) = {
      val bb = ByteBuffer.wrap(v)
      bb.asIntBuffer().get
    }
  }

  implicit val  bytesToDouble = new GetValueConcept[Double] {
    def get(v: Array[Byte]) = {
      val bb = ByteBuffer.wrap(v)
      bb.asDoubleBuffer().get
    }
  }

  implicit val  bytesToString = new GetValueConcept[String] {
    def get(v: Array[Byte]) = new String(v)
  }
}


object BootStrap extends App {

  import LevelDBWrapper._
  import BytesUtils._

  open[Unit](Paths.get("example"), implicit db => {

    writeBatch[Unit](wb => {
      iterator[Unit](it => {
        it.foreach(n => wb.delete(n.getKey))
      })

      for(i <- 1 to 10) {
        wb.put(toBytes("a%s".format(i)), toBytes(1))
      }
    })

    readOnly[Unit]((db, ro) => {
      iterator[Unit](it => {
        val result = it.foldLeft(0.0)((acc,n) => {acc + getValueFeelSoGood[Int](n.getValue)})
        println(result)
      })(db, Option(ro))
    })

  })


}

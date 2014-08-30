package me.masahito.sleveldb

import java.nio.ByteBuffer

/**
 * ByteArrayを扱うための処理群
 */
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
  implicit val longToBytes = new BytesConcept[Long] {
    // Int の double 定義
    def byteArray(v: Long) = {
      ByteBuffer.allocate(8).putLong(v).array()
    }
  }
  implicit val doubleToBytes = new BytesConcept[Double] {
    // Double の double 定義
    def byteArray(v: Double) = {
      ByteBuffer.allocate(8).putDouble(v).array()
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
    def get(v: Array[Byte]):Int = {
      ByteBuffer.wrap(v).asIntBuffer().get
    }
  }

  implicit val bytesToLong = new GetValueConcept[Long] {
    def get(v: Array[Byte]):Long = {
      ByteBuffer.wrap(v).asLongBuffer().get
    }
  }

  implicit val  bytesToDouble = new GetValueConcept[Double] {
    def get(v: Array[Byte]):Double = {
      ByteBuffer.wrap(v).asDoubleBuffer().get
    }
  }

  implicit val  bytesToString = new GetValueConcept[String] {
    def get(v: Array[Byte]):String = new String(v)
  }
}

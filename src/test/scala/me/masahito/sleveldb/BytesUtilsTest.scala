package me.masahito.sleveldb

import org.scalatest.FunSuite
import BytesUtils._

class BytesUtilsTest extends FunSuite {
  test("convert Int to bytes") {
    val test = 1
    val bytes = toBytes(test)
    assert(test === getValueFeelSoGood[Int](bytes))
  }

  test("convert Long to bytes") {
    val test = 1L
    val bytes = toBytes(test)
    assert(test === getValueFeelSoGood[Long](bytes))
  }

  test("convert str to bytes") {
    val str = "str"
    val a = toBytes(str)
    assert(str === getValueFeelSoGood[String](a))
  }
}

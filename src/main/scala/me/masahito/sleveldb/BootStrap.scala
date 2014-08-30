package me.masahito.sleveldb

import java.nio.file.Paths
import LevelDBWrapper._
import BytesUtils._

object BootStrap extends App {
  openAutoClose[Unit](Paths.get("example"), implicit db => {

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

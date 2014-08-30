package me.masahito

import java.nio.file.Paths
import me.masahito.sleveldb.LevelDBWrapper._
import me.masahito.sleveldb.BytesUtils._

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

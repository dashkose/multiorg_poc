/*
 * TODO: License goes here!
 */
package me.xsight.productengine.spark.runtime

import zio.{config => _, _}


object Main extends ZIOAppDefault {

  def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    Application.app
}
